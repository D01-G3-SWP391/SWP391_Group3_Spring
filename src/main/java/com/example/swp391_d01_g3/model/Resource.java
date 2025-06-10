package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    private String resourceTitle;

    @Column(columnDefinition = "TEXT")
    private String resourceContent;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    // Thêm liên kết đến User (admin)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Account createdBy;

    // Nếu muốn, có thể thêm thông tin ngày tạo, sửa
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public enum ResourceType {
        interview_guide, application_tips, quotes
    }

    public Resource() {
    }

    public Resource(Long resourceId, String resourceTitle, String resourceContent, String imageUrl, ResourceType resourceType, Account createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.resourceId = resourceId;
        this.resourceTitle = resourceTitle;
        this.resourceContent = resourceContent;
        this.imageUrl = imageUrl;
        this.resourceType = resourceType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getResourceContent() {
        return resourceContent;
    }

    public void setResourceContent(String resourceContent) {
        this.resourceContent = resourceContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Account getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
