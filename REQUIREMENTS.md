# 📋 YÊU CẦU DỰ ÁN: APP "BÚN DUNG"

## 🧩 TỔNG QUAN DỰ ÁN

Ứng dụng Android quản lý quán bún gia đình hoạt động **cục bộ trong mạng LAN** (không cần internet, chỉ cần WiFi nội bộ + XAMPP chạy trên máy chủ).

### 🎯 Mục tiêu chính

Ứng dụng giúp các vai trò trong quán (Phục vụ, Bếp, Chủ quán) phối hợp qua cùng một hệ thống:

1. **Phục vụ** ghi đơn hàng từ khách.
2. **Bếp** xem và xử lý các đơn.
3. **Chủ quán** xem thống kê, theo dõi hoạt động.
4. Các bên trao đổi thông tin realtime (cập nhật trạng thái đơn, hoàn thành, thanh toán).

### 🌐 Kiến trúc hệ thống

- **Môi trường**: Mạng LAN nội bộ (không cần Internet)
- **Backend**: XAMPP (PHP + MySQL) chạy trên máy chủ cục bộ
- **Frontend**: Android App (Java)
- **Giao tiếp**: RESTful API (JSON)

---

## 🖥️ PHẦN 1: BACKEND (XAMPP - PHP + MySQL)

### 1.1. Cấu trúc thư mục

```
htdocs/
└── bun_dung_api/
    ├── config/
    │   └── Database.php              # Kết nối database
    ├── models/
    │   ├── User.php                  # Model người dùng
    │   ├── Order.php                 # Model đơn hàng
    │   └── MenuItem.php              # Model món ăn
    ├── auth/
    │   └── login.php                 # API đăng nhập
    ├── orders/
    │   ├── get_orders.php            # Lấy danh sách đơn hàng
    │   ├── add_order.php             # Thêm đơn hàng mới
    │   ├── update_status.php         # Cập nhật trạng thái đơn
    │   └── complete_order.php        # Hoàn thành đơn hàng
    ├── menu/
    │   └── get_menu.php              # Lấy thực đơn
    ├── stats/
    │   └── get_statistics.php        # Lấy thống kê (cho chủ quán)
    └── database.sql                  # Script tạo database
```

### 1.2. Cấu trúc Database (MySQL)

#### Bảng `users`
Lưu thông tin người dùng và vai trò

```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('phuc_vu', 'bep', 'chu_quan') NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Dữ liệu mẫu:**
```sql
INSERT INTO users (username, password, role, full_name) VALUES
('phucvu1', '$2y$10$...', 'phuc_vu', 'Nguyễn Văn A'),
('bep1', '$2y$10$...', 'bep', 'Trần Văn B'),
('chuquan', '$2y$10$...', 'chu_quan', 'Chị Dung');
```

#### Bảng `menu_items`
Lưu danh sách món ăn

```sql
CREATE TABLE menu_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    image VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Dữ liệu mẫu:**
```sql
INSERT INTO menu_items (name, price, category, image) VALUES
('Bún bò Huế', 45000, 'Bún', 'bun_bo_hue.jpg'),
('Bún riêu', 40000, 'Bún', 'bun_rieu.jpg'),
('Bún chả', 45000, 'Bún', 'bun_cha.jpg'),
('Nước ngọt', 15000, 'Đồ uống', 'nuoc_ngot.jpg'),
('Trà đá', 5000, 'Đồ uống', 'tra_da.jpg');
```

#### Bảng `orders`
Lưu thông tin đơn hàng

```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    table_number INT NOT NULL,
    status ENUM('moi', 'dang_nau', 'hoan_thanh', 'da_thanh_toan') DEFAULT 'moi',
    server_id INT,
    total_amount DECIMAL(10, 2),
    payment_method ENUM('tien_mat', 'vi_dien_tu') DEFAULT 'tien_mat',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (server_id) REFERENCES users(id)
);
```

