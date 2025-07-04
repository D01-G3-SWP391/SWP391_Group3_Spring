package com.example.swp391_d01_g3.controller.chat;

import com.example.swp391_d01_g3.dto.ChatMessageDTO;
import com.example.swp391_d01_g3.dto.ChatRoomDTO;
import com.example.swp391_d01_g3.dto.CreateChatRoomDTO;
import com.example.swp391_d01_g3.model.ChatMessage;
import com.example.swp391_d01_g3.model.ChatRoom;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.repository.IStudentRepository;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import com.example.swp391_d01_g3.service.chat.IChatService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 💬 Chat Controller - Xử lý WebSocket Messages & REST API
 * 
 * Controller này xử lý:
 * 1. WebSocket messages (real-time chat)
 * 2. REST API cho chat history và room management
 * 3. User presence và typing indicators
 */
@Controller
@Slf4j
public class ChatController {
    
    @Autowired
    private IChatService chatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private IStudentRepository studentRepository;
    
    @Autowired
    private IEmployerRepository employerRepository;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private IEmployerService employerService;

    // ========== WEBSOCKET MESSAGE HANDLERS ==========

    /**
     * 📨 Xử lý tin nhắn được gửi qua WebSocket
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO messageDTO, Principal principal) {
        try {
            log.info("Received message from user: {}", principal.getName());
            
            // Get userId from email
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            if (account == null) {
                throw new RuntimeException("Account not found for email: " + email);
            }
            
            Long userId = account.getUserId().longValue();
            messageDTO.setSenderId(userId);
            
            // Determine user type
            String userType = determineUserType(userId);
            messageDTO.setSenderType(userType);
            
            // Save message vào database
            ChatMessage savedMessage = chatService.sendMessage(messageDTO);
            
            log.info("Message sent successfully from user: {}", userId);
            
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            // Gửi error message về client
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                Map.of("error", "Không thể gửi tin nhắn: " + e.getMessage())
            );
        }
    }

    /**
     * 👋 Xử lý user join chat room
     */
    @MessageMapping("/chat.joinRoom")
    public void joinRoom(@Payload Map<String, Object> payload, Principal principal) {
        try {
            // Get userId from email
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            if (account == null) {
                throw new RuntimeException("Account not found for email: " + email);
            }
            
            Long userId = account.getUserId().longValue();
            Long chatRoomId = Long.parseLong(payload.get("chatRoomId").toString());
            String userType = determineUserType(userId);
            
            log.info("User {} joining room {}", userId, chatRoomId);
            
            // Validate user có quyền join room này không
            if (!chatService.hasAccessToChatRoom(chatRoomId, userId, userType)) {
                throw new SecurityException("No access to this room");
            }
            
            // Mark messages as read khi join room
            chatService.markMessagesAsRead(chatRoomId, userId);
            
            // Update user presence
            chatService.updateUserPresence(userId, userType, true);
            
            log.info("User {} successfully joined room {}", userId, chatRoomId);
            
        } catch (Exception e) {
            log.error("Error joining room: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                Map.of("error", "Không thể tham gia room: " + e.getMessage())
            );
        }
    }

    /**
     * 🏃 Xử lý user leave chat room
     */
    @MessageMapping("/chat.leaveRoom")
    public void leaveRoom(@Payload Map<String, Object> payload, Principal principal) {
        try {
            // Get userId from email
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            if (account == null) {
                throw new RuntimeException("Account not found for email: " + email);
            }
            
            Long userId = account.getUserId().longValue();
            String userType = determineUserType(userId);
            
            // Update user presence
            chatService.updateUserPresence(userId, userType, false);
            
            log.info("User {} left room", userId);
            
        } catch (Exception e) {
            log.error("Error leaving room: {}", e.getMessage());
        }
    }

    /**
     * ⌨️ Xử lý typing indicator
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, Object> payload, Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            
            // Validate payload có đủ thông tin không
            Object chatRoomIdObj = payload.get("chatRoomId");
            Object isTypingObj = payload.get("isTyping");
            
            if (chatRoomIdObj == null || isTypingObj == null) {
                log.warn("Invalid typing payload: missing chatRoomId or isTyping");
                return;
            }
            
            Long chatRoomId = Long.parseLong(chatRoomIdObj.toString());
            boolean isTyping = Boolean.parseBoolean(isTypingObj.toString());
            String userType = determineUserType(userId);
            
            // Update typing status
            chatService.updateTypingStatus(chatRoomId, userId, userType, isTyping);
            
        } catch (Exception e) {
            log.error("Error handling typing: {}", e.getMessage());
        }
    }

    /**
     * ✅ Mark messages as read
     */
    @MessageMapping("/chat.markRead")
    public void markMessagesAsRead(@Payload Map<String, Object> payload, Principal principal) {
        try {
            // Get userId from email instead of parsing principal name as Long
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            if (account == null) {
                throw new RuntimeException("Account not found for email: " + email);
            }
            
            Long userId = account.getUserId().longValue();
            Long chatRoomId = Long.parseLong(payload.get("chatRoomId").toString());
            
            chatService.markMessagesAsRead(chatRoomId, userId);
            
            log.info("Messages marked as read for user {} in room {}", userId, chatRoomId);
            
        } catch (Exception e) {
            log.error("Error marking messages as read: {}", e.getMessage());
        }
    }

