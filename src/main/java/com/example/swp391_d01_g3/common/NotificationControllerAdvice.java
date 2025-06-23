package com.example.swp391_d01_g3.common;

import com.example.swp391_d01_g3.dto.NotificationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class NotificationControllerAdvice {

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IAccountService accountService;

    @ModelAttribute("notifications")
    public List<NotificationDTO> getNotifications() {
        try {
            if (AuthenticationHelper.isUserAuthenticated()) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                Account account = accountService.findByEmail(email);
                
                if (account != null) {
                    return notificationService.getLatestUserNotifications(account.getUserId(), 5)
                            .stream()
                            .map(NotificationDTO::fromEntity)
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            // Log error but don't break the page
            System.err.println("Error loading notifications: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @ModelAttribute("unreadCount")
    public Integer getUnreadCount() {
        try {
            if (AuthenticationHelper.isUserAuthenticated()) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                Account account = accountService.findByEmail(email);
                
                if (account != null) {
                    return notificationService.getUnreadNotificationCount(account.getUserId());
                }
            }
        } catch (Exception e) {
            // Log error but don't break the page
            System.err.println("Error loading unread count: " + e.getMessage());
        }
        return 0;
    }
} 