#### Bảng `order_details`
Lưu chi tiết các món trong đơn hàng

```sql
CREATE TABLE order_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    note TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);
```

### 1.3. Vai trò người dùng

| Vai trò      | Mã vai trò    | Quyền hạn                                                              |
| ------------ | ------------- | ---------------------------------------------------------------------- |
| **Phục vụ**  | `phuc_vu`     | Tạo đơn hàng, xem đơn của mình, cập nhật trạng thái thanh toán         |
| **Bếp**      | `bep`         | Xem đơn mới, cập nhật trạng thái "Đang nấu" → "Hoàn thành"            |
| **Chủ quán** | `chu_quan`    | Xem tất cả đơn, thống kê doanh thu, quản lý menu & nhân viên          |

### 1.4. API Endpoints

#### 🔐 Authentication

##### POST `/auth/login.php`
Đăng nhập vào hệ thống

**Request:**
```json
{
    "username": "phucvu1",
    "password": "123456"
}
```

**Response (Success):**
```json
{
    "success": true,
    "message": "Đăng nhập thành công",
    "data": {
        "user_id": 1,
        "username": "phucvu1",
        "full_name": "Nguyễn Văn A",
        "role": "phuc_vu",
        "token": "eyJ0eXAiOiJKV1QiLCJhbGc..."
    }
}
```

**Response (Error):**
```json
{
    "success": false,
    "message": "Tên đăng nhập hoặc mật khẩu không đúng"
}
```

---

#### 🍜 Menu Management

##### GET `/menu/get_menu.php`
Lấy danh sách món ăn

**Query Parameters:**
- `category` (optional): Lọc theo danh mục

**Response:**
```json
{
    "success": true,
    "data": [
        {
            "id": 1,
            "name": "Bún bò Huế",
            "price": 45000,
            "category": "Bún",
            "image": "bun_bo_hue.jpg",
            "is_available": true
        },
        {
            "id": 2,
            "name": "Bún riêu",
            "price": 40000,
            "category": "Bún",
            "image": "bun_rieu.jpg",
            "is_available": true
        }
    ]
}
```

---

#### 📝 Order Management

##### GET `/orders/get_orders.php`
Lấy danh sách đơn hàng

**Query Parameters:**
- `role`: Vai trò người dùng (`phuc_vu`, `bep`, `chu_quan`)
- `status` (optional): Lọc theo trạng thái
- `server_id` (optional): Lọc theo người phục vụ

**Response:**
```json
{
    "success": true,
    "data": [
        {
            "id": 1,
            "table_number": 5,
            "status": "moi",
            "server_name": "Nguyễn Văn A",
            "total_amount": 90000,
            "created_at": "2025-10-07 10:30:00",
            "items": [
                {
                    "menu_item_id": 1,
                    "name": "Bún bò Huế",
                    "quantity": 2,
                    "price": 45000,
                    "note": "Ít ớt"
                }
            ]
        }
    ]
}
```

##### POST `/orders/add_order.php`
Tạo đơn hàng mới

**Request:**
```json
{
    "table_number": 5,
    "server_id": 1,
    "items": [
        {
            "menu_item_id": 1,
            "quantity": 2,
            "note": "Ít ớt"
        },
        {
            "menu_item_id": 4,
            "quantity": 1
        }
    ]
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đơn hàng đã được tạo thành công",
    "order_id": 1
}
```

##### POST `/orders/update_status.php`
Cập nhật trạng thái đơn hàng

**Request:**
```json
{
    "order_id": 1,
    "status": "dang_nau"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Cập nhật trạng thái thành công"
}
```

**Các trạng thái:**
- `moi`: Đơn mới (vừa tạo)
- `dang_nau`: Bếp đang nấu
- `hoan_thanh`: Món đã xong
- `da_thanh_toan`: Đã thanh toán

##### POST `/orders/complete_order.php`
Hoàn thành và thanh toán đơn hàng

