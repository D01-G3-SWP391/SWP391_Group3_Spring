// Chat Notification JavaScript Functions

// WebSocket variables - tham khảo từ jobPostApplications.html
let chatWebSocketClient = null;
let currentChatRoomId = null;
let currentUserId = null;
let currentUserName = null;

document.addEventListener('DOMContentLoaded', function() {
    // Initialize user info first
    initializeUserInfo();
    
    // Update chat rooms initially and every 30 seconds
    updateChatRooms();
    setInterval(updateChatRooms, 30000);
    
    // Add ESC key listener to close dropdown and modal
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            const dropdown = document.getElementById('chatNotificationDropdown');
            if (dropdown && dropdown.style.display === 'block') {
                dropdown.style.display = 'none';
            }
            
            // Close chat modal if open
            const chatOverlay = document.getElementById('chatOverlay');
            if (chatOverlay && chatOverlay.classList.contains('show')) {
                closeChat();
            }
        }
    });
    
    // Close chat modal when clicking overlay
    const chatOverlay = document.getElementById('chatOverlay');
    if (chatOverlay) {
        chatOverlay.addEventListener('click', function(event) {
            if (event.target === chatOverlay) {
                closeChat();
            }
        });
    }
    
    // Initialize message form if exists
    const messageForm = document.getElementById('messageForm');
    if (messageForm) {
        messageForm.addEventListener('submit', sendMessage);
    }
});

// Initialize user info from API
function initializeUserInfo() {
    fetch('/api/chat/current-user')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get user info');
            }
            return response.json();
        })
        .then(userInfo => {
            // Set global variables - tham khảo từ jobPostApplications.html
            currentUserId = userInfo.userId;
            currentUserName = userInfo.fullName;
            
            window.currentUserId = userInfo.userId;
            window.currentUserName = userInfo.fullName;
            window.currentUserRole = userInfo.role;
            
            console.log('✅ Chat notification - User info loaded:', userInfo);
            
            // Initialize WebSocket after user info is loaded
            initializeWebSocket();
        })
        .catch(error => {
            console.log('⚠️ Could not load user info for chat notification:', error);
            // Fallback values
            currentUserId = 1;
            currentUserName = "User";
            initializeWebSocket();
        });
}

// Initialize WebSocket - tham khảo từ jobPostApplications.html
function initializeWebSocket() {
    if (window.ChatWebSocketClient) {
        chatWebSocketClient = new ChatWebSocketClient();

        // Set callbacks with debug
        chatWebSocketClient.onMessageReceived = (message) => {
            console.log('🔄 WebSocket callback triggered with message:', message);
            handleNewMessage(message);
        };
        chatWebSocketClient.onConnected = () => console.log('✅ Chat WebSocket connected');
        chatWebSocketClient.onError = (error) => console.error('❌ Chat error:', error);

        // Connect to WebSocket
        chatWebSocketClient.connect(currentUserId, currentUserName);
    }
}

// Handle new message received from WebSocket - tham khảo từ jobPostApplications.html
function handleNewMessage(message) {
    console.log('💬 Handling new message:', message);

    // Only add message to UI if it's from someone else and we're in the right room
    if (message.senderId !== currentUserId && currentChatRoomId && 
        message.chatRoomId === parseInt(currentChatRoomId)) {
        addMessageToUI(message);
        
        // Update chat rooms list to reflect new message
        updateChatRooms();
    }
}

// Toggle chat notification dropdown
function toggleChatNotifications(event) {
    event.stopPropagation();
    const dropdown = document.getElementById('chatNotificationDropdown');
    const isVisible = dropdown.style.display === 'block';
    
    if (!isVisible) {
        // Close other dropdowns if open
        closeOtherDropdowns();
        
        dropdown.style.display = 'block';
        updateChatRooms(); // Refresh data when opening
    } else {
        dropdown.style.display = 'none';
    }
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const bell = document.getElementById('chatNotificationBell');
    const dropdown = document.getElementById('chatNotificationDropdown');
    if (bell && dropdown && !bell.contains(event.target) && dropdown.style.display === 'block') {
        dropdown.style.display = 'none';
    }
});

// Close other dropdowns (notification bell, AI chat, etc.)
function closeOtherDropdowns() {
    // Close notification bell if open
    const notificationDropdown = document.getElementById('notificationDropdown');
    if (notificationDropdown && notificationDropdown.style.display === 'block') {
        notificationDropdown.style.display = 'none';
    }
    
    // Close AI chat if open
    const aiChatModal = document.getElementById('aiChatModal');
    if (aiChatModal && aiChatModal.style.display === 'block') {
        aiChatModal.style.display = 'none';
    }
}

