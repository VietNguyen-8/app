<?php
/**
 * API: Cập nhật trạng thái đơn hàng
 * Method: POST
 * Body: {
 *   "order_id": 1,
 *   "status": "dang_nau"
 * }
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

// Lấy dữ liệu POST
$data = json_decode(file_get_contents("php://input"));

// Validate input
if(empty($data->order_id) || empty($data->status)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Thiếu thông tin (order_id, status)"
    ));
    exit();
}

// Validate status
$valid_statuses = array('moi', 'dang_nau', 'hoan_thanh', 'da_thanh_toan');
if(!in_array($data->status, $valid_statuses)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Trạng thái không hợp lệ"
    ));
    exit();
}

// Set properties
$order->id = $data->order_id;
$order->status = $data->status;

// Cập nhật status
if($order->updateStatus()) {
    http_response_code(200);
    echo json_encode(array(
        "success" => true,
        "message" => "Cập nhật trạng thái thành công"
    ));
} else {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Không thể cập nhật trạng thái"
    ));
}
?>