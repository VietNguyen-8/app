# 🚀 HƯỚNG DẪN TRIỂN KHAI APP "BÚN DUNG"

Hướng dẫn chi tiết cài đặt và triển khai hoàn chỉnh hệ thống quản lý quán bún.

---

## 📋 CHUẨN BỊ

### Phần cứng cần thiết
- ✅ 1 máy tính/laptop làm server (chạy XAMPP)
- ✅ 2-4 thiết bị Android (phục vụ, bếp, chủ quán)
- ✅ 1 Router WiFi (hoặc dùng WiFi có sẵn)

### Phần mềm cần cài đặt
- ✅ XAMPP (PHP + MySQL)
- ✅ Android Studio (để build APK)
- ✅ phpMyAdmin (đi kèm XAMPP)

---

## 🖥️ PHẦN 1: CÀI ĐẶT BACKEND (XAMPP)

### Bước 1: Cài đặt XAMPP

**Windows:**
1. Download XAMPP từ: https://www.apachefriends.org/
2. Chạy file cài đặt (xampp-windows-x64-xxx-installer.exe)
3. Chọn components: Apache, MySQL, PHP, phpMyAdmin
4. Cài đặt vào `C:\xampp`
5. Hoàn tất cài đặt

**Mac:**
1. Download XAMPP cho Mac
2. Kéo XAMPP vào Applications
3. Mở XAMPP và Start Apache + MySQL

**Linux:**
```bash
wget https://www.apachefriends.org/xampp-files/xxx/xampp-linux-x64-xxx-installer.run
chmod +x xampp-linux-x64-xxx-installer.run
sudo ./xampp-linux-x64-xxx-installer.run
```

### Bước 2: Khởi động XAMPP

1. Mở XAMPP Control Panel
2. Click **Start** ở dòng Apache
3. Click **Start** ở dòng MySQL
4. Đợi cả 2 chuyển sang màu xanh

### Bước 3: Tạo Database

**Cách 1: Qua phpMyAdmin (Khuyên dùng)**

1. Mở trình duyệt, truy cập: `http://localhost/phpmyadmin`
2. Click tab **SQL** ở trên
3. Mở file `backend/database.sql` bằng Notepad
4. Copy toàn bộ nội dung
5. Paste vào ô SQL trong phpMyAdmin
6. Click nút **Go** (hoặc **Thực hiện**)
7. Database `bun_dung` sẽ được tạo tự động

**Cách 2: Import file SQL**

1. Trong phpMyAdmin, click **Import**
2. Click **Choose File**
3. Chọn file `backend/database.sql`
4. Click **Go**

### Bước 4: Copy Backend API

**Windows:**
```bash
# Copy thư mục backend vào htdocs
xcopy backend C:\xampp\htdocs\bun_dung_api /E /I
```

**Mac/Linux:**
```bash
sudo cp -r backend /Applications/XAMPP/htdocs/bun_dung_api
# hoặc
sudo cp -r backend /opt/lampp/htdocs/bun_dung_api
```

### Bước 5: Test Backend API

1. Mở trình duyệt
2. Truy cập: `http://localhost/bun_dung_api/menu/get_menu.php`
3. Nếu thấy JSON hiển thị → **Thành công!** ✅

```json
{
  "success": true,
  "count": 11,
  "data": [...]
}
```

### Bước 6: Lấy IP máy chủ

**Windows:**
```bash
ipconfig
# Tìm dòng IPv4 Address: 192.168.x.xxx
```

**Mac:**
```bash
ifconfig en0 | grep inet
# Hoặc: System Preferences → Network
```

**Linux:**
```bash
ifconfig | grep inet
# hoặc
ip addr show
```

**Lưu lại IP này** (VD: `192.168.1.100`) → Sẽ dùng trong bước sau!

---

## 📱 PHẦN 2: BUILD ANDROID APP

### Bước 1: Mở Project trong Android Studio

