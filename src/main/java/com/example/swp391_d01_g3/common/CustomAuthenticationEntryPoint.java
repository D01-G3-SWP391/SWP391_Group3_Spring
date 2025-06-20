package com.example.swp391_d01_g3.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        
        // Ignore static resources and return 404
        if (requestUri.contains("favicon.ico") || requestUri.startsWith("/.well-known") ||
                requestUri.endsWith(".css") || requestUri.endsWith(".js") || requestUri.endsWith(".json")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Don't save API endpoints to REDIRECT_URL - they should return 401 for AJAX calls
        boolean isApiEndpoint = requestUri.startsWith("/notifications/") || 
                               requestUri.startsWith("/api/") ||
                               requestUri.contains("/latest") ||
                               requestUri.contains("/count") ||
                               requestUri.contains("/mark-read") ||
                               requestUri.contains("/delete");
        
        if (isApiEndpoint) {
            // For API endpoints, return 401 status for proper AJAX error handling
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication required\"}");
            return;
        }

        // Only save non-API URLs for redirect after login
        String targetUrl = requestUri;
        String queryString = request.getQueryString();
        if (queryString != null) {
            targetUrl += "?" + queryString;
        }

        request.getSession().setAttribute("REDIRECT_URL", targetUrl);
        response.sendRedirect("/Login");
    }
}
