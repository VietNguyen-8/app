<?php
/**
 * MenuItem Model
 * Quản lý thông tin món ăn
 */
class MenuItem {
    private $conn;
    private $table_name = "menu_items";

    // Properties
    public $id;
    public $name;
    public $price;
    public $category;
    public $description;
    public $image;
    public $is_available;

    public function __construct($db) {
        $this->conn = $db;
    }

    /**
     * Lấy danh sách tất cả món ăn
     * @param string|null $category - Lọc theo danh mục (optional)
     * @return array
     */
    public function getAll($category = null) {
        $query = "SELECT id, name, price, category, description, image, is_available 
                  FROM " . $this->table_name . " 
                  WHERE 1=1";

        if($category) {
            $query .= " AND category = :category";
        }

        $query .= " ORDER BY category, name";

        $stmt = $this->conn->prepare($query);

        if($category) {
            $stmt->bindParam(":category", $category);
        }

        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    /**
     * Lấy chỉ món còn hàng
     * @return array
     */
    public function getAvailable() {
        $query = "SELECT id, name, price, category, description, image 
                  FROM " . $this->table_name . " 
                  WHERE is_available = 1 
                  ORDER BY category, name";

        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    /**
     * Lấy thông tin món theo ID
     * @return array|false
     */
    public function getById() {
        $query = "SELECT id, name, price, category, description, image, is_available 
                  FROM " . $this->table_name . " 
                  WHERE id = :id 
                  LIMIT 1";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":id", $this->id);
        $stmt->execute();

        if($stmt->rowCount() > 0) {
            return $stmt->fetch(PDO::FETCH_ASSOC);
        }
        
        return false;
    }

    /**
     * Cập nhật trạng thái món (có/hết hàng)
     * @return bool
     */
    public function updateAvailability() {
        $query = "UPDATE " . $this->table_name . " 
                  SET is_available = :is_available 
                  WHERE id = :id";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":is_available", $this->is_available);
        $stmt->bindParam(":id", $this->id);

        return $stmt->execute();
    }

    /**
     * Tạo món mới
     * @return bool
     */
    public function create() {
        $query = "INSERT INTO " . $this->table_name . " 
                  SET name = :name,
                      price = :price,
                      category = :category,
                      description = :description,
                      image = :image";

        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(":name", $this->name);
        $stmt->bindParam(":price", $this->price);
        $stmt->bindParam(":category", $this->category);
        $stmt->bindParam(":description", $this->description);
        $stmt->bindParam(":image", $this->image);

        return $stmt->execute();
    }
}
?>