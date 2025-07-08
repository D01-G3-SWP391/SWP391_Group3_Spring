package com.example.swp391_d01_g3.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationHelper {
    
    /**
     * Check if user is authenticated and return appropriate redirect URL
     * @return redirect URL if user is authenticated, null if user is not authenticated
     */
    public static String getRedirectUrlForAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            
            // User is already logged in, redirect to appropriate dashboard
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
                return "redirect:/Admin";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_employer"))) {
                return "redirect:/Employee";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_student"))) {
                return "redirect:/Student";
            } else {
                return "redirect:/HomePage";
            }
        }
        return null; // User is not authenticated
    }
    
    /**
     * Check if user is authenticated
     * @return true if user is authenticated, false otherwise
     */
    public static boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !(authentication instanceof AnonymousAuthenticationToken);
    }
    
    /**
     * Get current authenticated user email
     * @return user email if authenticated, null otherwise
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName(); // Returns email
        }
        return null;
    }
    
    /**
     * Check if current user has admin role
     * @return true if user is admin, false otherwise
     */
    public static boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"));
        }
        return false;
    }
} 