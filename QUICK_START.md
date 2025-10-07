# ⚡ QUICK START - App "Bún Dung"

Hướng dẫn nhanh để chạy app trong 15 phút!

---

## 🎯 MỤC TIÊU

Sau 15 phút, bạn sẽ có:
- ✅ Backend API đang chạy
- ✅ App Android đã cài đặt
- ✅ Test thành công luồng hoàn chỉnh

---

## ⏱️ PHẦN 1: BACKEND (5 phút)

### Bước 1: Cài XAMPP (2 phút)
```
1. Download: https://www.apachefriends.org/download.html
2. Cài đặt (Next → Next → Finish)
3. Mở XAMPP Control Panel
4. Click Start ở Apache
5. Click Start ở MySQL
```

### Bước 2: Tạo Database (2 phút)
```
1. Mở browser: http://localhost/phpmyadmin
2. Click tab "SQL"
3. Mở file: workspace/backend/database.sql
4. Copy toàn bộ nội dung
5. Paste vào ô SQL
6. Click "Go"
7. ✅ Database "bun_dung" đã tạo!
```

### Bước 3: Copy API (1 phút)
**Windows:**
```cmd
xcopy workspace\backend C:\xampp\htdocs\bun_dung_api /E /I
```

**Mac/Linux:**
```bash
sudo cp -r workspace/backend /Applications/XAMPP/htdocs/bun_dung_api
```

### Bước 4: Test API
```
Mở browser: http://localhost/bun_dung_api/menu/get_menu.php
Thấy JSON → ✅ Backend OK!
```

---

## 📱 PHẦN 2: ANDROID APP (5 phút)

### Bước 1: Lấy IP máy chủ (30 giây)
**Windows:**
```cmd
ipconfig
→ Tìm "IPv4 Address": 192.168.x.xxx
```

**Mac:**
```bash
ifconfig en0 | grep inet
→ Lấy địa chỉ inet: 192.168.x.xxx
```

**Lưu lại IP này!** VD: `192.168.1.100`

### Bước 2: Build APK (3 phút)

**Option A: Có Android Studio**
```
1. Mở Android Studio
2. File → Open → chọn thư mục "workspace/android"
3. Đợi Gradle sync xong
4. Mở: app/src/main/java/com/bundung/network/ApiConfig.java
5. Sửa dòng:
   private static final String BASE_URL = "http://192.168.1.100/bun_dung_api/";
   (Thay 192.168.1.100 bằng IP máy chủ của bạn)
6. Build → Build APK
7. File APK ở: app/build/outputs/apk/debug/app-debug.apk
```

**Option B: Có file APK sẵn**
```
1. Tìm file app-debug.apk hoặc app-release.apk
2. Nhớ cấu hình IP đúng trong ApiConfig.java trước khi build
```

### Bước 3: Cài APK (1 phút)
```
1. Copy file APK vào điện thoại Android
2. Bật "Unknown sources" trong Settings
3. Mở file APK và cài đặt
4. ✅ App đã cài!
```

---

## 🧪 PHẦN 3: TEST (5 phút)

### Test 1: Đăng nhập (30 giây)
```
1. Mở app "Bún Dung"
2. Nhập:
   Username: phucvu1
   Password: 123456
3. Click "Đăng nhập"
4. ✅ Vào được màn hình Phục vụ!
```

### Test 2: Ghi đơn - Phục vụ (2 phút)
```
1. Nhập số bàn: 5
2. Chọn món "Bún bò Huế"
3. Click nút "+" để tăng số lượng lên 2
4. Nhập ghi chú: "Ít ớt"
5. Click FAB "Gửi đơn"
6. ✅ Thông báo "Đơn hàng đã được gửi thành công"
```

### Test 3: Nhận đơn - Bếp (1 phút)
```
1. Đăng xuất (Menu → Logout)
2. Đăng nhập lại:
   Username: bep1
   Password: 123456
3. Tab "Đơn mới" hiển thị đơn bàn 5
4. Click "Bắt đầu nấu"
5. Đơn chuyển sang tab "Đang nấu"
6. Click "Hoàn thành"
7. ✅ Đơn chuyển sang tab "Hoàn thành"
```

### Test 4: Xem thống kê - Chủ quán (1 phút)
```
1. Đăng xuất
2. Đăng nhập:
   Username: chuquan
   Password: 123456
3. Xem:
   - Tổng doanh thu
   - Tổng đơn hàng
   - Biểu đồ thanh toán
4. ✅ Hiển thị số liệu!
```

---

## ✅ CHECKLIST NHANH

### Backend
- [ ] XAMPP đã cài
- [ ] Apache đang chạy (port 80)
- [ ] MySQL đang chạy (port 3306)
- [ ] Database "bun_dung" đã tạo
- [ ] API test OK: http://localhost/bun_dung_api/menu/get_menu.php

### Network
- [ ] Đã lấy IP máy chủ (192.168.x.xxx)
- [ ] Máy chủ và điện thoại cùng WiFi

### Android
- [ ] Đã build APK
- [ ] IP backend đã cấu hình đúng trong ApiConfig.java
- [ ] APK đã cài lên điện thoại
- [ ] Đăng nhập thành công

### Test
- [ ] Phục vụ ghi đơn OK
- [ ] Bếp nhận đơn OK
- [ ] Chủ quán xem thống kê OK

---

## 🆘 LỖI THƯỜNG GẶP

### "Không kết nối được server"
```
→ Kiểm tra:
1. Apache đang chạy chưa?
2. IP đúng chưa?
3. Cùng WiFi chưa?
4. Firewall chặn port 80 không?

→ Fix nhanh: Tắt Firewall tạm thời để test
```

### "Database error"
```
→ Import lại database.sql
→ Check: Username=root, Password=(trống)
```

### "App crash"
```
→ Check IP format trong ApiConfig.java
→ Phải là: http://192.168.x.xxx/bun_dung_api/
→ Không được: http://192.168.x.xxx:80/bun_dung_api/
```

---

## 📞 CẦN GIÚP?

1. Xem [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) - Hướng dẫn chi tiết
2. Xem [README.md](./README.md) - Tổng quan
3. Xem [backend/README.md](./backend/README.md) - API docs
4. Xem [android/README.md](./android/README.md) - Android docs

---

## 🎉 HOÀN TẤT!

**Nếu đã test thành công hết 4 bước ở trên:**

🎊 **CHÚC MỪNG!** 🎊

Bạn đã triển khai thành công app "Bún Dung"!

Bây giờ bạn có thể:
- ✅ Sử dụng ngay trong quán
- ✅ Cài thêm APK lên nhiều thiết bị
- ✅ Bắt đầu kinh doanh hiệu quả hơn!

---

**Chúc bạn thành công! 🍜💪**

*Version: 1.0*
*Time to deploy: 15 minutes*