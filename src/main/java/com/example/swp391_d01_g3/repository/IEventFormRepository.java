package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.EventForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEventFormRepository extends JpaRepository<EventForm, Integer> {

    // Kiểm tra student đã đăng ký event chưa
    boolean existsByEventEventIdAndStudentStudentId(Integer eventId, Integer studentId);

    // Tìm registration của student cho event cụ thể
    Optional<EventForm> findByEventEventIdAndStudentStudentId(Integer eventId, Integer studentId);

    // THÊM: Tìm tất cả EventForm theo eventId (cần cho delete cascade)
    List<EventForm> findByEventEventId(Integer eventId);

    // Lấy tất cả registrations của student
    List<EventForm> findByStudentStudentIdOrderByRegistrationDateDesc(Integer studentId);

    // Lấy tất cả registrations của event
    List<EventForm> findByEventEventIdOrderByRegistrationDateAsc(Integer eventId);

    // Lấy registrations với phân trang cho student
    Page<EventForm> findByStudentStudentIdOrderByRegistrationDateDesc(Integer studentId, Pageable pageable);

    // Lấy registrations với phân trang cho event
    Page<EventForm> findByEventEventIdOrderByRegistrationDateAsc(Integer eventId, Pageable pageable);

    // Đếm số lượng registrations của event
    long countByEventEventId(Integer eventId);

    // Đếm số lượng registrations của student
    long countByStudentStudentId(Integer studentId);

    // THÊM: Xóa tất cả EventForm theo eventId (cho cascade delete)
    @Modifying
    @Query("DELETE FROM EventForm ef WHERE ef.event.eventId = :eventId")
    void deleteByEventEventId(@Param("eventId") Integer eventId);

    // Lấy events mà student đã đăng ký
    @Query("SELECT ef FROM EventForm ef " +
            "JOIN FETCH ef.event e " +
            "WHERE ef.student.studentId = :studentId " +
            "AND e.approvalStatus = 'APPROVED' " +
            "ORDER BY ef.registrationDate DESC")
    List<EventForm> findStudentRegisteredEvents(@Param("studentId") Integer studentId);

    // Lấy students đã đăng ký event
    @Query("SELECT ef FROM EventForm ef " +
            "JOIN FETCH ef.student s " +
            "WHERE ef.event.eventId = :eventId " +
            "ORDER BY ef.registrationDate ASC")
    List<EventForm> findEventRegisteredStudents(@Param("eventId") Integer eventId);

    // THÊM: Lấy EventForm với event và student details
    @Query("SELECT ef FROM EventForm ef " +
            "JOIN FETCH ef.event e " +
            "JOIN FETCH ef.student s " +
            "WHERE ef.event.eventId = :eventId")
    List<EventForm> findEventRegistrationsWithDetails(@Param("eventId") Integer eventId);
}
