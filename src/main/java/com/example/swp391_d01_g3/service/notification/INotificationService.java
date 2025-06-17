package com.example.swp391_d01_g3.service.notification;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Notification;
import java.util.List;

public interface INotificationService {
    Notification createNotification(Account user, String title, String message, String type, Long relatedId);
    List<Notification> getUserNotifications(Integer userId);
    List<Notification> getLatestUserNotifications(Integer userId, int limit);
    int getUnreadNotificationCount(Integer userId);
    void markAsRead(Integer notificationId);
    void markAllAsRead(Integer userId);
    void deleteNotification(Integer notificationId);
    void deleteAllNotifications(Integer userId);
} 