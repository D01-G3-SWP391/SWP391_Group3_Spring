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
                                  HttpSession session){
        new AccountDTO().validate(accountDTO,bindingResult);
        if (bindingResult.hasErrors()){
            return "register/registerStudentPage";
        }
        
        // Kiểm tra email đã tồn tại chưa
        Account existingAccount = iAccountService.findByEmail(accountDTO.getEmail());
        if (existingAccount != null) {
            bindingResult.rejectValue("email", "error.email", "Email này đã được sử dụng. Vui lòng chọn email khác.");
            return "register/registerStudentPage";
        }
        
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
        session.setAttribute("otpExpirationTime", new Date(System.currentTimeMillis() + 3 * 60 * 1000)); // 3 phút
        
        redirectAttributes.addFlashAttribute("messages", "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra và nhập mã để hoàn tất đăng ký.");
        return "redirect:/Register/EmailVerification/verify?email=" + accountDTO.getEmail();
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
        new EmployerDTO().validate(employerDTO,bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("jobFields",iJobfieldService.findAll());
            return "register/registerEmployerPage";
        }
        
        // Kiểm tra email đã tồn tại chưa
        Account existingAccount = iAccountService.findByEmail(employerDTO.getEmail());
        if (existingAccount != null) {
            model.addAttribute("accountEmployerDTO", employerDTO);
            model.addAttribute("jobFields",iJobfieldService.findAll());
            model.addAttribute("errorMessage", "Email này đã được sử dụng. Vui lòng chọn email khác.");
            return "register/registerEmployerPage";
        }
        
        // Kiểm tra job field hợp lệ
        Optional<JobField> jobFieldOptional = iJobfieldService.findById(employerDTO.getJobsFieldId());
        if (!jobFieldOptional.isPresent()) {
            model.addAttribute("accountEmployerDTO", employerDTO);
            model.addAttribute("jobFields",iJobfieldService.findAll());
            model.addAttribute("errorMessage", "Lĩnh vực công việc không hợp lệ.");
            return "register/registerEmployerPage";
        }
        
        // Tạo DTO để lưu trong session
        PendingRegisterDTO pendingRegistration = new PendingRegisterDTO();
        BeanUtils.copyProperties(employerDTO, pendingRegistration);
        pendingRegistration.setRole(Account.Role.employer);
        pendingRegistration.setPassword(passwordEncoder.encode(employerDTO.getPassword()));
        
        // Lưu thông tin employer
        pendingRegistration.setCompanyName(employerDTO.getCompanyName());
        pendingRegistration.setCompanyAddress(employerDTO.getCompanyAddress());
        pendingRegistration.setCompanyDescription(employerDTO.getCompanyDescription());
        pendingRegistration.setLogoUrl(employerDTO.getLogoUrl());
        pendingRegistration.setJobsFieldId(employerDTO.getJobsFieldId());
        
        // Gửi OTP và lưu thông tin trong session
        Integer otp = emailService.sendVerifyMailForRegistration(employerDTO.getEmail(), employerDTO.getFullName());
        
        // Lưu thông tin đăng ký và OTP trong session
        session.setAttribute("pendingRegistration", pendingRegistration);
        session.setAttribute("registrationOTP", otp);
        session.setAttribute("otpExpirationTime", new Date(System.currentTimeMillis() + 3 * 60 * 1000)); // 10 phút
        
        redirectAttributes.addFlashAttribute("messages", "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra và nhập mã để hoàn tất đăng ký.");
        return "redirect:/Register/EmailVerification/verify?email=" + employerDTO.getEmail();
    }
}
