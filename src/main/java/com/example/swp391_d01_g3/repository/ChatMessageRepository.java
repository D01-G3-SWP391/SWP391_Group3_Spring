package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 📨 ChatMessage Repository
 * 
 * Repository để truy vấn dữ liệu ChatMessage
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Lấy messages của một chat room (có phân trang)
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "ORDER BY cm.sentAt ASC")
    Page<ChatMessage> findByRoomIdOrderBySentAtAsc(@Param("roomId") Long roomId, Pageable pageable);
    
    /**
     * Lấy messages của một chat room (không phân trang)
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "ORDER BY cm.sentAt ASC")
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(@Param("roomId") Long roomId);
    
    /**
     * Lấy tin nhắn mới nhất của một chat room
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "ORDER BY cm.sentAt DESC LIMIT 1")
    ChatMessage findLatestMessageByRoomId(@Param("roomId") Long roomId);
    
    /**
     * Đếm số tin nhắn chưa đọc trong một room cho một user cụ thể
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.sender.userId != :userId " +
           "AND cm.isRead = false")
    long countUnreadMessagesByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * Lấy tin nhắn chưa đọc trong một room cho một user cụ thể
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.sender.userId != :userId " +
           "AND cm.isRead = false " +
           "ORDER BY cm.sentAt ASC")
    List<ChatMessage> findUnreadMessagesByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * Đánh dấu tất cả tin nhắn trong room là đã đọc cho user cụ thể
     */
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true, cm.readAt = :readAt " +
           "WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.sender.userId != :userId " +
           "AND cm.isRead = false")
    int markAllMessagesAsReadInRoom(@Param("roomId") Long roomId, 
                                   @Param("userId") Long userId, 
                                   @Param("readAt") LocalDateTime readAt);
    
    /**
     * Lấy tin nhắn gần đây của user (across all rooms)
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.chatRoom.student.account.userId = :userId OR cm.chatRoom.employer.account.userId = :userId) " +
           "ORDER BY cm.sentAt DESC")
    Page<ChatMessage> findRecentMessagesByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Tìm tin nhắn theo nội dung (search)
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.messageContent LIKE %:searchTerm% " +
           "ORDER BY cm.sentAt DESC")
    List<ChatMessage> searchMessagesByContentInRoom(@Param("roomId") Long roomId, @Param("searchTerm") String searchTerm);
    
    /**
     * Lấy tin nhắn có file đính kèm trong một room
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.fileUrl IS NOT NULL " +
           "ORDER BY cm.sentAt DESC")
    List<ChatMessage> findMessagesWithFilesByRoomId(@Param("roomId") Long roomId);
    
    /**
     * Đếm tổng số tin nhắn trong một room
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId")
    long countMessagesByRoomId(@Param("roomId") Long roomId);
    
    /**
     * Lấy tin nhắn trong khoảng thời gian
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.sentAt BETWEEN :startDate AND :endDate " +
           "ORDER BY cm.sentAt ASC")
    List<ChatMessage> findMessagesByRoomIdAndDateRange(@Param("roomId") Long roomId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Xóa tin nhắn cũ hơn một khoảng thời gian
     */
    @Modifying
    @Query("DELETE FROM ChatMessage cm WHERE cm.sentAt < :beforeDate")
    int deleteMessagesOlderThan(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * Lấy statistics: tin nhắn theo ngày
     */
    @Query("SELECT DATE(cm.sentAt) as messageDate, COUNT(cm) as messageCount " +
           "FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "GROUP BY DATE(cm.sentAt) " +
           "ORDER BY messageDate DESC")
    List<Object[]> getMessageStatisticsByDate(@Param("roomId") Long roomId);
    
    /**
     * Lấy tin nhắn gần đây nhất để làm preview
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId IN :roomIds " +
           "AND cm.sentAt = (SELECT MAX(cm2.sentAt) FROM ChatMessage cm2 WHERE cm2.chatRoom.chatRoomId = cm.chatRoom.chatRoomId)")
    List<ChatMessage> findLatestMessagesForRooms(@Param("roomIds") List<Long> roomIds);
    
    /**
     * Đếm tin nhắn của một user trong room
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.sender.userId = :userId")
    long countMessagesByRoomIdAndSenderId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * Lấy tin nhắn cuối cùng của user trong room
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :roomId " +
           "AND cm.sender.userId = :userId " +
           "ORDER BY cm.sentAt DESC LIMIT 1")
    ChatMessage findLastMessageBySenderInRoom(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * Lấy tin nhắn của chat room với pagination thủ công
     */
    @Query(value = "SELECT * FROM chat_message WHERE chat_room_id = :chatRoomId " +
                   "ORDER BY sent_at ASC LIMIT :limit OFFSET :offset", 
           nativeQuery = true)
    List<ChatMessage> findByChatRoomIdWithPagination(@Param("chatRoomId") Long chatRoomId, 
                                                    @Param("offset") int offset, 
                                                    @Param("limit") int limit);
} 