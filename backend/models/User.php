<?php
/**
 * User Model
 * Quản lý thông tin người dùng
 */
class User {
    private $conn;
    private $table_name = "users";

    // Properties
    public $id;
    public $username;
    public $password;
    public $role;
    public $full_name;
    public $is_active;
    public $created_at;

    public function __construct($db) {
        $this->conn = $db;
    }

    /**
     * Đăng nhập
     * @return bool|array
     */
    public function login() {
        $query = "SELECT id, username, password, role, full_name, is_active 
                  FROM " . $this->table_name . " 
                  WHERE username = :username AND is_active = 1 
                  LIMIT 1";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":username", $this->username);
        $stmt->execute();

        if($stmt->rowCount() > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            
            // Verify password
            if(password_verify($this->password, $row['password'])) {
                return array(
                    "id" => $row['id'],
                    "username" => $row['username'],
                    "role" => $row['role'],
                    "full_name" => $row['full_name']
                );
            }
        }
        
        return false;
    }

    /**
     * Lấy thông tin user theo ID
     * @return bool|array
     */
    public function getById() {
        $query = "SELECT id, username, role, full_name, is_active 
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
     * Lấy danh sách tất cả users
     * @return array
     */
    public function getAll() {
        $query = "SELECT id, username, role, full_name, is_active, created_at 
                  FROM " . $this->table_name . " 
                  ORDER BY created_at DESC";

        $stmt = $this->conn->prepare($query);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    /**
     * Tạo user mới
     * @return bool
     */
    public function create() {
        $query = "INSERT INTO " . $this->table_name . " 
                  SET username = :username,
                      password = :password,
                      role = :role,
                      full_name = :full_name";

        $stmt = $this->conn->prepare($query);

        // Hash password
        $hashed_password = password_hash($this->password, PASSWORD_BCRYPT);

        $stmt->bindParam(":username", $this->username);
        $stmt->bindParam(":password", $hashed_password);
        $stmt->bindParam(":role", $this->role);
        $stmt->bindParam(":full_name", $this->full_name);

        if($stmt->execute()) {
            return true;
        }
        
        return false;
    }
}
?>