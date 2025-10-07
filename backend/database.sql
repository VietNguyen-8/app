-- ========================================
-- DATABASE: BÚN DUNG
-- Hệ thống quản lý quán bún gia đình
-- ========================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS bun_dung CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bun_dung;

-- ========================================
-- Bảng USERS - Quản lý người dùng
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'Password đã hash bằng bcrypt',
    role ENUM('phuc_vu', 'bep', 'chu_quan') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Bảng MENU_ITEMS - Danh sách món ăn
-- ========================================
CREATE TABLE IF NOT EXISTS menu_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) DEFAULT 'Món chính',
    description TEXT,
    image VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Bảng ORDERS - Đơn hàng
-- ========================================
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    table_number INT NOT NULL,
    status ENUM('moi', 'dang_nau', 'hoan_thanh', 'da_thanh_toan') DEFAULT 'moi',
    server_id INT NOT NULL,
    total_amount DECIMAL(10, 2) DEFAULT 0,
    payment_method ENUM('tien_mat', 'vi_dien_tu') DEFAULT 'tien_mat',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (server_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_status (status),
    INDEX idx_table (table_number),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Bảng ORDER_DETAILS - Chi tiết đơn hàng
-- ========================================
CREATE TABLE IF NOT EXISTS order_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL COMMENT 'Giá tại thời điểm đặt',
    note TEXT COMMENT 'Ghi chú món ăn (VD: ít ớt, nhiều hành)',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT,
    INDEX idx_order (order_id),
    INDEX idx_menu_item (menu_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- DỮ LIỆU MẪU
-- ========================================

-- Thêm người dùng (password mặc định: 123456)
-- Hash bcrypt của "123456": $2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
INSERT INTO users (username, password, role, full_name) VALUES
('phucvu1', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'phuc_vu', 'Nguyễn Văn A'),
('phucvu2', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'phuc_vu', 'Trần Thị B'),
('bep1', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'bep', 'Lê Văn C'),
('chuquan', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'chu_quan', 'Chị Dung');

-- Thêm món ăn
INSERT INTO menu_items (name, price, category, description, image) VALUES
-- Món bún
('Bún bò Huế', 45000, 'Bún', 'Bún bò Huế đặc sản, nước dùng đậm đà', 'bun_bo_hue.jpg'),
('Bún riêu', 40000, 'Bún', 'Bún riêu cua đồng, nước dùng chua nhẹ', 'bun_rieu.jpg'),
('Bún chả', 45000, 'Bún', 'Bún chả Hà Nội, thịt nướng thơm ngon', 'bun_cha.jpg'),
('Bún thịt nướng', 40000, 'Bún', 'Bún thịt nướng, rau sống, nước mắm pha', 'bun_thit_nuong.jpg'),
('Bún mọc', 35000, 'Bún', 'Bún chả mọc, nước dùng trong ngọt', 'bun_moc.jpg'),

-- Đồ uống
('Nước ngọt', 15000, 'Đồ uống', 'Coca, Pepsi, 7Up, Sprite', 'nuoc_ngot.jpg'),
('Trà đá', 5000, 'Đồ uống', 'Trà đá miễn phí khi order món', 'tra_da.jpg'),
('Nước cam', 20000, 'Đồ uống', 'Nước cam ép tươi', 'nuoc_cam.jpg'),
('Sữa đậu nành', 15000, 'Đồ uống', 'Sữa đậu nành nóng/lạnh', 'sua_dau_nanh.jpg'),

-- Món thêm
('Chả giò (2 cái)', 20000, 'Món thêm', 'Chả giò chiên giòn', 'cha_gio.jpg'),
('Nem chua rán', 25000, 'Món thêm', 'Nem chua rán giòn', 'nem_chua_ran.jpg');

-- ========================================
-- Thêm đơn hàng mẫu
-- ========================================

-- Đơn 1: Bàn 5, trạng thái mới
INSERT INTO orders (table_number, status, server_id, created_at) VALUES
(5, 'moi', 1, NOW());
SET @order1_id = LAST_INSERT_ID();

INSERT INTO order_details (order_id, menu_item_id, quantity, price, note) VALUES
(@order1_id, 1, 2, 45000, 'Ít ớt'),
(@order1_id, 6, 1, 15000, NULL);

UPDATE orders SET total_amount = (SELECT SUM(quantity * price) FROM order_details WHERE order_id = @order1_id) WHERE id = @order1_id;

-- Đơn 2: Bàn 3, đang nấu
INSERT INTO orders (table_number, status, server_id, created_at) VALUES
(3, 'dang_nau', 1, DATE_SUB(NOW(), INTERVAL 5 MINUTE));
SET @order2_id = LAST_INSERT_ID();

INSERT INTO order_details (order_id, menu_item_id, quantity, price) VALUES
(@order2_id, 2, 1, 40000),
(@order2_id, 7, 1, 5000);

UPDATE orders SET total_amount = (SELECT SUM(quantity * price) FROM order_details WHERE order_id = @order2_id) WHERE id = @order2_id;

-- Đơn 3: Bàn 7, hoàn thành
INSERT INTO orders (table_number, status, server_id, created_at) VALUES
(7, 'hoan_thanh', 2, DATE_SUB(NOW(), INTERVAL 10 MINUTE));
SET @order3_id = LAST_INSERT_ID();

INSERT INTO order_details (order_id, menu_item_id, quantity, price) VALUES
(@order3_id, 3, 3, 45000);

UPDATE orders SET total_amount = (SELECT SUM(quantity * price) FROM order_details WHERE order_id = @order3_id) WHERE id = @order3_id;

-- Đơn 4: Bàn 2, đã thanh toán
INSERT INTO orders (table_number, status, server_id, payment_method, created_at, completed_at) VALUES
(2, 'da_thanh_toan', 1, 'tien_mat', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 50 MINUTE));
SET @order4_id = LAST_INSERT_ID();

INSERT INTO order_details (order_id, menu_item_id, quantity, price) VALUES
(@order4_id, 1, 1, 45000),
(@order4_id, 4, 1, 40000),
(@order4_id, 6, 2, 15000);

UPDATE orders SET total_amount = (SELECT SUM(quantity * price) FROM order_details WHERE order_id = @order4_id) WHERE id = @order4_id;

-- ========================================
-- VIEWS - Tạo view để dễ query
-- ========================================

-- View đơn hàng với thông tin đầy đủ
CREATE OR REPLACE VIEW v_orders_full AS
SELECT 
    o.id,
    o.table_number,
    o.status,
    o.total_amount,
    o.payment_method,
    o.created_at,
    o.updated_at,
    o.completed_at,
    u.id as server_id,
    u.full_name as server_name
FROM orders o
JOIN users u ON o.server_id = u.id;

-- View thống kê món bán chạy
CREATE OR REPLACE VIEW v_popular_items AS
SELECT 
    mi.id,
    mi.name,
    mi.category,
    mi.price,
    COUNT(od.id) as times_ordered,
    SUM(od.quantity) as total_quantity,
    SUM(od.quantity * od.price) as total_revenue
FROM menu_items mi
LEFT JOIN order_details od ON mi.id = od.menu_item_id
LEFT JOIN orders o ON od.order_id = o.id AND o.status = 'da_thanh_toan'
GROUP BY mi.id
ORDER BY total_quantity DESC;

-- ========================================
-- Thông tin đăng nhập
-- ========================================
/*
THÔNG TIN ĐĂNG NHẬP MẶC ĐỊNH:

Username: phucvu1    | Password: 123456 | Role: Phục vụ
Username: phucvu2    | Password: 123456 | Role: Phục vụ  
Username: bep1       | Password: 123456 | Role: Bếp
Username: chuquan    | Password: 123456 | Role: Chủ quán

HƯỚNG DẪN CÀI ĐẶT:
1. Mở XAMPP Control Panel
2. Start Apache và MySQL
3. Mở phpMyAdmin (http://localhost/phpmyadmin)
4. Tạo database mới hoặc import file này
5. Click "Import" và chọn file database.sql
6. Hoặc copy toàn bộ SQL và chạy trong tab SQL

LƯU Ý:
- Đảm bảo MySQL đang chạy
- Database sẽ tự động tạo nếu chưa có
- Dữ liệu mẫu sẽ được insert tự động
*/