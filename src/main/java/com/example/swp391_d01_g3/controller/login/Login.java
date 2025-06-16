package com.example.swp391_d01_g3.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/Login")
public class Login {

    @GetMapping("")
    public String login(@RequestParam(value = "returnUrl", required = false) String returnUrl,
                       HttpServletRequest request){
        // If returnUrl is provided, store it in session for after login redirect
        if (returnUrl != null && !returnUrl.isEmpty() && 
            !returnUrl.contains("favicon.ico") && !returnUrl.startsWith("/.well-known")) {
            request.getSession().setAttribute("REDIRECT_URL", returnUrl);
            System.out.println("Stored REDIRECT_URL in session: " + returnUrl);
        }
        

        return "login/loginPage";
    }
}
