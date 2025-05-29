package com.example.swp391_d01_g3.controller.email;

import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.swp391_d01_g3.model.Account;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class EmailController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private IAccountService accountService;

    @GetMapping("/send-email")
    public String showSendEmailForm() {
        return "email/sendEmail";
    }

    @PostMapping("/send-email")
    public  String sendEmail(@RequestParam String email, Model model) {
        try {
            Account account = accountService.findByEmail(email);
            if (account == null) {
                model.addAttribute("error", "Email không tồn tại!");
                return "email/sendEmail";
            }

            String subject = "Chào mừng bạn đến với ứng dụng SWP391_D01_G3!";
            String body = "Xin chào " + account.getFullName() + ",\n\n"
                    + "Cảm ơn bạn đã sử dụng ứng dụng của chúng tôi.\n"
                    + "Trân trọng,\nĐội ngũ SWP391_D01_G3";
            emailService.sendEmail(email, subject, body);

            model.addAttribute("message", "Email đã được gửi thành công đến " + email);
        } catch (Exception e) {
            model.addAttribute("error", "Gửi email thất bại: " + e.getMessage());
        }
        return "email/sendEmailLogin";
    }
}
