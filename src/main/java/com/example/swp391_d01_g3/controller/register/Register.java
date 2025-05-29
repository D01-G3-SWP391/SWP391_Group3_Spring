package com.example.swp391_d01_g3.controller.register;

import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/Register")
public class Register {

    @Autowired
    private IJobfieldService iJobfieldService;

    @Autowired
    private IStudentService iStudentService;

    @Autowired
    private IEmployerService iEmployerService;

    @Autowired
    private IAccountService iAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    @GetMapping("")
    public String showRegister (){
        return "register/registerPage";
    }
    @GetMapping("/Student")
    public String showRegisterStudent(Model model){
        AccountDTO accountDTO = new AccountDTO();
        model.addAttribute("accountDTO",accountDTO);
        return "register/registerStudentPage";
    }
    @PostMapping("/registerStudent")
    public String registerStudent(@Valid @ModelAttribute("accountDTO") AccountDTO accountDTO, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes, Model model){
        new AccountDTO().validate(accountDTO,bindingResult);
        if (bindingResult.hasErrors()){
            return "register/registerStudentPage";
        }
        Account account = new Account();
        BeanUtils.copyProperties(accountDTO,account);
        account.setRole(Account.Role.student);
        account.setStatus(Account.Status.active);
        account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        Account savedAccount = iAccountService.save(account);

        if (savedAccount != null && savedAccount.getUserId() != null) {
            Student student = new Student();
            student.setAccount(savedAccount);
            iStudentService.save(student);
            
            // Gửi email chào mừng
            sendWelcomeEmail(savedAccount.getEmail(), savedAccount.getFullName(), "Student");
        }
        
        redirectAttributes.addFlashAttribute("messages", "Registration successful!");
        return "redirect:/Login";
    }
    @GetMapping("/Employer")
    public String showRegisterEmployer(Model model){
        AccountEmployerDTO accountEmployerDTO = new AccountEmployerDTO();
        model.addAttribute("jobFields",iJobfieldService.findAll());
        model.addAttribute("accountEmployerDTO",accountEmployerDTO);
        return "register/registerEmployerPage";
    }
    @PostMapping("/registerEmployer")
    public String registerEmployer(@Valid @ModelAttribute("accountEmployerDTO") AccountEmployerDTO accountEmployerDTO, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, Model model){
        new AccountEmployerDTO().validate(accountEmployerDTO,bindingResult);
        if (bindingResult.hasErrors()){
            return "register/registerEmployerPage";
        }
        Account account = new Account();
        BeanUtils.copyProperties(accountEmployerDTO,account);
        account.setRole(Account.Role.employer);
        account.setStatus(Account.Status.active);
        account.setPassword(passwordEncoder.encode(accountEmployerDTO.getPassword()));
        Account savedAccount = iAccountService.save(account);

        if (savedAccount == null || savedAccount.getUserId() == null) {
            model.addAttribute("accountEmployerDTO", accountEmployerDTO);
            model.addAttribute("jobFields",iJobfieldService.findAll());
            model.addAttribute("errorMessage", "Lỗi khi tạo tài khoản. Vui lòng thử lại.");
            return "register/registerEmployerPage";
        }

        Employer employer = new Employer();
        employer.setCompanyName(accountEmployerDTO.getCompanyName());
        employer.setCompanyAddress(accountEmployerDTO.getCompanyAddress());
        employer.setCompanyDescription(accountEmployerDTO.getCompanyDescription());
        employer.setLogoUrl(accountEmployerDTO.getLogoUrl());

        Optional<JobField> jobFieldOptional = iJobfieldService.findById(accountEmployerDTO.getJobsFieldId());
        if (jobFieldOptional.isPresent()) {
            employer.setJobField(jobFieldOptional.get());
        } else {
            model.addAttribute("accountEmployerDTO", accountEmployerDTO);
            model.addAttribute("jobFields",iJobfieldService.findAll());
            model.addAttribute("errorMessage", "Lĩnh vực công việc không hợp lệ.");
            return "register/registerEmployerPage";
        }

        employer.setAccount(savedAccount);
        iEmployerService.saveEmployer(employer);
        
        // Gửi email chào mừng
        sendWelcomeEmail(savedAccount.getEmail(), savedAccount.getFullName(), "Employer");

        redirectAttributes.addFlashAttribute("messages", "Registration successful!");
        return "redirect:/Login";
    }
    
    /**
     * Gửi email chào mừng cho thành viên mới đăng ký
     */
    private void sendWelcomeEmail(String email, String fullName, String role) {
        try {
            String subject = "🎉 Chào mừng bạn đến với SWP391 Job Portal!";
            String roleText = role.equals("Student") ? "Sinh viên" : "Nhà tuyển dụng";
            
            String body = "Xin chào " + fullName + ",\n\n" +
                         "🎉 Chào mừng bạn đã gia nhập cộng đồng SWP391 Job Portal với vai trò " + roleText + "!\n\n" +
                         "✅ Tài khoản của bạn đã được tạo thành công\n" +
                         "📧 Email đăng nhập: " + email + "\n" +
                         "👤 Vai trò: " + roleText + "\n\n";
            
            if (role.equals("Student")) {
                body += "🚀 Bạn có thể bắt đầu:\n" +
                       "   • Tìm kiếm việc làm phù hợp\n" +
                       "   • Cập nhật hồ sơ cá nhân\n" +
                       "   • Tham gia các sự kiện tuyển dụng\n" +
                       "   • Nộp đơn ứng tuyển trực tuyến\n\n";
            } else {
                body += "🚀 Bạn có thể bắt đầu:\n" +
                       "   • Đăng tin tuyển dụng\n" +
                       "   • Quản lý hồ sơ công ty\n" +
                       "   • Tổ chức sự kiện tuyển dụng\n" +
                       "   • Tìm kiếm ứng viên phù hợp\n\n";
            }
            
            body += "💬 Nếu có bất kỳ thắc mắc nào, đừng ngại liên hệ với chúng tôi.\n\n" +
                   "Chúc bạn có trải nghiệm tuyệt vời!\n\n" +
                   "Trân trọng,\n" +
                   "🏢 Đội ngũ SWP391 Job Portal\n" +
                   "📞 Hotline: 1900-xxxx\n" +
                   "🌐 Website: http://localhost:8080";
            
            emailService.sendEmail(email, subject, body);
            System.out.println("✅ Welcome email sent to: " + email + " (Role: " + role + ")");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email to: " + email + " - Error: " + e.getMessage());
        }
    }
}
