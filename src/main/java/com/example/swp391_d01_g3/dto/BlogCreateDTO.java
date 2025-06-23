package com.example.swp391_d01_g3.dto;

import com.example.swp391_d01_g3.model.BlogPost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class BlogCreateDTO {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 200, message = "Tiêu đề phải có từ 5 đến 200 ký tự")
    private String title;
    
    @NotBlank(message = "Tóm tắt không được để trống")
    @Size(min = 10, max = 500, message = "Tóm tắt phải có từ 10 đến 500 ký tự")
    private String summary;
    
    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 20, message = "Nội dung phải có ít nhất 20 ký tự")
    private String content;
    
    @NotNull(message = "Trạng thái không được để trống")
    private BlogPost.BlogStatus status;
    
    private Long resourceId;
    
    // Constructors
    public BlogCreateDTO() {}
    
    public BlogCreateDTO(String title, String summary, String content, BlogPost.BlogStatus status, Long resourceId) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.status = status;
        this.resourceId = resourceId;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public BlogPost.BlogStatus getStatus() {
        return status;
    }
    
    public void setStatus(BlogPost.BlogStatus status) {
        this.status = status;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
    
    // Convert to BlogPost entity
    public BlogPost toBlogPost() {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(this.title);
        blogPost.setSummary(this.summary);
        blogPost.setContent(this.content);
        blogPost.setStatus(this.status != null ? this.status : BlogPost.BlogStatus.DRAFT);
        return blogPost;
    }
} 