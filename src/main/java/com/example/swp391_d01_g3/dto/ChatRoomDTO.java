package com.example.swp391_d01_g3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🏠 Chat Room DTO
 * 
 * DTO để truyền thông tin chat room qua API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    
    private Long chatRoomId;
    private String otherParticipantName;
    private String otherParticipantAvatar;
    private String lastMessage;
    private String lastMessageTime;
    private Integer unreadCount;
    private Boolean isOnline;
    private Long jobPostId;
    private String jobTitle;
    private String jobLocation;
    
    /**
     * Constructor cho basic chat room info
     */
    public ChatRoomDTO(Long chatRoomId, String otherParticipantName, String otherParticipantAvatar) {
        this.chatRoomId = chatRoomId;
        this.otherParticipantName = otherParticipantName;
        this.otherParticipantAvatar = otherParticipantAvatar;
        this.unreadCount = 0;
        this.isOnline = false;
    }
    
    /**
     * Kiểm tra có tin nhắn chưa đọc không
     */
    public boolean hasUnreadMessages() {
        return unreadCount != null && unreadCount > 0;
    }
    
    /**
     * Lấy text hiển thị cho last message time
     */
    public String getDisplayTime() {
        return lastMessageTime != null ? lastMessageTime : "";
    }
    
    /**
     * Lấy preview text cho last message
     */
    public String getMessagePreview() {
        if (lastMessage == null || lastMessage.trim().isEmpty()) {
            return "Chưa có tin nhắn";
        }
        return lastMessage.length() > 50 ? lastMessage.substring(0, 50) + "..." : lastMessage;
    }
} 