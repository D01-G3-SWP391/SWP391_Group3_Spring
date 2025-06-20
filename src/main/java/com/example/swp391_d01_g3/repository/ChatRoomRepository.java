package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🗄️ ChatRoom Repository
 * 
 * Repository để truy vấn dữ liệu ChatRoom
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    /**
     * Tìm chat room giữa student và employer cho một job post cụ thể
     * 🔧 Added ORDER BY và LIMIT để handle duplicates
     */
    @Query(value = "SELECT * FROM chat_room cr WHERE cr.student_id = :studentId " +
                   "AND cr.employer_id = :employerId " +
                   "AND cr.job_post_id = :jobPostId " +
                   "AND cr.is_active = true " +
                   "ORDER BY cr.chat_room_id ASC LIMIT 1", nativeQuery = true)
    Optional<ChatRoom> findByStudentAndEmployerAndJobPost(
            @Param("studentId") Long studentId,
            @Param("employerId") Long employerId,
            @Param("jobPostId") Long jobPostId
    );
    
    /**
     * Tìm chat room giữa student và employer (không cần job post)
     * 🔧 Added ORDER BY và LIMIT để handle duplicates
     */
    @Query(value = "SELECT * FROM chat_room cr WHERE cr.student_id = :studentId " +
                   "AND cr.employer_id = :employerId " +
                   "AND cr.job_post_id IS NULL " +
                   "AND cr.is_active = true " +
                   "ORDER BY cr.chat_room_id ASC LIMIT 1", nativeQuery = true)
    Optional<ChatRoom> findByStudentAndEmployer(
            @Param("studentId") Long studentId,
            @Param("employerId") Long employerId
    );
    
    /**
     * Lấy tất cả chat rooms của một student theo account userId
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.student.account.userId = :userId " +
           "AND cr.isActive = true ORDER BY cr.lastMessageAt DESC NULLS LAST, cr.createdAt DESC")
    List<ChatRoom> findByStudentIdOrderByLastMessageAtDesc(@Param("userId") Long userId);
    
    /**
     * Lấy tất cả chat rooms của một employer theo account userId
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.employer.account.userId = :userId " +
           "AND cr.isActive = true ORDER BY cr.lastMessageAt DESC NULLS LAST, cr.createdAt DESC")
    List<ChatRoom> findByEmployerIdOrderByLastMessageAtDesc(@Param("userId") Long userId);
    
    /**
     * Lấy chat rooms của user (student hoặc employer)
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "(cr.student.account.userId = :userId OR cr.employer.account.userId = :userId) " +
           "AND cr.isActive = true " +
           "ORDER BY cr.lastMessageAt DESC NULLS LAST, cr.createdAt DESC")
    List<ChatRoom> findByUserIdOrderByLastMessageAtDesc(@Param("userId") Long userId);
    
    /**
     * Kiểm tra user có access tới room này không
     */
    @Query("SELECT COUNT(cr) > 0 FROM ChatRoom cr WHERE cr.chatRoomId = :roomId " +
           "AND (cr.student.account.userId = :userId OR cr.employer.account.userId = :userId) " +
           "AND cr.isActive = true")
    boolean existsByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * Lấy participant còn lại trong chat room
     */
    @Query("SELECT CASE " +
           "WHEN cr.student.account.userId = :currentUserId THEN cr.employer.account.userId " +
           "ELSE cr.student.account.userId " +
           "END FROM ChatRoom cr WHERE cr.chatRoomId = :roomId")
    Optional<Long> findOtherParticipantId(@Param("roomId") Long roomId, @Param("currentUserId") Long currentUserId);
    
    /**
     * Đếm số chat rooms chưa đọc của user
     */
    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE " +
           "((cr.student.account.userId = :userId AND cr.studentUnreadCount > 0) OR " +
           "(cr.employer.account.userId = :userId AND cr.employerUnreadCount > 0)) " +
           "AND cr.isActive = true")
    long countUnreadRoomsByUserId(@Param("userId") Long userId);
    
    /**
     * Lấy chat rooms với tin nhắn chưa đọc
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "((cr.student.account.userId = :userId AND cr.studentUnreadCount > 0) OR " +
           "(cr.employer.account.userId = :userId AND cr.employerUnreadCount > 0)) " +
           "AND cr.isActive = true " +
           "ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findUnreadRoomsByUserId(@Param("userId") Long userId);
    
    /**
     * Tìm chat rooms theo job post
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.jobPost.jobPostId = :jobPostId " +
           "AND cr.isActive = true ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByJobPostId(@Param("jobPostId") Long jobPostId);
    
    /**
     * Xóa mềm chat room (set isActive = false)
     */
    @Query("UPDATE ChatRoom cr SET cr.isActive = false WHERE cr.chatRoomId = :roomId")
    void softDeleteByRoomId(@Param("roomId") Long roomId);
    
    /**
     * Lấy số lượng chat rooms của user (for statistics)
     */
    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE " +
           "(cr.student.account.userId = :userId OR cr.employer.account.userId = :userId) " +
           "AND cr.isActive = true")
    long countByUserId(@Param("userId") Long userId);
} 