**Request:**
```json
{
    "order_id": 1,
    "payment_method": "tien_mat"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đơn hàng đã được thanh toán"
}
```

---

#### 📊 Statistics (Chủ quán)

##### GET `/stats/get_statistics.php`
Lấy thống kê

**Query Parameters:**
- `date` (optional): Ngày cụ thể (YYYY-MM-DD)
- `from_date` (optional): Từ ngày
- `to_date` (optional): Đến ngày

**Response:**
```json
{
    "success": true,
    "data": {
        "total_orders": 25,
        "total_revenue": 1250000,
        "orders_by_status": {
            "moi": 3,
            "dang_nau": 5,
            "hoan_thanh": 2,
            "da_thanh_toan": 15
        },
        "top_dishes": [
            {
                "name": "Bún bò Huế",
                "quantity_sold": 35,
                "revenue": 1575000
            },
            {
                "name": "Bún riêu",
                "quantity_sold": 28,
                "revenue": 1120000
            }
        ],
        "revenue_by_payment": {
            "tien_mat": 800000,
            "vi_dien_tu": 450000
        }
    }
}
```

---

## 📱 PHẦN 2: FRONTEND (Android - Java)

### 2.1. Cấu trúc Project

```
app/src/main/
├── java/com/bundung/
│   ├── activities/
│   │   ├── LoginActivity.java          # Màn hình đăng nhập
│   │   ├── WaiterActivity.java         # Màn hình phục vụ
│   │   ├── KitchenActivity.java        # Màn hình bếp
│   │   └── OwnerActivity.java          # Màn hình chủ quán
│   ├── adapters/
│   │   ├── MenuAdapter.java            # Adapter hiển thị menu
│   │   └── OrderAdapter.java           # Adapter hiển thị đơn hàng
│   ├── models/
│   │   ├── User.java                   # Model người dùng
│   │   ├── MenuItem.java               # Model món ăn
│   │   ├── Order.java                  # Model đơn hàng
│   │   └── OrderDetail.java            # Model chi tiết đơn
│   ├── network/
│   │   ├── ApiService.java             # Service gọi API
│   │   └── ApiConfig.java              # Cấu hình API
│   └── utils/
│       ├── SessionManager.java         # Quản lý phiên đăng nhập
│       └── Constants.java              # Hằng số
└── res/
    ├── layout/
    │   ├── activity_login.xml
    │   ├── activity_waiter.xml
    │   ├── activity_kitchen.xml
    │   ├── activity_owner.xml
    │   ├── item_menu.xml               # Layout item menu
    │   └── item_order.xml              # Layout item đơn hàng
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── drawable/
        └── (các icon và hình ảnh)
```

### 2.2. Cấu hình Gradle

**build.gradle (Module: app)**

```gradle
dependencies {
    // Material Design
    implementation 'com.google.android.material:material:1.10.0'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    
    // RecyclerView
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    
    // CardView
    implementation 'androidx.cardview:cardview:1.0.0'
    
    // Chart (cho thống kê)
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    
    // SwipeRefreshLayout
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
}
```

### 2.3. Thiết kế UI/UX

#### 🎨 Màu sắc chủ đạo

