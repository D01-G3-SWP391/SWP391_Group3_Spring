package com.example.swp391_d01_g3.service.security;

import com.example.swp391_d01_g3.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private IAccountService accountService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load user info from Google
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        // Get email from OAuth2 user attributes
        String email = oauth2User.getAttribute("email");
        
        if (email != null) {
            // Check if account exists and is banned
            Account existingAccount = accountService.findByEmailAnyStatus(email);
            
            if (existingAccount != null && existingAccount.getStatus() == Account.Status.inactive) {
                // Account is banned - throw exception to prevent authentication
                OAuth2Error oauth2Error = new OAuth2Error("Account is banned", "Your account has been banned from the system", null);
                throw new OAuth2AuthenticationException(oauth2Error);
            }
        }
        
        return oauth2User;
    }
} 