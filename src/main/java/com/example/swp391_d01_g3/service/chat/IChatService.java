package com.example.swp391_d01_g3.service.chat;

import com.example.swp391_d01_g3.dto.ChatMessageDTO;
import com.example.swp391_d01_g3.dto.ChatRoomDTO;
import com.example.swp391_d01_g3.dto.CreateChatRoomDTO;
import com.example.swp391_d01_g3.model.ChatMessage;
import com.example.swp391_d01_g3.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 💬 Chat Service Interface
 * 
 * Service quản lý tất cả chức năng chat
 */
public interface IChatService {
    
    // ========== CHAT ROOM MANAGEMENT ==========
    
    /**
     * Tạo hoặc tìm chat room giữa student và employer
     */
    ChatRoom findOrCreateChatRoom(CreateChatRoomDTO createChatRoomDTO);
    
    /**
     * Lấy danh sách chat room của user
     */
    List<ChatRoomDTO> getUserChatRooms(Long userId, String userType);
    
    /**
     * Lấy thông tin chat room cụ thể
     */
    Optional<ChatRoomDTO> getChatRoomInfo(Long chatRoomId, Long userId);
    
    /**
     * Kiểm tra user có quyền truy cập chat room không
     */
    boolean hasAccessToChatRoom(Long chatRoomId, Long userId, String userType);
    
    // ========== MESSAGE MANAGEMENT ==========
    
    /**
     * Gửi tin nhắn mới
     */
    ChatMessage sendMessage(ChatMessageDTO messageDTO);
    
    /**
     * Lấy lịch sử tin nhắn của chat room (có phân trang)
     */
    Page<ChatMessageDTO> getChatHistory(Long chatRoomId, Long userId, Pageable pageable);
    
    /**
     * Lấy tin nhắn của chat room (List format)
     */
    List<ChatMessageDTO> getChatMessages(Long chatRoomId, int page, int size);
    
    /**
     * Đánh dấu tin nhắn đã đọc
     */
    void markMessagesAsRead(Long chatRoomId, Long userId);
    
    /**
     * Tìm kiếm tin nhắn trong chat room
     */
    List<ChatMessageDTO> searchMessages(Long chatRoomId, String keyword, Long userId);
    
    /**
     * Lấy số tin nhắn chưa đọc của user
     */
    int getUnreadMessageCount(Long userId, String userType);
    
    /**
     * Lấy số tin nhắn chưa đọc trong chat room cụ thể
     */
    int getUnreadMessageCount(Long chatRoomId, Long userId);
    
    // ========== USER PRESENCE ==========
    
    /**
     * Cập nhật trạng thái online của user
     */
    void updateUserPresence(Long userId, String userType, boolean isOnline);
    
    /**
     * Kiểm tra user có online không
     */
    boolean isUserOnline(Long userId, String userType);
    
    /**
     * Cập nhật trạng thái typing
     */
    void updateTypingStatus(Long chatRoomId, Long userId, String userType, boolean isTyping);
    
    // FILE SHARING sẽ implement sau
    
    // ========== UTILITY METHODS ==========
    
    /**
     * Chuyển đổi ChatMessage entity thành DTO
     */
    ChatMessageDTO convertToDTO(ChatMessage message);
    
    /**
     * Chuyển đổi ChatRoom entity thành DTO
     */
    ChatRoomDTO convertToDTO(ChatRoom chatRoom, Long currentUserId, String currentUserType);
}
