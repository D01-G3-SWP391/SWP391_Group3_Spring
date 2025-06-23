package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 💬 ChatRoom Entity
 * 
 * Đại diện cho một phòng chat giữa Student và Employer
 * Mỗi cặp Student-Employer có thể có nhiều chat room (theo job post)
 */
@Entity
@Table(name = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;
    
    /**
     * Student tham gia chat
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    /**
     * Employer tham gia chat
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;
    
    /**
     * Job Post làm context cho cuộc chat (optional)
     * Có thể null nếu là chat tổng quát
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;
    
    /**
     * Thời gian tạo chat room
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Thời gian tin nhắn cuối cùng
     */
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;
    
    /**
     * Tin nhắn cuối cùng (để hiển thị preview)
     */
    @Column(name = "last_message", length = 500)
    private String lastMessage;
    
    /**
     * Chat room có đang active không
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    /**
     * Số tin nhắn chưa đọc của Student
     */
    @Column(name = "student_unread_count")
    private Integer studentUnreadCount = 0;
    
    /**
     * Số tin nhắn chưa đọc của Employer
     */
    @Column(name = "employer_unread_count")
    private Integer employerUnreadCount = 0;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
    
    /**
     * Tạo unique identifier cho chat room
     */
    public String getRoomIdentifier() {
        return "room_" + chatRoomId;
    }
    
    /**
     * Kiểm tra user có phải là participant của room này không
     */
    public boolean isParticipant(Long userId) {
        return (student != null && student.getAccount().getUserId().equals(userId)) ||
               (employer != null && employer.getAccount().getUserId().equals(userId));
    }
    
    /**
     * Lấy participant còn lại (không phải current user)
     */
    public Account getOtherParticipant(Long currentUserId) {
        if (student != null && student.getAccount().getUserId().equals(currentUserId)) {
            return employer.getAccount();
        } else if (employer != null && employer.getAccount().getUserId().equals(currentUserId)) {
            return student.getAccount();
        }
        return null;
    }
} 