// Chat Notification JavaScript Functions

document.addEventListener('DOMContentLoaded', function() {
    // Update chat rooms initially and every 30 seconds
    updateChatRooms();
    setInterval(updateChatRooms, 30000);
    
    // Add ESC key listener to close dropdown
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            const dropdown = document.getElementById('chatNotificationDropdown');
            if (dropdown && dropdown.style.display === 'block') {
                dropdown.style.display = 'none';
            }
        }
    });
});

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
                    
                    // Determine other user info
                    const otherUser = chatRoom.otherUser || {};
                    const displayName = otherUser.fullName || otherUser.email || 'Unknown User';
                    const lastMessage = chatRoom.lastMessage || 'Chưa có tin nhắn';
                    const timeAgo = chatRoom.lastMessageTime ? formatTimeAgo(chatRoom.lastMessageTime) : '';
                    const unreadCount = chatRoom.unreadCount || 0;
                    
                    // Create text-based avatar (first letter of name)
                    const avatarText = displayName.charAt(0).toUpperCase();
                    
                    chatItem.innerHTML = `
                        <div class="chat-notification-content" onclick="openChatWithUser(${chatRoom.chatRoomId}, '${otherUser.userType || 'student'}', ${otherUser.userId || 0})">
                            <div class="chat-notification-avatar">
                                <div class="chat-notification-avatar-text">${avatarText}</div>
                                <div class="chat-notification-status ${otherUser.isOnline ? 'online' : 'offline'}"></div>
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

// Open chat modal (you need to implement this based on your current chat modal)
function openChatModal(chatRoomId, userType, userId) {
    // This should open your existing chat modal/interface
    // You might need to adapt this to your current chat implementation
    console.log('Opening chat with room:', chatRoomId, 'userType:', userType, 'userId:', userId);
    
    // Example: If you have a chat modal function
    if (typeof openChatWithStudent === 'function') {
        openChatWithStudent(userId);
    } else if (typeof openChatWithEmployer === 'function') {
        openChatWithEmployer(userId);
    } else {
        // Fallback: redirect to chat page
        window.location.href = `/chat?room=${chatRoomId}`;
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
    const date = new Date(dateString);
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
}
