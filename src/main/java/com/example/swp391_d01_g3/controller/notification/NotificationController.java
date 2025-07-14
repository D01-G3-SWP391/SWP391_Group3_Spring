package com.example.swp391_d01_g3.controller.notification;

import com.example.swp391_d01_g3.dto.NotificationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Notification;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("")
    public String showNotifications(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to view notifications");
            return "redirect:/login";
        }

        String email = authentication.getName();
        Account account = accountService.findByEmail(email);
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Account not found");
            return "redirect:/login";
        }

        List<Notification> notifications = notificationService.getUserNotifications(account.getUserId());
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("notifications", notificationDTOs);
        return "notifications/notifications-list";
    }

    // AJAX endpoint for getting latest notifications for the notification bell
    @GetMapping("/latest")
    @ResponseBody
    public List<NotificationDTO> getLatestNotifications(Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                return notificationService.getLatestUserNotifications(account.getUserId(), 5)
                        .stream()
                        .map(NotificationDTO::fromEntity)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    // AJAX endpoint for getting unread notification count for the notification bell
    @GetMapping("/count")
    @ResponseBody
    public Map<String, Integer> getUnreadCount(Authentication authentication) {
        Map<String, Integer> response = new HashMap<>();
        if (authentication != null) {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                response.put("count", notificationService.getUnreadNotificationCount(account.getUserId()));
                return response;
            }
        }
        response.put("count", 0);
        return response;
    }

    // AJAX endpoint for marking a single notification as read
    @PostMapping("/{id}/mark-read")
    @ResponseBody
    public Map<String, String> markAsRead(
            @PathVariable("id") Long notificationId,
            Authentication authentication
    ) {
        Map<String, String> response = new HashMap<>();
        try {
            notificationService.markAsRead(notificationId);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("error", "Failed to mark notification as read");
        }
        return response;
    }

    // AJAX endpoint for marking all notifications as read
    @PostMapping("/mark-all-read")
    @ResponseBody
    public Map<String, String> markAllAsRead(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("error", "User not authenticated");
            return response;
        }

        try {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                notificationService.markAllAsRead(account.getUserId());
                response.put("status", "success");
            } else {
                response.put("error", "Account not found");
            }
        } catch (Exception e) {
            response.put("error", "Failed to mark all notifications as read");
        }
        return response;
    }

    // AJAX endpoint for deleting a single notification
    @DeleteMapping("/{id}")
    @ResponseBody
    public Map<String, String> deleteNotification(
            @PathVariable("id") Long notificationId,
            Authentication authentication
    ) {
        Map<String, String> response = new HashMap<>();
        try {
            notificationService.deleteNotification(notificationId);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("error", "Failed to delete notification");
        }
        return response;
    }

    // AJAX endpoint for deleting all notifications
    @DeleteMapping("/delete-all")
    @ResponseBody
    public Map<String, String> deleteAllNotifications(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("error", "User not authenticated");
            return response;
        }

        try {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                notificationService.deleteAllNotifications(account.getUserId());
                response.put("status", "success");
            } else {
                response.put("error", "Account not found");
            }
        } catch (Exception e) {
            response.put("error", "Failed to delete all notifications");
        }
        return response;
    }
    
    // ========== WEBSOCKET MESSAGE HANDLERS ==========
    
    /**
     * 🔔 Subscribe to personal notifications via WebSocket
     */
    @MessageMapping("/notifications.subscribe")
    public void subscribeToNotifications(Principal principal) {
        try {
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            
            if (account != null) {
                // Send latest notifications to the user
                List<NotificationDTO> notifications = notificationService.getLatestUserNotifications(account.getUserId(), 10)
                        .stream()
                        .map(NotificationDTO::fromEntity)
                        .collect(Collectors.toList());
                
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "initial_load", "notifications", notifications)
                );
                
                // Send unread count
                int unreadCount = notificationService.getUnreadNotificationCount(account.getUserId());
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", unreadCount)
                );
            }
        } catch (Exception e) {
            System.err.println("Error subscribing to notifications: " + e.getMessage());
        }
    }
    
    /**
     * ✅ Mark notification as read via WebSocket
     */
    @MessageMapping("/notifications.markAsRead")
    public void markAsReadViaWebSocket(@Payload Map<String, Object> payload, Principal principal) {
        try {
            Long notificationId = Long.parseLong(payload.get("notificationId").toString());
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            
            if (account != null) {
                notificationService.markAsRead(notificationId);
                
                // Send updated unread count
                int unreadCount = notificationService.getUnreadNotificationCount(account.getUserId());
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", unreadCount)
                );
                
                // Send success response
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "mark_read_success", "notificationId", notificationId)
                );
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/notifications",
                Map.of("type", "error", "message", "Failed to mark notification as read")
            );
        }
    }
    
    /**
     * 🗑️ Delete notification via WebSocket
     */
    @MessageMapping("/notifications.delete")
    public void deleteNotificationViaWebSocket(@Payload Map<String, Object> payload, Principal principal) {
        try {
            Long notificationId = Long.parseLong(payload.get("notificationId").toString());
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            
            if (account != null) {
                notificationService.deleteNotification(notificationId);
                
                // Send updated unread count
                int unreadCount = notificationService.getUnreadNotificationCount(account.getUserId());
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", unreadCount)
                );
                
                // Send success response
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "delete_success", "notificationId", notificationId)
                );
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/notifications",
                Map.of("type", "error", "message", "Failed to delete notification")
            );
        }
    }
    
    /**
     * ✅ Mark all notifications as read via WebSocket
     */
    @MessageMapping("/notifications.markAllAsRead")
    public void markAllAsReadViaWebSocket(Principal principal) {
        try {
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            
            if (account != null) {
                notificationService.markAllAsRead(account.getUserId());
                
                // Send success response
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "mark_all_read_success")
                );
                
                // Send updated unread count (should be 0)
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", 0)
                );
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/notifications",
                Map.of("type", "error", "message", "Failed to mark all notifications as read")
            );
        }
    }
    
    /**
     * 🗑️ Delete all notifications via WebSocket
     */
    @MessageMapping("/notifications.deleteAll")
    public void deleteAllNotificationsViaWebSocket(Principal principal) {
        try {
            String email = principal.getName();
            Account account = accountService.findByEmail(email);
            
            if (account != null) {
                notificationService.deleteAllNotifications(account.getUserId());
                
                // Send success response
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "delete_all_success")
                );
                
                // Send updated unread count (should be 0)
                messagingTemplate.convertAndSendToUser(
                    email,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", 0)
                );
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/notifications",
                Map.of("type", "error", "message", "Failed to delete all notifications")
            );
        }
    }
} 