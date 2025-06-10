package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AccountDTO implements Validator {
    private Long idUser;
    
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 6, max = 100, message = "Họ và tên phải từ 6 đến 100 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹĐđ\\s]+$", message = "Họ và tên chỉ được chứa chữ cái và khoảng trắng")
    private String fullName;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[1-9][0-9]{8,9}$", message = "Số điện thoại không đúng định dạng (10-11 số, bắt đầu bằng 0 hoặc +84)")
    private String phone;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Mật khẩu phải chứa ít nhất 1 chữ thường, 1 chữ hoa và 1 số")
    private String password;
    
    // Trường xác nhận mật khẩu - không cần lưu vào database
    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String confirmPassword;

    public AccountDTO() {
    }

    public AccountDTO(Long idUser, String fullName, String email, String phone, String password) {
        this.idUser = idUser;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AccountDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountDTO accountDTO = (AccountDTO) target;
        
        // Custom validation cho họ tên
        if (accountDTO.getFullName() != null) {
            String fullName = accountDTO.getFullName().trim();
            if (fullName.length() > 0) {
                // Kiểm tra không có số trong tên
                if (fullName.matches(".*\\d.*")) {
                    errors.rejectValue("fullName", "error.fullName", "Họ và tên không được chứa số");
                }
                
                // Kiểm tra không có nhiều khoảng trắng liên tiếp
                if (fullName.matches(".*\\s{2,}.*")) {
                    errors.rejectValue("fullName", "error.fullName", "Họ và tên không được chứa nhiều khoảng trắng liên tiếp");
                }
                
                // Kiểm tra các ký tự đặc biệt không mong muốn
                if (fullName.matches(".*[0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                    errors.rejectValue("fullName", "error.fullName", "Họ và tên chỉ được chứa chữ cái và khoảng trắng");
                }
            }
        }
        
        // Custom validation cho email
        if (accountDTO.getEmail() != null) {
            String email = accountDTO.getEmail().trim().toLowerCase();
            // Kiểm tra email có domain hợp lệ và không chứa ký tự đặc biệt
            if (email.length() > 0 && !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                errors.rejectValue("email", "error.email", "Email không đúng định dạng");
            }
        }
        
        // Custom validation cho số điện thoại
        if (accountDTO.getPhone() != null) {
            String phone = accountDTO.getPhone().trim();
            if (phone.length() > 0) {
                // Chuẩn hóa số điện thoại Việt Nam
                if (phone.startsWith("+84")) {
                    phone = "0" + phone.substring(3);
                }
                
                // Kiểm tra đầu số hợp lệ của Việt Nam
                if (!phone.matches("^0(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$")) {
                    errors.rejectValue("phone", "error.phone", "Số điện thoại không đúng định dạng Việt Nam");
                }
            }
        }
        
        // Custom validation cho mật khẩu
        if (accountDTO.getPassword() != null) {
            String password = accountDTO.getPassword();
            if (password.length() > 0) {
                // Kiểm tra không chứa khoảng trắng
                if (password.contains(" ")) {
                    errors.rejectValue("password", "error.password", "Mật khẩu không được chứa khoảng trắng");
                }
                
                int complexity = 0;
                if (password.matches(".*[a-z].*")) complexity++;
                if (password.matches(".*[A-Z].*")) complexity++;
                if (password.matches(".*\\d.*")) complexity++;
                if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) complexity++;
                
                if (complexity < 3) {
                    errors.rejectValue("password", "error.password", "Mật khẩu cần có ít nhất 3 trong 4 yếu tố: chữ thường, chữ hoa, số, ký tự đặc biệt");
                }
            }
        }
        
        // Validation xác nhận mật khẩu
        if (accountDTO.getPassword() != null && accountDTO.getConfirmPassword() != null) {
            if (!accountDTO.getPassword().equals(accountDTO.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp");
            }
        }
    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}
