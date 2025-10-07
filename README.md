# 🍜 App "Bún Dung" - Hệ thống quản lý quán bún gia đình

Ứng dụng Android quản lý quán bún hoạt động trong mạng LAN cục bộ (không cần Internet).

## 🎯 Tổng quan nhanh

**Vai trò:**
- 👨‍🍳 **Bếp**: Nhận và xử lý đơn hàng
- 🙋 **Phục vụ**: Ghi đơn, thanh toán
- 👔 **Chủ quán**: Xem thống kê, quản lý

**Công nghệ:**
- Backend: PHP + MySQL (XAMPP)
- Frontend: Android (Java)
- Kết nối: RESTful API trong mạng LAN

---

## 📋 Tài liệu đầy đủ

- **[REQUIREMENTS.md](./REQUIREMENTS.md)** - Yêu cầu chi tiết dự án
- **[DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)** - Hướng dẫn triển khai từng bước
- **[backend/README.md](./backend/README.md)** - Tài liệu Backend API
- **[android/README.md](./android/README.md)** - Tài liệu Android App

---

## 🚀 Bắt đầu nhanh

### 1️⃣ Cài đặt Backend (5 phút)

```bash
# 1. Cài XAMPP từ https://www.apachefriends.org/
# 2. Start Apache + MySQL
# 3. Mở phpMyAdmin: http://localhost/phpmyadmin
# 4. Import file backend/database.sql
# 5. Copy thư mục backend vào htdocs/bun_dung_api
# 6. Test: http://localhost/bun_dung_api/menu/get_menu.php
```

### 2️⃣ Build Android App (10 phút)

```bash
# 1. Mở Android Studio
# 2. Open project → chọn thư mục android
# 3. Đợi Gradle sync
# 4. Sửa IP trong ApiConfig.java → IP máy chủ XAMPP
# 5. Build → Build APK
# 6. Cài APK lên điện thoại
```

### 3️⃣ Test hệ thống

**Đăng nhập:**
- Phục vụ: `phucvu1` / `123456`
- Bếp: `bep1` / `123456`
- Chủ quán: `chuquan` / `123456`

**Luồng test:**
1. Phục vụ ghi đơn bàn 5: Bún bò Huế x2
2. Bếp nhận đơn → Bắt đầu nấu → Hoàn thành
3. Phục vụ thanh toán
4. Chủ quán xem thống kê

---

## 📂 Cấu trúc dự án

```
workspace/
├── backend/                    # Backend API (PHP + MySQL)
│   ├── config/                 # Database config
│   ├── models/                 # PHP models
│   ├── auth/                   # Login API
│   ├── menu/                   # Menu APIs
│   ├── orders/                 # Order APIs
│   ├── stats/                  # Statistics API
│   ├── database.sql            # Database schema
│   └── README.md
│
├── android/                    # Android App (Java)
│   └── app/src/main/
│       ├── java/com/bundung/
│       │   ├── activities/     # Activities (Login, Waiter, Kitchen, Owner)
│       │   ├── adapters/       # RecyclerView adapters
│       │   ├── models/         # Data models
│       │   ├── network/        # API service (Retrofit)
│       │   └── utils/          # Utilities
│       ├── res/
│       │   ├── layout/         # XML layouts
│       │   ├── values/         # Colors, Strings, Themes
│       │   └── menu/           # Menu XMLs
│       └── AndroidManifest.xml
│
├── REQUIREMENTS.md             # Yêu cầu chi tiết
├── DEPLOYMENT_GUIDE.md         # Hướng dẫn triển khai
└── README.md                   # File này
```

---

## 🌟 Tính năng chính

### Phục vụ
- ✅ Xem menu món ăn (grid view)
- ✅ Chọn món, điều chỉnh số lượng
- ✅ Thêm ghi chú (VD: "Ít ớt")
- ✅ Nhập số bàn
- ✅ Gửi đơn hàng
- ✅ Badge hiển thị số món đã chọn

