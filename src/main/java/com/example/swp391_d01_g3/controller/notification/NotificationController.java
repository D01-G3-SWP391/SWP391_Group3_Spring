package com.example.swp391_d01_g3.controller.notification;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Notification;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IAccountService accountService;

    @GetMapping("")
    public String showNotifications(Model model, Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                List<Notification> notifications = notificationService.getUserNotifications(account.getUserId());
                model.addAttribute("notifications", notifications);
            }
        }
        return "notifications/notifications-list";
    }

    @GetMapping("/latest")
    @ResponseBody
    public ResponseEntity<List<Notification>> getLatestNotifications(Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                List<Notification> notifications = notificationService.getLatestUserNotifications(account.getUserId(), 5);
                return ResponseEntity.ok(notifications);
            }
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getUnreadCount(Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                int count = notificationService.getUnreadNotificationCount(account.getUserId());
                return ResponseEntity.ok(Map.of("count", count));
            }
        }
        return ResponseEntity.ok(Map.of("count", 0));
    }

    @PostMapping("/{id}/mark-read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    @ResponseBody
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            Account account = accountService.findByEmail(email);
            if (account != null) {
                notificationService.markAllAsRead(account.getUserId());
            }
        }
        return ResponseEntity.ok().build();
    }
} 