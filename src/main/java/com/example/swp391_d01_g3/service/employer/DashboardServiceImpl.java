package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.dto.*;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobApplicationRepository;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private IJobApplicationRepository jobApplicationRepository;

    @Autowired
    private IJobPostRepository jobPostRepository;

    @Override
    public EmployerDashboardDTO getEmployerDashboard(Integer employerId) {
        EmployerDashboardDTO dashboard = new EmployerDashboardDTO();
        
        // 1. Get overview statistics
        setOverviewStatistics(dashboard, employerId);
        
        // 2. Get job post statistics
        setJobPostStatistics(dashboard, employerId);
        
        // 3. Get application trends
        setApplicationTrends(dashboard, employerId);
        
        // 4. Get application status distribution
        setApplicationStatusDistribution(dashboard, employerId);
        
        // 5. Get popular job posts
        setPopularJobPosts(dashboard, employerId);
        
        // 6. Get recent applications
        setRecentApplications(dashboard, employerId);
        

        
        return dashboard;
    }

    private void setOverviewStatistics(EmployerDashboardDTO dashboard, Integer employerId) {
        // Total job posts - chỉ đếm job posts ACTIVE
        Long totalJobPosts = jobPostRepository.countJobPostsByEmployerIdAndDisplayStatus(
                employerId, JobPost.DisplayStatus.ACTIVE);
        dashboard.setTotalJobPosts(totalJobPosts != null ? totalJobPosts : 0L);
        
        // Job posts by approval status
        Long pendingJobPosts = jobPostRepository.countJobPostsByEmployerIdAndApprovalStatus(
                employerId, JobPost.ApprovalStatus.PENDING);
        Long approvedJobPosts = jobPostRepository.countJobPostsByEmployerIdAndApprovalStatus(
                employerId, JobPost.ApprovalStatus.APPROVED);
        Long rejectedJobPosts = jobPostRepository.countJobPostsByEmployerIdAndApprovalStatus(
                employerId, JobPost.ApprovalStatus.REJECTED);
        
        // Job posts by display status
        Long activeJobPosts = jobPostRepository.countJobPostsByEmployerIdAndDisplayStatus(
                employerId, JobPost.DisplayStatus.ACTIVE);
        Long inactiveJobPosts = jobPostRepository.countJobPostsByEmployerIdAndDisplayStatus(
                employerId, JobPost.DisplayStatus.INACTIVE);
        
        // Set JobPost status statistics
        dashboard.setPendingJobPosts(pendingJobPosts != null ? pendingJobPosts : 0L);
        dashboard.setApprovedJobPosts(approvedJobPosts != null ? approvedJobPosts : 0L);
        dashboard.setRejectedJobPosts(rejectedJobPosts != null ? rejectedJobPosts : 0L);
        dashboard.setActiveJobPosts(activeJobPosts != null ? activeJobPosts : 0L);
        dashboard.setInactiveJobPosts(inactiveJobPosts != null ? inactiveJobPosts : 0L);
        
        // Set JobPost status chart data
        Map<String, Long> jobPostStatusChart = new HashMap<>();
        jobPostStatusChart.put("PENDING", pendingJobPosts != null ? pendingJobPosts : 0L);
        jobPostStatusChart.put("APPROVED", approvedJobPosts != null ? approvedJobPosts : 0L);
        jobPostStatusChart.put("REJECTED", rejectedJobPosts != null ? rejectedJobPosts : 0L);
        jobPostStatusChart.put("ACTIVE", activeJobPosts != null ? activeJobPosts : 0L);
        jobPostStatusChart.put("INACTIVE", inactiveJobPosts != null ? inactiveJobPosts : 0L);
        dashboard.setJobPostStatusChart(jobPostStatusChart);
        
        // Application statistics
        Long totalApplications = jobApplicationRepository.countApplicationsByEmployerId(employerId);
        Long submittedApplications = jobApplicationRepository.countApplicationsByEmployerIdAndStatus(
                employerId, JobApplication.ApplicationStatus.SUBMITTED);
        Long interviewApplications = jobApplicationRepository.countApplicationsByEmployerIdAndStatus(
                employerId, JobApplication.ApplicationStatus.INTERVIEW);
        Long acceptedApplications = jobApplicationRepository.countApplicationsByEmployerIdAndStatus(
                employerId, JobApplication.ApplicationStatus.ACCEPTED);
        Long rejectedApplications = jobApplicationRepository.countApplicationsByEmployerIdAndStatus(
                employerId, JobApplication.ApplicationStatus.REJECTED);
        
        dashboard.setTotalApplications(totalApplications != null ? totalApplications : 0L);
        dashboard.setPendingApplications(submittedApplications != null ? submittedApplications : 0L);
        dashboard.setInterviewApplications(interviewApplications != null ? interviewApplications : 0L);
        dashboard.setAcceptedApplications(acceptedApplications != null ? acceptedApplications : 0L);
        dashboard.setRejectedApplications(rejectedApplications != null ? rejectedApplications : 0L);
    }

    private void setJobPostStatistics(EmployerDashboardDTO dashboard, Integer employerId) {
        List<Object[]> jobPostStats = jobApplicationRepository.getApplicationCountByJobPost(employerId);
        
        List<JobPostStatsDTO> jobPostStatsList = jobPostStats.stream()
                .map(row -> new JobPostStatsDTO(
                        (String) row[0],  // jobTitle
                        (Long) row[1],    // applicationCount
                        "ACTIVE"          // status - can be enhanced later
                ))
                .collect(Collectors.toList());
        
        dashboard.setJobPostStats(jobPostStatsList);
    }

    private void setApplicationTrends(EmployerDashboardDTO dashboard, Integer employerId) {
        // Get trends for last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> trends = jobApplicationRepository.getApplicationTrendsByEmployerId(employerId, thirtyDaysAgo);
        
        // Create a map of date -> count from database results
        Map<LocalDate, Long> dbTrends = trends.stream()
                .collect(Collectors.toMap(
                        row -> ((Date) row[0]).toLocalDate(),
                        row -> (Long) row[1]
                ));
        
        // Generate full 30 days with 0 count for missing dates
        List<ApplicationTrendDTO> trendList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().minusDays(29); // Start from 29 days ago to today (30 days total)
        
        for (int i = 0; i < 30; i++) {
            LocalDate date = currentDate.plusDays(i);
            Long count = dbTrends.getOrDefault(date, 0L); // Use 0 if no applications on that date
            trendList.add(new ApplicationTrendDTO(date, count));
        }
        
        dashboard.setApplicationTrends(trendList);
    }

    private void setApplicationStatusDistribution(EmployerDashboardDTO dashboard, Integer employerId) {
        List<Object[]> statusDistribution = jobApplicationRepository.getApplicationStatusDistribution(employerId);
        
        Map<String, Long> statusMap = statusDistribution.stream()
                .collect(Collectors.toMap(
                        row -> {
                            JobApplication.ApplicationStatus status = (JobApplication.ApplicationStatus) row[0];
                            // Map SUBMITTED to PENDING for UI display
                            if (status == JobApplication.ApplicationStatus.SUBMITTED) {
                                return "PENDING";
                            }
                            return status.name();
                        },
                        row -> (Long) row[1]
                ));
        
        dashboard.setApplicationStatusChart(statusMap);
    }

    private void setPopularJobPosts(EmployerDashboardDTO dashboard, Integer employerId) {
        Pageable topFive = PageRequest.of(0, 5);
        List<Object[]> popularJobs = jobApplicationRepository.getTopJobPostsByApplicationCount(employerId, topFive);
        
        List<JobPostPopularityDTO> popularJobsList = popularJobs.stream()
                .map(row -> {
                    String jobTitle = (String) row[0];
                    Long applicationCount = (Long) row[1];
                    Date createdDate = (Date) row[2];
                    String formattedDate = createdDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    JobPost.ApprovalStatus approvalStatus = (JobPost.ApprovalStatus) row[3];
                    return new JobPostPopularityDTO(jobTitle, applicationCount, formattedDate, approvalStatus.name());
                })
                .collect(Collectors.toList());
        
        dashboard.setPopularJobPosts(popularJobsList);
    }

    private void setRecentApplications(EmployerDashboardDTO dashboard, Integer employerId) {
        Pageable recentFive = PageRequest.of(0, 10);
        List<Object[]> recentApps = jobApplicationRepository.getRecentApplicationsByEmployerId(employerId, recentFive);
        
        List<RecentApplicationDTO> recentAppsList = recentApps.stream()
                .map(row -> {
                    String candidateName = (String) row[0];
                    String jobTitle = (String) row[1];
                    LocalDateTime appliedAt = (LocalDateTime) row[2];
                    JobApplication.ApplicationStatus status = (JobApplication.ApplicationStatus) row[3];
                    
                    String formattedDate = appliedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    return new RecentApplicationDTO(candidateName, jobTitle, formattedDate, status.name());
                })
                .collect(Collectors.toList());
        
        dashboard.setRecentApplications(recentAppsList);
    }
    

} 