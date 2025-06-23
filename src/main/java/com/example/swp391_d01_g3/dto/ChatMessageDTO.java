package com.example.swp391_d01_g3.dto;

import com.example.swp391_d01_g3.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 📨 ChatMessageDTO - Data Transfer Object cho WebSocket
 * 
 * Sử dụng để truyền tin nhắn qua WebSocket connection
 * Tránh gửi toàn bộ entity qua network
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    private Long id;  // Khớp với service
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String senderType;  // String thay vì enum
    private String content;     // Khớp với service
    private String attachmentUrl;
    private String attachmentType;
    private Boolean isRead;
    private String sentAt;
    
    // Loại bỏ static method này vì service đã handle conversion
    
    /**
     * Lấy thời gian gửi dạng ngắn (chỉ giờ:phút)
     */
    public String getShortTime() {
        if (sentAt != null && sentAt.length() >= 5) {
            return sentAt.substring(11, 16); // "HH:mm" from "yyyy-MM-dd HH:mm:ss"
        }
        return "";
    }
    
    /**
     * Kiểm tra có file đính kèm không
     */
    public boolean hasFile() {
        return attachmentUrl != null && !attachmentUrl.trim().isEmpty();
    }
} 