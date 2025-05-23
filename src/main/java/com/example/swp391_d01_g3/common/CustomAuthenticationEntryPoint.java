//package com.example.swp391_d01_g3.common;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response,
//                         AuthenticationException authException) throws IOException, ServletException {
//        String requestUri = request.getRequestURI();
//        if (requestUri.contains("favicon.ico") || requestUri.startsWith("/.well-known") ||
//                requestUri.endsWith(".css") || requestUri.endsWith(".js") || requestUri.endsWith(".json")) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//
//        // Lưu URL hiện tại vào session
//        String targetUrl = requestUri;
//        String queryString = request.getQueryString();
//        if (queryString != null) {
//            targetUrl += "?" + queryString;
//        }
//
//        request.getSession().setAttribute("REDIRECT_URL", targetUrl);
//        response.sendRedirect("/login");
//    }
//}