### Bếp
- ✅ 3 tabs: Đơn mới | Đang nấu | Hoàn thành
- ✅ Cập nhật trạng thái đơn
- ✅ Kéo xuống để refresh
- ✅ Hiển thị thời gian đơn được tạo
- ✅ Màu sắc phân biệt trạng thái

### Chủ quán
- ✅ Tổng doanh thu hôm nay
- ✅ Tổng số đơn hàng
- ✅ Trung bình tiền/đơn
- ✅ Biểu đồ phương thức thanh toán (Pie chart)
- ✅ Top món bán chạy
- ✅ Thống kê theo ngày

---

## 🛠️ Công nghệ sử dụng

### Backend
- **PHP**: 7.4+
- **MySQL**: 5.7+
- **XAMPP**: 3.3.0+
- **Architecture**: MVC
- **API Style**: RESTful JSON

### Android
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Language**: Java
- **Architecture**: MVC
- **Libraries**:
  - Retrofit 2 (HTTP client)
  - Gson (JSON parsing)
  - Material Components (UI)
  - MPAndroidChart (Charts)
  - Glide (Image loading)

---

## 📊 Database Schema

```sql
users           # Người dùng (phuc_vu, bep, chu_quan)
menu_items      # Danh sách món ăn
orders          # Đơn hàng
order_details   # Chi tiết món trong đơn
```

**Trạng thái đơn hàng:**
- `moi` → `dang_nau` → `hoan_thanh` → `da_thanh_toan`

---

## 🔐 Security

⚠️ **Lưu ý quan trọng:**

App này được thiết kế cho **môi trường LAN nội bộ**. Không nên expose ra Internet vì:

- ❌ Không có HTTPS (dùng HTTP)
- ❌ Token authentication đơn giản
- ❌ CORS cho phép tất cả origins
- ❌ Không có rate limiting

Nếu muốn deploy lên production/Internet, cần:
- ✅ Cấu hình HTTPS
- ✅ Dùng JWT authentication
- ✅ Rate limiting
- ✅ Input validation nâng cao
- ✅ CORS restrictive

---

## 🐛 Troubleshooting

### "Không thể kết nối server"
```
1. Kiểm tra Apache + MySQL đang chạy
2. Kiểm tra IP đúng chưa
3. Ping từ điện thoại đến máy chủ
4. Tắt Firewall tạm thời để test
```

### "Database error"
```
1. Import lại database.sql
2. Check config/Database.php
3. Username: root, Password: (trống)
```

### App crash
```
1. Xem logcat trong Android Studio
2. Check IP format trong ApiConfig.java
3. Rebuild project
```

---

## 📈 Roadmap

**v1.0** (Hiện tại)
- ✅ Login với 3 roles
- ✅ Ghi đơn hàng
- ✅ Xử lý đơn (bếp)
- ✅ Thống kê cơ bản

**v1.1** (Tương lai)
- [ ] Push notification
- [ ] Offline mode (Room DB)
- [ ] In hóa đơn Bluetooth
- [ ] Quản lý menu từ app
- [ ] Lịch sử chi tiết

**v2.0** (Mở rộng)
- [ ] Multi-language
- [ ] Cloud sync
- [ ] App khách hàng (order tự phục vụ)
- [ ] Tích hợp QR payment

---

## 👥 Contributors

Dự án được phát triển bởi AI Assistant dựa trên yêu cầu của bạn.

---

## 📄 License

Dự án này được phát triển cho mục đích học tập và sử dụng nội bộ.

---

## 🙋 Support

Nếu cần hỗ trợ:

1. Đọc [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)
2. Check logs (logcat + Apache error.log)
3. Test API với Postman
4. Google error message

---

**Version:** 1.0  
**Last Updated:** 2025-10-07  
**Status:** Production Ready ✅

**Chúc bạn thành công với quán bún! 🍜🎉**