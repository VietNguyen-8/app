<?php
/**
 * API: Lấy danh sách món ăn
 * Method: GET
 * Query params: ?category=Bún (optional)
 */

// Headers
include_once '../config/cors.php';

// Database & Models
include_once '../config/Database.php';
include_once '../models/MenuItem.php';

// Khởi tạo database
$database = new Database();
$db = $database->getConnection();

// Khởi tạo MenuItem model
$menuItem = new MenuItem($db);

// Lấy category từ query params (nếu có)
$category = isset($_GET['category']) ? $_GET['category'] : null;

// Lấy danh sách món
$items = $menuItem->getAvailable();

// Filter by category nếu có
if($category) {
    $items = array_filter($items, function($item) use ($category) {
        return $item['category'] === $category;
    });
    $items = array_values($items); // Re-index array
}

if(count($items) > 0) {
    http_response_code(200);
    echo json_encode(array(
        "success" => true,
        "count" => count($items),
        "data" => $items
    ));
} else {
    http_response_code(200);
    echo json_encode(array(
        "success" => true,
        "count" => 0,
        "data" => array(),
        "message" => "Không có món ăn nào"
    ));
}
?>