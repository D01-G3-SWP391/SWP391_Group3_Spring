package com.example.swp391_d01_g3.controller.forgotpassword;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.ForgotPassword;
import com.example.swp391_d01_g3.repository.ForgotPasswordRepository;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import com.example.swp391_d01_g3.service.email.EmailService;
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
    private IAccountRepository iAccountRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    public String showForgotForm(){
        return "forgotPassword/enterMailForm";
    }
    @GetMapping("/VerifyMail")
    public String verifyEmail(@RequestParam("email") String email, Model model) {
        Account account = iAccountRepository.findByEmail(email);
        if (account != null) {
            emailService.sendForgotPassEmail(email);
            model.addAttribute("message", "OTP đã được gửi tới email của bạn.");
            model.addAttribute("account",account);
        } else {
            model.addAttribute("message", "Không tìm thấy email.");
        }
        return "forgotPassword/enterOTPForm";
    }
    @PostMapping("/VerifyOTP")
    public String verifyOTP(@RequestParam Integer otp, Model model, RedirectAttributes redirectAttributes) {
        try {
            ForgotPassword fp = forgotPasswordRepository.findByOtpAndAccount(otp)
                    .orElseThrow(() -> new RuntimeException("OTP không hợp lệ cho email"));

            if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
                forgotPasswordRepository.deleteById(fp.getFpid());
                model.addAttribute("otp_failed", "OTP đã hết hạn!");
                return "forgotPassword/enterOTPForm";
            }
            redirectAttributes.addFlashAttribute("otp_success","OTP đã xác thực thành công");
            model.addAttribute("accountEmail", fp.getAccount().getEmail());
            return "forgotPassword/enterNewPassword";
        } catch (RuntimeException e) {
            model.addAttribute("message", e.getMessage());
            return "forgotPassword/enterOTPForm";
        }
    }

    @PostMapping("/ChangePassword")
    public String changePassword(@RequestParam String email,
                                 @RequestParam("password") String newPassword,
                                 Model model) {
        Account account = iAccountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("message", "Email không tồn tại. Vui lòng xác thực lại email và OTP.");
            return "login/loginPage";
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        iAccountRepository.save(account);
        model.addAttribute("changepass", "Đổi mật khẩu thành công.");
        return "login/loginPage";
    }
    }


