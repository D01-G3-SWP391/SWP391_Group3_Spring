# Notification System Documentation

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Technical Architecture](#technical-architecture)
4. [Database Schema](#database-schema)
5. [API Endpoints](#api-endpoints)
6. [Frontend Components](#frontend-components)
7. [Implementation Guide](#implementation-guide)
8. [Best Practices](#best-practices)

## Overview

Hệ thống thông báo (Notification System) là một phần quan trọng của ứng dụng, cho phép gửi và quản lý các thông báo realtime tới người dùng. Hệ thống hỗ trợ nhiều loại thông báo khác nhau và cung cấp giao diện thân thiện với người dùng.

### Mục đích
- Thông báo người dùng về các sự kiện quan trọng
- Cập nhật trạng thái đơn ứng tuyển
- Thông báo về các hoạt động của hệ thống
- Tăng tương tác người dùng

## Features

### 1. Notification Bell
- Hiển thị số lượng thông báo chưa đọc
- Cập nhật realtime (polling mỗi 30 giây)
- Hiển thị 5 thông báo mới nhất
- Hỗ trợ đánh dấu đã đọc và xóa thông báo

### 2. Notification List Page
- Hiển thị tất cả thông báo
- Phân trang và sắp xếp theo thời gian
- Lọc theo trạng thái đã đọc/chưa đọc
- Quản lý thông báo (đánh dấu đã đọc, xóa)

### 3. Notification Types
- JOB_APPLICATION: Thông báo về đơn ứng tuyển
- SYSTEM: Thông báo hệ thống
- EVENT: Thông báo sự kiện
- ACCOUNT: Thông báo tài khoản

## Technical Architecture

### 1. Backend Components

#### 1.1 Model
```java
@Entity
public class Notification {
    @Id
    private Long id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String notificationType;
    private Long relatedId;
    @ManyToOne
    private Account user;
}
```

#### 1.2 DTO
```java
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String notificationType;
    private Long relatedId;
    private Integer userId;
}
```

#### 1.3 Service Layer
```java
public interface INotificationService {
    Notification createNotification(...);
    List<Notification> getUserNotifications(...);
    void markAsRead(...);
    void deleteNotification(...);
}
```

### 2. Frontend Components

#### 2.1 Notification Bell
```html
<div class="notification-bell-wrapper">
    <div class="notification-bell-button">
        <span class="notification-bell-badge">0</span>
    </div>
    <div class="notification-bell-dropdown">
        <!-- Notification items -->
    </div>
</div>
```

#### 2.2 JavaScript Functions
```javascript
function updateNotifications() {
    // Fetch and update notifications
}

function markAsRead(id) {
    // Mark notification as read
}

function deleteNotification(id) {
    // Delete notification
}
```

## Database Schema

### notifications Table
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    notification_type VARCHAR(50) NOT NULL,
    related_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES accounts(id)
);
```

## API Endpoints

### 1. Get Notifications
```
GET /notifications
Response: List<NotificationDTO>
```

### 2. Get Latest Notifications
```
GET /notifications/latest
Response: List<NotificationDTO>
```

### 3. Get Unread Count
```
GET /notifications/count
Response: {"count": number}
```

### 4. Mark as Read
```
POST /notifications/{id}/mark-read
Response: {"status": "success"}
```

### 5. Delete Notification
```
DELETE /notifications/{id}
Response: {"status": "success"}
```

## Implementation Guide

### 1. Tạo Notification Mới

```java
// Service
NotificationService.createNotification(
    Account user,
    String title,
    String message,
    String type,
    Long relatedId
);

// Example Usage
notificationService.createNotification(
    user,
    "Đơn ứng tuyển mới",
    "Bạn đã nộp đơn thành công cho vị trí X",
    "JOB_APPLICATION",
    jobId
);
```

### 2. Cấu hình Frontend Polling

```javascript
// Trong notification-bell.html
document.addEventListener('DOMContentLoaded', function() {
    // Initial load
    updateNotifications();
    
    // Setup polling
    setInterval(updateNotifications, 30000);
});
```

### 3. Xử lý Sự kiện

```javascript
// Click vào notification
function handleNotificationClick(id) {
    markAsRead(id);
    // Navigate to related content
    navigateToContent(notificationType, relatedId);
}

// Xóa notification
function handleDelete(id) {
    if (confirm('Bạn có chắc muốn xóa thông báo này?')) {
        deleteNotification(id);
    }
}
```

## Best Practices

### 1. Performance
- Sử dụng DTO để giảm dữ liệu truyền tải
- Giới hạn số lượng thông báo trong notification bell
- Implement caching cho unread count
- Sử dụng batch operations cho mark all/delete all

### 2. Security
- Kiểm tra quyền truy cập mỗi notification
- Validate input data
- Sử dụng CSRF protection
- Implement rate limiting

### 3. UX Guidelines
- Hiển thị thông báo mới nhất đầu tiên
- Sử dụng màu sắc phân biệt đã đọc/chưa đọc
- Hiển thị thời gian tương đối (vd: "2 phút trước")
- Cung cấp feedback khi thực hiện actions

### 4. Maintenance
- Log các operations quan trọng
- Monitor performance metrics
- Cleanup old notifications
- Regular testing of realtime updates

## Troubleshooting

### Common Issues

1. Notification không cập nhật
```javascript
// Kiểm tra polling interval
console.log('Last update:', lastUpdateTime);
// Kiểm tra network requests
```

2. Circular Reference trong JSON
```java
// Sử dụng DTO thay vì entity
NotificationDTO dto = NotificationDTO.fromEntity(notification);
```

3. Performance Issues
```java
// Implement caching
@Cacheable("notification-count")
public int getUnreadCount() { ... }
```

---

## Contributing

Khi thêm tính năng mới vào hệ thống notification:

1. Tạo branch mới
2. Update documentation
3. Add tests
4. Create pull request

## Support

Liên hệ team development để được hỗ trợ thêm về hệ thống notification. 