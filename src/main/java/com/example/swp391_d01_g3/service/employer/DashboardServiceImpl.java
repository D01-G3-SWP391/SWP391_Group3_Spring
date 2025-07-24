package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.dto.*;
import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IEventFormRepository;
import com.example.swp391_d01_g3.repository.IEventRepository;
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
    
    @Autowired
    private IEventRepository eventRepository;
    
    @Autowired
    private IEventFormRepository eventFormRepository;

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
        
        // 7. Get event statistics
        setEventStatistics(dashboard, employerId);
        
        // 8. Get event participant data
        setEventParticipantStats(dashboard, employerId);
        
        // 9. Get recent events
        setRecentEvents(dashboard, employerId);
        
        return dashboard;
    }

    private void setOverviewStatistics(EmployerDashboardDTO dashboard, Integer employerId) {
        // Total job posts - chỉ đếm job posts ACTIVE
        Long totalJobPosts = jobPostRepository.countJobPostsByEmployerIdAndDisplayStatus(
                employerId, JobPost.DisplayStatus.ACTIVE);
        dashboard.setTotalJobPosts(totalJobPosts != null ? totalJobPosts : 0L);
        
        // Job posts by approval status
        Long approvedJobPosts = jobPostRepository.countJobPostsByEmployerIdAndApprovalStatus(
                employerId, JobPost.ApprovalStatus.APPROVED);
        
        // Job posts by display status
        Long activeJobPosts = jobPostRepository.countJobPostsByEmployerIdAndDisplayStatus(
                employerId, JobPost.DisplayStatus.ACTIVE);
        Long hiddenJobPosts = jobPostRepository.countJobPostsByEmployerIdAndDisplayStatus(
                employerId, JobPost.DisplayStatus.INACTIVE);
        
        dashboard.setActiveJobPosts(activeJobPosts != null ? activeJobPosts : 0L);
        dashboard.setHiddenJobPosts(hiddenJobPosts != null ? hiddenJobPosts : 0L);
        
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
                    return new JobPostPopularityDTO(jobTitle, applicationCount, formattedDate);
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
    
    private void setEventStatistics(EmployerDashboardDTO dashboard, Integer employerId) {
        // Lấy tất cả sự kiện của employer
        List<Event> allEvents = eventRepository.findByEmployer_EmployerIdOrderByEventDateDesc(employerId);
        
        // Lọc ra chỉ những sự kiện ACTIVE
        List<Event> activeEvents = allEvents.stream()
                .filter(event -> event.getEventStatus() == Event.EventStatus.ACTIVE)
                .collect(Collectors.toList());
        
        // Đếm tổng số sự kiện ACTIVE
        Long totalEvents = (long) activeEvents.size();
        dashboard.setTotalEvents(totalEvents);
        
        // Đếm số sự kiện sắp diễn ra (ACTIVE và chưa diễn ra)
        LocalDateTime now = LocalDateTime.now();
        Long upcomingEvents = activeEvents.stream()
                .filter(event -> event.getEventDate().isAfter(now))
                .count();
        dashboard.setUpcomingEvents(upcomingEvents);
        
        // Đếm số sự kiện đã qua (ACTIVE và đã diễn ra)
        Long pastEvents = activeEvents.stream()
                .filter(event -> event.getEventDate().isBefore(now))
                .count();
        dashboard.setPastEvents(pastEvents);
        
        // Đếm tổng số người tham gia (chỉ đếm cho sự kiện ACTIVE)
        Long totalParticipants = 0L;
        for (Event event : activeEvents) {
            Long participantsCount = eventFormRepository.countByEventEventId(event.getEventId());
            totalParticipants += participantsCount;
        }
        dashboard.setTotalEventParticipants(totalParticipants);
    }
    
    private void setEventParticipantStats(EmployerDashboardDTO dashboard, Integer employerId) {
        // Lấy tất cả sự kiện của employer
        List<Event> allEvents = eventRepository.findByEmployer_EmployerIdOrderByEventDateDesc(employerId);
        
        // Lọc ra chỉ những sự kiện ACTIVE
        List<Event> activeEvents = allEvents.stream()
                .filter(event -> event.getEventStatus() == Event.EventStatus.ACTIVE)
                .collect(Collectors.toList());
            
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        List<EventStatsDTO> eventStatsList = new ArrayList<>();
        
        // Lấy tối đa 5 sự kiện ACTIVE có nhiều người tham gia nhất
        activeEvents.stream()
                .sorted((e1, e2) -> {
                    Long count1 = eventFormRepository.countByEventEventId(e1.getEventId());
                    Long count2 = eventFormRepository.countByEventEventId(e2.getEventId());
                    return count2.compareTo(count1); // Sắp xếp giảm dần
                })
                .limit(5)
                .forEach(event -> {
                    Long participantCount = eventFormRepository.countByEventEventId(event.getEventId());
                    String status = event.getEventDate().isAfter(now) ? "UPCOMING" : "PAST";
                    String eventDate = event.getEventDate().format(formatter);
                    
                    eventStatsList.add(new EventStatsDTO(
                            event.getEventTitle(),
                            participantCount,
                            status,
                            eventDate
                    ));
                });
        
        dashboard.setEventStats(eventStatsList);
    }
    
    private void setRecentEvents(EmployerDashboardDTO dashboard, Integer employerId) {
        // Lấy tất cả sự kiện của employer
        List<Event> allEvents = eventRepository.findByEmployer_EmployerIdOrderByEventDateDesc(employerId);
        
        // Lọc ra chỉ những sự kiện ACTIVE
        List<Event> activeEvents = allEvents.stream()
                .filter(event -> event.getEventStatus() == Event.EventStatus.ACTIVE)
                .collect(Collectors.toList());
            
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        List<RecentEventDTO> recentEventsList = activeEvents.stream()
                .sorted(Comparator.comparing(Event::getEventDate).reversed()) // Sắp xếp theo ngày gần đây nhất
                .limit(5) // Lấy tối đa 5 sự kiện
                .map(event -> {
                    Long participantCount = eventFormRepository.countByEventEventId(event.getEventId());
                    String status = event.getEventDate().isAfter(now) ? "UPCOMING" : "PAST";
                    String eventDate = event.getEventDate().format(formatter);
                    
                    return new RecentEventDTO(
                            event.getEventTitle(),
                            eventDate,
                            event.getEventLocation(),
                            participantCount,
                            status
                    );
                })
                .collect(Collectors.toList());
        
        dashboard.setRecentEvents(recentEventsList);
    }
} 