package com.example.swp391_d01_g3.dto;

// Add imports for validation if needed, e.g., javax.validation.constraints.* or jakarta.validation.constraints.*

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;

public class EmployerDTO implements Validator {

    // Account fields
    private String fullName;
    private String email;
    private String phone;
    private String password;
    // Consider adding a password confirmation field: private String confirmPassword;

    // Employer fields
    private String companyName;
    private String companyAddress;
    private String companyDescription;
    private String logoUrl; // This might be handled as a file upload later
    private Integer jobsFieldId; // Đổi sang Integer

    // Constructors
    public EmployerDTO() {
    }

    public EmployerDTO(String fullName, String email, String phone, String password, String companyName, String companyAddress, String companyDescription, String logoUrl, Integer jobsFieldId) { // Đổi sang Integer
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyDescription = companyDescription;
        this.logoUrl = logoUrl;
        this.jobsFieldId = jobsFieldId; 
    }

    // Constructor for edit profile - tạo từ Account và Employer
    public EmployerDTO(Account account, Employer employer) {
        if (account != null) {
            this.fullName = account.getFullName();
            this.email = account.getEmail();
            this.phone = account.getPhone();
        }
        if (employer != null) {
            this.companyName = employer.getCompanyName();
            this.companyAddress = employer.getCompanyAddress();
            this.companyDescription = employer.getCompanyDescription();
            this.logoUrl = employer.getLogoUrl();
            if (employer.getJobField() != null) {
                this.jobsFieldId = employer.getJobField().getJobFieldId();
            }
        }
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Integer getJobsFieldId() { // Đổi sang Integer
        return jobsFieldId;
    }

    public void setJobsFieldId(Integer jobsFieldId) { // Đổi sang Integer
        this.jobsFieldId = jobsFieldId;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
} 