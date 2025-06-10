package com.example.swp391_d01_g3.controller.register;

import com.example.swp391_d01_g3.dto.AccountDTO;
import com.example.swp391_d01_g3.dto.EmployerDTO;
import com.example.swp391_d01_g3.dto.PendingRegisterDTO;
import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import jakarta.servlet.http.HttpSession;
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

import java.util.Date;
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
    public String registerStudent(@Valid @ModelAttribute("accountDTO") AccountDTO accountDTO, 
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model,
                                  HttpSession session){
        
        // Chuẩn hóa dữ liệu đầu vào
        if (accountDTO.getFullName() != null) {
            accountDTO.setFullName(accountDTO.getFullName().trim().replaceAll("\\s+", " "));
        }
        if (accountDTO.getEmail() != null) {
            accountDTO.setEmail(accountDTO.getEmail().trim().toLowerCase());
        }
        if (accountDTO.getPhone() != null) {
            accountDTO.setPhone(accountDTO.getPhone().trim().replaceAll("\\s", ""));
        }
        
        // Thực hiện custom validation
        new AccountDTO().validate(accountDTO, bindingResult);
        
        // Kiểm tra có lỗi validation không
        if (bindingResult.hasErrors()) {
            // Log errors for debugging
            System.out.println("Validation errors found:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("- " + error.getDefaultMessage());
            });
            
            model.addAttribute("accountDTO", accountDTO);
            return "register/registerStudentPage";
        }
        
        // Kiểm tra email đã tồn tại chưa
        Account existingAccount = iAccountService.findByEmail(accountDTO.getEmail());
        if (existingAccount != null) {
            bindingResult.rejectValue("email", "error.email", "Email này đã được sử dụng. Vui lòng chọn email khác.");
            model.addAttribute("accountDTO", accountDTO);
            return "register/registerStudentPage";
        }
        
        // Kiểm tra số điện thoại đã tồn tại chưa (nếu cần)
        // Account existingPhoneAccount = iAccountService.findByPhone(accountDTO.getPhone());
        // if (existingPhoneAccount != null) {
        //     bindingResult.rejectValue("phone", "error.phone", "Số điện thoại này đã được sử dụng. Vui lòng chọn số khác.");
        //     model.addAttribute("accountDTO", accountDTO);
        //     return "register/registerStudentPage";
        // }
        
        try {
            // Tạo DTO để lưu trong session
            PendingRegisterDTO pendingRegistration = new PendingRegisterDTO();
            BeanUtils.copyProperties(accountDTO, pendingRegistration);
            pendingRegistration.setRole(Account.Role.student);
            pendingRegistration.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
            
            // Gửi OTP và lưu thông tin trong session
            Integer otp = emailService.sendVerifyMailForRegistration(accountDTO.getEmail(), accountDTO.getFullName());
            
            // Lưu thông tin đăng ký và OTP trong session
            session.setAttribute("pendingRegistration", pendingRegistration);
            session.setAttribute("registrationOTP", otp);
            session.setAttribute("otpExpirationTime", new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 phút
            
            // Log successful registration attempt
            System.out.println("Registration OTP sent successfully to: " + accountDTO.getEmail());
            
            redirectAttributes.addFlashAttribute("messages", "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra và nhập mã để hoàn tất đăng ký.");
            return "redirect:/Register/EmailVerification/verify?email=" + accountDTO.getEmail();
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error during student registration: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("accountDTO", accountDTO);
            model.addAttribute("errorMessage", "Có lỗi xảy ra trong quá trình đăng ký. Vui lòng thử lại sau.");
            return "register/registerStudentPage";
        }
    }
    @GetMapping("/Employer")
    public String showRegisterEmployer(Model model){
        EmployerDTO employerDTO = new EmployerDTO();
        model.addAttribute("jobFields",iJobfieldService.findAll());
        model.addAttribute("accountEmployerDTO", employerDTO);
        return "register/registerEmployerPage";
    }
    @PostMapping("/registerEmployer")
    public String registerEmployer(@Valid @ModelAttribute("accountEmployerDTO") EmployerDTO employerDTO,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, 
                                   Model model,
                                   HttpSession session){
        
        // Chuẩn hóa dữ liệu đầu vào (data normalization đã được thực hiện trong setter methods của DTO)
        // Nhưng vẫn đảm bảo các giá trị được chuẩn hóa
        if (employerDTO.getFullName() != null) {
            employerDTO.setFullName(employerDTO.getFullName().trim().replaceAll("\\s+", " "));
        }
        if (employerDTO.getEmail() != null) {
            employerDTO.setEmail(employerDTO.getEmail().trim().toLowerCase());
        }
        if (employerDTO.getPhone() != null) {
            String normalizedPhone = employerDTO.getPhone().trim().replaceAll("\\s", "");
            if (normalizedPhone.startsWith("+84")) {
                normalizedPhone = "0" + normalizedPhone.substring(3);
            } else if (normalizedPhone.startsWith("84")) {
                normalizedPhone = "0" + normalizedPhone.substring(2);
            }
            employerDTO.setPhone(normalizedPhone);
        }
        
        // Thực hiện custom validation
        new EmployerDTO().validate(employerDTO, bindingResult);
        
        // Kiểm tra có lỗi validation không
        if (bindingResult.hasErrors()) {
            // Log errors for debugging
            System.out.println("Employer registration validation errors found:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("- " + error.getDefaultMessage());
            });
            
            model.addAttribute("accountEmployerDTO", employerDTO);
            model.addAttribute("jobFields", iJobfieldService.findAll());
            return "register/registerEmployerPage";
        }
        
        // Kiểm tra email đã tồn tại chưa
        Account existingAccount = iAccountService.findByEmail(employerDTO.getEmail());
        if (existingAccount != null) {
            bindingResult.rejectValue("email", "error.email", "Email này đã được sử dụng. Vui lòng chọn email khác.");
            model.addAttribute("accountEmployerDTO", employerDTO);
            model.addAttribute("jobFields", iJobfieldService.findAll());
            return "register/registerEmployerPage";
        }
        
        // Kiểm tra job field hợp lệ
        Optional<JobField> jobFieldOptional = iJobfieldService.findById(employerDTO.getJobsFieldId());
        if (!jobFieldOptional.isPresent()) {
            bindingResult.rejectValue("jobsFieldId", "error.jobField", "Lĩnh vực công việc không hợp lệ.");
            model.addAttribute("accountEmployerDTO", employerDTO);
            model.addAttribute("jobFields", iJobfieldService.findAll());
            return "register/registerEmployerPage";
        }
        
        try {
            // Tạo DTO để lưu trong session
            PendingRegisterDTO pendingRegistration = new PendingRegisterDTO();
            BeanUtils.copyProperties(employerDTO, pendingRegistration);
            pendingRegistration.setRole(Account.Role.employer);
            pendingRegistration.setPassword(passwordEncoder.encode(employerDTO.getPassword()));
            
            // Lưu thông tin employer
            pendingRegistration.setCompanyName(employerDTO.getCompanyName());
            pendingRegistration.setCompanyAddress(employerDTO.getCompanyAddress());
//            pendingRegistration.setCompanyDescription(employerDTO.getCompanyDescription());
            pendingRegistration.setLogoUrl(employerDTO.getLogoUrl());
            pendingRegistration.setJobsFieldId(employerDTO.getJobsFieldId());
            
            // Gửi OTP và lưu thông tin trong session
            Integer otp = emailService.sendVerifyMailForRegistration(employerDTO.getEmail(), employerDTO.getFullName());
            
            // Lưu thông tin đăng ký và OTP trong session
            session.setAttribute("pendingRegistration", pendingRegistration);
            session.setAttribute("registrationOTP", otp);
            session.setAttribute("otpExpirationTime", new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 phút
            
            // Log successful registration attempt
            System.out.println("Employer registration OTP sent successfully to: " + employerDTO.getEmail());
            
            redirectAttributes.addFlashAttribute("messages", "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra và nhập mã để hoàn tất đăng ký.");
            return "redirect:/Register/EmailVerification/verify?email=" + employerDTO.getEmail();
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error during employer registration: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("accountEmployerDTO", employerDTO);
            model.addAttribute("jobFields", iJobfieldService.findAll());
            model.addAttribute("errorMessage", "Có lỗi xảy ra trong quá trình đăng ký. Vui lòng thử lại sau.");
            return "register/registerEmployerPage";
        }
    }
}
