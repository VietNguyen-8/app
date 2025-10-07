<?php
/**
 * API: Lấy danh sách đơn hàng
 * Method: GET
 * Query params: ?status=moi&server_id=1
 */

// Headers
include_once '../config/cors.php';

// Database & Models
include_once '../config/Database.php';
include_once '../models/Order.php';

// Khởi tạo database
$database = new Database();
$db = $database->getConnection();

// Khởi tạo Order model
$order = new Order($db);

// Lấy params từ query string
$status = isset($_GET['status']) ? $_GET['status'] : null;
$server_id = isset($_GET['server_id']) ? $_GET['server_id'] : null;
$role = isset($_GET['role']) ? $_GET['role'] : null;

// Nếu role là bếp, chỉ lấy đơn mới và đang nấu
if($role === 'bep') {
    if(!$status) {
        $status = 'moi'; // Mặc định hiển thị đơn mới
    }
}

// Lấy danh sách đơn
$orders = $order->getAll($status, $server_id);

http_response_code(200);
echo json_encode(array(
    "success" => true,
    "count" => count($orders),
    "data" => $orders
));
?>