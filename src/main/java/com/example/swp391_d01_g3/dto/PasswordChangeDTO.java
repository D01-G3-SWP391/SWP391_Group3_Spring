package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.*;

public class PasswordChangeDTO {
    
    private String currentPassword;
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Mật khẩu phải chứa ít nhất 1 chữ thường, 1 chữ hoa và 1 số")
    private String newPassword;
    
    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String confirmPassword;

    public PasswordChangeDTO() {
    }

    public PasswordChangeDTO(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword != null ? currentPassword.trim() : null;
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