package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 🚫 BanRecord Entity
 * 
 * Lưu trữ thông tin chi tiết về việc ban user
 * Bao gồm lý do, thời gian ban, và admin thực hiện
 */
@Entity
@Table(name = "ban_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_record_id")
    private Integer banRecordId;
    
    /**
     * Account bị ban
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Account bannedUser;
    
    /**
     * Admin thực hiện ban (nullable nếu admin bị xóa)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banned_by", nullable = true)
    private Account bannedByAdmin;
    
    /**
     * Lý do ban
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ban_reason", nullable = false)
    private BanReason banReason;
    
    /**
     * Mô tả chi tiết lý do ban (tùy chọn)
     */
    @Column(name = "ban_description", columnDefinition = "TEXT")
    private String banDescription;
    
    /**
     * Loại thời gian ban
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_type", nullable = false)
    private BanDurationType banDurationType;
    
    /**
     * Số ngày ban (null nếu permanent)
     */
    @Column(name = "duration_days")
    private Integer banDurationDays;
    
    /**
     * Thời gian bắt đầu ban
     */
    @Column(name = "ban_start_date", nullable = false)
    private LocalDateTime bannedAt;
    
    /**
     * Thời gian hết hạn ban (null nếu permanent)
     */
    @Column(name = "ban_end_date")
    private LocalDateTime banExpiresAt;
    
    /**
     * Trạng thái ban hiện tại
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BanStatus status = BanStatus.ACTIVE;
    
    /**
     * Thời gian tạo record
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Thời gian update gần nhất
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Enum cho lý do ban
     */
    public enum BanReason {
        SPAM("Spam nội dung"),
        INAPPROPRIATE_CONTENT("Nội dung không phù hợp"),
        FAKE_INFORMATION("Thông tin giả mạo"),
        HARASSMENT("Quấy rối người khác"),
        FRAUD("Lừa đảo"),
        MULTIPLE_ACCOUNTS("Tạo nhiều tài khoản"),
        VIOLATION_TERMS("Vi phạm điều khoản sử dụng"),
        SYSTEM_ABUSE("Lạm dụng hệ thống"),
        OTHER("Lý do khác");
        
        private final String description;
        
        BanReason(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Enum cho loại thời gian ban
     */
    public enum BanDurationType {
        TEMPORARY("Tạm thời"),
        PERMANENT("Vĩnh viễn");
        
        private final String description;
        
        BanDurationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Enum cho trạng thái ban
     */
    public enum BanStatus {
        ACTIVE("Đang ban"),
        EXPIRED("Hết hạn"),
        UNBANNED("Đã unban");
        
        private final String description;
        
        BanStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (bannedAt == null) {
            bannedAt = now;
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = BanStatus.ACTIVE;
        }
        
        // Tính toán thời gian hết hạn cho ban tạm thời
        if (banDurationType == BanDurationType.TEMPORARY && banDurationDays != null && banDurationDays > 0) {
            banExpiresAt = bannedAt.plusDays(banDurationDays);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Kiểm tra ban đã hết hạn chưa
     */
    public boolean isExpired() {
        if (banDurationType == BanDurationType.PERMANENT) {
            return false;
        }
        return banExpiresAt != null && LocalDateTime.now().isAfter(banExpiresAt);
    }
    
    /**
     * Kiểm tra ban còn hiệu lực không
     */
    public boolean isActive() {
        return status == BanStatus.ACTIVE && !isExpired();
    }
    
    /**
     * Đánh dấu đã unban
     */
    public void markAsUnbanned(Account adminUser) {
        this.status = BanStatus.UNBANNED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Đánh dấu ban đã hết hạn
     */
    public void markAsExpired() {
        this.status = BanStatus.EXPIRED;
    }
    
    /**
     * Lấy thông tin thời gian ban còn lại
     */
    public String getRemainingTimeText() {
        if (banDurationType == BanDurationType.PERMANENT) {
            return "Vĩnh viễn";
        }
        
        if (banExpiresAt == null) {
            return "Không xác định";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(banExpiresAt)) {
            return "Đã hết hạn";
        }
        
        long daysRemaining = java.time.Duration.between(now, banExpiresAt).toDays();
        long hoursRemaining = java.time.Duration.between(now, banExpiresAt).toHours() % 24;
        
        if (daysRemaining > 0) {
            return String.format("%d ngày %d giờ", daysRemaining, hoursRemaining);
        } else {
            return String.format("%d giờ", hoursRemaining);
        }
    }
} 