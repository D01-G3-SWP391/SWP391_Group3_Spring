package com.example.swp391_d01_g3.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setCc("trantafi204@gmail.com");
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setFrom("vantai04102004@gmail.com");
        mailSender.send(simpleMailMessage);
        System.out.println("Email sent" + to);
    }
    
    /**
     * Gửi email chào mừng cho thành viên mới
     * @param email Email người nhận
     * @param fullName Tên đầy đủ
     * @param role Vai trò: "Student", "Employer", "Google"
     */
    public void sendWelcomeEmail(String email, String fullName, String role) {
        try {
            String subject = "🎉 Chào mừng bạn đến với JOB4YOU!";
            String roleText = getRoleText(role);
            
            String body = buildWelcomeEmailBody(fullName, email, role, roleText);
            
            sendEmail(email, subject, body);
            System.out.println(" Welcome email sent to: " + email + " (Role: " + role + ")");
            
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to: " + email + " - Error: " + e.getMessage());
        }
    }
    
    private String getRoleText(String role) {
        return switch (role) {
            case "Student" -> "Sinh viên";
            case "Employer" -> "Nhà tuyển dụng";
            case "Google" -> "Người dùng";
            default -> "Thành viên";
        };
    }
    
    private String buildWelcomeEmailBody(String fullName, String email, String role, String roleText) {
        StringBuilder body = new StringBuilder();
        body.append("Xin chào ").append(fullName).append(",\n\n");
        
        if (role.equals("Google")) {
            body.append("🎉 Chào mừng bạn đã gia nhập cộng đồng JOB4YOU!\n\n")
                .append("✅ Tài khoản của bạn đã được tạo thành công thông qua Google\n");
        } else {
            body.append("🎉 Chào mừng bạn đã gia nhập cộng đồng JOB4YOU với vai trò ").append(roleText).append("!\n\n")
                .append("✅ Tài khoản của bạn đã được tạo thành công\n");
        }
        
        body.append("📧 Email đăng nhập: ").append(email).append("\n");
        if (!role.equals("Google")) {
            body.append("👤 Vai trò: ").append(roleText).append("\n");
        }
        body.append("\n");
        
        // Thêm hướng dẫn theo role
        body.append("🚀 Bạn có thể bắt đầu:\n");
        if (role.equals("Student") || role.equals("Google")) {
            body.append("   • Tìm kiếm việc làm phù hợp\n")
                .append("   • Cập nhật hồ sơ cá nhân\n")
                .append("   • Tham gia các sự kiện tuyển dụng\n")
                .append("   • Nộp đơn ứng tuyển trực tuyến\n\n");
        } else if (role.equals("Employer")) {
            body.append("   • Đăng tin tuyển dụng\n")
                .append("   • Quản lý hồ sơ công ty\n")
                .append("   • Tổ chức sự kiện tuyển dụng\n")
                .append("   • Tìm kiếm ứng viên phù hợp\n\n");
        }
        
        body.append("💬 Nếu có bất kỳ thắc mắc nào, đừng ngại liên hệ với chúng tôi.\n\n")
            .append("Chúc bạn có trải nghiệm tuyệt vời!\n\n")
            .append("Trân trọng,\n")
            .append("🏢 Đội ngũ JOB4YOU\n")
            .append("📞 Hotline: 1900-xxxx\n")
            .append("🌐 Website: http://localhost:8080");
            
        return body.toString();
    }
}
