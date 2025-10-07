<?php
/**
 * Order Model
 * Quản lý đơn hàng
 */
class Order {
    private $conn;
    private $table_name = "orders";
    private $details_table = "order_details";

    // Properties
    public $id;
    public $table_number;
    public $status;
    public $server_id;
    public $total_amount;
    public $payment_method;
    public $created_at;
    public $updated_at;
    public $completed_at;

    public function __construct($db) {
        $this->conn = $db;
    }

    /**
     * Lấy danh sách đơn hàng
     * @param string|null $status - Lọc theo trạng thái
     * @param int|null $server_id - Lọc theo người phục vụ
     * @return array
     */
    public function getAll($status = null, $server_id = null) {
        $query = "SELECT 
                    o.id,
                    o.table_number,
                    o.status,
                    o.total_amount,
                    o.payment_method,
                    o.created_at,
                    o.updated_at,
                    u.full_name as server_name
                  FROM " . $this->table_name . " o
                  JOIN users u ON o.server_id = u.id
                  WHERE 1=1";

        if($status) {
            $query .= " AND o.status = :status";
        }

        if($server_id) {
            $query .= " AND o.server_id = :server_id";
        }

        $query .= " ORDER BY o.created_at DESC";

        $stmt = $this->conn->prepare($query);

        if($status) {
            $stmt->bindParam(":status", $status);
        }

        if($server_id) {
            $stmt->bindParam(":server_id", $server_id);
        }

        $stmt->execute();
        $orders = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Lấy chi tiết món cho mỗi đơn
        foreach($orders as &$order) {
            $order['items'] = $this->getOrderDetails($order['id']);
        }

        return $orders;
    }

