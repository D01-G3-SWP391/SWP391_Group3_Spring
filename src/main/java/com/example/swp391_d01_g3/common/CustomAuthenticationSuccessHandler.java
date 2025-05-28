package com.example.swp391_d01_g3.common;

import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.security.AccountDetailsServiceImpl;
import com.example.swp391_d01_g3.service.security.IAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private AccountDetailsServiceImpl accountDetailsService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private IAccountService accountService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = (String) request.getSession().getAttribute("REDIRECT_URL");
        System.out.println("REDIRECT_URL from session: " + targetUrl);
        request.getSession().removeAttribute("REDIRECT_URL");
        
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            String email = (String) attributes.get("email");
            String fullName = (String) attributes.get("name");
            System.out.println("Logged in with Google - Email: " + email + ", Name: " + fullName);

            // Kiểm tra user có tồn tại chưa để biết có phải tài khoản mới
            boolean isNewUser = accountService.findByEmail(email) == null;
            
            accountDetailsService.saveOAuthUser(attributes);
            UserDetails userDetails = accountDetailsService.loadUserByUsername(email);
            
            // CHỈ gửi email cho tài khoản MỚI
            if (isNewUser) {
                sendWelcomeEmail(email, fullName, "Google");
            }
            
            // Tạo Authentication object đúng cách
            authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // Đã TẮT email notification cho login thường xuyên bằng Email/Password
        
        if (targetUrl == null || targetUrl.isEmpty() || targetUrl.contains("favicon.ico") ||
                targetUrl.contains("error") || targetUrl.startsWith("/.well-known") ||
                targetUrl.endsWith(".css") || targetUrl.endsWith(".js") || targetUrl.endsWith(".json")) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
                targetUrl = "/Admin";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_employer"))) {
                targetUrl = "/Employer";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_student"))) {
                targetUrl = "/Student";
            } else {
                targetUrl = "/HomePage";
            }
        }
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    /**
     * Gửi email chào mừng cho tài khoản mới (chỉ dùng cho Google login)
     */
    private void sendWelcomeEmail(String email, String fullName, String loginMethod) {
        try {
            String subject = "🎉 Chào mừng bạn đến với SWP391 Job Portal!";
            String body = "Xin chào " + fullName + ",\n\n" +
                         "🎉 Chào mừng bạn đã gia nhập cộng đồng SWP391 Job Portal!\n\n" +
                         "✅ Tài khoản của bạn đã được tạo thành công thông qua " + loginMethod + "\n" +
                         "📧 Email đăng nhập: " + email + "\n\n" +
                         "🚀 Bạn có thể bắt đầu:\n" +
                         "   • Tìm kiếm việc làm phù hợp\n" +
                         "   • Cập nhật hồ sơ cá nhân\n" +
                         "   • Tham gia các sự kiện tuyển dụng\n\n" +
                         "💬 Nếu có bất kỳ thắc mắc nào, đừng ngại liên hệ với chúng tôi.\n\n" +
                         "Trân trọng,\n" +
                         "🏢 Đội ngũ SWP391 Job Portal\n" +
                         "📞 Hotline: 1900-xxxx\n" +
                         "🌐 Website: http://localhost:8080";
            
            emailService.sendEmail(email, subject, body);
            System.out.println("✅ Welcome email sent to NEW user: " + email + " via " + loginMethod);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email to: " + email + " - Error: " + e.getMessage());
        }
    }
}