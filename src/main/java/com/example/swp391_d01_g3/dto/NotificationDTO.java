package com.example.swp391_d01_g3.dto;

import com.example.swp391_d01_g3.model.Notification;
import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String notificationType;
    private Long relatedId;
    private Integer userId;

    public NotificationDTO() {
    }

    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setRead(notification.isRead());
        dto.setNotificationType(notification.getNotificationType());
        dto.setRelatedId(notification.getRelatedId());
        dto.setUserId(notification.getUser().getUserId());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
} 