    /**
     * Lấy chi tiết đơn hàng
     * @param int $order_id
     * @return array
     */
    public function getOrderDetails($order_id) {
        $query = "SELECT 
                    od.id,
                    od.menu_item_id,
                    mi.name,
                    od.quantity,
                    od.price,
                    od.note
                  FROM " . $this->details_table . " od
                  JOIN menu_items mi ON od.menu_item_id = mi.id
                  WHERE od.order_id = :order_id";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":order_id", $order_id);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    /**
     * Tạo đơn hàng mới
     * @param array $items - Mảng các món: [{menu_item_id, quantity, note}]
     * @return int|false - Order ID nếu thành công
     */
    public function create($items) {
        try {
            $this->conn->beginTransaction();

            // Tạo order
            $query = "INSERT INTO " . $this->table_name . " 
                      SET table_number = :table_number,
                          server_id = :server_id,
                          status = 'moi'";

            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":table_number", $this->table_number);
            $stmt->bindParam(":server_id", $this->server_id);
            $stmt->execute();

            $order_id = $this->conn->lastInsertId();

            // Thêm chi tiết đơn
            $total = 0;
            $query_detail = "INSERT INTO " . $this->details_table . " 
                            SET order_id = :order_id,
                                menu_item_id = :menu_item_id,
                                quantity = :quantity,
                                price = :price,
                                note = :note";

            $stmt_detail = $this->conn->prepare($query_detail);

            foreach($items as $item) {
                // Lấy giá món
                $query_price = "SELECT price FROM menu_items WHERE id = :id";
                $stmt_price = $this->conn->prepare($query_price);
                $stmt_price->bindParam(":id", $item['menu_item_id']);
                $stmt_price->execute();
                $price = $stmt_price->fetchColumn();

                $stmt_detail->bindParam(":order_id", $order_id);
                $stmt_detail->bindParam(":menu_item_id", $item['menu_item_id']);
                $stmt_detail->bindParam(":quantity", $item['quantity']);
                $stmt_detail->bindParam(":price", $price);
                $stmt_detail->bindParam(":note", $item['note']);
                $stmt_detail->execute();

                $total += $price * $item['quantity'];
            }

            // Cập nhật tổng tiền
            $query_update = "UPDATE " . $this->table_name . " 
                            SET total_amount = :total 
                            WHERE id = :id";
            $stmt_update = $this->conn->prepare($query_update);
            $stmt_update->bindParam(":total", $total);
            $stmt_update->bindParam(":id", $order_id);
            $stmt_update->execute();

            $this->conn->commit();
            return $order_id;

        } catch(Exception $e) {
            $this->conn->rollBack();
            return false;
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng
     * @return bool
     */
    public function updateStatus() {
        $query = "UPDATE " . $this->table_name . " 
                  SET status = :status";
        
        // Nếu status là hoàn thành, cập nhật completed_at
        if($this->status === 'hoan_thanh') {
            $query .= ", completed_at = NOW()";
        }

        $query .= " WHERE id = :id";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":status", $this->status);
        $stmt->bindParam(":id", $this->id);

        return $stmt->execute();
    }

    /**
     * Hoàn thành đơn hàng (thanh toán)
     * @return bool
     */
    public function complete() {
        $query = "UPDATE " . $this->table_name . " 
                  SET status = 'da_thanh_toan',
                      payment_method = :payment_method,
                      completed_at = NOW()
                  WHERE id = :id";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":payment_method", $this->payment_method);
        $stmt->bindParam(":id", $this->id);

        return $stmt->execute();
    }

    /**
     * Lấy thống kê
     * @param string|null $date - Ngày cụ thể (YYYY-MM-DD)
     * @param string|null $from_date - Từ ngày
     * @param string|null $to_date - Đến ngày
     * @return array
     */
    public function getStatistics($date = null, $from_date = null, $to_date = null) {
        $where_clause = "WHERE o.status = 'da_thanh_toan'";

        if($date) {
            $where_clause .= " AND DATE(o.completed_at) = :date";
        } elseif($from_date && $to_date) {
            $where_clause .= " AND DATE(o.completed_at) BETWEEN :from_date AND :to_date";
        }

        // Tổng quan
        $query = "SELECT 
                    COUNT(*) as total_orders,
                    COALESCE(SUM(total_amount), 0) as total_revenue
                  FROM " . $this->table_name . " o " . $where_clause;

        $stmt = $this->conn->prepare($query);
        if($date) {
            $stmt->bindParam(":date", $date);
        } elseif($from_date && $to_date) {
            $stmt->bindParam(":from_date", $from_date);
            $stmt->bindParam(":to_date", $to_date);
        }
        $stmt->execute();
        $overview = $stmt->fetch(PDO::FETCH_ASSOC);

        // Đơn hàng theo trạng thái
        $query_status = "SELECT status, COUNT(*) as count 
                        FROM " . $this->table_name . " 
                        GROUP BY status";
        $stmt_status = $this->conn->prepare($query_status);
        $stmt_status->execute();
        $by_status = $stmt_status->fetchAll(PDO::FETCH_KEY_PAIR);

        // Món bán chạy
        $query_dishes = "SELECT 
                          mi.name,
                          SUM(od.quantity) as quantity_sold,
                          SUM(od.quantity * od.price) as revenue
                        FROM order_details od
                        JOIN menu_items mi ON od.menu_item_id = mi.id
                        JOIN orders o ON od.order_id = o.id
                        " . $where_clause . "
                        GROUP BY mi.id
                        ORDER BY quantity_sold DESC
                        LIMIT 10";

        $stmt_dishes = $this->conn->prepare($query_dishes);
        if($date) {
            $stmt_dishes->bindParam(":date", $date);
        } elseif($from_date && $to_date) {
            $stmt_dishes->bindParam(":from_date", $from_date);
            $stmt_dishes->bindParam(":to_date", $to_date);
        }
        $stmt_dishes->execute();
        $top_dishes = $stmt_dishes->fetchAll(PDO::FETCH_ASSOC);

        // Doanh thu theo phương thức thanh toán
        $query_payment = "SELECT 
                           payment_method,
                           SUM(total_amount) as revenue
                         FROM " . $this->table_name . " o
                         " . $where_clause . "
                         GROUP BY payment_method";

        $stmt_payment = $this->conn->prepare($query_payment);
        if($date) {
            $stmt_payment->bindParam(":date", $date);
        } elseif($from_date && $to_date) {
            $stmt_payment->bindParam(":from_date", $from_date);
            $stmt_payment->bindParam(":to_date", $to_date);
        }
        $stmt_payment->execute();
        $revenue_by_payment = $stmt_payment->fetchAll(PDO::FETCH_KEY_PAIR);

        return array(
            "total_orders" => (int)$overview['total_orders'],
            "total_revenue" => (float)$overview['total_revenue'],
            "orders_by_status" => $by_status,
            "top_dishes" => $top_dishes,
            "revenue_by_payment" => $revenue_by_payment
        );
    }
}
?>