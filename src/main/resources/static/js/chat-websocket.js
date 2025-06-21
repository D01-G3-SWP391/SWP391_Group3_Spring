/**
 * 🚀 WebSocket Chat Client
 * 
 * JavaScript client để kết nối và giao tiếp với WebSocket server
 * Sử dụng SockJS + STOMP protocol
 */

class ChatWebSocketClient {
    constructor() {
        this.stompClient = null;
        this.currentUserId = null;
        this.currentUserName = null;
        this.currentRoomId = null;
        this.isConnected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 3000; // 3 seconds
        
        // Callbacks - Initialize with empty functions instead of null
        this.onMessageReceived = () => {};
        this.onUserJoined = () => {};
        this.onUserLeft = () => {};
        this.onTyping = () => {};
        this.onReadReceipt = () => {};
        this.onError = () => {};
        this.onConnected = () => {};
        this.onDisconnected = () => {};
    }

    /**
     * 🔌 Kết nối tới WebSocket server
     */
    connect(userId, userName) {
        this.currentUserId = userId;
        this.currentUserName = userName;
        
        // Tạo SockJS connection
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        // Tắt debug logs (tùy chọn)
        this.stompClient.debug = null;
        
        // Kết nối với headers
        const headers = {
            'userId': userId,
            'userName': userName
        };
        
        this.stompClient.connect(headers, 
            (frame) => this.onConnectSuccess(frame),
            (error) => this.onConnectError(error)
        );
    }

    /**
     * ✅ Xử lý kết nối thành công
     */
    onConnectSuccess(frame) {
        console.log('🎉 WebSocket connected successfully!', frame);
        this.isConnected = true;
        this.reconnectAttempts = 0;
        
        // Subscribe to personal notifications
        this.stompClient.subscribe('/user/queue/notifications', (message) => {
            this.handleNotification(JSON.parse(message.body));
        });
        
        // Subscribe to error messages
        this.stompClient.subscribe('/user/queue/errors', (message) => {
            this.handleError(JSON.parse(message.body));
        });
        
        // Callback
        if (this.onConnected && typeof this.onConnected === 'function') {
            this.onConnected();
        }
        
        // Auto-join room nếu có
        if (this.currentRoomId) {
            this.joinRoom(this.currentRoomId);
        }
    }

    /**
     * ❌ Xử lý lỗi kết nối
     */
    onConnectError(error) {
        console.error('❌ WebSocket connection failed:', error);
        this.isConnected = false;
        
        if (this.onDisconnected && typeof this.onDisconnected === 'function') {
            this.onDisconnected(error);
        }
        
        // Auto reconnect
        this.attemptReconnect();
    }