// Global function to close chat notifications
window.closeChatNotifications = function() {
    const dropdown = document.getElementById('chatNotificationDropdown');
    if (dropdown) {
        dropdown.style.display = 'none';
    }
};

// Update chat rooms (for real-time updates)
function updateChatRooms() {
    fetch('/api/chat/rooms')
        .then(response => {
            // Check if user is authenticated
            if (response.status === 401) {
                console.log('🔒 User not authenticated for chat notification');
                return [];
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('🔍 Backend data structure:', data); // Debug backend data
            const chatList = document.getElementById('chatNotificationList');
            if (!chatList) return;
            
            chatList.innerHTML = '';

            if (data.length === 0) {
                chatList.innerHTML = `
                    <div class="chat-notification-empty">
                        <i class="bi bi-chat-square-text"></i>
                        <p>Chưa có cuộc trò chuyện nào</p>
                        <small>Các cuộc trò chuyện sẽ xuất hiện ở đây</small>
                    </div>
                `;
            } else {
                data.forEach(chatRoom => {
                    const chatItem = document.createElement('div');
                    chatItem.className = `chat-notification-item${chatRoom.hasUnreadMessages ? ' unread' : ''}`;
                    chatItem.dataset.roomId = chatRoom.chatRoomId;
                    
                    // Determine other user info - tham khảo từ jobPostApplications.html
                    const displayName = chatRoom.otherParticipantName || 'Unknown User';
                    const lastMessage = chatRoom.lastMessage || 'Chưa có tin nhắn';
                    const timeAgo = chatRoom.lastMessageTime ? formatTimeAgo(chatRoom.lastMessageTime) : 'Invalid Date';
                    const unreadCount = chatRoom.unreadCount || 0;
                    const isOnline = chatRoom.isOnline || false;
                    
                    // Create text-based avatar (first letter of name)
                    const avatarText = displayName.charAt(0).toUpperCase();
                    
                    chatItem.innerHTML = `
                        <div class="chat-notification-content" onclick="openChatWithUser(${chatRoom.chatRoomId}, 'student', 0)">
                            <div class="chat-notification-avatar">
                                <div class="chat-notification-avatar-text">${avatarText}</div>
                                <div class="chat-notification-status ${isOnline ? 'online' : 'offline'}"></div>
                            </div>
                            <div class="chat-notification-details">
                                <div class="chat-notification-name">${escapeHtml(displayName)}</div>
                                <div class="chat-notification-message">${escapeHtml(lastMessage)}</div>
                                <div class="chat-notification-time">${timeAgo}</div>
                            </div>
                            ${unreadCount > 0 ? `<div class="chat-notification-count">${unreadCount}</div>` : ''}
                        </div>
                    `;
                    
                    chatList.appendChild(chatItem);
                });
            }

            // Update badge count
            updateChatBadgeCount();
        })
        .catch(error => {
            console.error('Error updating chat rooms:', error);
            const chatList = document.getElementById('chatNotificationList');
            if (chatList) {
                chatList.innerHTML = `
                    <div class="chat-notification-error">
                        <i class="bi bi-exclamation-triangle"></i>
                        <p>Không thể tải cuộc trò chuyện</p>
                        <small>Vui lòng thử lại sau</small>
                    </div>
                `;
            }
        });
}

// Update chat badge count
function updateChatBadgeCount() {
    fetch('/api/chat/unread-count')
        .then(response => {
            // Check if user is authenticated
            if (response.status === 401) {
                console.log('🔒 User not authenticated for unread count');
                return 0;
            }
            if (!response.ok) {
                throw new Error('Failed to get unread count');
            }
            return response.json();
        })
        .then(count => {
            const badge = document.getElementById('chatBadge');
            if (badge) {
                if (count > 0) {
                    badge.style.display = 'flex';
                    badge.textContent = count > 99 ? '99+' : count;
                } else {
                    badge.style.display = 'none';
                }
            }
        })
        .catch(error => {
            console.error('Error updating chat badge count:', error);
        });
}

// Open chat with specific user
function openChatWithUser(chatRoomId, userType, userId) {
    // Close the dropdown
    const dropdown = document.getElementById('chatNotificationDropdown');
    if (dropdown) {
        dropdown.style.display = 'none';
    }
    
    // You can implement different ways to open chat:
    // Option 1: Open in modal
    openChatModal(chatRoomId, userType, userId);
    
    // Option 2: Navigate to chat page
    // window.location.href = `/chat/${chatRoomId}`;
    
    // Option 3: Open in sidebar
    // openChatSidebar(chatRoomId, userType, userId);
}

// Open chat modal (tích hợp với chat system hiện tại)
function openChatModal(chatRoomId, userType, userId) {
    console.log('🔓 Opening chat modal with room:', chatRoomId, 'userType:', userType, 'userId:', userId);
    
    // Set current chat room ID - QUAN TRỌNG cho WebSocket
    currentChatRoomId = String(chatRoomId);
    
    const chatOverlay = document.getElementById('chatOverlay');
    const chatOffcanvas = document.getElementById('chatOffcanvas');
    
    if (chatOverlay && chatOffcanvas) {
        // Show modal
        chatOverlay.classList.add('show');
        chatOffcanvas.classList.add('show');
        
        // Set current room ID
        window.currentChatRoomId = chatRoomId;
        
        // Join WebSocket room - QUAN TRỌNG cho real-time
        if (chatWebSocketClient && chatWebSocketClient.isConnected) {
            chatWebSocketClient.joinRoom(Number(currentChatRoomId));
            console.log('✅ Joined WebSocket room:', currentChatRoomId);
        }
        
        // Get room info and setup chat
        getChatRoomInfo(chatRoomId).then(roomInfo => {
            if (roomInfo) {
                // Update header với thông tin người chat
                document.getElementById('chatName').textContent = roomInfo.otherParticipantName || 'User';
                document.getElementById('chatAvatar').textContent = (roomInfo.otherParticipantName || 'U').charAt(0).toUpperCase();
                document.getElementById('chatStatus').textContent = roomInfo.isOnline ? 'Đang hoạt động' : 'Offline';
                
                // Load chat history
                loadChatHistory(chatRoomId);
                
                // Enable input
                const messageInput = document.getElementById('messageInput');
                const submitButton = document.querySelector('#messageForm button[type="submit"]');
                if (messageInput) {
                    messageInput.disabled = false;
                    messageInput.placeholder = `Nhập tin nhắn cho ${roomInfo.otherParticipantName}...`;
                    setTimeout(() => messageInput.focus(), 500);
                }
                if (submitButton) {
                    submitButton.disabled = false;
                }
                
                // Hide back button (không cần trong modal này)
                const backBtn = document.getElementById('chatBackBtn');
                if (backBtn) backBtn.style.display = 'none';
            }
        });
    } else {
        // Fallback: redirect to applications page
        console.log('📄 No modal found, redirecting to applications page');
        window.location.href = `/Employer/JobPosts/applications?room=${chatRoomId}`;
    }
}

// Open full chat page
function openChatPage() {
    window.location.href = '/chat';
}

// Mark all chat as read
function markAllChatAsRead(event) {
    event.preventDefault();
    event.stopPropagation();
    
    fetch('/api/chat/mark-all-read', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        // Check if user is authenticated
        if (response.status === 401) {
            console.log('🔒 User not authenticated for mark all read');
            return {};
        }
        if (!response.ok) {
            throw new Error('Failed to mark all as read');
        }
        return response.json();
    })
    .then(() => {
        // Remove unread class from all items
        document.querySelectorAll('.chat-notification-item.unread').forEach(item => {
            item.classList.remove('unread');
        });
        
        // Remove unread count badges
        document.querySelectorAll('.chat-notification-count').forEach(count => {
            count.remove();
        });
        
        updateChatBadgeCount();
    })
    .catch(error => {
        console.error('Error marking all chat as read:', error);
    });
}

