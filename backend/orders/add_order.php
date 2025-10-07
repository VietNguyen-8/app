<?php
/**
 * API: Tạo đơn hàng mới
 * Method: POST
 * Body: {
 *   "table_number": 5,
 *   "server_id": 1,
 *   "items": [
 *     {"menu_item_id": 1, "quantity": 2, "note": "Ít ớt"},
 *     {"menu_item_id": 6, "quantity": 1, "note": null}
 *   ]
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
if(empty($data->table_number) || empty($data->server_id) || empty($data->items)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Thiếu thông tin đơn hàng (table_number, server_id, items)"
    ));
    exit();
}

if(!is_array($data->items) || count($data->items) === 0) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Đơn hàng phải có ít nhất 1 món"
    ));
    exit();
}

// Set properties
$order->table_number = $data->table_number;
$order->server_id = $data->server_id;

// Chuyển items sang array
$items = array();
foreach($data->items as $item) {
    $items[] = array(
        'menu_item_id' => $item->menu_item_id,
        'quantity' => $item->quantity,
        'note' => isset($item->note) ? $item->note : null
    );
}

// Tạo đơn hàng
$order_id = $order->create($items);

if($order_id) {
    http_response_code(201);
    echo json_encode(array(
        "success" => true,
        "message" => "Đơn hàng đã được tạo thành công",
        "order_id" => $order_id
    ));
} else {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Không thể tạo đơn hàng. Vui lòng thử lại"
    ));
}
?>