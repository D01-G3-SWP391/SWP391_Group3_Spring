package com.example.swp391_d01_g3.service.email;

import com.example.swp391_d01_g3.model.ForgotPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Random;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.repository.ForgotPasswordRepository;
import com.example.swp391_d01_g3.repository.IAccountRepository;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private IAccountRepository accountRepository;
    
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setFrom("viettaifptudn@gmail.com");
        mailSender.send(simpleMailMessage);
        System.out.println("Email sent" + to);
    }
    public void sendForgotPassEmail(String to){
        SimpleMailMessage  simpleMailMessage = new SimpleMailMessage();
        int otp = otpGenerator();

        Account account = accountRepository.findByEmail(to);
        if (account == null) {
            System.err.println("Attempted to send forgot password email to non-existent account: " + to);
            // Optionally, you might want to throw an exception or handle this case differently
            // rather tha n silently returning, depending on your application's error handling strategy.
            return; 
        }

        // Xóa bản ghi ForgotPassword hiện có cho tài khoản này
        ForgotPassword existingForgotPassword = forgotPasswordRepository.findByAccount(account);
        if (existingForgotPassword != null) {
            // Hủy bỏ liên kết trong Tài khoản để ngăn chặn TransientObjectException
            if (account.getForgotPassword() != null && account.getForgotPassword().equals(existingForgotPassword)) {
                account.setForgotPassword(null);
            // Tùy chọn lưu tài khoản nếu cần lưu lại những thay đổi ngay lập tức, mặc dù không nhất thiết phải thực hiện trước khi xóa ForgotPassword            \
            }
            forgotPasswordRepository.delete(existingForgotPassword);
        }

        simpleMailMessage.setTo(to);
        simpleMailMessage.setText("This is OTP for your Forgot Password request: "+ otp +
                " OTP sẽ hết hạn trong 60 giây"
        );
        simpleMailMessage.setSubject("OTP for Forgot Password request");
        simpleMailMessage.setFrom("viettaifptudn@gmail.com");

        ForgotPassword forgotPasswordEntity = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000)) // OTP valid for 5 minutes (70*1000ms is 70 seconds)
                .account(account)
                .build();
        forgotPasswordRepository.save(forgotPasswordEntity);

        // Actually send the email
        mailSender.send(simpleMailMessage);
        System.out.println("Forgot password Email sent to " + to + " with OTP: " + otp);
    }
    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000,999_999);
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
