<?php
/**
 * CORS Configuration
 * Cho phép Android app truy cập API từ mạng LAN
 */

// Cho phép tất cả origins (vì chỉ dùng trong mạng LAN)
header("Access-Control-Allow-Origin: *");

// Cho phép các methods
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");

// Cho phép các headers
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Content type JSON
header("Content-Type: application/json; charset=UTF-8");

// Xử lý preflight request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}
?>