**colors.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Primary Colors -->
    <color name="primary_red">#E57373</color>          <!-- Đỏ bún Dung -->
    <color name="primary_dark">#D32F2F</color>
    <color name="accent">#FFF8E1</color>               <!-- Trắng kem -->
    
    <!-- Status Colors -->
    <color name="status_new">#FFC107</color>           <!-- Vàng - Đơn mới -->
    <color name="status_cooking">#FF9800</color>       <!-- Cam - Đang nấu -->
    <color name="status_done">#4CAF50</color>          <!-- Xanh - Hoàn thành -->
    <color name="status_paid">#2196F3</color>          <!-- Xanh dương - Đã thanh toán -->
    
    <!-- Background -->
    <color name="background">#FAFAFA</color>
    <color name="card_background">#FFFFFF</color>
    
    <!-- Text -->
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
</resources>
```

#### 📱 Màn hình Login

**activity_login.xml:**

**Thành phần:**
- Logo quán (ImageView)
- Tiêu đề "BÚN DUNG"
- EditText: Username
- EditText: Password (input type password)
- Button: Đăng nhập
- ProgressBar (ẩn khi không load)

**Đặc điểm:**
- Gradient background (đỏ → cam)
- Card chứa form đăng nhập
- Material Design EditText với icon

#### 📱 Màn hình Phục vụ

**activity_waiter.xml:**

**Thành phần:**
- Toolbar: Tên nhân viên, nút đăng xuất
- EditText: Số bàn
- RecyclerView: Danh sách món ăn (grid 2 cột)
- FloatingActionButton: "Gửi đơn"
- Badge hiển thị số món đã chọn

**Item Menu (item_menu.xml):**
- CardView
- ImageView: Hình món ăn
- TextView: Tên món
- TextView: Giá
- Button +/- để chọn số lượng
- EditText: Ghi chú (ẩn, hiện khi nhấn vào món)

#### 📱 Màn hình Bếp

**activity_kitchen.xml:**

**Thành phần:**
- Toolbar: "BẾP - BÚN DUNG"
- TabLayout: "Đơn mới" | "Đang nấu" | "Hoàn thành"
- ViewPager2: Hiển thị danh sách đơn theo tab
- SwipeRefreshLayout: Kéo để refresh

**Item Order (item_order.xml):**
- CardView với màu theo trạng thái
- TextView: Số bàn (lớn, nổi bật)
- RecyclerView: Danh sách món trong đơn
- TextView: Thời gian tạo đơn
- Button: "Bắt đầu nấu" / "Hoàn thành"

#### 📱 Màn hình Chủ quán

**activity_owner.xml:**

**Thành phần:**
- Toolbar với DatePicker (chọn ngày xem thống kê)
- ScrollView chứa:
  - CardView: Tổng doanh thu (số lớn, nổi bật)
  - CardView: Tổng đơn hàng
  - CardView: Trung bình/đơn
  - BarChart: Doanh thu theo giờ
  - RecyclerView: Top món bán chạy
  - PieChart: Phương thức thanh toán

### 2.4. Models (Java Classes)

#### User.java
```java
public class User {
    private int id;
    private String username;
    private String fullName;
    private String role;
    private String token;
    
    // Constructors, Getters, Setters
}
```

#### MenuItem.java
```java
public class MenuItem {
    private int id;
    private String name;
    private double price;
    private String category;
    private String image;
    private boolean isAvailable;
    private int quantity; // Số lượng đã chọn (cho giỏ hàng)
    
    // Constructors, Getters, Setters
}
```

#### Order.java
```java
public class Order {
    private int id;
    private int tableNumber;
    private String status;
    private String serverName;
    private double totalAmount;
    private String createdAt;
    private List<OrderDetail> items;
    
    // Constructors, Getters, Setters
}
```

#### OrderDetail.java
```java
public class OrderDetail {
    private int menuItemId;
    private String name;
    private int quantity;
    private double price;
    private String note;
    
    // Constructors, Getters, Setters
}
```

### 2.5. Network Layer

#### ApiConfig.java
```java
public class ApiConfig {
    // Địa chỉ IP máy chủ XAMPP trong mạng LAN
    public static final String BASE_URL = "http://192.168.1.100/bun_dung_api/";
    
    public static final String LOGIN = "auth/login.php";
    public static final String GET_MENU = "menu/get_menu.php";
    public static final String ADD_ORDER = "orders/add_order.php";
    public static final String GET_ORDERS = "orders/get_orders.php";
    public static final String UPDATE_STATUS = "orders/update_status.php";
    public static final String COMPLETE_ORDER = "orders/complete_order.php";
    public static final String GET_STATISTICS = "stats/get_statistics.php";
}
```

#### ApiService.java
```java
public interface ApiService {
    @POST(ApiConfig.LOGIN)
    Call<ApiResponse<User>> login(@Body LoginRequest request);
    
