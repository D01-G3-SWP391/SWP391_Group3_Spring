# 🧪 Test Ban Feature - Hướng dẫn kiểm tra

## ✅ Các vấn đề đã được sửa:

### 1. **Ban vĩnh viễn không hoạt động**
- ✅ **FIXED**: Validation logic trong `BanRequestDTO.java`
- ✅ **FIXED**: Controller xử lý `banDurationDays` cho permanent ban
- ✅ **FIXED**: Form template cho phép nhập 0 cho permanent ban

### 2. **Không hiển thị thông báo ban**
- ✅ **FIXED**: Cải thiện flash message với thông tin chi tiết
- ✅ **FIXED**: Template sử dụng `th:utext` để hiển thị emoji
- ✅ **FIXED**: CSS styling cho alert messages

### 3. **Email không gửi**
- ✅ **FIXED**: Sử dụng `CompletableFuture` cho async email sending
- ✅ **FIXED**: Better error handling và logging

---

## 🧪 Cách test:

### **Test 1: Ban tạm thời**
1. Đăng nhập admin
2. Vào **Student Management** hoặc **Employer Management**
3. Click nút **"Ban"** trên user active
4. Chọn:
   - **Lý do**: Spam nội dung
   - **Loại ban**: Tạm thời
   - **Số ngày**: 7
   - **Mô tả**: Test ban tạm thời
5. Click **"Xác nhận Ban User"**
6. **Kết quả mong đợi**:
   - ✅ Hiển thị thông báo: "✅ Đã ban student thành công với lý do: Spam nội dung (Thời gian: 7 ngày). Email thông báo đã được gửi."
   - ✅ User status chuyển thành "inactive"
   - ✅ Email được gửi đến user

### **Test 2: Ban vĩnh viễn**
1. Click nút **"Ban"** trên user active khác
2. Chọn:
   - **Lý do**: Lừa đảo
   - **Loại ban**: Vĩnh viễn
   - **Số ngày**: (để trống hoặc nhập 0)
   - **Mô tả**: Test ban vĩnh viễn
3. Click **"Xác nhận Ban User"**
4. **Kết quả mong đợi**:
   - ✅ Hiển thị thông báo: "✅ Đã ban student thành công với lý do: Lừa đảo (Thời gian: vĩnh viễn). Email thông báo đã được gửi."
   - ✅ User status chuyển thành "inactive"
   - ✅ Email được gửi đến user

### **Test 3: JavaScript Form Handling**
1. Mở modal ban
2. Chọn **"Vĩnh viễn"**
3. **Kết quả mong đợi**:
   - ✅ Input số ngày tự động chuyển thành 0
   - ✅ Input bị disable và có màu xám
4. Chọn lại **"Tạm thời"**
5. **Kết quả mong đợi**:
   - ✅ Input số ngày được enable lại
   - ✅ Input có màu trắng
   - ✅ Giá trị mặc định là 14

### **Test 4: Validation**
1. Thử ban với form không hợp lệ:
   - Không chọn lý do
   - Chọn tạm thời nhưng không nhập số ngày
   - Nhập số ngày > 365
2. **Kết quả mong đợi**:
   - ✅ Hiển thị thông báo lỗi cụ thể
   - ✅ Form không submit

### **Test 5: Unban**
1. Click nút **"Unban"** trên user đã bị ban
2. Confirm action
3. **Kết quả mong đợi**:
   - ✅ User status chuyển thành "active"
   - ✅ Hiển thị thông báo thành công
   - ✅ Email unban được gửi

---

## 🔍 Kiểm tra logs:

### **Console logs mong đợi:**
```
✅ Student banned successfully: user@email.com (Reason: Spam nội dung, Duration: 7 ngày)
🚫 Ban notification email sent successfully to: user@email.com (Duration: TEMPORARY)
```

### **Database kiểm tra:**
```sql
-- Kiểm tra ban_record table
SELECT * FROM ban_record WHERE banned_user_id = [user_id];

-- Kiểm tra account status
SELECT user_id, email, status FROM account WHERE user_id = [user_id];
```

---

## 🚨 Troubleshooting:

### **Nếu ban vĩnh viễn vẫn không hoạt động:**
1. Kiểm tra browser console cho JavaScript errors
2. Kiểm tra server logs cho validation errors
3. Verify database có bảng `ban_record`

### **Nếu email không gửi:**
1. Kiểm tra `application.properties` mail config
2. Verify Gmail app password
3. Check console logs cho email errors

### **Nếu thông báo không hiển thị:**
1. Kiểm tra flash message có được set không
2. Verify template có đúng logic hiển thị
3. Check browser console cho JavaScript errors

---

## 📝 Notes:

- **Ban vĩnh viễn**: Số ngày sẽ là `null` trong database
- **Ban tạm thời**: Số ngày từ 1-365, có `ban_expires_at`
- **Auto-unban**: Chạy mỗi 5 phút để unban expired users
- **Email notifications**: Gửi async để không block UI 