    /**
     * 🔄 Thử kết nối lại
     */
    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`🔄 Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
            
            setTimeout(() => {
                this.connect(this.currentUserId, this.currentUserName);
            }, this.reconnectDelay);
        } else {
            console.error('❌ Max reconnection attempts reached');
            if (this.onError && typeof this.onError === 'function') {
                this.onError('Không thể kết nối tới server. Vui lòng refresh trang.');
            }
        }
    }

    /**
     * 🚪 Tham gia chat room
     */
    joinRoom(roomId) {
        if (!this.isConnected) {
            console.warn('⚠️ WebSocket not connected, queuing room join...');
            this.currentRoomId = roomId;
            return;
        }
        
        this.currentRoomId = roomId;
        
        // Subscribe to room messages
        this.stompClient.subscribe(`/topic/chat/${roomId}`, (message) => {
            const messageData = JSON.parse(message.body);
            this.handleMessageReceived(messageData);
        });
        
        // Subscribe to presence updates  
        this.stompClient.subscribe(`/topic/chat/${roomId}/presence`, (message) => {
            this.handlePresenceUpdate(message.body);
        });
        
        // Subscribe to typing indicators
        this.stompClient.subscribe(`/topic/chat/${roomId}/typing`, (message) => {
            const typingData = JSON.parse(message.body);
            this.handleTypingIndicator(typingData);
        });
        
        // Subscribe to read receipts
        this.stompClient.subscribe(`/topic/chat/${roomId}/read`, (message) => {
            const readData = JSON.parse(message.body);
            this.handleReadReceipt(readData);
        });
        
        // Send join notification
        this.stompClient.send('/app/chat.joinRoom', {}, JSON.stringify({
            chatRoomId: roomId
        }));
        
        console.log(`🚪 Joined room: ${roomId}`);
    }

    /**
     * 🏃 Rời khỏi chat room
     */
    leaveRoom() {
        if (this.currentRoomId && this.isConnected) {
            this.stompClient.send('/app/chat.leaveRoom', {}, JSON.stringify({
                chatRoomId: this.currentRoomId
            }));
            
            console.log(`🏃 Left room: ${this.currentRoomId}`);
        }
        this.currentRoomId = null;
    }

    /**
     * 📨 Gửi tin nhắn text
     */
    sendMessage(content) {
        if (!this.isConnected || !this.currentRoomId) {
            console.error('❌ Cannot send message: not connected or no room');
            return false;
        }
        
        if (!content || content.trim() === '') {
            console.warn('⚠️ Cannot send empty message');
            return false;
        }
        
        const messageData = {
            chatRoomId: this.currentRoomId,
            content: content.trim(),
            senderId: this.currentUserId
        };
        
        this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(messageData));
        return true;
    }

    /**
     * 📎 Gửi tin nhắn có file
     */
    sendFileMessage(fileUrl, fileName, fileSize) {
        if (!this.isConnected || !this.currentRoomId) {
            console.error('❌ Cannot send file: not connected or no room');
            return false;
        }
        
        const messageData = {
            chatRoomId: this.currentRoomId,
            content: `📎 ${fileName}`,
            attachmentUrl: fileUrl,
            attachmentType: 'FILE',
            senderId: this.currentUserId
        };
        
        this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(messageData));
        return true;
    }

    /**
     * ⌨️ Gửi typing indicator
     */
    sendTypingIndicator(isTyping) {
        if (!this.isConnected || !this.currentRoomId) return;
        
        const typingData = {
            chatRoomId: this.currentRoomId,
            isTyping: isTyping
        };
        
        this.stompClient.send('/app/chat.typing', {}, JSON.stringify(typingData));
    }

    /**
     * ✅ Đánh dấu messages đã đọc
     */
    markMessagesAsRead() {
        if (!this.isConnected || !this.currentRoomId) return;
        
        const markReadData = {
            chatRoomId: this.currentRoomId
        };
        
        this.stompClient.send('/app/chat.markRead', {}, JSON.stringify(markReadData));
    }

    /**
     * 📨 Xử lý tin nhắn nhận được
     */
    handleMessageReceived(messageData) {
        console.log('📨 Message received:', messageData);
        
        // Không hiển thị tin nhắn của chính mình (đã hiển thị khi gửi)
        if (messageData.senderId !== this.currentUserId) {
            if (this.onMessageReceived && typeof this.onMessageReceived === 'function') {
                this.onMessageReceived(messageData);
            }
            
            // Browser notification nếu window không focus
            if (document.hidden && 'Notification' in window && Notification.permission === 'granted') {
                new Notification(`💬 ${messageData.senderName}`, {
                    body: messageData.content,
                    icon: '/images/bell.svg'
                });
            }
            
            // Auto mark as read nếu đang ở trong room
            if (document.hasFocus() && this.currentRoomId === messageData.chatRoomId) {
                setTimeout(() => this.markMessagesAsRead(), 1000);
            }
        }
    }

    /**
     * 👋 Xử lý presence updates
     */
    handlePresenceUpdate(presenceMessage) {
        console.log('👋 Presence update:', presenceMessage);
        
        if (presenceMessage.includes('tham gia')) {
            if (this.onUserJoined) {
                this.onUserJoined(presenceMessage);
            }
        } else if (presenceMessage.includes('rời')) {
            if (this.onUserLeft) {
                this.onUserLeft(presenceMessage);
            }
        }
    }

    /**
     * ⌨️ Xử lý typing indicator
     */
    handleTypingIndicator(typingData) {
        // Chỉ hiển thị typing của người khác
        if (typingData.userName !== this.currentUserName) {
                    if (this.onTyping && typeof this.onTyping === 'function') {
            this.onTyping(typingData);
        }
        }
    }

    /**
     * ✅ Xử lý read receipt
     */
    handleReadReceipt(readData) {
        if (readData.userId !== this.currentUserId) {
                    if (this.onReadReceipt && typeof this.onReadReceipt === 'function') {
            this.onReadReceipt(readData);
        }
        }
    }

    /**
     * 🔔 Xử lý notification
     */
    handleNotification(notification) {
        console.log('🔔 Notification:', notification);
        
        // Hiển thị browser notification nếu có permission
        if (Notification.permission === 'granted') {
            new Notification('Tin nhắn mới', {
                body: notification,
                icon: '/images/chat-icon.png'
            });
        }
    }

    /**
     * ❌ Xử lý error
     */
    handleError(error) {
        console.error('❌ Chat error:', error);
        if (this.onError) {
            this.onError(error);
        }
    }

    /**
     * 🔌 Ngắt kết nối
     */
    disconnect() {
        if (this.stompClient && this.isConnected) {
            this.leaveRoom();
            this.stompClient.disconnect(() => {
                console.log('👋 WebSocket disconnected');
                this.isConnected = false;
                if (this.onDisconnected) {
                    this.onDisconnected();
                }
            });
        }
    }

    /**
     * 📊 Lấy trạng thái kết nối
     */
    getConnectionStatus() {
        return {
            isConnected: this.isConnected,
            currentRoomId: this.currentRoomId,
            currentUserId: this.currentUserId,
            reconnectAttempts: this.reconnectAttempts
        };
    }
}

// Export cho sử dụng global
window.ChatWebSocketClient = ChatWebSocketClient; 