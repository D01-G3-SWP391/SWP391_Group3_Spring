/**
 * 💬 ChatWebSocketClient
 * 
 * Client-side WebSocket handler cho chat functionality
 */
class ChatWebSocketClient {
    constructor() {
        this.stompClient = null;
        this.isConnected = false;
        this.userId = null;
        this.userName = null;
        
        // Callbacks
        this.onMessageReceived = null;
        this.onConnected = null;
        this.onError = null;
    }
    
    /**
     * Kết nối tới WebSocket server
     */
    connect(userId, userName) {
        this.userId = userId;
        this.userName = userName;
        
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        // Disable debug logs
        this.stompClient.debug = null;
        
        this.stompClient.connect({}, 
            this.onConnect.bind(this), 
            this.handleError.bind(this)
        );
    }
    
    /**
     * Callback khi kết nối thành công
     */
    onConnect() {
        this.isConnected = true;
        console.log('✅ Connected to WebSocket');
        
        // Subscribe to private channel
        this.stompClient.subscribe(
            '/user/queue/messages',
            this.handleMessage.bind(this)
        );
        
        // Subscribe to errors channel
        this.stompClient.subscribe(
            '/user/queue/errors',
            this.handleError.bind(this)
        );
        
        // Notify caller
        if (this.onConnected) {
            this.onConnected();
        }
    }
    
    /**
     * Join chat room
     */
    joinRoom(chatRoomId) {
        if (!this.isConnected) return false;
        
        this.stompClient.send("/app/chat.joinRoom", {}, JSON.stringify({
            chatRoomId: chatRoomId,
            userId: this.userId
        }));
        
        // Subscribe to room messages
        this.stompClient.subscribe(
            '/topic/chat/' + chatRoomId,
            this.handleMessage.bind(this)
        );
        
        return true;
    }
    
    /**
     * Leave chat room
     */
    leaveRoom() {
        if (!this.isConnected) return false;
        
        this.stompClient.send("/app/chat.leaveRoom", {}, JSON.stringify({
            userId: this.userId
        }));
        
        return true;
    }
    
    /**
     * Send message
     */
    sendMessage(message) {
        if (!this.isConnected) return false;
        
        this.stompClient.send("/app/chat.sendMessage", {}, message);
        return true;
    }
    
    /**
     * Handle incoming message
     */
    handleMessage(payload) {
        try {
            const message = JSON.parse(payload.body);
            console.log('📨 Received message:', message);
            
            if (this.onMessageReceived) {
                this.onMessageReceived(message);
            }
        } catch (error) {
            console.error('Error handling message:', error);
            this.handleError(error);
        }
    }
    
    /**
     * Handle errors
     */
    handleError(error) {
        console.error('WebSocket error:', error);
        this.isConnected = false;
        
        if (this.onError) {
            this.onError(error);
        }
    }
    
    /**
     * Disconnect
     */
    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.isConnected = false;
            console.log('❌ Disconnected from WebSocket');
        }
    }
    
    /**
     * Send typing indicator
     */
    sendTyping(chatRoomId, isTyping) {
        if (!this.isConnected) return false;
        
        this.stompClient.send("/app/chat.typing", {}, JSON.stringify({
            chatRoomId: chatRoomId,
            userId: this.userId,
            isTyping: isTyping
        }));
        
        return true;
    }
    
    /**
     * Mark messages as read
     */
    markAsRead(chatRoomId) {
        if (!this.isConnected) return false;
        
        this.stompClient.send("/app/chat.markRead", {}, JSON.stringify({
            chatRoomId: chatRoomId,
            userId: this.userId
        }));
        
        return true;
    }
} 