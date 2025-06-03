package com.example.swp391_d01_g3.controller.forgotpassword;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.ForgotPassword;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.ForgotPasswordRepository;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import com.example.swp391_d01_g3.service.changePassword.ChangePassword;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.security.IAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            return "forgotPassword/enterNewPassword";
        } catch (RuntimeException e) {
            model.addAttribute("message", e.getMessage());
            return "forgotPassword/enterOTPForm";
        }
    }

    @PostMapping("/ChangePassword")
    public String changePassword(@RequestParam String email,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {
        Account account = iAccountService.findByEmail(email);
        if (account == null) {
            model.addAttribute("message", "Email không tồn tại. Vui lòng xác thực lại email và OTP.");
            return "login/loginPage";
        }
        if (!changePassword.isNewPasswordConfirmed(newPassword, confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "forgotPassword/enterNewPassword";
        }

        // Sử dụng service để kiểm tra độ dài mật khẩu
        if (!changePassword.isNewPasswordValidLength(newPassword, 6)) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            return "forgotPassword/enterNewPassword";
        }

        // Sử dụng service để kiểm tra mật khẩu mới không giống mật khẩu cũ
        if (!changePassword.isNewPasswordDifferent(newPassword, account.getPassword())) {
            model.addAttribute("error", "Mật khẩu mới phải khác mật khẩu hiện tại.");

            return "forgotPassword/enterNewPassword";
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        iAccountService.save(account);
        model.addAttribute("changePass", "Đổi mật khẩu thành công.");
        return "login/loginPage";
    }
    }


