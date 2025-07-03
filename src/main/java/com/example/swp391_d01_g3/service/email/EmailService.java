package com.example.swp391_d01_g3.service.email;

import com.example.swp391_d01_g3.dto.PendingRegisterDTO;
import com.example.swp391_d01_g3.model.ForgotPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
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
    
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            simpleMailMessage.setFrom("viettaifptudn@gmail.com");
            mailSender.send(simpleMailMessage);
            System.out.println("Email sent to: " + to + " - Thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + to + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendForgotPassEmail(String to){
        try {
            SimpleMailMessage  simpleMailMessage = new SimpleMailMessage();
            int otp = otpGenerator();

            Account account = accountRepository.findByEmail(to);
            if (account == null) {
                System.err.println("Attempted to send forgot password email to non-existent account: " + to);
                return CompletableFuture.failedFuture(new RuntimeException("Account not found: " + to));
            }

            // Xóa bản ghi ForgotPassword hiện có cho tài khoản này
            ForgotPassword existingForgotPassword = forgotPasswordRepository.findByAccount(account);
            if (existingForgotPassword != null) {
                // Hủy bỏ liên kết trong Tài khoản để ngăn chặn TransientObjectException
                if (account.getForgotPassword() != null && account.getForgotPassword().equals(existingForgotPassword)) {
                    account.setForgotPassword(null);
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
                    .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000)) // OTP valid for 60 seconds
                    .account(account)
                    .build();
            forgotPasswordRepository.save(forgotPasswordEntity);

            // Actually send the email
            mailSender.send(simpleMailMessage);
            System.out.println("Forgot password Email sent to " + to + " with OTP: " + otp + " - Thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Failed to send forgot password email to: " + to + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Gửi email xác thực với OTP cho người dùng mới đăng ký (không cần account trong DB)
     * @param email Email người nhận
     * @param fullName Tên đầy đủ của người dùng
     * @return CompletableFuture với OTP được tạo
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Integer> sendVerifyMailForRegistration(String email, String fullName) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            int otp = otpGenerator();

            // Tạo nội dung email
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Xin chào ").append(fullName).append(",\n\n");
            emailBody.append("🎉 Chào mừng bạn đến với JOB4YOU!\n\n");
            emailBody.append("Để hoàn tất quá trình đăng ký tài khoản, vui lòng xác thực email của bạn bằng mã OTP sau:\n\n");
            emailBody.append("🔐 Mã OTP: ").append(otp).append("\n\n");
            emailBody.append("⚠️ Mã OTP này sẽ hết hạn trong 10 phút.\n\n");
            emailBody.append("Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.\n\n");
            emailBody.append("Trân trọng,\n");
            emailBody.append("🏢 Đội ngũ JOB4YOU\n");
            emailBody.append("📞 Hotline: 1900-xxxx\n");
            emailBody.append("🌐 Website: http://localhost:8080");

            simpleMailMessage.setTo(email);
            simpleMailMessage.setText(emailBody.toString());
            simpleMailMessage.setSubject("🔐 Xác thực email đăng ký tài khoản JOB4YOU");
            simpleMailMessage.setFrom("viettaifptudn@gmail.com");

            // Gửi email
            mailSender.send(simpleMailMessage);
            System.out.println("Registration verification email sent to " + email + " with OTP: " + otp + " - Thread: " + Thread.currentThread().getName());
            
            return CompletableFuture.completedFuture(otp);
        } catch (Exception e) {
            System.err.println("Failed to send registration verification email to: " + email + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Tạo tài khoản thực sự sau khi xác thực OTP thành công
     * @param pendingRegistration Thông tin đăng ký đã được xác thực
     * @return Account đã được tạo
     */
    public Account createAccountAfterVerification(PendingRegisterDTO pendingRegistration) {
        // Tạo Account
        Account account = new Account();
        account.setFullName(pendingRegistration.getFullName());
        account.setEmail(pendingRegistration.getEmail());
        account.setPassword(pendingRegistration.getPassword());
        account.setPhone(pendingRegistration.getPhone());
        account.setRole(pendingRegistration.getRole());
        account.setAvatarUrl(pendingRegistration.getAvatarUrl());
        account.setStatus(Account.Status.active); // Kích hoạt ngay vì đã verify

        Account savedAccount = accountRepository.save(account);
        
        // Gửi email chào mừng sau khi tạo account thành công
        String roleText = account.getRole() != null ? account.getRole().name().toLowerCase() : "";
        String role = switch (roleText) {
            case "student" -> "Student";
            case "employer" -> "Employer";
            default -> null;
        };
        if (role != null) {
            sendWelcomeEmail(savedAccount.getEmail(), savedAccount.getFullName(), role);
        } else {
            System.err.println("Không xác định được vai trò, không gửi email chào mừng!");
        }
        
        System.out.println("Account created successfully after verification for: " + savedAccount.getEmail());
        return savedAccount;
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
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendWelcomeEmail(String email, String fullName, String role) {
        try {
            String subject = "🎉 Chào mừng bạn đến với JOB4YOU!";
            String roleText = getRoleText(role);
            
            String body = buildWelcomeEmailBody(fullName, email, role, roleText);
            
            // Gửi email trực tiếp thay vì gọi sendEmail async để tránh nested async
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            simpleMailMessage.setFrom("viettaifptudn@gmail.com");
            mailSender.send(simpleMailMessage);
            
            System.out.println("Welcome email sent to: " + email + " (Role: " + role + ") - Thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to: " + email + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
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

    /**
     * Gửi email lịch phỏng vấn cho ứng viên
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendInterviewScheduleEmail(String to, String candidateName, String jobTitle, String interviewTime, String interviewType, String meetingLink, String note) {
        try {
            String subject = "Lịch phỏng vấn vị trí " + jobTitle + " tại JOB4YOU";
            StringBuilder body = new StringBuilder();
            body.append("Xin chào ").append(candidateName).append(",\n\n");
            body.append("Chúc mừng bạn đã vượt qua vòng hồ sơ!\n");
            body.append("Chúng tôi xin mời bạn tham gia phỏng vấn với thông tin sau:\n");
            body.append("- Thời gian: ").append(interviewTime).append("\n");
            body.append("- Hình thức: ").append(interviewType).append("\n");
            if (meetingLink != null && !meetingLink.isEmpty()) {
                body.append("- Link phỏng vấn: ").append(meetingLink).append("\n");
            }
            if (note != null && !note.isEmpty()) {
                body.append("- Ghi chú: ").append(note).append("\n");
            }
            body.append("\nVui lòng phản hồi email này nếu bạn có thắc mắc.\n");
            body.append("Trân trọng,\nĐội ngũ JOB4YOU");
            
            // Gửi email trực tiếp
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body.toString());
            simpleMailMessage.setFrom("viettaifptudn@gmail.com");
            mailSender.send(simpleMailMessage);
            
            System.out.println("Interview schedule email sent to: " + to + " - Thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Failed to send interview schedule email to: " + to + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Gửi email thông báo phỏng vấn pass - được nhận vào làm việc
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendApplicationAcceptedEmail(String to, String candidateName, String jobTitle, String companyName) {
        try {
        String subject = "🎉 Chúc mừng! Bạn đã phỏng vấn PASS vị trí " + jobTitle + " tại " + companyName;
        StringBuilder body = new StringBuilder();

        body.append("Xin chào ").append(candidateName).append(",\n\n");
        body.append("🎉 CHÚC MỪNG BẠN ĐÃ PHỎNG VẤN PASS! 🎉\n\n");
        body.append("Chúng tôi rất vui mừng thông báo rằng bạn đã vượt qua thành công buổi phỏng vấn và ");
        body.append("CHÍNH THỨC trở thành thành viên mới của đội ngũ:\n\n");
        body.append("📋 Vị trí: ").append(jobTitle).append("\n");
        body.append("🏢 Công ty: ").append(companyName).append("\n\n");
        body.append("✨ KẾT QUẢ PHỎNG VẤN:\n");
        body.append("• Bạn đã thể hiện xuất sắc trong buổi phỏng vấn\n");
        body.append("• Kỹ năng chuyên môn phù hợp với yêu cầu công việc\n");
        body.append("• Thái độ làm việc tích cực và nhiệt huyết\n");
        body.append("• Sự phù hợp tuyệt vời với văn hóa công ty\n\n");
        body.append("📋 BƯỚC TIẾP THEO:\n");
        body.append("• Phòng Nhân sự sẽ liên hệ với bạn trong vòng 2-3 ngày làm việc\n");
        body.append("• Bạn sẽ nhận được thông tin chi tiết về:\n");
        body.append("  ✓ Ngày bắt đầu làm việc chính thức\n");
        body.append("  ✓ Mức lương và các chế độ phúc lợi\n");
        body.append("  ✓ Hợp đồng lao động và thủ tục pháp lý\n");
        body.append("  ✓ Chương trình định hướng cho nhân viên mới\n\n");
        body.append("📞 LƯU Ý QUAN TRỌNG:\n");
        body.append("• Vui lòng giữ điện thoại và email luôn sẵn sàng\n");
        body.append("• Chuẩn bị sẵn các giấy tờ cá nhân cần thiết\n");
        body.append("• Liên hệ ngay với chúng tôi nếu có thắc mắc\n\n");
        body.append("Chúng tôi rất hào hứng được đón chào bạn gia nhập đại gia đình ").append(companyName);
        body.append("! Tin rằng với tài năng và nhiệt huyết của mình, bạn sẽ góp phần ");
        body.append("tạo nên những thành công mới cho công ty.\n\n");
        body.append("Một lần nữa, xin chúc mừng bạn đã PASS phỏng vấn và chào mừng đến với team! 🎊\n\n");
        body.append("Trân trọng,\n");
        body.append("🏢 Ban Giám đốc & Phòng Nhân sự\n");
        body.append("🏷️ ").append(companyName).append("\n");
        body.append("📧 Thông qua hệ thống tuyển dụng JOB4YOU\n");
        body.append("🌐 Website: http://localhost:8080");

        // Gửi email trực tiếp
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body.toString());
        simpleMailMessage.setFrom("viettaifptudn@gmail.com");
        mailSender.send(simpleMailMessage);
        
        System.out.println("Application accepted email sent to: " + to + " - Thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Failed to send application accepted email to: " + to + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Gửi email thông báo ứng tuyển bị từ chối
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendApplicationRejectedEmail(String to, String candidateName, String jobTitle, String companyName) {
        try {
        String subject = "Thông báo kết quả ứng tuyển - " + jobTitle;
        StringBuilder body = new StringBuilder();

        body.append("Xin chào ").append(candidateName).append(",\n\n");
        body.append("Cảm ơn bạn đã quan tâm và dành thời gian ứng tuyển vào vị trí:\n\n");
        body.append("📋 Vị trí: ").append(jobTitle).append("\n");
        body.append("🏢 Công ty: ").append(companyName).append("\n\n");
        body.append("Sau khi xem xét kỹ lưỡng hồ sơ của bạn, rất tiếc chúng tôi phải thông báo rằng ");
        body.append("lần này bạn chưa phù hợp với vị trí công việc này.\n\n");
        body.append("Điều này không có nghĩa là hồ sơ của bạn không tốt. ");
        body.append("Đôi khi, quyết định này phụ thuộc vào nhiều yếu tố như kinh nghiệm cụ thể, ");
        body.append("kỹ năng chuyên môn hoặc sự phù hợp với văn hóa công ty.\n\n");
        body.append("🌟 Chúng tôi khuyến khích bạn:\n");
        body.append("• Tiếp tục theo dõi các cơ hội việc làm khác tại công ty\n");
        body.append("• Cải thiện và phát triển thêm kỹ năng của mình\n");
        body.append("• Ứng tuyển vào các vị trí phù hợp khác trên JOB4YOU\n\n");
        body.append("Chúc bạn sớm tìm được công việc phù hợp và thành công trong sự nghiệp!\n\n");
        body.append("Trân trọng,\n");
        body.append("🏢 ").append(companyName).append("\n");
        body.append("📧 Thông qua hệ thống JOB4YOU");

        // Gửi email trực tiếp
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body.toString());
        simpleMailMessage.setFrom("viettaifptudn@gmail.com");
        mailSender.send(simpleMailMessage);
        
        System.out.println("Application rejected email sent to: " + to + " - Thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Failed to send application rejected email to: " + to + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Gửi email thông báo job application thành công cho student
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendJobApplicationSuccessEmail(String studentEmail, String studentName, String jobTitle, String companyName, String applicationId) {
        try {
            String subject = "✅ Đơn ứng tuyển đã được gửi thành công - " + jobTitle;

            StringBuilder body = new StringBuilder();
            body.append("Xin chào ").append(studentName).append(",\n\n");
            body.append("🎉 Chúc mừng! Đơn ứng tuyển của bạn đã được gửi thành công.\n\n");
            body.append("📋 Thông tin đơn ứng tuyển:\n");
            body.append("   • Vị trí: ").append(jobTitle).append("\n");
            body.append("   • Công ty: ").append(companyName).append("\n");
            body.append("   • Thời gian gửi: ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");

            body.append("📝 Những bước tiếp theo:\n");
            body.append("   1. Nhà tuyển dụng sẽ xem xét hồ sơ của bạn\n");
            body.append("   2. Bạn sẽ nhận được thông báo khi có cập nhật\n");
            body.append("   3. Nếu được chọn, bạn sẽ được mời phỏng vấn\n\n");

            body.append("💡 Lời khuyên:\n");
            body.append("   • Kiểm tra email thường xuyên để không bỏ lỡ thông báo\n");
            body.append("   • Cập nhật hồ sơ cá nhân để tăng cơ hội\n");
            body.append("   • Tham gia các sự kiện tuyển dụng của chúng tôi\n\n");

            body.append("🔍 Theo dõi đơn ứng tuyển:\n");
            body.append("   Truy cập: http://localhost:8080/Student/applications\n\n");

            body.append("Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.\n\n");
            body.append("Chúc bạn thành công!\n\n");
            body.append("Trân trọng,\n");
            body.append("🏢 Đội ngũ JOB4YOU\n");
            body.append("📞 Hotline: 1900-xxxx\n");
            body.append("🌐 Website: http://localhost:8080");

            // Gửi email trực tiếp
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(studentEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body.toString());
            simpleMailMessage.setFrom("viettaifptudn@gmail.com");
            mailSender.send(simpleMailMessage);
            
            System.out.println("Job application success email sent to: " + studentEmail + " - Thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            System.err.println("Failed to send job application success email to: " + studentEmail + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Gửi email thông báo có đơn ứng tuyển mới cho employer
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendNewApplicationNotificationEmail(String employerEmail, String employerName, String jobTitle, String candidateName, String applicationId) {
        try {
            String subject = "📬 Đơn ứng tuyển mới - " + jobTitle;

            StringBuilder body = new StringBuilder();
            body.append("Xin chào ").append(employerName).append(",\n\n");
            body.append("🎯 Bạn có một đơn ứng tuyển mới cho vị trí: ").append(jobTitle).append("\n\n");
            body.append("👤 Thông tin ứng viên:\n");
            body.append("   • Tên: ").append(candidateName).append("\n");
            body.append("   • Thời gian nộp: ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");

            body.append("📋 Để xem chi tiết và phản hồi:\n");
            body.append("   Truy cập: http://localhost:8080/Employer/Applications\n\n");

            body.append("⏰ Lời khuyên:\n");
            body.append("   • Phản hồi sớm để tăng trải nghiệm ứng viên\n");
            body.append("   • Đánh giá hồ sơ một cách khách quan\n");
            body.append("   • Liên hệ ứng viên trong vòng 48 giờ\n\n");

            body.append("Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.\n\n");
            body.append("Trân trọng,\n");
            body.append("🏢 Đội ngũ JOB4YOU\n");
            body.append("📞 Hotline: 1900-xxxx\n");
            body.append("🌐 Website: http://localhost:8080");

            // Gửi email trực tiếp
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(employerEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body.toString());
            simpleMailMessage.setFrom("viettaifptudn@gmail.com");
            mailSender.send(simpleMailMessage);
            
            System.out.println("New application notification email sent to employer: " + employerEmail + " - Thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            System.err.println("Failed to send new application notification email to employer: " + employerEmail + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Gửi email thông báo có đơn ứng tuyển mới cho employer (từ form apply)
     * Sử dụng thông tin từ form thay vì thông tin student đã đăng nhập
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendNewApplicationNotificationEmailFromForm(String employerEmail, String employerName, String jobTitle,
                                                          String candidateName, String candidateEmail, String candidatePhone,
                                                          String applicationId, String description) {
        try {
            String subject = "📬 Đơn ứng tuyển mới - " + jobTitle;

            StringBuilder body = new StringBuilder();
            body.append("Xin chào ").append(employerName).append(",\n\n");
            body.append("🎯 Bạn có một đơn ứng tuyển mới cho vị trí: ").append(jobTitle).append("\n\n");
            body.append("👤 Thông tin ứng viên:\n");
            body.append("   • Tên: ").append(candidateName).append("\n");
            body.append("   • Email: ").append(candidateEmail).append("\n");
            body.append("   • Số điện thoại: ").append(candidatePhone).append("\n");
            body.append("   • Thời gian nộp: ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");

            if (description != null && !description.trim().isEmpty()) {
                body.append("📝 Nội dung ứng tuyển:\n");
                body.append("   ").append(description).append("\n\n");
            }

            body.append("📋 Để xem chi tiết và phản hồi:\n");
            body.append("   Truy cập: http://localhost:8080/Employer/Applications\n\n");

            body.append("⏰ Lời khuyên:\n");
            body.append("   • Phản hồi sớm để tăng trải nghiệm ứng viên\n");
            body.append("   • Đánh giá hồ sơ một cách khách quan\n");
            body.append("   • Liên hệ ứng viên trong vòng 48 giờ\n\n");

            body.append("Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.\n\n");
            body.append("Trân trọng,\n");
            body.append("🏢 Đội ngũ JOB4YOU\n");
            body.append("📞 Hotline: 1900-xxxx\n");
            body.append("🌐 Website: http://localhost:8080");

            // Gửi email trực tiếp
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(employerEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body.toString());
            simpleMailMessage.setFrom("viettaifptudn@gmail.com");
            mailSender.send(simpleMailMessage);
            
            System.out.println("New application notification email sent to employer: " + employerEmail + " (from form data) - Thread: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            System.err.println("Failed to send new application notification email to employer: " + employerEmail + " - Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
}
