/**
 * 🔔 WebSocket Client for Real-time Notifications
 * 
 * This client handles real-time notification updates via WebSocket
 */

class NotificationWebSocketClient {
    constructor() {
        this.stompClient = null;
        this.isConnected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 3000;
        
        // Callbacks
        this.onNotificationReceived = null;
        this.onUnreadCountUpdate = null;
        this.onConnected = null;
        this.onDisconnected = null;
        this.onError = null;
    }

    /**
     * 🔌 Connect to WebSocket server
     */
    connect() {
        try {
            // Create SockJS connection
            const socket = new SockJS('/ws');
            this.stompClient = Stomp.over(socket);
            
            // Disable debug logs
            this.stompClient.debug = null;
            
            // Connect
            this.stompClient.connect({}, 
                (frame) => this.onConnectSuccess(frame),
                (error) => this.onConnectError(error)
            );
            
            console.log('🔔 Connecting to notification WebSocket...');
        } catch (error) {
            console.error('❌ Failed to connect to notification WebSocket:', error);
            this.scheduleReconnect();
        }
    }

    /**
     * ✅ Handle connection success
     */
    onConnectSuccess(frame) {
        console.log('🎉 Notification WebSocket connected successfully!');
        this.isConnected = true;
        this.reconnectAttempts = 0;
        
        // Subscribe to personal notifications
        this.stompClient.subscribe('/user/queue/notifications', (message) => {
            this.handleNotificationMessage(JSON.parse(message.body));
        });
        
        // Subscribe to errors
        this.stompClient.subscribe('/user/queue/errors', (message) => {
            this.handleError(JSON.parse(message.body));
        });
        
        // Subscribe to notifications initially
        this.stompClient.send('/app/notifications.subscribe', {}, JSON.stringify({}));
        
        // Callback
        if (this.onConnected && typeof this.onConnected === 'function') {
            this.onConnected();
        }
    }

    /**
     * ❌ Handle connection error
     */
    onConnectError(error) {
        console.error('❌ Notification WebSocket connection error:', error);
        this.isConnected = false;
        
        if (this.onError && typeof this.onError === 'function') {
            this.onError(error);
        }
        
        this.scheduleReconnect();
    }

