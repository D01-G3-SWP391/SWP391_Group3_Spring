package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.*;

public class ForgotPasswordDTO {
    
    private String email;
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Mật khẩu phải chứa ít nhất 1 chữ thường, 1 chữ hoa và 1 số")
    private String newPassword;
    
    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String confirmPassword;

    public ForgotPasswordDTO() {
    }

    public ForgotPasswordDTO(String email, String newPassword, String confirmPassword) {
        this.email = email;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword != null ? newPassword.trim() : null;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword != null ? confirmPassword.trim() : null;
    }
} 