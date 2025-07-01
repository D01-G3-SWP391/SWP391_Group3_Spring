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
import org.springframework.security.core.context.SecurityContextHolder;
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
        // First check session attribute, then check query parameter
        String targetUrl = (String) request.getSession().getAttribute("REDIRECT_URL");
        String returnUrlParam = request.getParameter("returnUrl");

        
        // Prioritize returnUrl parameter if it exists and is valid
        if (returnUrlParam != null && !returnUrlParam.isEmpty() && 
            !returnUrlParam.contains("favicon.ico") && !returnUrlParam.startsWith("/.well-known")) {
            targetUrl = returnUrlParam;
        } else if (targetUrl != null && targetUrl.startsWith("/Login?returnUrl=")) {
            // Extract the actual returnUrl from /Login?returnUrl=... 
            try {
                String encodedReturnUrl = targetUrl.substring("/Login?returnUrl=".length());
                targetUrl = java.net.URLDecoder.decode(encodedReturnUrl, "UTF-8");
            } catch (Exception e) {

                targetUrl = null;
            }
        } else if (targetUrl == null || targetUrl.isEmpty()) {
        }
        
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            String email = (String) attributes.get("email");
            String fullName = (String) attributes.get("name");


            // Kiểm tra user có tồn tại chưa để biết có phải tài khoản mới
            boolean isNewUser = accountService.findByEmail(email) == null;
            
            accountDetailsService.saveOAuthUser(attributes);
            UserDetails userDetails = accountDetailsService.loadUserByUsername(email);
            
            // CHỈ gửi email cho tài khoản MỚI
            if (isNewUser) {
                emailService.sendWelcomeEmail(email, fullName, "Google");
            }
            
            // Tạo Authentication object đúng cách
            authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
           SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // Đã TẮT email notification cho login thường xuyên bằng Email/Password
        
        if (targetUrl == null || targetUrl.isEmpty() || targetUrl.contains("favicon.ico") ||
                targetUrl.contains("error") || targetUrl.startsWith("/.well-known") ||
                targetUrl.endsWith(".css") || targetUrl.endsWith(".js") || targetUrl.endsWith(".json") ||
                targetUrl.startsWith("/notifications/") || targetUrl.startsWith("/api/")) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
                targetUrl = "/";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_employer"))) {
                targetUrl = "/";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_student"))) {
                targetUrl = "/";
            } else {
                targetUrl = "/HomePage";
            }
        }
        clearAuthenticationAttributes(request);
        // Clear the REDIRECT_URL from session after using it
        request.getSession().removeAttribute("REDIRECT_URL");
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}