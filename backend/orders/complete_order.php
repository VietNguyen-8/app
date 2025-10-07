<?php
/**
 * API: Hoàn thành đơn hàng (Thanh toán)
 * Method: POST
 * Body: {
 *   "order_id": 1,
 *   "payment_method": "tien_mat"
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
if(empty($data->order_id) || empty($data->payment_method)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Thiếu thông tin (order_id, payment_method)"
    ));
    exit();
}

// Validate payment method
$valid_methods = array('tien_mat', 'vi_dien_tu');
if(!in_array($data->payment_method, $valid_methods)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Phương thức thanh toán không hợp lệ"
    ));
    exit();
}

// Set properties
$order->id = $data->order_id;
$order->payment_method = $data->payment_method;

// Hoàn thành đơn
if($order->complete()) {
    http_response_code(200);
    echo json_encode(array(
        "success" => true,
        "message" => "Đơn hàng đã được thanh toán thành công"
    ));
} else {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Không thể thanh toán đơn hàng"
    ));
}
?>