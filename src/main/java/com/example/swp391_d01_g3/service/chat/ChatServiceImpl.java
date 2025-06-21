package com.example.swp391_d01_g3.service.chat;

import com.example.swp391_d01_g3.dto.ChatMessageDTO;
import com.example.swp391_d01_g3.dto.ChatRoomDTO;
import com.example.swp391_d01_g3.dto.CreateChatRoomDTO;
import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Loại bỏ file handling imports vì không dùng
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 💬 Chat Service Implementation
 * 
 * Service quản lý tất cả chức năng chat
 */
@Service
@Slf4j
@Transactional
public class ChatServiceImpl implements IChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private IStudentRepository studentRepository;
    
    @Autowired
    private IEmployerRepository employerRepository;
    
    @Autowired
    private IJobPostRepository jobPostRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final Map<String, Boolean> userPresence = new HashMap<>();
    private final Map<String, Boolean> typingStatus = new HashMap<>();

    // ========== CHAT ROOM MANAGEMENT ==========

    @Override
    public ChatRoom findOrCreateChatRoom(CreateChatRoomDTO createChatRoomDTO) {
        log.info("Finding or creating chat room for student userId {} and employer userId {}", 
                createChatRoomDTO.getStudentId(), createChatRoomDTO.getEmployerId());
        
        // Lấy entities từ userId trước - sử dụng method mới để handle duplicates
        Student student = studentRepository.findFirstByAccount_UserId(createChatRoomDTO.getStudentId().intValue());
        Employer employer = employerRepository.findFirstByAccount_UserId(createChatRoomDTO.getEmployerId().intValue());
        
        if (student == null || employer == null) {
            throw new RuntimeException("Student hoặc Employer không tồn tại");
        }
        
        // Sử dụng studentId và employerId thực sự để tìm kiếm
        Long studentId = student.getStudentId().longValue();
        Long employerId = employer.getEmployerId().longValue();
        
        // Kiểm tra chat room đã tồn tại
        Optional<ChatRoom> existingRoom;
        if (createChatRoomDTO.hasJobContext()) {
            existingRoom = chatRoomRepository.findByStudentAndEmployerAndJobPost(
                studentId, 
                employerId, 
                createChatRoomDTO.getJobPostId()
            );
        } else {
            existingRoom = chatRoomRepository.findByStudentAndEmployer(
                studentId, 
                employerId
            );
        }
        
        if (existingRoom.isPresent()) {
            log.info("Found existing chat room: {}", existingRoom.get().getChatRoomId());
            return existingRoom.get();
        }
        
        // Tạo chat room mới
        ChatRoom newRoom = new ChatRoom();
        newRoom.setStudent(student);
        newRoom.setEmployer(employer);
        
        if (createChatRoomDTO.hasJobContext()) {
            Optional<JobPost> jobPost = jobPostRepository.findById(createChatRoomDTO.getJobPostId().intValue());
            jobPost.ifPresent(newRoom::setJobPost);
        }
        
        newRoom.setCreatedAt(LocalDateTime.now());
        newRoom.setStudentUnreadCount(0);
        newRoom.setEmployerUnreadCount(0);
        
        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        log.info("Created new chat room: {}", savedRoom.getChatRoomId());
        
        return savedRoom;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getUserChatRooms(Long userId, String userType) {
        log.info("Getting chat rooms for user {} with type {}", userId, userType);
        
        List<ChatRoom> chatRooms;
        if ("STUDENT".equals(userType)) {
            chatRooms = chatRoomRepository.findByStudentIdOrderByLastMessageAtDesc(userId);
        } else if ("EMPLOYER".equals(userType)) {
            chatRooms = chatRoomRepository.findByEmployerIdOrderByLastMessageAtDesc(userId);
        } else {
            return Collections.emptyList();
        }
        
        return chatRooms.stream()
                .map(room -> convertToDTO(room, userId, userType))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatRoomDTO> getChatRoomInfo(Long chatRoomId, Long userId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isEmpty()) {
            return Optional.empty();
        }
        
        ChatRoom room = chatRoom.get();
        String userType = room.getStudent().getAccount().getUserId().equals(userId.intValue()) ? "STUDENT" : "EMPLOYER";
        
        return Optional.of(convertToDTO(room, userId, userType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessToChatRoom(Long chatRoomId, Long userId, String userType) {
        return chatRoomRepository.existsByRoomIdAndUserId(chatRoomId, userId);
    }

    // ========== MESSAGE MANAGEMENT ==========

    @Override
    public ChatMessage sendMessage(ChatMessageDTO messageDTO) {
        log.info("Sending message from {} to chat room {}", messageDTO.getSenderId(), messageDTO.getChatRoomId());
        
        // Kiểm tra quyền truy cập
        if (!hasAccessToChatRoom(messageDTO.getChatRoomId(), messageDTO.getSenderId(), messageDTO.getSenderType())) {
            throw new RuntimeException("User không có quyền gửi tin nhắn vào chat room này");
        }
        
        // Lấy chat room và sender
        ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId()).orElse(null);
        if (chatRoom == null) {
            throw new RuntimeException("Chat room không tồn tại");
        }
        
        // Xác định sender account
        Account sender = null;
        if ("STUDENT".equals(messageDTO.getSenderType())) {
            sender = chatRoom.getStudent().getAccount();
        } else if ("EMPLOYER".equals(messageDTO.getSenderType())) {
            sender = chatRoom.getEmployer().getAccount();
        }
        
        if (sender == null) {
            throw new RuntimeException("Không tìm thấy sender");
        }
        
        // Tạo message entity
        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setSenderType(ChatMessage.SenderType.valueOf(messageDTO.getSenderType()));
        message.setMessageContent(messageDTO.getContent());
        message.setFileUrl(messageDTO.getAttachmentUrl());
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false);
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // Cập nhật chat room
        updateChatRoomAfterMessage(messageDTO.getChatRoomId(), messageDTO.getSenderType());
        
        // Gửi real-time notification
        ChatMessageDTO responseDTO = convertToDTO(savedMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + messageDTO.getChatRoomId(), responseDTO);
        
//        log.info("Message sent successfully: {}", savedMessage.getId());
        return savedMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getChatHistory(Long chatRoomId, Long userId, Pageable pageable) {
        log.info("Getting chat history for room {} by user {}", chatRoomId, userId);
        
        // Kiểm tra quyền truy cập
        String userType = determineUserType(chatRoomId, userId);
        if (!hasAccessToChatRoom(chatRoomId, userId, userType)) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        
        Page<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderBySentAtAsc(chatRoomId, pageable);
        
        List<ChatMessageDTO> messageDTOs = messages.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(messageDTOs, pageable, messages.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatMessages(Long chatRoomId, int page, int size) {
        log.info("Getting messages for chat room {} (page: {}, size: {})", chatRoomId, page, size);
        
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdWithPagination(
            chatRoomId, page * size, size);
        
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markMessagesAsRead(Long chatRoomId, Long userId) {
        log.info("Marking messages as read for chat room {} by user {}", chatRoomId, userId);
        
        String userType = determineUserType(chatRoomId, userId);
        
        // Đánh dấu tin nhắn đã đọc
        chatMessageRepository.markAllMessagesAsReadInRoom(chatRoomId, userId, LocalDateTime.now());
        
        // Reset unread count
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (chatRoom != null) {
            if ("STUDENT".equals(userType)) {
                chatRoom.setStudentUnreadCount(0);
            } else {
                chatRoom.setEmployerUnreadCount(0);
            }
            chatRoomRepository.save(chatRoom);
        }
        
        // Gửi read receipt notification
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId + "/read", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> searchMessages(Long chatRoomId, String keyword, Long userId) {
        log.info("Searching messages in room {} with keyword: {}", chatRoomId, keyword);
        
        String userType = determineUserType(chatRoomId, userId);
        if (!hasAccessToChatRoom(chatRoomId, userId, userType)) {
            return Collections.emptyList();
        }
        
        List<ChatMessage> messages = chatMessageRepository.searchMessagesByContentInRoom(chatRoomId, keyword);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadMessageCount(Long userId, String userType) {
        return (int) chatRoomRepository.countUnreadRoomsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadMessageCount(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (chatRoom == null) return 0;
        
        if (chatRoom.getStudent().getAccount().getUserId().equals(userId.intValue())) {
            return chatRoom.getStudentUnreadCount();
        } else if (chatRoom.getEmployer().getAccount().getUserId().equals(userId.intValue())) {
            return chatRoom.getEmployerUnreadCount();
        }
        
        return 0;
    }

    // ========== USER PRESENCE ==========

    @Override
    public void updateUserPresence(Long userId, String userType, boolean isOnline) {
        String key = userType + "_" + userId;
        userPresence.put(key, isOnline);
        
        // Broadcast presence update
        Map<String, Object> presenceUpdate = new HashMap<>();
        presenceUpdate.put("userId", userId);
        presenceUpdate.put("userType", userType);
        presenceUpdate.put("isOnline", isOnline);
        
        messagingTemplate.convertAndSend("/topic/presence", presenceUpdate);
        log.info("Updated presence for user {} ({}): {}", userId, userType, isOnline);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserOnline(Long userId, String userType) {
        String key = userType + "_" + userId;
        return userPresence.getOrDefault(key, false);
    }

    @Override
    public void updateTypingStatus(Long chatRoomId, Long userId, String userType, boolean isTyping) {
        String key = chatRoomId + "_" + userType + "_" + userId;
        typingStatus.put(key, isTyping);
        
        // Broadcast typing status
        Map<String, Object> typingUpdate = new HashMap<>();
        typingUpdate.put("chatRoomId", chatRoomId);
        typingUpdate.put("userId", userId);
        typingUpdate.put("userType", userType);
        typingUpdate.put("isTyping", isTyping);
        
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId + "/typing", typingUpdate);
    }

    // FILE SHARING sẽ được implement sau khi có CloudinaryService

    // ========== UTILITY METHODS ==========

    @Override
    public ChatMessageDTO convertToDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getMessageId());
        dto.setChatRoomId(message.getChatRoom().getChatRoomId());
        dto.setSenderId(message.getSender().getUserId().longValue());
        dto.setSenderType(message.getSenderType().name());
        dto.setContent(message.getMessageContent());
        dto.setAttachmentUrl(message.getFileUrl());
        dto.setSentAt(message.getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setIsRead(message.getIsRead());
        dto.setSenderName(message.getSender().getFullName());
        
        return dto;
    }

    @Override
    public ChatRoomDTO convertToDTO(ChatRoom chatRoom, Long currentUserId, String currentUserType) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setChatRoomId(chatRoom.getChatRoomId());
        
        // Xác định thông tin người chat cùng
        if ("STUDENT".equals(currentUserType)) {
            // Current user là student, lấy thông tin employer
            dto.setOtherParticipantName(chatRoom.getEmployer().getAccount().getFullName());
            dto.setOtherParticipantAvatar("/images/employee.jpeg");
            dto.setIsOnline(isUserOnline(chatRoom.getEmployer().getAccount().getUserId().longValue(), "EMPLOYER"));
            dto.setUnreadCount(chatRoom.getStudentUnreadCount());
        } else {
            // Current user là employer, lấy thông tin student
            dto.setOtherParticipantName(chatRoom.getStudent().getAccount().getFullName());
            dto.setOtherParticipantAvatar("/images/student.jpeg");
            dto.setIsOnline(isUserOnline(chatRoom.getStudent().getAccount().getUserId().longValue(), "STUDENT"));
            dto.setUnreadCount(chatRoom.getEmployerUnreadCount());
        }
        
        // Lấy tin nhắn cuối cùng
        ChatMessage lastMessage = chatMessageRepository.findLatestMessageByRoomId(chatRoom.getChatRoomId());
        if (lastMessage != null) {
            dto.setLastMessage(lastMessage.getMessageContent());
            dto.setLastMessageTime(lastMessage.getSentAt().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        
        // Thông tin job post nếu có
        if (chatRoom.getJobPost() != null) {
            dto.setJobPostId(chatRoom.getJobPost().getJobPostId().longValue());
            dto.setJobTitle(chatRoom.getJobPost().getJobTitle());
            dto.setJobLocation(chatRoom.getJobPost().getJobLocation());
        }
        
        return dto;
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void updateChatRoomAfterMessage(Long chatRoomId, String senderType) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (chatRoom == null) return;
        
        chatRoom.setLastMessageAt(LocalDateTime.now());
        
        // Tăng unread count cho người nhận
        if ("STUDENT".equals(senderType)) {
            chatRoom.setEmployerUnreadCount(chatRoom.getEmployerUnreadCount() + 1);
        } else {
            chatRoom.setStudentUnreadCount(chatRoom.getStudentUnreadCount() + 1);
        }
        
        chatRoomRepository.save(chatRoom);
    }

    private String determineUserType(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (chatRoom == null) return null;
        
        if (chatRoom.getStudent().getAccount().getUserId().equals(userId.intValue())) {
            return "STUDENT";
        } else if (chatRoom.getEmployer().getAccount().getUserId().equals(userId.intValue())) {
            return "EMPLOYER";
        }
        
        return null;
    }


}
