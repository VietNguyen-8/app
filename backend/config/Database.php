<?php
/**
 * Database Connection Class
 * Quản lý kết nối đến MySQL database
 */
class Database {
    // Cấu hình database
    private $host = "localhost";
    private $db_name = "bun_dung";
    private $username = "root";
    private $password = "";
    private $charset = "utf8mb4";
    
    public $conn;

    /**
     * Tạo kết nối đến database
     * @return PDO|null
     */
    public function getConnection() {
        $this->conn = null;

        try {
            $dsn = "mysql:host=" . $this->host . ";dbname=" . $this->db_name . ";charset=" . $this->charset;
            
            $options = array(
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES => false,
            );

            $this->conn = new PDO($dsn, $this->username, $this->password, $options);
            
        } catch(PDOException $exception) {
            echo json_encode(array(
                "success" => false,
                "message" => "Lỗi kết nối database: " . $exception->getMessage()
            ));
            exit();
        }

        return $this->conn;
    }
}
?>