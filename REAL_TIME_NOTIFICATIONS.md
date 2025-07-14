# 🔔 Real-time Notification System với WebSocket

## 📋 **Tổng quan**
Hệ thống notification đã được chuyển từ **polling mechanism** (30 giây) sang **real-time WebSocket** để cung cấp trải nghiệm user tức thì.

## 🚀 **Các thay đổi chính**

### **1. Backend Changes**

#### **NotificationController.java**
- ✅ **Thêm SimpMessagingTemplate** để push real-time messages
- ✅ **WebSocket MessageMapping handlers**:
  - `/app/notifications.subscribe` - Subscribe to personal notifications
  - `/app/notifications.markAsRead` - Mark as read via WebSocket
  - `/app/notifications.delete` - Delete via WebSocket
  - `/app/notifications.markAllAsRead` - Mark all as read via WebSocket
  - `/app/notifications.deleteAll` - Delete all via WebSocket

#### **NotificationService.java**
- ✅ **Real-time push** khi tạo notification mới
- ✅ **Real-time updates** cho mark as read, delete actions
- ✅ **Automatic unread count updates**

### **2. Frontend Changes**

#### **notification-websocket.js** (NEW)
- ✅ **WebSocket client** cho notifications
- ✅ **Auto-reconnection** mechanism
- ✅ **Browser notifications** support
- ✅ **Fallback to REST API** nếu WebSocket fail

#### **notification-bell.html**
- ✅ **Remove polling** (setInterval)
- ✅ **WebSocket integration**
- ✅ **Fallback mechanisms** cho REST API
- ✅ **Real-time UI updates**

## 🔧 **Cách hoạt động**

### **Real-time Flow:**
```
1. User A tạo job application
2. NotificationService.createNotification()
3. Save to database
4. Push via SimpMessagingTemplate
5. User B nhận notification tức thì
6. UI cập nhật real-time
7. Browser notification (nếu có permission)
```

### **WebSocket Channels:**
- `/user/queue/notifications` - Personal notifications
- `/user/queue/errors` - Error messages
- `/app/notifications.*` - User actions

## 📊 **Message Types**

### **Incoming Messages:**
```javascript
{
  "type": "new_notification",
  "notification": { ... }
}

{
  "type": "unread_count",
  "count": 5
}

{
  "type": "initial_load",
  "notifications": [ ... ]
}
```

### **Outgoing Messages:**
```javascript
// Subscribe
stompClient.send('/app/notifications.subscribe', {}, {});

// Mark as read
stompClient.send('/app/notifications.markAsRead', {}, {
  "notificationId": 123
});

// Delete
stompClient.send('/app/notifications.delete', {}, {
  "notificationId": 123
});
```

## 🧪 **Cách test hệ thống**

### **1. Test Real-time Notifications:**
```bash
# Terminal 1: Start application
./gradlew bootRun

# Browser 1: Login as Student
# Browser 2: Login as Employer

# Action: Student apply job
# Expected: Employer nhận notification tức thì (không cần refresh)
```

### **2. Test WebSocket Connection:**
```javascript
// Console Browser (F12)
console.log('WebSocket Client:', window.notificationWebSocketClient);
console.log('Connected:', window.notificationWebSocketClient?.isConnected);

// Expected: true
```

### **3. Test Fallback Mechanism:**
```javascript
// Disconnect WebSocket
window.notificationWebSocketClient?.disconnect();

// Try mark as read
markAsRead(123);

// Expected: Fallback to REST API
```

### **4. Test Browser Notifications:**
```javascript
// Check permission
console.log('Notification permission:', Notification.permission);

// Request permission
Notification.requestPermission();

// Expected: 'granted'
```

## 🔍 **Debug & Troubleshooting**

### **Common Issues:**

#### **1. WebSocket không connect:**
```javascript
// Check console logs
🔔 Connecting to notification WebSocket...
🎉 Notification WebSocket connected successfully!
```

#### **2. Notifications không hiển thị:**
```javascript
// Check if user authenticated
document.querySelector('.notification-bell-wrapper') !== null

// Check WebSocket subscription
window.notificationWebSocketClient?.isConnected === true
```

#### **3. Fallback không hoạt động:**
```javascript
// Check REST API endpoints
fetch('/notifications/count').then(r => r.json()).then(console.log);
```

### **Console Logs:**
```
🔔 Real-time notification sent to user: user@example.com
📨 Received notification message: { type: 'new_notification', ... }
🆕 New notification received: { title: 'New Job Application', ... }
📊 Unread count update: 3
```

## 🎯 **Performance Benefits**

### **Before (Polling):**
- ⏱️ **Delay**: 0-30 seconds
- 🌐 **Network**: 1 request/30s per user
- 📱 **Battery**: Constant background requests
- 🔄 **Server Load**: High with many users

### **After (WebSocket):**
- ⏱️ **Delay**: < 1 second
- 🌐 **Network**: Only when needed
- 📱 **Battery**: Minimal impact
- 🔄 **Server Load**: Lower, event-driven

## 📈 **Monitoring**

### **Server Metrics:**
```java
// Check active WebSocket connections
websocket.active.connections

// Check message throughput
websocket.messages.sent
websocket.messages.received
```

### **Client Metrics:**
```javascript
// Check connection health
window.notificationWebSocketClient?.reconnectAttempts
window.notificationWebSocketClient?.isConnected
```

## 🔒 **Security Features**

- ✅ **User-specific queues** (`/user/queue/notifications`)
- ✅ **Authentication required** (Principal trong MessageMapping)
- ✅ **XSS protection** (escapeHtml function)
- ✅ **CORS configuration** trong WebSocketConfig

## 📱 **Browser Compatibility**

- ✅ **Modern browsers**: Native WebSocket support
- ✅ **Older browsers**: SockJS fallback
- ✅ **Mobile**: Full support
- ✅ **Offline**: Graceful reconnection

## 🛡️ **Fallback Strategy**

1. **Primary**: WebSocket real-time
2. **Secondary**: REST API polling (existing endpoints)
3. **Tertiary**: Page refresh để load notifications

## 🎉 **Kết luận**

Hệ thống notification đã được **nâng cấp thành công** từ polling sang real-time WebSocket:

- ⚡ **Instant notifications** - Không delay
- 🔄 **Auto-reconnection** - Ổn định kết nối
- 📱 **Browser notifications** - Trải nghiệm tốt hơn
- 🔄 **Fallback support** - Luôn hoạt động
- 🎯 **Better performance** - Ít resource hơn

**Ready for production!** 🚀 