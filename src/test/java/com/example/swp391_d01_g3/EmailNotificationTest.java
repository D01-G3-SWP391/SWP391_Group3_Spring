package com.example.swp391_d01_g3;

import com.example.swp391_d01_g3.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EmailNotificationTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void testJobApplicationSuccessEmail() {
        // Test gửi email thông báo thành công cho student
        emailService.sendJobApplicationSuccessEmail(
            "test@example.com",
            "Nguyễn Văn A",
            "Java Developer",
            "FPT Software",
            "12345"
        );
        
        System.out.println("✅ Test email sent successfully!");
    }

    @Test
    public void testNewApplicationNotificationEmail() {
        // Test gửi email thông báo cho employer
        emailService.sendNewApplicationNotificationEmail(
            "employer@example.com",
            "HR Manager",
            "Java Developer",
            "Nguyễn Văn A",
            "12345"
        );
        
        System.out.println("✅ Test employer notification email sent successfully!");
    }
} 