    // ========== REST API ENDPOINTS ==========

    /**
     * 📋 Lấy danh sách chat rooms của user
     */
    @GetMapping("/api/chat/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDTO>> getUserChatRooms(Principal principal) {
        try {
            // Check if user is authenticated
            if (principal == null) {
                log.warn("Unauthenticated request to /api/chat/rooms");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Get userId from email
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            if (account == null) {
                throw new RuntimeException("Account not found for email: " + email);
            }
            
            Long userId = account.getUserId().longValue();
            String userType = determineUserType(userId);
            
            List<ChatRoomDTO> chatRooms = chatService.getUserChatRooms(userId, userType);
            
            return ResponseEntity.ok(chatRooms);
            
        } catch (Exception e) {
            log.error("Error getting user chat rooms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    /**
     * 🆕 Tạo hoặc lấy chat room
     */
    @PostMapping("/api/chat/rooms")
    @ResponseBody
    public ResponseEntity<ChatRoomDTO> getOrCreateChatRoom(@RequestBody CreateChatRoomDTO createDTO, Principal principal) {
        try {
            // Check if user is authenticated
            if (principal == null) {
                log.warn("Unauthenticated request to /api/chat/rooms");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Get userId from email
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            if (account == null) {
                throw new RuntimeException("Account not found for email: " + email);
            }
            
            Long currentUserId = account.getUserId().longValue();
            String currentUserType = determineUserType(currentUserId);
            
            // Validate input
            if (!createDTO.isValid()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Create or find chat room
            ChatRoom chatRoom = chatService.findOrCreateChatRoom(createDTO);
            
            // Convert to DTO
            ChatRoomDTO chatRoomDTO = chatService.convertToDTO(chatRoom, currentUserId, currentUserType);
            
            return ResponseEntity.ok(chatRoomDTO);
            
        } catch (Exception e) {
            log.error("Error creating chat room: {}", e.getMessage(), e);
            e.printStackTrace(); // Add stack trace
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 💬 Lấy tin nhắn của chat room
     */
    @GetMapping("/api/chat/rooms/{chatRoomId}/messages")
    @ResponseBody
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            String userType = determineUserType(userId);
            
            // Validate user có quyền truy cập chat room này không
            if (!chatService.hasAccessToChatRoom(chatRoomId, userId, userType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Lấy messages với pagination
            List<ChatMessageDTO> messages = chatService.getChatMessages(chatRoomId, page, size);
            
            return ResponseEntity.ok(messages);
            
        } catch (Exception e) {
            log.error("Error getting chat messages: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 🔍 Tìm kiếm tin nhắn
     */
    @GetMapping("/api/chat/rooms/{roomId}/search")
    @ResponseBody
    public ResponseEntity<List<ChatMessageDTO>> searchMessages(
            @PathVariable Long roomId,
            @RequestParam String keyword,
            Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            
            List<ChatMessageDTO> messages = chatService.searchMessages(roomId, keyword, userId);
            
            return ResponseEntity.ok(messages);
            
        } catch (Exception e) {
            log.error("Error searching messages: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 🔔 Lấy số tin nhắn chưa đọc
     */
    @GetMapping("/api/chat/unread-count")
    @ResponseBody
    public ResponseEntity<Integer> getUnreadCount(Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            String userType = determineUserType(userId);
            
            int unreadCount = chatService.getUnreadMessageCount(userId, userType);
            
            return ResponseEntity.ok(unreadCount);
            
        } catch (Exception e) {
            log.error("Error getting unread count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ✅ Mark all chat messages as read
     */
    @PostMapping("/api/chat/mark-all-read")
    @ResponseBody
    public ResponseEntity<Map<String, String>> markAllChatAsRead(Principal principal) {
        Map<String, String> response = new HashMap<>();
        
        try {
            Long userId = getUserIdFromPrincipal(principal);
            String userType = determineUserType(userId);
            
            // Get all chat rooms for this user
            List<ChatRoomDTO> chatRooms = chatService.getUserChatRooms(userId, userType);
            
            // Mark messages as read for each chat room
            for (ChatRoomDTO chatRoom : chatRooms) {
                chatService.markMessagesAsRead(chatRoom.getChatRoomId(), userId);
            }
            
            log.info("Marked all chat messages as read for user: {}", userId);
            response.put("status", "success");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error marking all chat as read: {}", e.getMessage());
            response.put("error", "Failed to mark all chat as read");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 📄 Lấy thông tin chat room cụ thể
     */
    @GetMapping("/api/chat/rooms/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDTO> getChatRoomInfo(@PathVariable Long roomId, Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            
            return chatService.getChatRoomInfo(roomId, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            log.error("Error getting chat room info: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/chat/current-user")
    @ResponseBody
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", account.getUserId());
            userInfo.put("fullName", account.getFullName());
            userInfo.put("email", account.getEmail());
            userInfo.put("role", account.getRole().name());
            
            // Lấy avatar từ Account table
            userInfo.put("avatarUrl", account.getAvatarUrl());
            
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            log.error("Error getting current user info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Xác định user type dựa trên userId
     * Check trong database để xem user là STUDENT hay EMPLOYER
     * 🆕 Sử dụng method mới để handle duplicate data
     */
    private String determineUserType(Long userId) {
        try {
            // Check if user is a student first - use new method to handle duplicates
            if (studentRepository.findFirstByAccount_UserId(userId.intValue()) != null) {
                return "STUDENT";
            }
            
            // Check if user is an employer - use new method to handle duplicates
            if (employerRepository.findFirstByAccount_UserId(userId.intValue()) != null) {
                return "EMPLOYER";
            }
            
            // Default fallback
            log.warn("User {} not found in Student or Employer tables, defaulting to STUDENT", userId);
            return "STUDENT";
            
        } catch (Exception e) {
            log.error("Error determining user type for user {}: {}", userId, e.getMessage());
            return "STUDENT";
        }
    }

    /**
     * Helper method để lấy userId từ Principal (email)
     */
    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated - Principal is null");
        }
        
        String email = principal.getName();
        Account account = accountService.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("Account not found for email: " + email);
        }
        return account.getUserId().longValue();
    }

    /**
     * 🔍 Tìm student by email để tạo chat room
     */
    @GetMapping("/api/students/findByEmail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findStudentByEmail(@RequestParam String email) {
        try {
            log.info("Finding student by email: {}", email);
            
            // Tìm student bằng email
            Student student = studentService.findByEmail(email);
            
            if (student == null) {
                log.warn("Student not found for email: {}", email);
                return ResponseEntity.notFound().build();
            }
            
            // Trả về thông tin student
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("studentId", student.getStudentId());
            studentInfo.put("userId", student.getAccount().getUserId());
            studentInfo.put("fullName", student.getAccount().getFullName());
            studentInfo.put("email", student.getAccount().getEmail());
            
            log.info("Found student: ID={}, UserId={}, Name={}", 
                    student.getStudentId(), 
                    student.getAccount().getUserId(), 
                    student.getAccount().getFullName());
            
            return ResponseEntity.ok(studentInfo);
            
        } catch (Exception e) {
            log.error("Error finding student by email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🔍 Tìm employer by email để tạo chat room (cho student)
     */
    @GetMapping("/api/employers/findByEmail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findEmployerByEmail(@RequestParam String email) {
        try {
            log.info("Finding employer by email: {}", email);
            
            // Tìm employer bằng email
            Employer employer = employerService.findByEmail(email);
            
            if (employer == null) {
                log.warn("Employer not found for email: {}", email);
                return ResponseEntity.notFound().build();
            }
            
            // Trả về thông tin employer
            Map<String, Object> employerInfo = new HashMap<>();
            employerInfo.put("employerId", employer.getEmployerId());
            employerInfo.put("userId", employer.getAccount().getUserId());
            employerInfo.put("fullName", employer.getAccount().getFullName());
            employerInfo.put("email", employer.getAccount().getEmail());
            employerInfo.put("companyName", employer.getCompanyName());
            
            log.info("Found employer: ID={}, UserId={}, Company={}", 
                    employer.getEmployerId(), 
                    employer.getAccount().getUserId(), 
                    employer.getCompanyName());
            
            return ResponseEntity.ok(employerInfo);
            
        } catch (Exception e) {
            log.error("Error finding employer by email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 