1. Mở Android Studio
2. **File** → **Open**
3. Chọn thư mục `android`
4. Click **OK**
5. Đợi Gradle sync (có thể mất 5-10 phút lần đầu)

### Bước 2: Cấu hình IP Backend

1. Mở file: `app/src/main/java/com/bundung/network/ApiConfig.java`
2. Tìm dòng:
   ```java
   private static final String BASE_URL = "http://192.168.1.100/bun_dung_api/";
   ```
3. Thay `192.168.1.100` bằng **IP máy chủ XAMPP** bạn đã lấy ở bước 6 phía trên

### Bước 3: Build APK

**Option 1: Build Release APK**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

Sau khi build xong, file APK sẽ ở:
```
android/app/build/outputs/apk/release/app-release.apk
```

**Option 2: Build Debug APK (Nhanh hơn)**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

File APK ở:
```
android/app/build/outputs/apk/debug/app-debug.apk
```

### Bước 4: Cài đặt APK vào thiết bị Android

**Cách 1: Qua USB**
1. Kết nối Android với máy tính bằng USB
2. Bật **USB Debugging** trên Android (Settings → Developer Options)
3. Copy file APK vào điện thoại
4. Mở file APK và cài đặt

**Cách 2: Qua Email/Cloud**
1. Gửi file APK qua Email hoặc upload lên Google Drive
2. Mở trên điện thoại và download
3. Cài đặt APK

**Cách 3: Chạy trực tiếp từ Android Studio**
1. Kết nối điện thoại qua USB
2. Chọn thiết bị trong Android Studio
3. Click nút **Run** (▶️)

---

## 🔧 PHẦN 3: CẤU HÌNH MẠNG

### Bước 1: Kết nối WiFi

**Quan trọng:** Tất cả thiết bị phải kết nối cùng 1 mạng WiFi!

- Máy chủ XAMPP → Kết nối WiFi
- Điện thoại Phục vụ → Kết nối cùng WiFi
- Điện thoại Bếp → Kết nối cùng WiFi  
- Điện thoại Chủ quán → Kết nối cùng WiFi

### Bước 2: Test kết nối

Trên điện thoại Android, mở trình duyệt:
```
http://192.168.1.100/bun_dung_api/menu/get_menu.php
```

Thay `192.168.1.100` bằng IP máy chủ của bạn.

Nếu thấy JSON → **Kết nối OK!** ✅

### Bước 3: Cấu hình Firewall (Windows)

Nếu không kết nối được:

1. **Control Panel** → **Windows Defender Firewall**
2. Click **Advanced settings**
3. Click **Inbound Rules** → **New Rule**
4. Chọn **Port** → Next
5. Chọn **TCP**, nhập `80` → Next
6. Chọn **Allow the connection** → Next
7. Đặt tên: `XAMPP Apache` → Finish

---

## 🎯 PHẦN 4: TEST HỆ THỐNG

### Test 1: Đăng nhập

**Phục vụ:**
- Username: `phucvu1`
- Password: `123456`

**Bếp:**
- Username: `bep1`
- Password: `123456`

**Chủ quán:**
- Username: `chuquan`
- Password: `123456`

### Test 2: Luồng hoàn chỉnh

**Bước 1: Phục vụ ghi đơn**
```
1. Đăng nhập bằng phucvu1/123456
2. Nhập số bàn: 5
3. Chọn món: Bún bò Huế (x2)
4. Thêm ghi chú: "Ít ớt"
5. Nhấn FAB "Gửi đơn"
```

**Bước 2: Bếp nhận đơn**
```
1. Đăng nhập bằng bep1/123456
2. Tab "Đơn mới" sẽ hiển thị đơn bàn 5
3. Nhấn "Bắt đầu nấu"
4. Đơn chuyển sang tab "Đang nấu"
5. Nấu xong, nhấn "Hoàn thành"
```