    @GET(ApiConfig.GET_MENU)
    Call<ApiResponse<List<MenuItem>>> getMenu();
    
    @POST(ApiConfig.ADD_ORDER)
    Call<ApiResponse<Integer>> addOrder(@Body AddOrderRequest request);
    
    @GET(ApiConfig.GET_ORDERS)
    Call<ApiResponse<List<Order>>> getOrders(
        @Query("role") String role,
        @Query("status") String status
    );
    
    @POST(ApiConfig.UPDATE_STATUS)
    Call<ApiResponse<Void>> updateOrderStatus(@Body UpdateStatusRequest request);
    
    @POST(ApiConfig.COMPLETE_ORDER)
    Call<ApiResponse<Void>> completeOrder(@Body CompleteOrderRequest request);
    
    @GET(ApiConfig.GET_STATISTICS)
    Call<ApiResponse<Statistics>> getStatistics(@Query("date") String date);
}
```

### 2.6. Session Management

#### SessionManager.java
```java
public class SessionManager {
    private SharedPreferences prefs;
    
    // Lưu thông tin đăng nhập
    public void saveLoginSession(User user);
    
    // Lấy thông tin người dùng hiện tại
    public User getCurrentUser();
    
    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn();
    
    // Đăng xuất
    public void logout();
}
```

---

## ⚙️ PHẦN 3: LUỒNG HOẠT ĐỘNG

### 3.1. Use Case: Phục vụ ghi đơn

**Luồng chính:**

1. Phục vụ đăng nhập bằng tài khoản `phuc_vu`
2. Hệ thống hiển thị màn hình WaiterActivity
3. Phục vụ nhập số bàn (VD: Bàn 5)
4. Phục vụ chọn món từ danh sách:
   - Nhấn nút "+" để thêm món
   - Nhấn vào món để thêm ghi chú (VD: "Ít ớt")
5. Badge hiển thị số món đã chọn
6. Phục vụ nhấn FloatingActionButton "Gửi đơn"
7. Hệ thống hiển thị dialog xác nhận
8. Phục vụ xác nhận → API `add_order.php` được gọi
9. Đơn hàng được tạo với status = `moi`
10. Bếp nhận được đơn mới

**Xử lý lỗi:**
- Nếu chưa chọn bàn → Hiển thị toast "Vui lòng nhập số bàn"
- Nếu chưa chọn món → Hiển thị toast "Vui lòng chọn ít nhất 1 món"
- Nếu API lỗi → Hiển thị lỗi, lưu đơn tạm trong Room (optional)

### 3.2. Use Case: Bếp xử lý đơn

**Luồng chính:**

1. Bếp đăng nhập bằng tài khoản `bep`
2. Hệ thống hiển thị KitchenActivity với tab "Đơn mới"
3. Bếp xem danh sách đơn có status = `moi`
4. Bếp chọn đơn, nhấn "Bắt đầu nấu"
5. API `update_status.php` được gọi với status = `dang_nau`
6. Đơn chuyển sang tab "Đang nấu"
7. Khi nấu xong, bếp nhấn "Hoàn thành"
8. API `update_status.php` được gọi với status = `hoan_thanh`
9. Phục vụ nhận thông báo món đã xong

**Auto-refresh:**
- Mỗi 10 giây tự động gọi API để lấy đơn mới
- Hoặc dùng SwipeRefreshLayout kéo xuống để refresh

### 3.3. Use Case: Phục vụ thanh toán

**Luồng chính:**

1. Phục vụ nhận thông báo đơn bàn X đã hoàn thành
2. Phục vụ mang món ra cho khách
3. Khách yêu cầu thanh toán
4. Phục vụ mở đơn, chọn phương thức thanh toán:
   - Tiền mặt
   - Ví điện tử
5. Nhấn "Thanh toán"
6. API `complete_order.php` được gọi
7. Đơn chuyển status = `da_thanh_toan`
8. Hiển thị hóa đơn (optional: in bill)

### 3.4. Use Case: Chủ quán xem thống kê

**Luồng chính:**

1. Chủ quán đăng nhập bằng tài khoản `chu_quan`
2. Hệ thống hiển thị OwnerActivity
3. Mặc định hiển thị thống kê hôm nay
4. Chủ quán có thể:
   - Chọn ngày khác để xem
   - Chọn khoảng thời gian (từ ngày X → ngày Y)
5. API `get_statistics.php` được gọi
6. Hiển thị:
   - Tổng doanh thu
   - Tổng số đơn
   - Biểu đồ doanh thu theo giờ
   - Top món bán chạy
   - Tỷ lệ thanh toán (tiền mặt vs ví điện tử)

---

## 🔔 PHẦN 4: TÍNH NĂNG BỔ SUNG (OPTIONAL)

### 4.1. Thông báo Realtime

**Cách triển khai:**

**Option 1: Polling (Đơn giản)**
- App tự động gọi API mỗi 5-10 giây
- Kiểm tra có đơn mới/cập nhật không

**Option 2: WebSocket (Phức tạp hơn)**
- Dùng Socket.IO hoặc WebSocket
- Server push thông báo realtime khi có sự kiện

### 4.2. In hóa đơn

- Kết nối máy in nhiệt qua Bluetooth
- Dùng thư viện in hóa đơn cho Android

### 4.3. Offline Mode

- Lưu menu trong Room Database
- Khi mất kết nối, vẫn xem được menu
- Đơn hàng lưu tạm, sync khi có mạng

### 4.4. Quản lý Menu (Admin)

- Thêm/sửa/xóa món ăn
- Cập nhật giá
- Đánh dấu món hết hàng

### 4.5. Quản lý Nhân viên

- Thêm/xóa tài khoản nhân viên
- Xem lịch sử làm việc
- Tính lương theo đơn

---

## 📊 PHẦN 5: YÊU CẦU KỸ THUẬT

### 5.1. Backend Requirements

- **PHP**: Version 7.4+
- **MySQL**: Version 5.7+
- **XAMPP**: Version 3.3.0+
- **Web Server**: Apache 2.4+

**Security:**
- Password hash với `password_hash()` (bcrypt)
- Validate input để tránh SQL Injection
- Sử dụng Prepared Statements
- CORS headers cho phép Android truy cập

### 5.2. Android Requirements

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Language**: Java
- **Architecture**: MVC hoặc MVP

**Libraries:**
- Retrofit 2: HTTP client
- Gson: JSON parsing
- Glide: Image loading
- Material Components: UI
- MPAndroidChart: Charts

### 5.3. Network Requirements

- Router WiFi nội bộ
- Máy chủ XAMPP có IP tĩnh trong LAN (VD: 192.168.1.100)
- Các thiết bị Android kết nối cùng mạng WiFi

---

## 🧪 PHẦN 6: TESTING & DEPLOYMENT

### 6.1. Testing Plan

**Backend Testing:**
- Test các API endpoint với Postman
- Test database constraints
- Test authentication & authorization

**Frontend Testing:**
- Test UI trên nhiều kích thước màn hình
- Test kết nối API
- Test các use case chính
- Test offline behavior

**Integration Testing:**
- Test toàn bộ luồng: Đăng nhập → Ghi đơn → Xử lý → Thanh toán
- Test đồng thời nhiều người dùng

### 6.2. Deployment

**Backend:**
1. Cài đặt XAMPP trên máy chủ
2. Import database.sql vào MySQL
3. Copy thư mục `bun_dung_api` vào `htdocs`
4. Cấu hình IP tĩnh cho máy chủ
5. Test API qua trình duyệt

**Frontend:**
1. Build APK file
2. Cài đặt APK vào các thiết bị Android
3. Cấu hình IP máy chủ trong `ApiConfig.java`
4. Test kết nối

---

## 📝 PHẦN 7: TIMELINE & MILESTONES

### Phase 1: Setup & Database (Tuần 1)
- ✅ Thiết lập XAMPP
- ✅ Tạo database & tables
- ✅ Insert dữ liệu mẫu

### Phase 2: Backend API (Tuần 2-3)
- ✅ Authentication API
- ✅ Menu API
- ✅ Orders API
- ✅ Statistics API
- ✅ Test tất cả endpoints

### Phase 3: Android UI (Tuần 4-5)
- ✅ Design màn hình Login
- ✅ Design màn hình Phục vụ
- ✅ Design màn hình Bếp
- ✅ Design màn hình Chủ quán

### Phase 4: Android Logic (Tuần 6-7)
- ✅ Network layer
- ✅ Models & Adapters
- ✅ Session management
- ✅ Kết nối API

### Phase 5: Integration & Testing (Tuần 8)
- ✅ Integration testing
- ✅ Bug fixes
- ✅ Performance optimization

### Phase 6: Deployment (Tuần 9)
- ✅ Deploy backend
- ✅ Build & install Android app
- ✅ Training người dùng

---

## 📞 PHỤ LỤC

### A. Mã trạng thái đơn hàng

| Mã             | Tên hiển thị    | Màu sắc  | Người cập nhật |
| -------------- | --------------- | -------- | -------------- |
| `moi`          | Đơn mới         | #FFC107  | Hệ thống       |
| `dang_nau`     | Đang nấu        | #FF9800  | Bếp            |
| `hoan_thanh`   | Hoàn thành      | #4CAF50  | Bếp            |
| `da_thanh_toan`| Đã thanh toán   | #2196F3  | Phục vụ        |

### B. Error Codes

| Code | Message                          | Giải pháp                   |
| ---- | -------------------------------- | --------------------------- |
| 401  | Unauthorized                     | Đăng nhập lại               |
| 403  | Forbidden                        | Không có quyền              |
| 404  | Not Found                        | Kiểm tra endpoint           |
| 500  | Internal Server Error            | Kiểm tra log server         |

### C. File cấu hình Database.php mẫu

```php
<?php
class Database {
    private $host = "localhost";
    private $db_name = "bun_dung";
    private $username = "root";
    private $password = "";
    public $conn;

