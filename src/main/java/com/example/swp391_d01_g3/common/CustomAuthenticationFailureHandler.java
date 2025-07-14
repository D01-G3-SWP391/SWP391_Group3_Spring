package com.example.swp391_d01_g3.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorType = "credentials";

        // Kiểm tra OAuth2AuthenticationException cho account bị ban
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            if ("Account is banned".equals(oauth2Exception.getError().getErrorCode())) {
                errorType = "banned";
            }
        }
        // Kiểm tra DisabledException cho cả trường hợp trực tiếp và qua cause
        else if (exception instanceof DisabledException) {
            errorType = "banned";
        } else {
            Throwable cause = exception.getCause();
            if (cause instanceof DisabledException) {
                errorType = "banned";
            }
        }

        response.sendRedirect("/Login?error=" + errorType);
    }
}