**Bước 3: Phục vụ thanh toán**
```
1. Phục vụ thấy đơn bàn 5 đã hoàn thành
2. Mang món ra cho khách
3. Nhấn "Thanh toán"
4. Chọn: Tiền mặt / Ví điện tử
5. Xác nhận
```

**Bước 4: Chủ quán xem thống kê**
```
1. Đăng nhập bằng chuquan/123456
2. Xem tổng doanh thu hôm nay
3. Xem số đơn đã bán
4. Xem biểu đồ
```

---

## 🐛 XỬ LÝ LỖI THƯỜNG GẶP

### Lỗi 1: "Không thể kết nối đến server"

**Nguyên nhân:**
- Backend chưa chạy
- IP sai
- Không cùng mạng WiFi

**Giải pháp:**
```
1. Kiểm tra Apache & MySQL đang chạy trong XAMPP
2. Ping IP máy chủ từ điện thoại:
   - Cài app "Network Utilities" 
   - Ping đến IP máy chủ
3. Kiểm tra Firewall
4. Thử tắt Firewall tạm thời để test
```

### Lỗi 2: "Database connection error"

**Giải pháp:**
```
1. Mở phpMyAdmin
2. Kiểm tra database "bun_dung" đã tồn tại chưa
3. Import lại file database.sql
4. Kiểm tra file config/Database.php:
   - Username: root
   - Password: (để trống)
   - Database: bun_dung
```

### Lỗi 3: App crash khi mở

**Giải pháp:**
```
1. Mở Android Studio
2. Xem logcat để tìm lỗi
3. Thường do:
   - Thiếu dependency
   - Layout XML lỗi
   - IP backend sai format
```

### Lỗi 4: "CORS error" hoặc "Network error"

**Giải pháp:**
```
1. Kiểm tra file backend/config/cors.php đã được include chưa
2. Thêm vào đầu mỗi file API:
   include_once '../config/cors.php';
```

---

## ✅ CHECKLIST TRIỂN KHAI

Hoàn thành các bước sau theo thứ tự:

### Backend
- [ ] XAMPP đã cài đặt
- [ ] Apache đang chạy (port 80)
- [ ] MySQL đang chạy (port 3306)
- [ ] Database "bun_dung" đã tạo
- [ ] 4 bảng đã có dữ liệu mẫu
- [ ] API test OK qua browser

### Network
- [ ] Đã lấy IP máy chủ
- [ ] Tất cả thiết bị cùng WiFi
- [ ] Firewall đã cấu hình
- [ ] Ping OK từ điện thoại đến máy chủ

### Android
- [ ] Project build thành công
- [ ] IP backend đã cấu hình đúng
- [ ] APK đã build
- [ ] APK đã cài lên 3+ thiết bị
- [ ] Đăng nhập OK
- [ ] Gửi đơn OK
- [ ] Nhận đơn OK (bếp)
- [ ] Thống kê OK (chủ quán)

---

## 🎓 VIDEO HƯỚNG DẪN (Tùy chọn)

Bạn có thể quay video demo các bước:

1. **Video 1:** Cài đặt XAMPP + Import database (5 phút)
2. **Video 2:** Build Android app + Cài APK (5 phút)
3. **Video 3:** Demo luồng hoàn chỉnh (10 phút)

---

## 📞 HỖ TRỢ

Nếu gặp vấn đề:

1. **Đọc lại hướng dẫn** từ đầu
2. **Kiểm tra logcat** (Android Studio)
3. **Kiểm tra error log** (XAMPP/apache/error.log)
4. **Test API** bằng Postman
5. **Google lỗi** với từ khóa chính xác

---

## 🎉 HOÀN TẤT!

Chúc mừng! Bạn đã triển khai thành công hệ thống quản lý quán bún.

Bây giờ bạn có thể:
- ✅ Ghi đơn hàng từ điện thoại
- ✅ Bếp nhận và xử lý đơn realtime
- ✅ Xem thống kê doanh thu
- ✅ Quản lý hoạt động quán hiệu quả

**Chúc bạn kinh doanh thành công! 🍜**