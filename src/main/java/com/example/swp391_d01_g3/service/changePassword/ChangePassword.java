package com.example.swp391_d01_g3.service.changePassword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePassword {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Kiểm tra mật khẩu hiện tại có đúng không
    public boolean isCurrentPasswordValid(String currentPassword, String encodedPassword) {
        return passwordEncoder.matches(currentPassword, encodedPassword);
    }

    // 2. Kiểm tra mật khẩu mới và xác nhận mật khẩu có trùng nhau không
    public boolean isNewPasswordConfirmed(String newPassword, String confirmPassword) {
        return newPassword.equals(confirmPassword);
    }

    // 3. Kiểm tra mật khẩu mới có đủ độ dài tối thiểu không
    public boolean isNewPasswordValidLength(String newPassword, int minLength) {
        return newPassword != null && newPassword.length() >= minLength;
    }

    // 4. Kiểm tra mật khẩu mới có khác mật khẩu cũ không
    public boolean isNewPasswordDifferent(String newPassword, String encodedOldPassword) {
        return !passwordEncoder.matches(newPassword, encodedOldPassword);
    }

}
