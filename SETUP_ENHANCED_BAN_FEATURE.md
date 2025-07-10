# 🚀 Setup Enhanced Ban/Unban Feature

## 📋 Những gì đã được thêm vào

### 1. Database Changes
- **Bảng mới**: `ban_record` với đầy đủ thông tin ban
- **9 lý do ban** predefined với thời gian phù hợp
- **Ban tạm thời** (1-365 ngày) và **ban vĩnh viễn**
- **Auto-unban** system với scheduled tasks

### 2. Backend Enhancements
- **New Services**: Enhanced ban với email notifications
- **New Controllers**: AJAX endpoints cho ban/unban
- **Email System**: 3 loại email (ban, unban, expiry reminder)
- **Scheduled Tasks**: Auto-unban và cleanup

### 3. Frontend Updates
- **Modern Modal**: Responsive ban form với validation
- **Real-time validation**: Form validation và ban summary
- **AJAX Integration**: Seamless user experience
- **Email notifications**: User feedback system

---

## 🛠️ Setup Instructions

### Step 1: Database Migration (IMPORTANT - DO THIS FIRST!)
```sql
-- Chạy script tạo bảng ban_record
source create_ban_record_table.sql

-- Hoặc copy-paste SQL này vào MySQL:
CREATE TABLE ban_record (
    ban_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    banned_by BIGINT NOT NULL,
    ban_reason ENUM(
        'SPAM', 
        'INAPPROPRIATE_CONTENT', 
        'FAKE_INFORMATION', 
        'HARASSMENT', 
        'FRAUD', 
        'MULTIPLE_ACCOUNTS', 
        'VIOLATION_TERMS', 
        'SYSTEM_ABUSE', 
        'OTHER'
    ) NOT NULL,
    ban_description TEXT,
    ban_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ban_end_date TIMESTAMP NULL,
    duration_type ENUM('TEMPORARY', 'PERMANENT') NOT NULL,
    duration_days INT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'UNBANNED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES account(user_id) ON DELETE CASCADE,
    FOREIGN KEY (banned_by) REFERENCES account(user_id) ON DELETE SET NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_ban_end_date (ban_end_date)
);
```

### Step 2: Start Application
```bash
# Build và chạy ứng dụng
./gradlew build
./gradlew bootRun

# Hoặc
java -jar build/libs/your-app.jar
```

### Step 3: Environment Variables
Đảm bảo có đủ env variables cho email:
```properties
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

---

## 🧪 Testing the Feature

### 1. Admin Login
- Đăng nhập với tài khoản admin
- Vào **Admin Panel** → **Student Management** hoặc **Employer Management**

### 2. Test Ban Feature
1. **Click nút "Ban"** trên user muốn ban
2. **Modal sẽ mở** với form ban chi tiết:
   - Chọn lý do ban (9 options)
   - Chọn loại ban: Tạm thời hoặc Vĩnh viễn
   - Với ban tạm thời: Chọn số ngày (1-365)
   - Thêm mô tả chi tiết (optional)
3. **Review ban summary** trước khi submit
4. **Click "Xác nhận Ban User"**
5. **Check email** của user bị ban

### 3. Test Unban Feature
1. **Click nút "Unban"** trên user đã bị ban
2. **Confirm action** trong popup
3. **Check email** của user được unban

### 4. Test Scheduled Tasks
- **Auto-unban**: Chạy mỗi 5 phút để unban expired users
- **Email reminders**: Gửi reminder 3 ngày trước hết hạn ban
- **Cleanup**: Dọn dẹp old ban records mỗi tuần

---

## 📧 Email Templates

### Ban Notification Email
- **Subject**: ⚠️ Tài khoản của bạn đã bị khóa - [Lý do]
- **Content**: Chi tiết lý do, thời gian, cách appeal
- **Format**: Professional HTML email

### Unban Notification Email  
- **Subject**: ✅ Tài khoản của bạn đã được kích hoạt lại
- **Content**: Thông báo reactivation, guidelines
- **Format**: Welcome back style

### Ban Expiry Reminder Email
- **Subject**: ⏰ Thông báo: Tài khoản sẽ được mở khóa trong 3 ngày
- **Content**: Countdown và hướng dẫn

---

## 🔍 Troubleshooting

### Thymeleaf Template Errors
- **Error: "Only variable expressions returning numbers or booleans are allowed"**
  - ✅ **FIXED**: We use `data-*` attributes instead of `th:onclick` 
  - ✅ **FIXED**: Event listeners are attached via JavaScript instead of inline handlers
  - If you still see this error, ensure you copied the corrected template files

### Modal không hiện
- Check browser console cho JavaScript errors
- Đảm bảo Bootstrap 5.3+ được load
- Verify ban-modal.html fragment được include đúng
- Check if CSS classes `.ban-user-btn` và `.unban-user-btn` exist

### Email không gửi được
- Check MAIL_USERNAME và MAIL_PASSWORD trong env
- Verify Gmail app password (not regular password)
- Check application.properties mail config

### Database errors
- Verify ban_record table được tạo thành công
- Check foreign key constraints
- Ensure proper indexes

### Scheduled tasks không chạy
- Verify @EnableScheduling trong Application.java
- Check logs cho scheduled task execution
- Ensure proper database connections

---

## 📊 Monitoring

### Admin Dashboard
- View total bans by reason
- Track ban durations and patterns
- Monitor auto-unban activities

### Database Queries
```sql
-- Check active bans
SELECT * FROM ban_record WHERE status = 'ACTIVE';

-- Ban statistics by reason
SELECT ban_reason, COUNT(*) as count 
FROM ban_record 
GROUP BY ban_reason 
ORDER BY count DESC;

-- Upcoming ban expirations
SELECT * FROM ban_record 
WHERE status = 'ACTIVE' 
AND ban_end_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY);
```

---

## 🎯 Key Features Working

✅ **Enhanced Ban Modal** - Professional UI với validation
✅ **Email Notifications** - 3 types of professional emails  
✅ **Auto-unban System** - Scheduled background processing
✅ **Comprehensive Logging** - Full audit trail
✅ **Modern AJAX** - Seamless user experience
✅ **Mobile Responsive** - Works on all devices
✅ **Data Validation** - Server-side and client-side
✅ **Error Handling** - Graceful error management

Happy banning! 🚫📧 