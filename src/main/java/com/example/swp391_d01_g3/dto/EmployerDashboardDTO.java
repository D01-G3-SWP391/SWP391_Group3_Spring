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
    
    // Chart Data
    private List<JobPostStatsDTO> jobPostStats;
    private List<ApplicationTrendDTO> applicationTrends;
    private Map<String, Long> applicationStatusChart;
    private List<JobPostPopularityDTO> popularJobPosts;
    
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
    
    public List<JobPostStatsDTO> getJobPostStats() { return jobPostStats; }
    public void setJobPostStats(List<JobPostStatsDTO> jobPostStats) { this.jobPostStats = jobPostStats; }
    
    public List<ApplicationTrendDTO> getApplicationTrends() { return applicationTrends; }
    public void setApplicationTrends(List<ApplicationTrendDTO> applicationTrends) { this.applicationTrends = applicationTrends; }
    
    public Map<String, Long> getApplicationStatusChart() { return applicationStatusChart; }
    public void setApplicationStatusChart(Map<String, Long> applicationStatusChart) { this.applicationStatusChart = applicationStatusChart; }
    
    public List<JobPostPopularityDTO> getPopularJobPosts() { return popularJobPosts; }
    public void setPopularJobPosts(List<JobPostPopularityDTO> popularJobPosts) { this.popularJobPosts = popularJobPosts; }
    
    public List<RecentApplicationDTO> getRecentApplications() { return recentApplications; }
    public void setRecentApplications(List<RecentApplicationDTO> recentApplications) { this.recentApplications = recentApplications; }
} 