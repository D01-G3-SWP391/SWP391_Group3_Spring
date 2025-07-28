package com.example.swp391_d01_g3.dto;

public class JobPostPopularityDTO {
    private String jobTitle;
    private Long applicationCount;
    private String createdAt;
    private String approvalStatus;
    
    public JobPostPopularityDTO() {}
    
    public JobPostPopularityDTO(String jobTitle, Long applicationCount, String createdAt) {
        this.jobTitle = jobTitle;
        this.applicationCount = applicationCount;
        this.createdAt = createdAt;
    }
    
    public JobPostPopularityDTO(String jobTitle, Long applicationCount, String createdAt, String approvalStatus) {
        this.jobTitle = jobTitle;
        this.applicationCount = applicationCount;
        this.createdAt = createdAt;
        this.approvalStatus = approvalStatus;
    }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public Long getApplicationCount() { return applicationCount; }
    public void setApplicationCount(Long applicationCount) { this.applicationCount = applicationCount; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
} 