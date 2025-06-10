package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Blog_image")
public class BlogImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_post_id", nullable = false)
    private BlogPost blogPost;
    
    @Column(nullable = false, length = 500)
    private String imageUrl;
    
    @Column(length = 255)
    private String altText; // Text thay thế cho ảnh (SEO)
    
    @Column(length = 500)
    private String caption; // Chú thích ảnh
    
    @Column(name = "display_order")
    private Integer displayOrder; // Thứ tự hiển thị ảnh
    
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    
    public enum ImageType {
        FEATURED,   // Ảnh đại diện chính của bài viết
        INLINE,     // Ảnh chèn trong nội dung
        BANNER,     // Ảnh banner header
    }
    
    // Constructors
    public BlogImage() {
    }
    
    public BlogImage(BlogPost blogPost, String imageUrl, String altText, String caption, 
                    Integer displayOrder, ImageType imageType) {
        this.blogPost = blogPost;
        this.imageUrl = imageUrl;
        this.altText = altText;
        this.caption = caption;
        this.displayOrder = displayOrder;
        this.imageType = imageType;
    }
    
    // Getters and Setters
    public Long getImageId() {
        return imageId;
    }
    
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
    
    public BlogPost getBlogPost() {
        return blogPost;
    }
    
    public void setBlogPost(BlogPost blogPost) {
        this.blogPost = blogPost;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getAltText() {
        return altText;
    }
    
    public void setAltText(String altText) {
        this.altText = altText;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public ImageType getImageType() {
        return imageType;
    }
    
    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

} 