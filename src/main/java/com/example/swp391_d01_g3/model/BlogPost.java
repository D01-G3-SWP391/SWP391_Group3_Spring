package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Blog_post")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogPostId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(length = 500)
    private String summary;

    @Column(length = 500)
    private String featuredImageUrl;
    

    
    @Enumerated(EnumType.STRING)
    private BlogStatus status;

    
    // Thông tin thời gian
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    // Metadata cho SEO
    @Column(length = 500)
    private String metaDescription;

    
    public enum BlogStatus {
        DRAFT, PUBLISHED, ARCHIVED
    }
    
    // Constructors
    public BlogPost() {
        this.status = BlogStatus.DRAFT;
    }
    
    public BlogPost(Resource resource, String title, String content, String summary, 
                   String featuredImageUrl, Account author) {
        this();
        this.resource = resource;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.featuredImageUrl = featuredImageUrl;
    }
    
    // Getters and Setters
    public Long getBlogPostId() {
        return blogPostId;
    }
    
    public void setBlogPostId(Long blogPostId) {
        this.blogPostId = blogPostId;
    }
    
    public Resource getResource() {
        return resource;
    }
    
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }
    
    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }
    

    
    public BlogStatus getStatus() {
        return status;
    }
    
    public void setStatus(BlogStatus status) {
        this.status = status;
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
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public String getMetaDescription() {
        return metaDescription;
    }
    
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

}
