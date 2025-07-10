# 🚫 Enhanced Ban/Unban Feature Documentation

## 📋 Tổng quan

Tính năng ban/unban nâng cao cho admin với các chức năng:
- ✅ **Ban với lý do cụ thể** - Admin chọn lý do từ danh sách có sẵn
- ⏰ **Ban theo thời gian** - Tạm thời (có thời hạn) hoặc vĩnh viễn
- 📧 **Email thông báo tự động** - Gửi email cho user bị ban/unban
- 🤖 **Tự động unban** - Hệ thống tự động unban khi hết hạn
- 📊 **Lịch sử ban** - Lưu trữ đầy đủ thông tin ban records

---

## 🏗️ Cấu trúc Database

### BanRecord Entity
```sql
CREATE TABLE ban_record (
    ban_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    banned_user_id INT NOT NULL,
    banned_by_admin_id INT NOT NULL,
    ban_reason ENUM('SPAM', 'INAPPROPRIATE_CONTENT', 'FAKE_INFORMATION', 'HARASSMENT', 'FRAUD', 'MULTIPLE_ACCOUNTS', 'VIOLATION_TERMS', 'SYSTEM_ABUSE', 'OTHER'),
    ban_description TEXT,
    ban_duration_type ENUM('TEMPORARY', 'PERMANENT'),
    ban_duration_days INT,
    banned_at DATETIME NOT NULL,
    ban_expires_at DATETIME,
    unbanned_at DATETIME,
    unbanned_by_admin_id INT,
    status ENUM('ACTIVE', 'EXPIRED', 'UNBANNED') DEFAULT 'ACTIVE',
    FOREIGN KEY (banned_user_id) REFERENCES account(user_id),
    FOREIGN KEY (banned_by_admin_id) REFERENCES account(user_id),
    FOREIGN KEY (unbanned_by_admin_id) REFERENCES account(user_id)
);
```

---

## 🎯 Các loại Ban Reasons

| Reason | Description | Typical Duration |
|--------|-------------|------------------|
| `SPAM` | Spam nội dung | 7-30 ngày |
| `INAPPROPRIATE_CONTENT` | Nội dung không phù hợp | 3-14 ngày |
| `FAKE_INFORMATION` | Thông tin giả mạo | 30-90 ngày |
| `HARASSMENT` | Quấy rối người khác | 14-180 ngày |
| `FRAUD` | Lừa đảo | 180-365 ngày |
| `MULTIPLE_ACCOUNTS` | Tạo nhiều tài khoản | 30-90 ngày |
| `VIOLATION_TERMS` | Vi phạm điều khoản sử dụng | 7-60 ngày |
| `SYSTEM_ABUSE` | Lạm dụng hệ thống | 30-180 ngày |
| `OTHER` | Lý do khác | Tùy admin |

---

## 🕒 Ban Duration Types

### 1. Temporary Ban (Tạm thời)
- **Có thời hạn**: 1-365 ngày
- **Tự động unban**: Khi hết thời gian
- **Email reminder**: Gửi 3 ngày trước khi hết hạn
- **Phù hợp cho**: Vi phạm nhẹ, cảnh báo

### 2. Permanent Ban (Vĩnh viễn)
- **Không thời hạn**: Vĩnh viễn
- **Chỉ admin unban**: Phải thủ công
- **Phù hợp cho**: Vi phạm nghiêm trọng

---

## 📧 Email Notifications

### 1. Ban Notification Email
**Gửi khi:** User bị ban
**Nội dung:**
- ⚠️ Thông báo tài khoản bị khóa
- 📋 Lý do và mô tả ban
- ⏰ Thời gian ban (tạm thời/vĩnh viễn)
- 📞 Thông tin liên hệ support

### 2. Unban Notification Email
**Gửi khi:** User được unban
**Nội dung:**
- 🎉 Thông báo tài khoản được mở khóa
- 📋 Thông tin ban trước đó
- ⚠️ Lưu ý tuân thủ quy định
- 🔗 Link đăng nhập

### 3. Ban Expiry Reminder Email
**Gửi khi:** 3 ngày trước khi ban hết hạn
**Nội dung:**
- 📅 Thông báo sắp được mở khóa
- ⏰ Thời gian còn lại
- 🔄 Hướng dẫn chuẩn bị

---

## 🔧 API Endpoints

### Admin Ban User
```http
POST /Admin/banStudentWithReason
POST /Admin/banEmployerWithReason
Content-Type: application/json

{
    "userId": 123,
    "banReason": "SPAM",
    "banDescription": "Spam nhiều bài viết quảng cáo",
    "banDurationType": "TEMPORARY",
    "banDurationDays": 7
}
```

### Admin Unban User
```http
POST /Admin/unbanStudentWithNotification/{userId}
POST /Admin/unbanEmployerWithNotification/{userId}
```

### Get Ban Reasons
```http
GET /Admin/api/ban-reasons
```

---

## 🤖 Scheduled Tasks

