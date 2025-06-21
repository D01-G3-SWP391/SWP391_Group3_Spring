package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📨 ChatMessage Entity
 * 
 * Đại diện cho một tin nhắn trong chat room
 * Hỗ trợ text, file, và image messages
 */
@Entity
@Table(name = "chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;
    
    /**
     * Chat room chứa message này
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    
    /**
     * Người gửi tin nhắn
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;
    
    /**
     * Loại người gửi (STUDENT, EMPLOYER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;
    
    /**
     * Loại tin nhắn (TEXT, FILE, IMAGE)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.TEXT;
    
    /**
     * Nội dung tin nhắn (text)
     */
    @Column(name = "message_content", columnDefinition = "TEXT")
    private String messageContent;
    
    /**
     * URL file đính kèm (nếu có)
     */
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    
    /**
     * Tên file gốc (nếu có file đính kèm)
     */
    @Column(name = "file_name", length = 255)
    private String fileName;
    
    /**
     * Kích thước file (bytes)
     */
    @Column(name = "file_size")
    private Long fileSize;
    
    /**
     * Tin nhắn đã được đọc chưa
     */
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    /**
     * Thời gian gửi tin nhắn
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    /**
     * Thời gian đọc tin nhắn
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (messageType == null) {
            messageType = MessageType.TEXT;
        }
    }
    
    /**
     * Enum cho loại người gửi
     */
    public enum SenderType {
        STUDENT,
        EMPLOYER,
        ADMIN
    }
    
    /**
     * Enum cho loại tin nhắn
     */
    public enum MessageType {
        TEXT,      // Tin nhắn text thông thường
        FILE,      // File đính kèm (PDF, DOC, etc.)
        IMAGE,     // Hình ảnh
        SYSTEM     // Tin nhắn hệ thống (join, leave, etc.)
    }
    
    /**
     * Đánh dấu tin nhắn đã đọc
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    /**
     * Kiểm tra có phải tin nhắn file không
     */
    public boolean hasFile() {
        return fileUrl != null && !fileUrl.trim().isEmpty();
    }
    
    /**
     * Lấy preview content (rút gọn cho hiển thị list)
     */
    public String getPreviewContent() {
        if (messageType == MessageType.FILE) {
            return "📎 " + (fileName != null ? fileName : "File đính kèm");
        } else if (messageType == MessageType.IMAGE) {
            return "🖼️ Hình ảnh";
        } else if (messageContent != null) {
            return messageContent.length() > 50 ? 
                   messageContent.substring(0, 50) + "..." : 
                   messageContent;
        }
        return "";
    }
} 