// Utility functions
function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

function formatTimeAgo(dateString) {
    if (!dateString) return 'Invalid Date';
    
    try {
        // Xử lý nhiều format từ backend
        let date;
        
        // Nếu là format HH:mm từ backend 
        if (/^\d{2}:\d{2}$/.test(dateString)) {
            return dateString; // Trả về luôn format HH:mm
        }
        
        // Nếu là ISO datetime hoặc format khác
        date = new Date(dateString);
        if (isNaN(date.getTime())) {
            return 'Invalid Date';
        }
        
        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);
        
        if (diffInSeconds < 60) {
            return 'Vừa xong';
        } else if (diffInSeconds < 3600) {
            const minutes = Math.floor(diffInSeconds / 60);
            return `${minutes} phút trước`;
        } else if (diffInSeconds < 86400) {
            const hours = Math.floor(diffInSeconds / 3600);
            return `${hours} giờ trước`;
        } else if (diffInSeconds < 604800) {
            const days = Math.floor(diffInSeconds / 86400);
            return `${days} ngày trước`;
        } else {
            return date.toLocaleDateString('vi-VN');
        }
    } catch (error) {
        console.warn('Error parsing date:', dateString, error);
        return 'Invalid Date';
    }
}

// ========== CHAT MODAL FUNCTIONS ==========