### 1. Expired Ban Check
- **Frequency**: Mỗi 5 phút
- **Cron**: `0 */5 * * * *`
- **Function**: Tự động unban users hết hạn

### 2. Ban Expiry Reminders
- **Frequency**: Mỗi ngày 9:00 AM
- **Cron**: `0 0 9 * * *`
- **Function**: Gửi email nhắc nhở ban sắp hết hạn

### 3. Cleanup Old Records
- **Frequency**: Chủ nhật 2:00 AM
- **Cron**: `0 0 2 * * SUN`
- **Function**: Archive ban records cũ

---

## 🎨 Frontend UI Components

### 1. Ban Modal
- **File**: `templates/fragments/ban-modal.html`
- **Features**:
  - 📝 Form chọn lý do ban
  - ⏰ Chọn thời gian ban (tạm thời/vĩnh viễn)
  - 📄 Mô tả chi tiết
  - 📊 Tóm tắt thông tin ban
  - ⚠️ Cảnh báo hành động

### 2. Unban Modal
- **Features**:
  - 👤 Hiển thị thông tin user
  - ✅ Xác nhận unban
  - 📧 Thông báo gửi email

### 3. JavaScript Functions
```javascript
// Mở modal ban
openBanModal(userId, userName, userEmail, userRole)

// Mở modal unban
openUnbanModal(userId, userName, userEmail, userRole)
```

---

## 🔒 Security & Permissions

### Admin Permissions Required
- ✅ Chỉ admin có quyền ban/unban
- ✅ Validate admin authentication
- ✅ Log tất cả hành động ban/unban
- ✅ Audit trail đầy đủ

### Data Validation
- ✅ Validate ban reason (enum)
- ✅ Validate duration (1-365 ngày)
- ✅ Check user tồn tại
- ✅ Check không ban admin
- ✅ Prevent duplicate ban

---

## 📊 Usage Examples

### 1. Ban Student với Spam
```java
BanRequestDTO banRequest = new BanRequestDTO();
banRequest.setUserId(123);
banRequest.setBanReason(BanRecord.BanReason.SPAM);
banRequest.setBanDescription("Đăng nhiều bài spam quảng cáo");
banRequest.setBanDurationType(BanRecord.BanDurationType.TEMPORARY);
banRequest.setBanDurationDays(7);

adminStudentService.banStudentWithReason(banRequest, adminId);
```

### 2. Ban Employer vĩnh viễn
```java
BanRequestDTO banRequest = new BanRequestDTO();
banRequest.setUserId(456);
banRequest.setBanReason(BanRecord.BanReason.FRAUD);
banRequest.setBanDescription("Lừa đảo ứng viên");
banRequest.setBanDurationType(BanRecord.BanDurationType.PERMANENT);

adminEmployerService.banEmployerWithReason(banRequest, adminId);
```

### 3. Unban với notification
```java
adminStudentService.unbanStudentWithNotification(userId, adminId);
```

---

## 🧪 Testing

### 1. Manual Testing
```java
// Force check expired bans
banExpiryScheduledService.forceCheckExpiredBans();

// Force send expiry reminders
banExpiryScheduledService.forceSendExpiryReminders();
```

### 2. Test Cases
- ✅ Ban user thành công
- ✅ Ban user đã bị ban (error)
- ✅ Unban user thành công
- ✅ Unban user chưa bị ban (error)
- ✅ Auto unban khi hết hạn
- ✅ Email notifications
- ✅ Scheduled tasks

---

## 🚀 Deployment Notes

### 1. Database Migration
```sql
-- Chạy script tạo bảng ban_record
-- Thêm indexes cho performance
CREATE INDEX idx_ban_record_user ON ban_record(banned_user_id);
CREATE INDEX idx_ban_record_status ON ban_record(status);
CREATE INDEX idx_ban_record_expires ON ban_record(ban_expires_at);
```

### 2. Configuration
```properties
# Enable scheduling
spring.task.scheduling.enabled=true

# Email configuration for ban notifications
spring.mail.host=smtp.gmail.com
spring.mail.port=587
```

### 3. Monitoring
- 📊 Monitor scheduled task execution
- 📧 Monitor email delivery rates
- 🔍 Monitor ban/unban activities
- ⚠️ Alert on failed auto-unbans

---

## 📈 Future Enhancements

### Possible Improvements
1. **Ban History Dashboard** - Thống kê ban activities
2. **Appeal System** - User có thể khiếu nại ban
3. **Progressive Penalties** - Tăng thời gian ban cho repeat offenders
4. **IP Ban Support** - Ban theo IP address
5. **Bulk Ban Operations** - Ban nhiều users cùng lúc
6. **Ban Templates** - Templates cho các loại vi phạm thường gặp

---

## 👥 Contact

Để hỗ trợ hoặc báo lỗi liên quan đến tính năng ban/unban:
- **Email**: admin@job4you.com
- **Documentation**: Xem file này
- **Logs**: Check application logs cho details 