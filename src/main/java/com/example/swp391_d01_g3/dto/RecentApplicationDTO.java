package com.example.swp391_d01_g3.dto;

public class RecentApplicationDTO {
    private String candidateName;
    private String jobTitle;
    private String appliedAt;
    private String status;
    
    public RecentApplicationDTO() {}
    
    public RecentApplicationDTO(String candidateName, String jobTitle, String appliedAt, String status) {
        this.candidateName = candidateName;
        this.jobTitle = jobTitle;
        this.appliedAt = appliedAt;
        this.status = status;
    }
    
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 