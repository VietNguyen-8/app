# 📱 ANDROID APP - BÚN DUNG

Ứng dụng Android quản lý quán bún, kết nối với backend API qua mạng LAN.

## 📋 Yêu cầu

- **Android Studio**: Arctic Fox (2020.3.1) trở lên
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Language**: Java
- **Backend**: API đang chạy trên XAMPP

## 🚀 Cài đặt

### 1. Mở project trong Android Studio

```bash
# Clone hoặc copy thư mục android vào máy
cd /path/to/android

# Mở Android Studio
# File -> Open -> Chọn thư mục android
```

### 2. Cấu hình IP Backend

Mở file `ApiConfig.java` và thay đổi IP:

```java
// Đường dẫn: app/src/main/java/com/bundung/network/ApiConfig.java
private static final String BASE_URL = "http://192.168.1.100/bun_dung_api/";
```

**Cách lấy IP máy chủ XAMPP:**

**Windows:**
```bash
ipconfig
# Tìm dòng IPv4 Address: 192.168.1.xxx
```

**Mac/Linux:**
```bash
ifconfig
# Tìm dòng inet: 192.168.1.xxx
```

**Nếu test trên Emulator:**
```java
// Dùng 10.0.2.2 để trỏ về localhost của máy host
private static final String BASE_URL = "http://10.0.2.2/bun_dung_api/";
```

### 3. Sync Gradle

Android Studio sẽ tự động sync Gradle khi mở project. Nếu không:

```
File -> Sync Project with Gradle Files
```

### 4. Build & Run

```
1. Kết nối thiết bị Android (hoặc mở Emulator)
2. Đảm bảo thiết bị và máy chủ XAMPP cùng mạng WiFi
3. Click nút Run (hoặc Shift + F10)
```

## 📂 Cấu trúc Project

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
│   │   ├── OrderDetail.java            # Model chi tiết đơn
│   │   └── Statistics.java             # Model thống kê
│   ├── network/
│   │   ├── ApiConfig.java              # Cấu hình Retrofit
│   │   ├── ApiService.java             # Interface API
│   │   └── ApiResponse.java            # Response wrapper
│   └── utils/
│       ├── SessionManager.java         # Quản lý session
│       └── Constants.java              # Hằng số
└── res/
    ├── layout/                         # XML layouts
    ├── values/                         # Colors, Strings, Themes
    └── menu/                           # Menu XML
```

## 🎨 Màn hình

### 1. Login Screen
- Đăng nhập bằng username/password
- Tự động chuyển đến màn hình phù hợp với role

### 2. Waiter Screen (Phục vụ)
- Xem danh sách món ăn (grid 2 cột)
- Chọn món, nhập số lượng, ghi chú
- Nhập số bàn
- Gửi đơn hàng

### 3. Kitchen Screen (Bếp)
- 3 tabs: Đơn mới | Đang nấu | Hoàn thành
- Cập nhật trạng thái đơn
- Kéo xuống để refresh

### 4. Owner Screen (Chủ quán)
- Xem tổng doanh thu
- Tổng số đơn hàng
- Trung bình/đơn
- Biểu đồ phương thức thanh toán

## 👥 Tài khoản test

| Username  | Password | Role        | Màn hình      |
| --------- | -------- | ----------- | ------------- |
| phucvu1   | 123456   | phuc_vu     | WaiterActivity|
| bep1      | 123456   | bep         | KitchenActivity|
| chuquan   | 123456   | chu_quan    | OwnerActivity |

## 🔧 Troubleshooting

### Lỗi kết nối API

**1. Kiểm tra backend có chạy không**
```bash
# Mở trình duyệt, truy cập:
http://192.168.1.100/bun_dung_api/menu/get_menu.php

# Nếu thấy JSON -> Backend OK
```

**2. Kiểm tra thiết bị cùng mạng WiFi**
- Máy chủ XAMPP và thiết bị Android phải cùng WiFi
- Ping từ máy Android đến máy chủ

**3. Kiểm tra Firewall**
- Windows: Tắt Firewall tạm thời để test
- Hoặc cho phép port 80 qua Firewall

### Lỗi Gradle

```bash
# Clean project
./gradlew clean

# Rebuild
Build -> Clean Project
Build -> Rebuild Project
```

### Lỗi API timeout

Tăng timeout trong `ApiConfig.java`:

```java
.connectTimeout(60, TimeUnit.SECONDS)
.readTimeout(60, TimeUnit.SECONDS)
```

## 📦 Dependencies

```gradle
// Material Design
implementation 'com.google.android.material:material:1.10.0'

// Networking - Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Image Loading - Glide
implementation 'com.github.bumptech.glide:glide:4.16.0'

// Chart - MPAndroidChart
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

## 🎯 Luồng hoạt động

### Phục vụ ghi đơn
```
1. Đăng nhập (phucvu1/123456)
2. Nhập số bàn
3. Chọn món -> Nhấn +/- để điều chỉnh số lượng
4. Nhập ghi chú (nếu có)
5. Nhấn FAB "Gửi đơn"
6. Đơn được gửi lên server
```

### Bếp xử lý đơn
```
1. Đăng nhập (bep1/123456)
2. Tab "Đơn mới" hiển thị các đơn chưa nấu
3. Nhấn "Bắt đầu nấu"
4. Đơn chuyển sang tab "Đang nấu"
5. Khi xong, nhấn "Hoàn thành"
6. Đơn chuyển sang tab "Hoàn thành"
```

### Chủ quán xem thống kê
```
1. Đăng nhập (chuquan/123456)
2. Xem tổng doanh thu hôm nay
3. Xem biểu đồ phương thức thanh toán
4. Kéo xuống để refresh
```

## 🔒 Security Notes

- ⚠️ App này thiết kế cho môi trường LAN nội bộ
- Không có mã hóa HTTPS (dùng HTTP)
- Token đơn giản (không dùng JWT)
- Không nên expose ra Internet

## 📝 TODO (Tính năng mở rộng)

- [ ] Push notification khi có đơn mới
- [ ] Offline mode với Room Database
- [ ] In hóa đơn qua Bluetooth
- [ ] Quản lý menu từ app
- [ ] Lịch sử đơn hàng chi tiết

## 🙋 Hỗ trợ

Nếu gặp lỗi:

1. Check logcat trong Android Studio
2. Kiểm tra API response trong console
3. Test API bằng Postman
4. Đảm bảo database đã được import

---

**Version:** 1.0  
**Last Updated:** 2025-10-07