    /**
     * 🔄 Schedule reconnection
     */
    scheduleReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`🔄 Scheduling notification WebSocket reconnect (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
            
            setTimeout(() => {
                this.connect();
            }, this.reconnectDelay);
        } else {
            console.error('❌ Max reconnection attempts reached for notification WebSocket');
        }
    }

    /**
     * 📨 Handle incoming notification messages
     */
    handleNotificationMessage(data) {
        console.log('📨 Received notification message:', data);
        
        switch (data.type) {
            case 'new_notification':
                this.handleNewNotification(data.notification);
                break;
            case 'unread_count':
                this.handleUnreadCountUpdate(data.count);
                break;
            case 'initial_load':
                this.handleInitialLoad(data.notifications);
                break;
            case 'mark_read_success':
                this.handleMarkReadSuccess(data.notificationId);
                break;
            case 'delete_success':
                this.handleDeleteSuccess(data.notificationId);
                break;
            case 'mark_all_read_success':
                this.handleMarkAllReadSuccess();
                break;
            case 'delete_all_success':
                this.handleDeleteAllSuccess();
                break;
            case 'error':
                console.error('❌ Notification error:', data.message);
                break;
            default:
                console.warn('⚠️ Unknown notification message type:', data.type);
        }
    }

    /**
     * 🆕 Handle new notification
     */
    handleNewNotification(notification) {
        console.log('🆕 New notification received:', notification);
        
        // Add to UI
        this.addNotificationToUI(notification);
        
        // Show browser notification if supported
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification(notification.title, {
                body: notification.message,
                icon: '/images/bell.svg'
            });
        }
        
        // Callback
        if (this.onNotificationReceived && typeof this.onNotificationReceived === 'function') {
            this.onNotificationReceived(notification);
        }
    }

    /**
     * 📊 Handle unread count update
     */
    handleUnreadCountUpdate(count) {
        console.log('📊 Unread count update:', count);
        
        // Update badge
        const badge = document.querySelector('.notification-bell-badge');
        if (badge) {
            if (count > 0) {
                badge.style.display = 'flex';
                badge.textContent = count;
            } else {
                badge.style.display = 'none';
            }
        }
        
        // Callback
        if (this.onUnreadCountUpdate && typeof this.onUnreadCountUpdate === 'function') {
            this.onUnreadCountUpdate(count);
        }
    }

    /**
     * 📋 Handle initial load
     */
    handleInitialLoad(notifications) {
        console.log('📋 Initial notifications load:', notifications);
        
        const notificationList = document.getElementById('notificationList');
        if (notificationList) {
            notificationList.innerHTML = '';
            
            if (notifications.length === 0) {
                notificationList.innerHTML = `
                    <div class="notification-bell-empty">
                        <i class="bi bi-bell-slash"></i>
                        <p>No notifications</p>
                    </div>
                `;
            } else {
                notifications.forEach(notification => {
                    this.addNotificationToUI(notification);
                });
            }
        }
    }

    /**
     * ✅ Handle mark read success
     */
    handleMarkReadSuccess(notificationId) {
        console.log('✅ Mark read success:', notificationId);
        
        const notificationItem = document.querySelector(`[data-id="${notificationId}"]`);
        if (notificationItem) {
            notificationItem.classList.remove('unread');
        }
    }

    /**
     * 🗑️ Handle delete success
     */
    handleDeleteSuccess(notificationId) {
        console.log('🗑️ Delete success:', notificationId);
        
        const notificationItem = document.querySelector(`[data-id="${notificationId}"]`);
        if (notificationItem) {
            notificationItem.remove();
        }
        
        // Check if list is empty
        const notificationList = document.getElementById('notificationList');
        if (notificationList && notificationList.children.length === 0) {
            notificationList.innerHTML = `
                <div class="notification-bell-empty">
                    <i class="bi bi-bell-slash"></i>
                    <p>No notifications</p>
                </div>
            `;
        }
    }

    /**
     * ✅ Handle mark all read success
     */
    handleMarkAllReadSuccess() {
        console.log('✅ Mark all read success');
        
        const unreadItems = document.querySelectorAll('.notification-bell-item.unread');
        unreadItems.forEach(item => item.classList.remove('unread'));
    }

    /**
     * 🗑️ Handle delete all success
     */
    handleDeleteAllSuccess() {
        console.log('🗑️ Delete all success');
        
        const notificationList = document.getElementById('notificationList');
        if (notificationList) {
            notificationList.innerHTML = `
                <div class="notification-bell-empty">
                    <i class="bi bi-bell-slash"></i>
                    <p>No notifications</p>
                </div>
            `;
        }
    }

    /**
     * 🎨 Add notification to UI
     */
    addNotificationToUI(notification) {
        const notificationList = document.getElementById('notificationList');
        if (!notificationList) return;
        
        // Remove empty message if exists
        const emptyMessage = notificationList.querySelector('.notification-bell-empty');
        if (emptyMessage) {
            emptyMessage.remove();
        }
        
        // Create notification element
        const notificationItem = document.createElement('div');
        notificationItem.className = `notification-bell-item${notification.read ? '' : ' unread'}`;
        notificationItem.dataset.id = notification.id;
        
        notificationItem.innerHTML = `
            <div class="notification-bell-content" onclick="markAsReadViaWebSocket(${notification.id})">
                <div class="notification-bell-title">${this.escapeHtml(notification.title)}</div>
                <div class="notification-bell-message">${this.escapeHtml(notification.message)}</div>
                <div class="notification-bell-time">${this.formatDate(notification.createdAt)}</div>
            </div>
            <div class="notification-bell-delete" onclick="deleteNotificationViaWebSocket(event, ${notification.id})">
                <i class="bi bi-trash"></i>
            </div>
        `;
        
        // Add to top of list
        notificationList.insertBefore(notificationItem, notificationList.firstChild);
    }

    /**
     * ✅ Mark notification as read via WebSocket
     */
    markAsRead(notificationId) {
        if (this.isConnected) {
            this.stompClient.send('/app/notifications.markAsRead', {}, JSON.stringify({
                notificationId: notificationId
            }));
        }
    }

    /**
     * 🗑️ Delete notification via WebSocket
     */
    deleteNotification(notificationId) {
        if (this.isConnected) {
            this.stompClient.send('/app/notifications.delete', {}, JSON.stringify({
                notificationId: notificationId
            }));
        }
    }

    /**
     * 🔌 Disconnect
     */
    disconnect() {
        if (this.stompClient && this.isConnected) {
            this.stompClient.disconnect();
            this.isConnected = false;
            
            if (this.onDisconnected && typeof this.onDisconnected === 'function') {
                this.onDisconnected();
            }
        }
    }

    /**
     * 🛠️ Handle error
     */
    handleError(error) {
        console.error('❌ Notification WebSocket error:', error);
        
        if (this.onError && typeof this.onError === 'function') {
            this.onError(error);
        }
    }

    /**
     * 🔒 Escape HTML
     */
    escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }

    /**
     * 📅 Format date
     */
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN') + ' ' + date.toLocaleTimeString('vi-VN', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }
}

// Global instance
let notificationWebSocketClient = null;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Only initialize if user is authenticated
    if (document.querySelector('.notification-bell-wrapper')) {
        notificationWebSocketClient = new NotificationWebSocketClient();
        notificationWebSocketClient.connect();
        
        // Request notification permission
        if ('Notification' in window && Notification.permission === 'default') {
            Notification.requestPermission();
        }
    }
});

// Global functions for UI interactions
function markAsReadViaWebSocket(notificationId) {
    if (notificationWebSocketClient) {
        notificationWebSocketClient.markAsRead(notificationId);
    }
}

function deleteNotificationViaWebSocket(event, notificationId) {
    event.stopPropagation();
    if (notificationWebSocketClient) {
        notificationWebSocketClient.deleteNotification(notificationId);
    }
}

// Export for use in other scripts
window.NotificationWebSocketClient = NotificationWebSocketClient;
window.notificationWebSocketClient = notificationWebSocketClient; 