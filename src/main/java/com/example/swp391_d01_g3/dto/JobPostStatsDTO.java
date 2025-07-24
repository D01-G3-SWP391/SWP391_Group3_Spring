package com.example.swp391_d01_g3.dto;

public class JobPostStatsDTO {
    private String jobTitle;
    private Long applicationCount;
    private String status;
    
    public JobPostStatsDTO() {}
    
    public JobPostStatsDTO(String jobTitle, Long applicationCount, String status) {
        this.jobTitle = jobTitle;
        this.applicationCount = applicationCount;
        this.status = status;
    }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public Long getApplicationCount() { return applicationCount; }
    public void setApplicationCount(Long applicationCount) { this.applicationCount = applicationCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 