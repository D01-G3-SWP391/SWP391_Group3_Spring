package com.example.swp391_d01_g3.controller.emailverification;

import com.example.swp391_d01_g3.dto.PendingRegisterDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/Register/EmailVerification")
public class EmailVerificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private IAccountService accountService;
    
    @Autowired
    private IStudentService studentService;
    
    @Autowired
    private IEmployerService employerService;
    
    @Autowired
    private IJobfieldService jobfieldService;

    @GetMapping("/verify")
    public String showVerifyForm(@RequestParam("email") String email, Model model, HttpSession session) {
        PendingRegisterDTO pendingRegistration = (PendingRegisterDTO) session.getAttribute("pendingRegistration");
        
        if (pendingRegistration == null || !pendingRegistration.getEmail().equals(email)) {
            model.addAttribute("error", "Phiên đăng ký đã hết hạn hoặc không hợp lệ. Vui lòng đăng ký lại.");
            return "register/registerPage";
        }
        
        model.addAttribute("email", email);
        model.addAttribute("fullName", pendingRegistration.getFullName());
        model.addAttribute("message", "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra và nhập mã để hoàn tất đăng ký.");
        return "register/verifyEmailForm";
    }

    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam("email") String email, 
                           @RequestParam("otp") Integer otp, 
                           RedirectAttributes redirectAttributes,
                           Model model,
                           HttpSession session) {
        try {
            PendingRegisterDTO pendingRegistration = (PendingRegisterDTO) session.getAttribute("pendingRegistration");
            Integer sessionOTP = (Integer) session.getAttribute("registrationOTP");
            Date expirationTime = (Date) session.getAttribute("otpExpirationTime");
            
            // Kiểm tra session data
            if (pendingRegistration == null || sessionOTP == null || expirationTime == null) {
                model.addAttribute("email", email);
                model.addAttribute("error", "Phiên xác thực đã hết hạn. Vui lòng đăng ký lại.");
                return "register/verifyEmailForm";
            }
            
            // Kiểm tra email khớp
            if (!pendingRegistration.getEmail().equals(email)) {
                model.addAttribute("email", email);
                model.addAttribute("error", "Email không khớp với phiên đăng ký.");
                return "register/verifyEmailForm";
            }
            
            // Kiểm tra OTP khớp
            if (!sessionOTP.equals(otp)) {
                model.addAttribute("email", email);
                model.addAttribute("fullName", pendingRegistration.getFullName());
                model.addAttribute("error", "Mã OTP không chính xác. Vui lòng thử lại.");
                return "register/verifyEmailForm";
            }
            
            // Kiểm tra OTP hết hạn
            if (expirationTime.before(new Date())) {
                model.addAttribute("email", email);
                model.addAttribute("fullName", pendingRegistration.getFullName());
                model.addAttribute("error", "Mã OTP đã hết hạn. Vui lòng gửi lại mã mới.");
                return "register/verifyEmailForm";
            }
            
            // OTP hợp lệ - tạo account thực sự
            Account savedAccount = emailService.createAccountAfterVerification(pendingRegistration);
            
            if (savedAccount != null) {
                // Tạo Student hoặc Employer tùy theo role
                if (pendingRegistration.getRole() == Account.Role.student) {
                    Student student = new Student();
                    student.setAccount(savedAccount);
                    studentService.save(student);
                } else if (pendingRegistration.getRole() == Account.Role.employer) {
                    Employer employer = new Employer();
                    employer.setCompanyName(pendingRegistration.getCompanyName());
                    employer.setCompanyAddress(pendingRegistration.getCompanyAddress());
                    employer.setCompanyDescription(pendingRegistration.getCompanyDescription());
                    // Lưu logo vào account.avatarUrl thay vì employer.logoUrl
                    savedAccount.setAvatarUrl(pendingRegistration.getLogoUrl());
                    accountService.save(savedAccount);
                    employer.setAccount(savedAccount);
                    
                    // Set JobField
                    if (pendingRegistration.getJobsFieldId() != null) {
                        Optional<JobField> jobField = jobfieldService.findById(pendingRegistration.getJobsFieldId());
                        jobField.ifPresent(employer::setJobField);
                    }
                    
                    employerService.saveEmployer(employer);
                }
                
                // Xóa session data
                session.removeAttribute("pendingRegistration");
                session.removeAttribute("registrationOTP");
                session.removeAttribute("otpExpirationTime");
                
                redirectAttributes.addFlashAttribute("messages", 
                    "🎉 Đăng ký thành công!");
                return "redirect:/Login";
            } else {
                model.addAttribute("email", email);
                model.addAttribute("error", "Đã xảy ra lỗi khi tạo tài khoản. Vui lòng thử lại.");
                return "register/verifyEmailForm";
            }
            
        } catch (Exception e) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình xác thực. Vui lòng thử lại.");
            return "register/verifyEmailForm";
        }
    }

    @PostMapping("/resendOTP")
    public String resendOTP(@RequestParam("email") String email, Model model, HttpSession session) {
        PendingRegisterDTO pendingRegistration = (PendingRegisterDTO) session.getAttribute("pendingRegistration");
        
        if (pendingRegistration == null || !pendingRegistration.getEmail().equals(email)) {
            model.addAttribute("error", "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.");
            return "register/registerPage";
        }
        
        try {
            // Gửi OTP mới
            CompletableFuture<Integer> newOTPFuture = emailService.sendVerifyMailForRegistration(email, pendingRegistration.getFullName());
            
            // Lấy giá trị OTP từ CompletableFuture
            Integer newOTP = newOTPFuture.get(); // Chờ async operation hoàn thành
            
            // Cập nhật session
            session.setAttribute("registrationOTP", newOTP);
            session.setAttribute("otpExpirationTime", new Date(System.currentTimeMillis() + 10 * 60 * 1000)); // 10 phút
            
            System.out.println("New OTP sent successfully to: " + email + " with OTP: " + newOTP);
        } catch (Exception e) {
            System.err.println("Error resending OTP to: " + email + " - Error: " + e.getMessage());
            model.addAttribute("error", "Đã xảy ra lỗi khi gửi lại mã OTP. Vui lòng thử lại sau.");
        }
        
        model.addAttribute("email", email);
        model.addAttribute("fullName", pendingRegistration.getFullName());
        model.addAttribute("message", "Mã OTP mới đã được gửi đến email của bạn.");
        return "register/verifyEmailForm";
    }

    @GetMapping("/required")
    public String showEmailVerificationRequired(@RequestParam(value = "email", required = false) String email, Model model) {
        if (email != null && !email.isEmpty()) {
            Account account = accountService.findByEmail(email);
            if (account != null && account.getStatus() == Account.Status.inactive) {
                model.addAttribute("email", email);
                model.addAttribute("fullName", account.getFullName());
                model.addAttribute("message", "Tài khoản của bạn chưa được kích hoạt. Vui lòng xác thực email để tiếp tục.");
                return "register/emailRequired";
            }
        }
        return "redirect:/Login";
    }
} 