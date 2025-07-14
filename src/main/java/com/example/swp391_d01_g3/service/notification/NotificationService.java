package com.example.swp391_d01_g3.service.notification;

import com.example.swp391_d01_g3.dto.NotificationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Notification;
import com.example.swp391_d01_g3.repository.INotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private INotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification createNotification(Account user, String title, String message, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setRelatedId(relatedId);
        
        // Save to database
        Notification savedNotification = notificationRepository.save(notification);
        
        // Push real-time notification to user
        try {
            NotificationDTO notificationDTO = NotificationDTO.fromEntity(savedNotification);
            
            // Send new notification to user
            messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                Map.of("type", "new_notification", "notification", notificationDTO)
            );
            
            // Send updated unread count
            int unreadCount = getUnreadNotificationCount(user.getUserId());
            messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                Map.of("type", "unread_count", "count", unreadCount)
            );
            
            System.out.println("🔔 Real-time notification sent to user: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send real-time notification: " + e.getMessage());
        }
        
        return savedNotification;
    }

    @Override
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Notification> getLatestUserNotifications(Integer userId, int limit) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadNotificationCount(Integer userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
            
            // Push real-time update
            try {
                String userEmail = notification.getUser().getEmail();
                int unreadCount = getUnreadNotificationCount(notification.getUser().getUserId());
                
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "mark_read_success", "notificationId", notificationId)
                );
                
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", unreadCount)
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to send mark-as-read real-time update: " + e.getMessage());
            }
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (!notifications.isEmpty()) {
            String userEmail = notifications.get(0).getUser().getEmail();
            notifications.forEach(notification -> notification.setRead(true));
            notificationRepository.saveAll(notifications);
            
            // Push real-time update
            try {
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "mark_all_read_success")
                );
                
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", 0)
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to send mark-all-as-read real-time update: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            String userEmail = notification.getUser().getEmail();
            Integer userId = notification.getUser().getUserId();
            
            notificationRepository.delete(notification);
            
            // Push real-time update
            try {
                int unreadCount = getUnreadNotificationCount(userId);
                
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "delete_success", "notificationId", notificationId)
                );
                
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", unreadCount)
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to send delete real-time update: " + e.getMessage());
            }
        });
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (!notifications.isEmpty()) {
            String userEmail = notifications.get(0).getUser().getEmail();
            notificationRepository.deleteAll(notifications);
            
            // Push real-time update
            try {
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "delete_all_success")
                );
                
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    Map.of("type", "unread_count", "count", 0)
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to send delete-all real-time update: " + e.getMessage());
            }
        }
    }
} 