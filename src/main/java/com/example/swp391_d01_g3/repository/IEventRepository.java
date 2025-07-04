package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.model.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IEventRepository extends JpaRepository<Event, Integer> {
    
    // Tìm events theo approval status với phân trang
    Page<Event> findByApprovalStatusOrderByEventDateAsc(Event.ApprovalStatus status, Pageable pageable);

    // THÊM: Order by created date for admin management
    Page<Event> findByApprovalStatusOrderByCreatedAtDesc(Event.ApprovalStatus status, Pageable pageable);

    // Tìm kiếm events theo title hoặc description
    Page<Event> findByApprovalStatusAndEventTitleContainingIgnoreCaseOrEventDescriptionContainingIgnoreCase(
            Event.ApprovalStatus status, String titleKeyword, String descKeyword, Pageable pageable);

    // Tìm kiếm events theo title hoặc description với filter active
    Page<Event> findByApprovalStatusAndEventStatusAndEventTitleContainingIgnoreCaseOrEventDescriptionContainingIgnoreCase(
            Event.ApprovalStatus approvalStatus, Event.EventStatus eventStatus, String titleKeyword, String descKeyword, Pageable pageable);


    // Lấy upcoming events
    Page<Event> findByApprovalStatusAndEventDateAfterOrderByEventDateAsc(
            Event.ApprovalStatus status, LocalDateTime currentTime, Pageable pageable);

    // Tìm events liên quan
    @Query("SELECT e FROM Event e WHERE e.eventId != :eventId " +
            "AND (e.employer.employerId = :employerId OR e.eventLocation LIKE %:location%) " +
            "AND e.approvalStatus = :status " +
            "ORDER BY e.eventDate ASC")
    List<Event> findRelatedEvents(@Param("eventId") Integer eventId,
                                  @Param("employerId") Integer employerId,
                                  @Param("location") String location,
                                  @Param("status") Event.ApprovalStatus status,
                                  Pageable pageable);

    // Tìm events liên quan (chỉ active)
    @Query("SELECT e FROM Event e WHERE e.eventId != :eventId " +
           "AND (e.employer.employerId = :employerId OR e.eventLocation LIKE %:location%) " +
           "AND e.approvalStatus = :approvalStatus " +
           "AND e.eventStatus = :eventStatus " +
           "ORDER BY e.eventDate ASC")
    List<Event> findRelatedActiveEvents(@Param("eventId") Integer eventId,
                                        @Param("employerId") Integer employerId,
                                        @Param("location") String location,
                                        @Param("approvalStatus") Event.ApprovalStatus approvalStatus,
                                        @Param("eventStatus") Event.EventStatus eventStatus,
                                        Pageable pageable);


    // Đếm events theo approval status
    long countByApprovalStatus(Event.ApprovalStatus status);


    // Đếm events theo approval status và event status
    long countByApprovalStatusAndEventStatus(Event.ApprovalStatus approvalStatus, Event.EventStatus eventStatus);

    // Đếm events theo job field (thông qua employer)
    @Query("SELECT COUNT(e) FROM Event e " +
            "JOIN e.employer emp " +
            "JOIN emp.jobField jf " +
            "WHERE jf.jobFieldName = :jobFieldName " +
            "AND e.approvalStatus = :status")
    long countEventsByJobField(@Param("jobFieldName") String jobFieldName,
                               @Param("status") Event.ApprovalStatus status);

    // Đếm events theo job field và event status
    @Query("SELECT COUNT(e) FROM Event e " +
           "JOIN e.employer emp " +
           "JOIN emp.jobField jf " +
           "WHERE jf.jobFieldName = :jobFieldName " +
           "AND e.approvalStatus = :approvalStatus " +
           "AND e.eventStatus = :eventStatus")
    long countEventsByJobFieldAndEventStatus(@Param("jobFieldName") String jobFieldName,
                                             @Param("approvalStatus") Event.ApprovalStatus approvalStatus,
                                             @Param("eventStatus") Event.EventStatus eventStatus);


    // Lấy all events với phân trang cho admin
    Page<Event> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Tìm events theo employer
    List<Event> findByEmployer_EmployerIdOrderByEventDateDesc(Integer employerId);

    // Tìm events theo employer với phân trang
    Page<Event> findByEmployer_EmployerIdOrderByEventDateDesc(Integer employerId, Pageable pageable);

    // Lấy events theo status và trong khoảng thời gian
    @Query("SELECT e FROM Event e WHERE e.approvalStatus = :status " +
            "AND e.eventDate BETWEEN :startDate AND :endDate " +
            "ORDER BY e.eventDate ASC")
    List<Event> findEventsByStatusAndDateRange(@Param("status") Event.ApprovalStatus status,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // ADMIN SEARCH METHODS - Đã có sẵn và hoàn chỉnh
    @Query("SELECT e FROM Event e WHERE " +
            "(e.eventTitle LIKE %:keyword% OR e.eventDescription LIKE %:keyword% OR " +
            "e.eventLocation LIKE %:keyword% OR e.employer.companyName LIKE %:keyword%) " +
            "ORDER BY e.createdAt DESC")
    Page<Event> searchEventsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.approvalStatus = :status AND " +
            "(e.eventTitle LIKE %:keyword% OR e.eventDescription LIKE %:keyword% OR " +
            "e.eventLocation LIKE %:keyword% OR e.employer.companyName LIKE %:keyword%) " +
            "ORDER BY e.createdAt DESC")
    Page<Event> searchEventsByKeywordAndStatus(@Param("keyword") String keyword,
                                               @Param("status") Event.ApprovalStatus status,
                                               Pageable pageable);

    // Find events that can be deleted (cancelled or past events)
    @Query("SELECT e FROM Event e WHERE " +
            "e.eventStatus = :cancelledStatus OR e.eventDate < :cutoffDate " +
            "ORDER BY e.eventDate DESC")
    Page<Event> findDeletableEvents(@Param("cancelledStatus") Event.EventStatus cancelledStatus,
                                    @Param("cutoffDate") LocalDateTime cutoffDate,
                                    Pageable pageable);

    // EMPLOYER METHODS
    Page<Event> findByEmployer(Employer employer, Pageable pageable);

    Page<Event> findByEventTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // THÊM: Additional useful methods for admin
    @Query("SELECT e FROM Event e WHERE e.approvalStatus = :status " +
            "AND e.eventDate > :currentDate " +
            "ORDER BY e.eventDate ASC")
    Page<Event> findUpcomingEventsByStatus(@Param("status") Event.ApprovalStatus status,
                                           @Param("currentDate") LocalDateTime currentDate,
                                           Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.approvalStatus = :status " +
            "AND e.eventDate < :currentDate " +
            "ORDER BY e.eventDate DESC")
    Page<Event> findPastEventsByStatus(@Param("status") Event.ApprovalStatus status,
                                       @Param("currentDate") LocalDateTime currentDate,
                                       Pageable pageable);

    // THÊM: Statistics methods
    @Query("SELECT COUNT(e) FROM Event e WHERE e.approvalStatus = :status " +
            "AND e.eventDate > :currentDate")
    long countUpcomingEventsByStatus(@Param("status") Event.ApprovalStatus status,
                                     @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.approvalStatus = :status " +
            "AND e.eventDate < :currentDate")
    long countPastEventsByStatus(@Param("status") Event.ApprovalStatus status,
                                 @Param("currentDate") LocalDateTime currentDate);

    // THÊM: Find events by multiple criteria
    @Query("SELECT e FROM Event e WHERE " +
            "(:status IS NULL OR e.approvalStatus = :status) AND " +
            "(:keyword IS NULL OR e.eventTitle LIKE %:keyword% OR e.eventDescription LIKE %:keyword% OR " +
            "e.eventLocation LIKE %:keyword% OR e.employer.companyName LIKE %:keyword%) AND " +
            "(:startDate IS NULL OR e.eventDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.eventDate <= :endDate) " +
            "ORDER BY e.createdAt DESC")
    Page<Event> findEventsByCriteria(@Param("status") Event.ApprovalStatus status,
                                     @Param("keyword") String keyword,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);

    // Tìm events đã approve, active và chưa quá hạn
    @Query("SELECT e FROM Event e WHERE e.approvalStatus = 'APPROVED' " +
           "AND e.eventStatus = 'ACTIVE' " +
           "AND e.eventDate > :currentTime " +
           "ORDER BY e.eventDate ASC")
    Page<Event> findActiveApprovedEvents(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

}