// Get chat room info
function getChatRoomInfo(chatRoomId) {
    return fetch(`/api/chat/rooms/${chatRoomId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get room info');
            }
            return response.json();
        })
        .catch(error => {
            console.error('Error getting room info:', error);
            return null;
        });
}

// Load chat history for modal
function loadChatHistory(roomId) {
    console.log('📜 Loading chat history for room:', roomId);
    
    fetch(`/api/chat/rooms/${roomId}/messages?page=0&size=50`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load messages');
            }
            return response.json();
        })
        .then(messages => {
            const messagesContainer = document.getElementById('chatMessages');
            if (!messagesContainer) return;
            
            messagesContainer.innerHTML = ''; // Clear existing messages
            
            // Handle both array and paginated response
            const messageList = Array.isArray(messages) ? messages : (messages.content || []);
            
            if (messageList.length === 0) {
                messagesContainer.innerHTML = `
                    <div style="text-align: center; padding: 40px 20px; color: #666;">
                        <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 15px; margin-bottom: 20px;">
                            <h5 style="margin: 0 0 10px 0;">💬 Cuộc trò chuyện mới</h5>
                            <p style="margin: 0; font-size: 14px; opacity: 0.9;">
                                Bắt đầu cuộc trò chuyện bằng cách gửi tin nhắn đầu tiên!
                            </p>
                        </div>
                        <div style="color: #999; font-size: 12px;">
                            💡 Tin nhắn sẽ được gửi real-time
                        </div>
                    </div>
                `;
            } else {
                messageList.forEach(message => {
                    addMessageToUI(message);
                });
            }
            
            // Scroll to bottom
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        })
        .catch(error => {
            console.error('Error loading chat history:', error);
            const messagesContainer = document.getElementById('chatMessages');
            if (messagesContainer) {
                messagesContainer.innerHTML = `
                    <div style="text-align: center; padding: 20px; color: #666;">
                        <p>Không thể tải lịch sử chat</p>
                        <p><small>Bắt đầu cuộc trò chuyện bằng cách gửi tin nhắn đầu tiên</small></p>
                    </div>
                `;
            }
        });
}

// Add message to UI
function addMessageToUI(message) {
    const messagesContainer = document.getElementById('chatMessages');
    if (!messagesContainer) return;
    
    // Generate unique ID for duplicate detection - tham khảo từ jobPostApplications.html
    const messageId = message.id || `local_${message.content}_${message.senderId}_${message.sentAt}`;

    // Check for duplicate messages
    if (document.querySelector(`[data-message-id="${messageId}"]`)) {
        console.log('🔄 Duplicate message detected, skipping:', message.content);
        return;
    }
    
    // Safe timestamp parsing
    let messageTime = 'Bây giờ';
    try {
        if (message.sentAt) {
            const date = new Date(message.sentAt);
            if (!isNaN(date.getTime())) {
                messageTime = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
            }
        } else if (message.timestamp) {
            const date = new Date(message.timestamp);
            if (!isNaN(date.getTime())) {
                messageTime = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
            }
        }
    } catch (error) {
        console.warn('Invalid timestamp:', message.sentAt || message.timestamp);
        messageTime = new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    }
    
    // Determine if message is sent by current user
    const isSentByMe = message.senderId == currentUserId;
    const messageType = isSentByMe ? 'sent' : 'received';
    
    const messageElement = document.createElement('div');
    messageElement.className = `message ${messageType}`;
    messageElement.setAttribute('data-message-id', messageId);
    messageElement.innerHTML = `
        <div class="message-bubble">
            ${escapeHtml(message.content)}
            <div class="message-time">${messageTime}</div>
        </div>
    `;
    
    // Remove empty state if exists
    const emptyState = messagesContainer.querySelector('div[style*="text-align: center"]');
    if (emptyState && emptyState.innerHTML.includes('Cuộc trò chuyện mới')) {
        emptyState.remove();
    }
    
    messagesContainer.appendChild(messageElement);
    
    // Scroll to bottom
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
    
    console.log('💬 Added message to UI:', message.content, 'ID:', messageId);
}

// Close chat modal
function closeChat() {
    console.log('❌ Closing chat modal');
    
    // Leave WebSocket room - QUAN TRỌNG
    if (chatWebSocketClient && currentChatRoomId) {
        chatWebSocketClient.leaveRoom();
        console.log('✅ Left WebSocket room:', currentChatRoomId);
        currentChatRoomId = null;
    }
    
    const chatOverlay = document.getElementById('chatOverlay');
    const chatOffcanvas = document.getElementById('chatOffcanvas');
    
    if (chatOverlay) chatOverlay.classList.remove('show');
    if (chatOffcanvas) chatOffcanvas.classList.remove('show');
    
    // Reset current room
    window.currentChatRoomId = null;
    
    // Reset input
    const messageInput = document.getElementById('messageInput');
    const submitButton = document.querySelector('#messageForm button[type="submit"]');
    if (messageInput) {
        messageInput.disabled = true;
        messageInput.placeholder = 'Chọn cuộc trò chuyện để nhắn tin...';
        messageInput.value = '';
    }
    if (submitButton) {
        submitButton.disabled = true;
    }
}

// Send message from modal - tham khảo từ jobPostApplications.html
function sendMessage(event) {
    event.preventDefault();
    
    const messageInput = document.getElementById('messageInput');
    const content = messageInput.value.trim();
    
    if (!content || !currentChatRoomId) {
        console.log('⚠️ Cannot send message - missing content or room ID');
        return;
    }
    
    // Prevent double sending
    if (messageInput.dataset.sending === 'true') {
        console.log('⚠️ Message already being sent, ignoring duplicate');
        return;
    }
    
    console.log('📤 Sending message:', content, 'to room:', currentChatRoomId);
    
    // Mark as sending
    messageInput.dataset.sending = 'true';
    messageInput.disabled = true;
    
    // Send via WebSocket if available - tham khảo từ jobPostApplications.html
    if (chatWebSocketClient && chatWebSocketClient.isConnected) {
        const success = chatWebSocketClient.sendMessage(content);

        if (success) {
            // Add message to UI immediately for better UX
            const message = {
                content: content,
                senderId: currentUserId,
                sentAt: new Date().toISOString()
            };
            addMessageToUI(message);

            // Clear input
            messageInput.value = '';
            
            // Update chat rooms list
            updateChatRooms();
        } else {
            console.error('❌ Failed to send message via WebSocket');
            alert('Không thể gửi tin nhắn. Vui lòng thử lại.');
        }
    } else {
        console.error('❌ WebSocket not connected');
        alert('Kết nối chat chưa sẵn sàng. Vui lòng thử lại.');
    }

    // Reset sending state
    setTimeout(() => {
        messageInput.dataset.sending = 'false';
        messageInput.disabled = false;
        messageInput.focus();
    }, 100);
}

// Get current user ID
function getCurrentUserId() {
    // Try to get from various sources
    return window.currentUserId || 
           document.querySelector('meta[name="user-id"]')?.content ||
           null;
}

// Get current user type
function getCurrentUserType() {
    // Try to get from various sources
    return window.currentUserRole || 
           document.querySelector('meta[name="user-role"]')?.content ||
           'STUDENT'; // Default fallback
}

// Show chat rooms list (for back button)
function showChatRoomsList() {
    const messagesContainer = document.getElementById('chatMessages');
    if (!messagesContainer) return;
    
    // Update header
    document.getElementById('chatName').textContent = 'Chat';
    document.getElementById('chatStatus').textContent = 'Chọn cuộc trò chuyện';
    document.getElementById('chatAvatar').textContent = '👤';
    
    // Hide back button
    const backBtn = document.getElementById('chatBackBtn');
    if (backBtn) backBtn.style.display = 'none';
    
    // Load chat rooms list
    loadChatRoomsList();
    
    // Disable input
    const messageInput = document.getElementById('messageInput');
    if (messageInput) {
        messageInput.disabled = true;
        messageInput.placeholder = 'Chọn cuộc trò chuyện để nhắn tin...';
    }
}

// Load chat rooms list in modal
function loadChatRoomsList() {
    const messagesContainer = document.getElementById('chatMessages');
    if (!messagesContainer) return;
    
    messagesContainer.innerHTML = `
        <div style="text-align: center; padding: 20px; color: #666;">
            <div style="margin-bottom: 15px;">
                <div class="spinner-border spinner-border-sm" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <span style="margin-left: 8px;">Đang tải cuộc trò chuyện...</span>
            </div>
        </div>
    `;
    
    // Fetch chat rooms
    fetch('/api/chat/rooms')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load chat rooms');
            }
            return response.json();
        })
        .then(rooms => {
            if (rooms.length === 0) {
                messagesContainer.innerHTML = `
                    <div style="text-align: center; padding: 40px 20px; color: #666;">
                        <h5>💬 Chưa có cuộc trò chuyện</h5>
                        <p>Bắt đầu chat từ danh sách ứng viên hoặc tin tuyển dụng</p>
                    </div>
                `;
                return;
            }
            
            // Create rooms list
            const roomsHTML = rooms.map(room => {
                const avatar = (room.otherParticipantName || 'U').charAt(0).toUpperCase();
                const lastMessage = room.lastMessage || 'Chưa có tin nhắn';
                const lastMessageTime = formatTimeAgo(room.lastMessageTime);
                const unreadBadge = room.unreadCount > 0 ? 
                    `<span style="background: #dc3545; color: white; border-radius: 12px; padding: 2px 8px; font-size: 11px; margin-left: 8px;">${room.unreadCount}</span>` : '';
                
                return `
                    <div onclick="openSpecificChatRoom(${room.chatRoomId}, '${room.otherParticipantName}')" 
                         style="display: flex; align-items: center; padding: 15px 20px; border-bottom: 1px solid #eee; cursor: pointer; transition: background 0.2s;"
                         onmouseover="this.style.background='#f8f9fa'" onmouseout="this.style.background='white'">
                        
                        <div style="width: 45px; height: 45px; border-radius: 50%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                                   color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; margin-right: 15px; font-size: 18px;">
                            ${avatar}
                        </div>
                        
                        <div style="flex: 1; min-width: 0;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">
                                <h6 style="margin: 0; font-weight: 600; color: #333; font-size: 15px;">${room.otherParticipantName}</h6>
                                <div style="display: flex; align-items: center;">
                                    <small style="color: #666; font-size: 12px;">${lastMessageTime}</small>
                                    ${unreadBadge}
                                </div>
                            </div>
                            <p style="margin: 0; color: #666; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                ${lastMessage}
                            </p>
                        </div>
                    </div>
                `;
            }).join('');
            
            messagesContainer.innerHTML = `
                <div style="padding: 10px;">
                    <div style="margin-bottom: 15px; padding: 10px; background: #f8f9fa; border-radius: 8px; border-left: 4px solid #28a745;">
                        <small style="color: #666; font-size: 12px;">
                            📱 Chọn cuộc trò chuyện để bắt đầu nhắn tin
                        </small>
                    </div>
                    ${roomsHTML}
                </div>
            `;
        })
        .catch(error => {
            console.error('Error loading chat rooms:', error);
            messagesContainer.innerHTML = `
                <div style="text-align: center; padding: 20px; color: #666;">
                    <p>Không thể tải cuộc trò chuyện</p>
                    <p><small>Vui lòng thử lại sau</small></p>
                </div>
            `;
        });
}

// Open specific chat room from list
function openSpecificChatRoom(roomId, participantName) {
    console.log('🔓 Opening specific chat room:', roomId, 'with:', participantName);
    
    // Update header
    document.getElementById('chatName').textContent = participantName;
    document.getElementById('chatAvatar').textContent = participantName.charAt(0).toUpperCase();
    document.getElementById('chatStatus').textContent = 'Đang hoạt động';
    
    // Show back button
    const backBtn = document.getElementById('chatBackBtn');
    if (backBtn) backBtn.style.display = 'inline-block';
    
    // Set current room
    window.currentChatRoomId = roomId;
    
    // Load chat history
    loadChatHistory(roomId);
    
    // Enable input
    const messageInput = document.getElementById('messageInput');
    if (messageInput) {
        messageInput.disabled = false;
        messageInput.placeholder = `Nhập tin nhắn cho ${participantName}...`;
        setTimeout(() => messageInput.focus(), 500);
    }
}
