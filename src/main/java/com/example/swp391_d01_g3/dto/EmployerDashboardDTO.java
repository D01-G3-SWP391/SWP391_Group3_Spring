package com.example.swp391_d01_g3.dto;

import java.util.List;
import java.util.Map;

public class EmployerDashboardDTO {
    
    // Overview Statistics
    private Long totalJobPosts;
    private Long totalApplications;
    private Long pendingApplications;
    private Long interviewApplications;
    private Long acceptedApplications;
    private Long rejectedApplications;
    private Long activeJobPosts;
    private Long hiddenJobPosts;
    
    // Job Post Status Statistics
    private Long pendingJobPosts;
    private Long approvedJobPosts;
    private Long rejectedJobPosts;
    private Long inactiveJobPosts;

    
    // Chart Data
    private List<JobPostStatsDTO> jobPostStats;
    private List<ApplicationTrendDTO> applicationTrends;
    private Map<String, Long> applicationStatusChart;
    private Map<String, Long> jobPostStatusChart;
    private List<JobPostPopularityDTO> popularJobPosts;
//
    // Recent Activities
    private List<RecentApplicationDTO> recentApplications;
    
    // Constructors
    public EmployerDashboardDTO() {}
    
    public EmployerDashboardDTO(Long totalJobPosts, Long totalApplications, Long pendingApplications,
                               Long interviewApplications, Long acceptedApplications, Long rejectedApplications, 
                               Long activeJobPosts, Long hiddenJobPosts) {
        this.totalJobPosts = totalJobPosts;
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
        this.interviewApplications = interviewApplications;
        this.acceptedApplications = acceptedApplications;
        this.rejectedApplications = rejectedApplications;
        this.activeJobPosts = activeJobPosts;
        this.hiddenJobPosts = hiddenJobPosts;
    }
    
    // Getters and Setters
    public Long getTotalJobPosts() { return totalJobPosts; }
    public void setTotalJobPosts(Long totalJobPosts) { this.totalJobPosts = totalJobPosts; }
    
    public Long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(Long totalApplications) { this.totalApplications = totalApplications; }
    
    public Long getPendingApplications() { return pendingApplications; }
    public void setPendingApplications(Long pendingApplications) { this.pendingApplications = pendingApplications; }
    
    public Long getInterviewApplications() { return interviewApplications; }
    public void setInterviewApplications(Long interviewApplications) { this.interviewApplications = interviewApplications; }
    
    public Long getAcceptedApplications() { return acceptedApplications; }
    public void setAcceptedApplications(Long acceptedApplications) { this.acceptedApplications = acceptedApplications; }
    
    public Long getRejectedApplications() { return rejectedApplications; }
    public void setRejectedApplications(Long rejectedApplications) { this.rejectedApplications = rejectedApplications; }
    
    public Long getActiveJobPosts() { return activeJobPosts; }
    public void setActiveJobPosts(Long activeJobPosts) { this.activeJobPosts = activeJobPosts; }
    
    public Long getHiddenJobPosts() { return hiddenJobPosts; }
    public void setHiddenJobPosts(Long hiddenJobPosts) { this.hiddenJobPosts = hiddenJobPosts; }
    
    // Job Post Status getters and setters
    public Long getPendingJobPosts() { return pendingJobPosts; }
    public void setPendingJobPosts(Long pendingJobPosts) { this.pendingJobPosts = pendingJobPosts; }
    
    public Long getApprovedJobPosts() { return approvedJobPosts; }
    public void setApprovedJobPosts(Long approvedJobPosts) { this.approvedJobPosts = approvedJobPosts; }
    
    public Long getRejectedJobPosts() { return rejectedJobPosts; }
    public void setRejectedJobPosts(Long rejectedJobPosts) { this.rejectedJobPosts = rejectedJobPosts; }
    
    public Long getInactiveJobPosts() { return inactiveJobPosts; }
    public void setInactiveJobPosts(Long inactiveJobPosts) { this.inactiveJobPosts = inactiveJobPosts; }
    

    public List<JobPostStatsDTO> getJobPostStats() { return jobPostStats; }
    public void setJobPostStats(List<JobPostStatsDTO> jobPostStats) { this.jobPostStats = jobPostStats; }
    
    public List<ApplicationTrendDTO> getApplicationTrends() { return applicationTrends; }
    public void setApplicationTrends(List<ApplicationTrendDTO> applicationTrends) { this.applicationTrends = applicationTrends; }
    
    public Map<String, Long> getApplicationStatusChart() { return applicationStatusChart; }
    public void setApplicationStatusChart(Map<String, Long> applicationStatusChart) { this.applicationStatusChart = applicationStatusChart; }
    
    public Map<String, Long> getJobPostStatusChart() { return jobPostStatusChart; }
    public void setJobPostStatusChart(Map<String, Long> jobPostStatusChart) { this.jobPostStatusChart = jobPostStatusChart; }
    
    public List<JobPostPopularityDTO> getPopularJobPosts() { return popularJobPosts; }
    public void setPopularJobPosts(List<JobPostPopularityDTO> popularJobPosts) { this.popularJobPosts = popularJobPosts; }
    
    public List<RecentApplicationDTO> getRecentApplications() { return recentApplications; }
    public void setRecentApplications(List<RecentApplicationDTO> recentApplications) { this.recentApplications = recentApplications; }
} 