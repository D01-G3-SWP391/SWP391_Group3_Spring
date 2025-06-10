//package com.example.swp391_d01_g3.dto;
//
//import com.example.swp391_d01_g3.model.Resource;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//
//public class BlogPostCreateDTO {
//
//    @NotNull(message = "Resource ID không được để trống")
//    private Long resourceId;
//
//    @NotBlank(message = "Tiêu đề không được để trống")
//    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
//    private String title;
//
//    @NotBlank(message = "Nội dung không được để trống")
//    private String content;
//
//    @Size(max = 500, message = "Tóm tắt không được vượt quá 500 ký tự")
//    private String summary;
//
//    @Size(max = 500, message = "URL ảnh đại diện không được vượt quá 500 ký tự")
//    private String featuredImageUrl;
//
//    @Size(max = 500, message = "Meta description không được vượt quá 500 ký tự")
//    private String metaDescription;
//
//    // Resource type để tạo resource mới nếu cần
//    private Resource.ResourceType resourceType;
//
//    @Size(max = 255, message = "Tiêu đề resource không được vượt quá 255 ký tự")
//    private String resourceTitle;
//
//    private String resourceContent;
//
//    private String resourceImageUrl;
//
//    // Constructors
//    public BlogPostCreateDTO() {
//    }
//
//    public BlogPostCreateDTO(Long resourceId, String title, String content, String summary,
//                            String featuredImageUrl, String metaDescription) {
//        this.resourceId = resourceId;
//        this.title = title;
//        this.content = content;
//        this.summary = summary;
//        this.featuredImageUrl = featuredImageUrl;
//        this.metaDescription = metaDescription;
//    }
//
//    // Getters and Setters
//    public Long getResourceId() {
//        return resourceId;
//    }
//
//    public void setResourceId(Long resourceId) {
//        this.resourceId = resourceId;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public String getSummary() {
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//
//    public String getFeaturedImageUrl() {
//        return featuredImageUrl;
//    }
//
//    public void setFeaturedImageUrl(String featuredImageUrl) {
//        this.featuredImageUrl = featuredImageUrl;
//    }
//
//    public String getMetaDescription() {
//        return metaDescription;
//    }
//
//    public void setMetaDescription(String metaDescription) {
//        this.metaDescription = metaDescription;
//    }
//
//    public Resource.ResourceType getResourceType() {
//        return resourceType;
//    }
//
//    public void setResourceType(Resource.ResourceType resourceType) {
//        this.resourceType = resourceType;
//    }
//
//    public String getResourceTitle() {
//        return resourceTitle;
//    }
//
//    public void setResourceTitle(String resourceTitle) {
//        this.resourceTitle = resourceTitle;
//    }
//
//    public String getResourceContent() {
//        return resourceContent;
//    }
//
//    public void setResourceContent(String resourceContent) {
//        this.resourceContent = resourceContent;
//    }
//
//    public String getResourceImageUrl() {
//        return resourceImageUrl;
//    }
//
//    public void setResourceImageUrl(String resourceImageUrl) {
//        this.resourceImageUrl = resourceImageUrl;
//    }
//}