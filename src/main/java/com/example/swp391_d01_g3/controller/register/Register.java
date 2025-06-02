package com.example.swp391_d01_g3.controller.register;

import com.example.swp391_d01_g3.dto.AccountDTO;
import com.example.swp391_d01_g3.dto.AccountEmployerDTO;
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
                                  RedirectAttributes redirectAttributes){
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
            emailService.sendWelcomeEmail(savedAccount.getEmail(), savedAccount.getFullName(), "Student");
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
        emailService.sendWelcomeEmail(savedAccount.getEmail(), savedAccount.getFullName(), "Employer");

        redirectAttributes.addFlashAttribute("messages", "Registration successful!");
        return "redirect:/Login";
    }
}
