package com.example.swp391_d01_g3.service.event;

import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.model.EventForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IEventService {

    /**
     * Tìm tất cả events đã được approve với phân trang
     */
    Page<Event> findByApprovalStatus(Event.ApprovalStatus status, Pageable pageable);

    /**
     * Tìm kiếm events theo title hoặc description
     */
    Page<Event> searchEvents(String keyword, Pageable pageable);

    /**
     * Tìm kiếm events theo keyword và status
     */
    Page<Event> searchEventsByKeywordAndStatus(String keyword, Event.ApprovalStatus status, Pageable pageable);

    /**
     * Lấy danh sách events sắp tới
     */
    List<Event> getUpcomingEvents(int limit);

    /**
     * Tìm event theo ID
     */
    Event findById(Integer eventId);

    /**
     * Lấy events liên quan (cùng employer hoặc cùng địa điểm)
     */
    List<Event> getRelatedEvents(Integer eventId, int limit);

    /**
     * Kiểm tra sinh viên đã đăng ký event chưa
     */
    boolean isStudentRegistered(Integer eventId, Integer studentId);

    /**
     * Đăng ký event
     */
    void registerEvent(EventForm eventForm);

    /**
     * Hủy đăng ký event
     */
    void unregisterEvent(Integer eventId, Integer studentId);

    /**
     * Đếm số lượng events đã approve
     */
    long countApprovedEvents();

    /**
     * Đếm events theo status
     */
    long countEventsByStatus(Event.ApprovalStatus status);

    /**
     * Đếm events theo job field
     */
    long countEventsByJobField(String jobFieldName);

    /**
     * Lưu event
     */
    Event save(Event event);

    /**
     * SỬA: Xóa event (với cascade delete EventForm)
     */
    void delete(Integer eventId);

    /**
     * Xóa event với cascade delete EventForm
     */
    void deleteEventWithRegistrations(Integer eventId);

    /**
     * Lấy tất cả events cho admin
     */
    Page<Event> findAll(Pageable pageable);

    /**
     * Approve event
     */
    void approveEvent(Integer eventId, Integer approvedById);

    /**
     * Reject event
     */
    void rejectEvent(Integer eventId, Integer rejectedById);

    /**
     * Lấy danh sách eventId mà student đã đăng ký
     */
    List<Integer> findRegisteredEventIdsByStudentId(Integer studentId);

    /**
     * Lấy events theo employer với phân trang
     */
    Page<Event> findByEmployer(Employer employer, Pageable pageable);

    Page<Event> getAllEvents(String keyword, Pageable pageable);
    Event getEventById(Long id);
    Page<Event> findByApprovalStatusAndEventDateAfterOrderByEventDateAsc(
            Event.ApprovalStatus status, LocalDateTime currentTime, Pageable pageable);
}