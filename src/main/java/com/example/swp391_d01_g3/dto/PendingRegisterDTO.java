package com.example.swp391_d01_g3.dto;

import com.example.swp391_d01_g3.model.Account;
import lombok.Data;

import java.io.Serializable;

@Data
public class PendingRegisterDTO implements Serializable {
    private String fullName;
    private String email;
    private String password; // Already encoded
    private String phone;
    private Account.Role role;
    private String avatarUrl;
    
    // Additional fields for Employer registration
    private String companyName;
    private String companyAddress;
    private String companyDescription;
    private String logoUrl;
    private Integer jobsFieldId;
} 