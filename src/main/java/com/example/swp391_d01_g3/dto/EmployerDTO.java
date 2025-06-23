package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;

public class EmployerDTO implements Validator {

    // Account fields
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 6, max = 100, message = "Họ và tên phải từ 6 đến 100 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹĐđ\\s]+$", message = "Họ và tên chỉ được chứa chữ cái và khoảng trắng")
    private String fullName;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$", message = "Số điện thoại không đúng định dạng Việt Nam")
    private String phone;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Mật khẩu phải chứa ít nhất 1 chữ thường, 1 chữ hoa và 1 số")
    private String password;
    
    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String confirmPassword;

    // Employer fields
    @NotBlank(message = "Tên công ty không được để trống")
    @Size(min = 2, max = 255, message = "Tên công ty phải từ 2 đến 255 ký tự")
    private String companyName;
    
    @NotBlank(message = "Địa chỉ công ty không được để trống")
    @Size(min = 5, max = 500, message = "Địa chỉ công ty phải từ 5 đến 500 ký tự")
    private String companyAddress;
    
//    @NotBlank(message = "Mô tả công ty không được để trống")
//    @Size(min = 10, max = 1000, message = "Mô tả công ty phải từ 10 đến 1000 ký tự")
//    private String companyDescription;
    
    private String logoUrl; // This might be handled as a file upload later
    
    @NotNull(message = "Vui lòng chọn lĩnh vực công việc")
    private Integer jobsFieldId;

    // Constructors
    public EmployerDTO() {
    }

    public EmployerDTO(String fullName, String email, String phone, String password, String confirmPassword, String companyName, String companyAddress, String logoUrl, Integer jobsFieldId) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
//        this.companyDescription = companyDescription;
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
        // Normalize fullName: trim, remove extra spaces, proper case
        if (fullName != null) {
            this.fullName = fullName.trim().replaceAll("\\s+", " ");
        } else {
            this.fullName = fullName;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        // Normalize email: trim and toLowerCase
        if (email != null) {
            this.email = email.trim().toLowerCase();
        } else {
            this.email = email;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        // Normalize phone: remove spaces and convert +84 to 0
        if (phone != null) {
            String normalizedPhone = phone.trim().replaceAll("\\s+", "");
            if (normalizedPhone.startsWith("+84")) {
                normalizedPhone = "0" + normalizedPhone.substring(3);
            } else if (normalizedPhone.startsWith("84")) {
                normalizedPhone = "0" + normalizedPhone.substring(2);
            }
            this.phone = normalizedPhone;
        } else {
            this.phone = phone;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        // Normalize company name: trim and remove extra spaces
        if (companyName != null) {
            this.companyName = companyName.trim().replaceAll("\\s+", " ");
        } else {
            this.companyName = companyName;
        }
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        // Normalize address: trim and remove extra spaces
        if (companyAddress != null) {
            this.companyAddress = companyAddress.trim().replaceAll("\\s+", " ");
        } else {
            this.companyAddress = companyAddress;
        }
    }

//    public String getCompanyDescription() {
//        return companyDescription;
//    }

//    public void setCompanyDescription(String companyDescription) {
//        // Normalize description: trim and remove extra spaces
//        if (companyDescription != null) {
//            this.companyDescription = companyDescription.trim().replaceAll("\\s+", " ");
//        } else {
//            this.companyDescription = companyDescription;
//        }
//    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Integer getJobsFieldId() {
        return jobsFieldId;
    }

    public void setJobsFieldId(Integer jobsFieldId) {
        this.jobsFieldId = jobsFieldId;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return EmployerDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EmployerDTO employerDTO = (EmployerDTO) target;
        
        // Validate password confirmation
        if (employerDTO.getPassword() != null && employerDTO.getConfirmPassword() != null) {
            if (!employerDTO.getPassword().equals(employerDTO.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
            }
        }
        
        // Additional validation for fullName to prevent numbers
        if (employerDTO.getFullName() != null && employerDTO.getFullName().matches(".*\\d.*")) {
            errors.rejectValue("fullName", "fullName.containsNumbers", "Họ và tên không được chứa số");
        }
        
        // Validate phone number doesn't contain repeated patterns
        if (employerDTO.getPhone() != null) {
            String phone = employerDTO.getPhone();
            if (phone.matches("(.)\\1{9,}")) { // Same digit repeated 10+ times
                errors.rejectValue("phone", "phone.invalidPattern", "Số điện thoại không hợp lệ");
            }
        }
    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
} 