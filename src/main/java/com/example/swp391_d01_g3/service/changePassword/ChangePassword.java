package com.example.swp391_d01_g3.service.changePassword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

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
        return newPassword != null && confirmPassword != null && newPassword.equals(confirmPassword);
    }

    // 3. Kiểm tra mật khẩu mới có đủ độ dài tối thiểu không
    public boolean isNewPasswordValidLength(String newPassword, int minLength) {
        return newPassword != null && newPassword.length() >= minLength;
    }

    // 4. Kiểm tra mật khẩu mới có khác mật khẩu cũ không
    public boolean isNewPasswordDifferent(String newPassword, String encodedOldPassword) {
        return !passwordEncoder.matches(newPassword, encodedOldPassword);
    }

    // 5. Chuẩn hóa dữ liệu mật khẩu
    public String normalizePassword(String password) {
        if (password == null) return null;
        return password.trim();
    }

    // 6. Validate mật khẩu với các rule phức tạp (giống như trang đăng ký)
    public void validatePassword(String password, String confirmPassword, Errors errors) {
        if (password == null || password.trim().isEmpty()) {
            errors.rejectValue("newPassword", "password.required", "Mật khẩu không được để trống");
            return;
        }

        String normalizedPassword = normalizePassword(password);

        // Kiểm tra độ dài
        if (normalizedPassword.length() < 8) {
            errors.rejectValue("newPassword", "password.tooShort", "Mật khẩu phải có ít nhất 8 ký tự");
        }

        if (normalizedPassword.length() > 50) {
            errors.rejectValue("newPassword", "password.tooLong", "Mật khẩu không được vượt quá 50 ký tự");
        }

        // Kiểm tra không chứa khoảng trắng
        if (normalizedPassword.contains(" ")) {
            errors.rejectValue("newPassword", "password.noWhitespace", "Mật khẩu không được chứa khoảng trắng");
        }

        // Kiểm tra độ phức tạp (giống như AccountDTO validation)
        int complexity = 0;
        if (normalizedPassword.matches(".*[a-z].*")) complexity++; // Chữ thường
        if (normalizedPassword.matches(".*[A-Z].*")) complexity++; // Chữ hoa
        if (normalizedPassword.matches(".*\\d.*")) complexity++;   // Số
        if (normalizedPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) complexity++; // Ký tự đặc biệt

        if (complexity < 3) {
            errors.rejectValue("newPassword", "password.complexity", 
                "Mật khẩu cần có ít nhất 3 trong 4 yếu tố: chữ thường, chữ hoa, số, ký tự đặc biệt");
        }

        // Kiểm tra xác nhận mật khẩu
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            errors.rejectValue("confirmPassword", "confirmPassword.required", "Vui lòng xác nhận mật khẩu");
        } else if (!normalizedPassword.equals(normalizePassword(confirmPassword))) {
            errors.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }
    }

    // 7. Validate mật khẩu với mật khẩu cũ
    public void validatePasswordWithOld(String currentPassword, String newPassword, String confirmPassword, 
                                      String encodedOldPassword, Errors errors) {
        
        // Validate basic password rules
        validatePassword(newPassword, confirmPassword, errors);

        // Kiểm tra mật khẩu hiện tại (chỉ cho change password, không cho forgot password)
        if (currentPassword != null) {
            if (currentPassword.trim().isEmpty()) {
                errors.rejectValue("currentPassword", "currentPassword.required", "Vui lòng nhập mật khẩu hiện tại");
            } else if (!isCurrentPasswordValid(currentPassword, encodedOldPassword)) {
                errors.rejectValue("currentPassword", "currentPassword.invalid", "Mật khẩu hiện tại không đúng");
            }
        }

        // Kiểm tra mật khẩu mới khác mật khẩu cũ
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (!isNewPasswordDifferent(normalizePassword(newPassword), encodedOldPassword)) {
                errors.rejectValue("newPassword", "password.sameAsOld", "Mật khẩu mới phải khác mật khẩu hiện tại");
            }
        }
    }

    // 8. Validate chỉ mật khẩu mới (cho forgot password)
    public void validateNewPasswordOnly(String newPassword, String confirmPassword, Errors errors) {
        validatePassword(newPassword, confirmPassword, errors);
    }
}
