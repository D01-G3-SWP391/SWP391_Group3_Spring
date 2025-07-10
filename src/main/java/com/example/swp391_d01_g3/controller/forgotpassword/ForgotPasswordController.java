package com.example.swp391_d01_g3.controller.forgotpassword;

import com.example.swp391_d01_g3.dto.ForgotPasswordDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.ForgotPassword;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.ForgotPasswordRepository;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import com.example.swp391_d01_g3.service.changePassword.ChangePassword;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.security.IAccountServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.Date;


@Controller
@RequestMapping("/ForgotPassword")
public class ForgotPasswordController {
    @Autowired
    private IAccountServiceImpl iAccountService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChangePassword changePassword;

    @GetMapping("")
    public String showForgotForm(){
        return "forgotPassword/enterMailForm";
    }
    @GetMapping("/VerifyMail")
    public String verifyEmail(@RequestParam("email") String email, Model model) {
        Account account = iAccountService.findByEmail(email);
        if (account != null) {
            emailService.sendForgotPassEmail(email);
            model.addAttribute("sendOtp", "OTP đã được gửi tới email của bạn.");
            model.addAttribute("account",account);
        } else {
            model.addAttribute("emailNotFound", "Không tìm thấy email.");
            model.addAttribute("reEnterEmail", "Vui lòng nhập địa chỉ email hợp lệ.");
            return "forgotPassword/enterMailForm";
        }
        return "forgotPassword/enterOTPForm";
    }
    @PostMapping("/VerifyOTP")
    public String verifyOTP(@RequestParam Integer otp, Model model) {
        try {
            ForgotPassword fp = forgotPasswordRepository.findByOtpAndAccount(otp)
                    .orElseThrow(() -> new RuntimeException("OTP không hợp lệ cho email"));

            if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
                forgotPasswordRepository.deleteById(fp.getFpid());
                model.addAttribute("otp_failed", "OTP đã hết hạn!");
                return "forgotPassword/enterOTPForm";
            }
            model.addAttribute("otpSuccess", "OTP đã xác thực thành công");
            model.addAttribute("accountEmail", fp.getAccount().getEmail());
            model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
            return "forgotPassword/enterNewPassword";
        } catch (RuntimeException e) {
            model.addAttribute("message", e.getMessage());
            return "forgotPassword/enterOTPForm";
        }
    }

    @PostMapping("/ChangePassword")
    public String changePassword(@RequestParam String email,
                                 @Valid @ModelAttribute ForgotPasswordDTO forgotPasswordDTO,
                                 BindingResult bindingResult,
                                 Model model) {
        
        // Chuẩn hóa dữ liệu đầu vào
        if (forgotPasswordDTO.getNewPassword() != null) {
            forgotPasswordDTO.setNewPassword(forgotPasswordDTO.getNewPassword().trim());
        }
        if (forgotPasswordDTO.getConfirmPassword() != null) {
            forgotPasswordDTO.setConfirmPassword(forgotPasswordDTO.getConfirmPassword().trim());
        }
        
        // Set email vào DTO
        forgotPasswordDTO.setEmail(email);
        
        Account account = iAccountService.findByEmail(email);
        if (account == null) {
            model.addAttribute("message", "Email không tồn tại. Vui lòng xác thực lại email và OTP.");
            return "login/loginPage";
        }

        // Sử dụng validation service mới
        changePassword.validateNewPasswordOnly(
            forgotPasswordDTO.getNewPassword(), 
            forgotPasswordDTO.getConfirmPassword(), 
            bindingResult
        );

        // Kiểm tra mật khẩu mới không giống mật khẩu cũ
        if (forgotPasswordDTO.getNewPassword() != null && 
            !changePassword.isNewPasswordDifferent(forgotPasswordDTO.getNewPassword(), account.getPassword())) {
            bindingResult.rejectValue("newPassword", "password.sameAsOld", "Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        // Kiểm tra có lỗi validation không
        if (bindingResult.hasErrors()) {
            // Log errors for debugging
            System.out.println("Forgot password validation errors found:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("- " + error.getDefaultMessage());
            });
            
            model.addAttribute("forgotPasswordDTO", forgotPasswordDTO);
            model.addAttribute("accountEmail", email);
            
            // Hiển thị lỗi đầu tiên
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            model.addAttribute("error", errorMessage);
            return "forgotPassword/enterNewPassword";
        }

        // Cập nhật mật khẩu
        account.setPassword(passwordEncoder.encode(changePassword.normalizePassword(forgotPasswordDTO.getNewPassword())));
        iAccountService.save(account);
        model.addAttribute("changePass", "Đổi mật khẩu thành công.");
        return "login/loginPage";
    }
    }