    public function getConnection() {
        $this->conn = null;
        try {
            $this->conn = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name,
                $this->username,
                $this->password
            );
            $this->conn->exec("set names utf8");
        } catch(PDOException $exception) {
            echo "Connection error: " . $exception->getMessage();
        }
        return $this->conn;
    }
}
?>
```

---

## 🎯 KẾT LUẬN

Tài liệu này mô tả đầy đủ yêu cầu cho dự án **App "Bún Dung"** - một ứng dụng quản lý quán bún gia đình hoạt động trong mạng LAN cục bộ.

**Điểm mạnh của hệ thống:**
- ✅ Không phụ thuộc Internet
- ✅ Chi phí thấp (chỉ cần WiFi + XAMPP)
- ✅ Dễ triển khai & bảo trì
- ✅ UI/UX thân thiện, dễ sử dụng
- ✅ Phân quyền rõ ràng

**Roadmap mở rộng:**
- 🔮 Thêm tính năng đặt bàn trước
- 🔮 Tích hợp thanh toán QR Code
- 🔮 Báo cáo chi tiết hơn cho chủ quán
- 🔮 App khách hàng (order tự phục vụ)

---

**Người soạn:** AI Assistant  
**Ngày tạo:** 2025-10-07  
**Phiên bản:** 1.0