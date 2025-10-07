# 🖥️ BACKEND API - BÚN DUNG

Backend API cho ứng dụng quản lý quán bún, chạy trên XAMPP (PHP + MySQL).

## 📁 Cấu trúc thư mục

```
backend/
├── config/
│   ├── Database.php        # Kết nối database
│   └── cors.php           # Cấu hình CORS
├── models/
│   ├── User.php           # Model người dùng
│   ├── MenuItem.php       # Model món ăn
│   └── Order.php          # Model đơn hàng
├── auth/
│   └── login.php          # API đăng nhập
├── menu/
│   └── get_menu.php       # API lấy thực đơn
├── orders/
│   ├── get_orders.php     # API lấy đơn hàng
│   ├── add_order.php      # API tạo đơn mới
│   ├── update_status.php  # API cập nhật trạng thái
│   └── complete_order.php # API thanh toán
├── stats/
│   └── get_statistics.php # API thống kê
├── database.sql           # Script tạo database
└── README.md
```

## 🚀 Cài đặt

### 1. Cài đặt XAMPP

- Download XAMPP từ: https://www.apachefriends.org/
- Cài đặt và khởi động Apache + MySQL

### 2. Tạo Database

**Cách 1: Import file SQL**
1. Mở phpMyAdmin: http://localhost/phpmyadmin
2. Click "Import"
3. Chọn file `database.sql`
4. Click "Go"

**Cách 2: Copy & Paste**
1. Mở phpMyAdmin
2. Click tab "SQL"
3. Copy toàn bộ nội dung file `database.sql`
4. Paste và click "Go"

### 3. Copy code vào htdocs

```bash
# Copy thư mục backend vào htdocs
cp -r backend /Applications/XAMPP/htdocs/bun_dung_api
# hoặc trên Windows
xcopy backend C:\xampp\htdocs\bun_dung_api /E /I
```

### 4. Cấu hình Database

Mở file `config/Database.php` và chỉnh sửa nếu cần:

```php
private $host = "localhost";
private $db_name = "bun_dung";
private $username = "root";
private $password = "";  // Để trống nếu dùng XAMPP mặc định
```

### 5. Test API

Mở trình duyệt và truy cập:

```
http://localhost/bun_dung_api/menu/get_menu.php
```

Nếu thấy JSON trả về → Thành công! ✅

## 📡 API Endpoints

### 🔐 Authentication

#### POST `/auth/login.php`
Đăng nhập

**Request:**
```json
{
    "username": "phucvu1",
    "password": "123456"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đăng nhập thành công",
    "data": {
        "user_id": 1,
        "username": "phucvu1",
        "full_name": "Nguyễn Văn A",
        "role": "phuc_vu",
        "token": "..."
    }
}
```

### 🍜 Menu

#### GET `/menu/get_menu.php`
Lấy danh sách món ăn

**Response:**
```json
{
    "success": true,
    "count": 11,
    "data": [...]
}
```

### 📝 Orders

#### GET `/orders/get_orders.php?status=moi`
Lấy danh sách đơn hàng

#### POST `/orders/add_order.php`
Tạo đơn mới

#### POST `/orders/update_status.php`
Cập nhật trạng thái

#### POST `/orders/complete_order.php`
Thanh toán đơn

### 📊 Statistics

#### GET `/stats/get_statistics.php?date=2025-10-07`
Lấy thống kê

## 👥 Tài khoản mẫu

| Username  | Password | Role      |
| --------- | -------- | --------- |
| phucvu1   | 123456   | phuc_vu   |
| phucvu2   | 123456   | phuc_vu   |
| bep1      | 123456   | bep       |
| chuquan   | 123456   | chu_quan  |

## 🔧 Troubleshooting

### Lỗi kết nối database
- Kiểm tra MySQL đã chạy chưa
- Kiểm tra tên database trong `config/Database.php`

### Lỗi CORS
- File `config/cors.php` đã được include trong mọi API
- Nếu vẫn lỗi, check Apache config

### API trả về lỗi 500
- Check Apache error log: `xampp/logs/error.log`
- Check PHP error reporting

## 📱 Kết nối với Android

Trong Android app, cấu hình base URL:

```java
// Lấy IP máy chủ XAMPP trong mạng LAN
public static final String BASE_URL = "http://192.168.1.100/bun_dung_api/";
```

**Cách lấy IP:**
- Windows: `ipconfig`
- Mac/Linux: `ifconfig`

## 🔒 Security Notes

⚠️ **Lưu ý**: API này được thiết kế cho môi trường LAN nội bộ. Không nên expose ra Internet công khai vì:

- Chưa có rate limiting
- Token đơn giản (nên dùng JWT trong production)
- CORS cho phép tất cả origins

Nếu muốn deploy lên production, cần thêm:
- JWT authentication
- Rate limiting
- Input sanitization nâng cao
- HTTPS
- Firewall rules