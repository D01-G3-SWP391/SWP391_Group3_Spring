package com.example.swp391_d01_g3.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        // Debug: In ra authorities để kiểm tra
        System.out.println("User authorities: " + authentication.getAuthorities());
        
        String targetUrl = (String) request.getSession().getAttribute("REDIRECT_URL");
        System.out.println("REDIRECT_URL from session: " + targetUrl);
        request.getSession().removeAttribute("REDIRECT_URL");

        // Nếu không có redirect URL hoặc URL không hợp lệ, xác định URL dựa trên role
        if (targetUrl == null || targetUrl.isEmpty() || targetUrl.contains("favicon.ico") ||
                targetUrl.contains("error") || targetUrl.startsWith("/.well-known") ||
                targetUrl.endsWith(".css") || targetUrl.endsWith(".js") || targetUrl.endsWith(".json")) {
            
            // Kiểm tra role và redirect phù hợp (chú ý Spring Security tự động thêm ROLE_ prefix)
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
                targetUrl = "/HomePage"; // Redirect admin đến homepage tạm thời
                System.out.println("Redirecting admin to: " + targetUrl);
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_employer"))) {
                targetUrl = "/HomePage"; // Redirect employer đến homepage tạm thời
                System.out.println("Redirecting employer to: " + targetUrl);
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_student"))) {
                targetUrl = "/HomePage"; // Redirect student đến homepage
                System.out.println("Redirecting student to: " + targetUrl);
            } else {
                // Default redirect cho tất cả roles khác
                targetUrl = "/HomePage";
                System.out.println("Default redirect to: " + targetUrl);
            }
        }

        System.out.println("Final redirect URL: " + targetUrl);
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}