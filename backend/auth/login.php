<?php
/**
 * API: Đăng nhập
 * Method: POST
 * Body: {"username": "phucvu1", "password": "123456"}
 */

// Headers
include_once '../config/cors.php';

// Database & Models
include_once '../config/Database.php';
include_once '../models/User.php';

// Khởi tạo database
$database = new Database();
$db = $database->getConnection();

// Khởi tạo User model
$user = new User($db);

// Lấy dữ liệu POST
$data = json_decode(file_get_contents("php://input"));

// Validate input
if(empty($data->username) || empty($data->password)) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu"
    ));
    exit();
}

// Set properties
$user->username = $data->username;
$user->password = $data->password;

// Thực hiện login
$result = $user->login();

if($result) {
    // Generate simple token (trong production nên dùng JWT)
    $token = base64_encode($result['username'] . ':' . time());
    
    http_response_code(200);
    echo json_encode(array(
        "success" => true,
        "message" => "Đăng nhập thành công",
        "data" => array(
            "user_id" => $result['id'],
            "username" => $result['username'],
            "full_name" => $result['full_name'],
            "role" => $result['role'],
            "token" => $token
        )
    ));
} else {
    http_response_code(401);
    echo json_encode(array(
        "success" => false,
        "message" => "Tên đăng nhập hoặc mật khẩu không đúng"
    ));
}
?>