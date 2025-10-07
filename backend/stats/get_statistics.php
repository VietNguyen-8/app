<?php
/**
 * API: Lấy thống kê
 * Method: GET
 * Query params: ?date=2025-10-07 hoặc ?from_date=2025-10-01&to_date=2025-10-07
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
$date = isset($_GET['date']) ? $_GET['date'] : null;
$from_date = isset($_GET['from_date']) ? $_GET['from_date'] : null;
$to_date = isset($_GET['to_date']) ? $_GET['to_date'] : null;

// Nếu không có params, mặc định lấy hôm nay
if(!$date && !$from_date && !$to_date) {
    $date = date('Y-m-d');
}

// Lấy thống kê
$stats = $order->getStatistics($date, $from_date, $to_date);

http_response_code(200);
echo json_encode(array(
    "success" => true,
    "data" => $stats
));
?>