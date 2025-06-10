package com.example.swp391_d01_g3.controller.employer;

import com.example.swp391_d01_g3.dto.EmployerDTO;
import com.example.swp391_d01_g3.dto.EmployerEditDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.service.changePassword.ChangePassword;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.security.IAccountServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/Employer")
public class EmployerDashboard {
    @Autowired
    private IAccountServiceImpl iAccountServiceImpl;

    @Autowired
    private IEmployerService employerService;

    @Autowired
    private IAccountService IAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChangePassword changePassword;

    @Autowired
    private IJobfieldService jobfieldService;

    @GetMapping("")
    public String showEmployeeDashboard() {
        return "employee/dashboardEmployee";
    }
    
    @GetMapping("/Profile")
    public String showProfile(Model model, Principal principal) {
        if (principal != null) {
            String currentUserEmail = principal.getName();
            Account currentAccount = IAccountService.findByEmail(currentUserEmail);

            if (currentAccount != null) {
                Employer employer = employerService.findByUserId(currentAccount.getUserId());
                
                // Debug company description from database
//                if (employer != null && employer.getCompanyDescription() != null) {
//                    String dbDescription = employer.getCompanyDescription();
//                    System.out.println("📖 Company Description from database:");
//                    System.out.println("📄 Raw text: [" + dbDescription + "]");
//                    System.out.println("📏 Length: " + dbDescription.length());
//                    System.out.println("🔍 Contains \\n: " + dbDescription.contains("\n"));
//                    System.out.println("🔍 Contains \\r: " + dbDescription.contains("\r"));
//                    // In từng ký tự để debug
//                    for (int i = 0; i < Math.min(dbDescription.length(), 50); i++) {
//                        char c = dbDescription.charAt(i);
//                        System.out.print("'" + c + "'(" + (int)c + ") ");
//                    }
//                    System.out.println();
//                }
                
                model.addAttribute("currentAccount", currentAccount);
                model.addAttribute("employer", employer);
            }
        }

        return "employee/profileEmployer";
    }
    
    @GetMapping("/ChangePassword")
    public String showChangePasswordForm(Model model, Principal principal) {
        if (principal != null) {
            String currentUserEmail = principal.getName();
            Account currentAccount = IAccountService.findByEmail(currentUserEmail);
            
            if (currentAccount != null) {
                Employer employer = employerService.findByUserId(currentAccount.getUserId());
                model.addAttribute("currentAccount", currentAccount);
                model.addAttribute("employer", employer);
            }
        }
        return "employee/changePassword";
    }
    
    @PostMapping("/ChangePassword")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để thực hiện chức năng này.");
            return "redirect:/login";
        }
        
        String currentUserEmail = principal.getName();
        Account account = IAccountService.findByEmail(currentUserEmail);
        
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/Employer/Profile";
        }

        // Kiểm tra mật khẩu hiện tại
        if (!changePassword.isCurrentPasswordValid(currentPassword, account.getPassword())) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
            model.addAttribute("currentAccount", account);
            Employer employer = employerService.findByUserId(account.getUserId());
            model.addAttribute("employer", employer);
            return "employee/changePassword";
        }
        
        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!changePassword.isNewPasswordConfirmed(newPassword, confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            model.addAttribute("currentAccount", account);
            Employer employer = employerService.findByUserId(account.getUserId());
            model.addAttribute("employer", employer);
            return "employee/changePassword";
        }
        
        // Kiểm tra độ dài mật khẩu
        if (!changePassword.isNewPasswordValidLength(newPassword, 6)) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            model.addAttribute("currentAccount", account);
            Employer employer = employerService.findByUserId(account.getUserId());
            model.addAttribute("employer", employer);
            return "employee/changePassword";
        }
        
        // Kiểm tra mật khẩu mới không giống mật khẩu cũ
        if (!changePassword.isNewPasswordDifferent(newPassword, account.getPassword())) {
            model.addAttribute("error", "Mật khẩu mới phải khác mật khẩu hiện tại.");
            model.addAttribute("currentAccount", account);
            Employer employer = employerService.findByUserId(account.getUserId());
            model.addAttribute("employer", employer);
            return "employee/changePassword";
        }
        
        // Cập nhật mật khẩu
        account.setPassword(passwordEncoder.encode(newPassword));
        iAccountServiceImpl.save(account);
        
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/Employer/Profile";
    }

    @GetMapping("/EditProfile")
    public String showEditForm(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Account employerAccount = IAccountService.findByEmail(email);
            Employer employerDetails = null;
            if (employerAccount != null) {
                employerDetails = employerService.findByUserId(employerAccount.getUserId());
            }
            
            // Sử dụng EmployerEditDTO thay vì EmployerDTO
            EmployerEditDTO employerProfileDTO = new EmployerEditDTO(employerAccount, employerDetails);
            
            model.addAttribute("employerProfileDTO", employerProfileDTO);
            model.addAttribute("jobFields", jobfieldService.findAll());
            return "employee/editEmployerProfile";
        }
        return "redirect:/Employer/Profile";
    }

    @PostMapping("/EditProfile")
    public String editProfile(@Valid @ModelAttribute("employerProfileDTO") EmployerEditDTO employerEditDTO, 
                             BindingResult bindingResult, 
                             @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                             Model model,
                             Principal principal, 
                             RedirectAttributes redirectAttributes) {
//        System.out.println("🔥 POST EditProfile được gọi!");
//        System.out.println("📧 User email: " + (principal != null ? principal.getName() : "null"));
//        System.out.println("📝 Form data: " + employerEditDTO.toString());
        
        if (principal == null) {
            return "redirect:/login";
        }

        String currentUserEmail = principal.getName();
        Account currentAccount = IAccountService.findByEmail(currentUserEmail);

        if (currentAccount == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/Employer/Profile";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("jobFields", jobfieldService.findAll());
            return "employee/editEmployerProfile";
        }

        // Cập nhật thông tin Account
        currentAccount.setFullName(employerEditDTO.getFullName());
        currentAccount.setPhone(employerEditDTO.getPhone());
        IAccountService.save(currentAccount);

        // Cập nhật thông tin Employer
        Employer employer = employerService.findByUserId(currentAccount.getUserId());
        if (employer != null) {
            employer.setCompanyName(employerEditDTO.getCompanyName());
            employer.setCompanyAddress(employerEditDTO.getCompanyAddress());
            
            // Debug company description
            String description = employerEditDTO.getCompanyDescription();
//            System.out.println("📝 Company Description from form:");
//            System.out.println("📄 Raw text: [" + description + "]");
//            System.out.println("📏 Length: " + (description != null ? description.length() : 0));
//            if (description != null) {
//                System.out.println("🔍 Contains \\n: " + description.contains("\n"));
//                System.out.println("🔍 Contains \\r: " + description.contains("\r"));
//                // In từng ký tự để debug
//                for (int i = 0; i < Math.min(description.length(), 50); i++) {
//                    char c = description.charAt(i);
//                    System.out.print("'" + c + "'(" + (int)c + ") ");
//                }
//                System.out.println();
//            }
            
            employer.setCompanyDescription(description);
            
            // Xử lý upload logo nếu có file mới
            if (logoFile != null && !logoFile.isEmpty()) {
                try {
                    String logoUrl = saveLogoFile(logoFile);
                    employer.setLogoUrl(logoUrl);
//                    System.out.println("Logo uploaded successfully: " + logoUrl);
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Lỗi khi upload logo: " + e.getMessage());
                    model.addAttribute("jobFields", jobfieldService.findAll());
                    return "employee/editEmployerProfile";
                }
            } else {
                // Giữ nguyên logo cũ nếu không upload file mới
                employer.setLogoUrl(employerEditDTO.getLogoUrl());
//                System.out.println("📝 Keeping existing logo: " + employerEditDTO.getLogoUrl());
            }
            
            // Cập nhật JobField
            if (employerEditDTO.getJobsFieldId() != null) {
                Optional<JobField> jobField = jobfieldService.findById(employerEditDTO.getJobsFieldId());
                if (jobField.isPresent()) {
                    employer.setJobField(jobField.get());
                }
            }
            
            employerService.updateEmployer(employer);
        }

//        System.out.println("✅ Update successful, redirecting to profile");
        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/Employer/Profile";
    }

    private String saveLogoFile(MultipartFile file) throws IOException {
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là ảnh (JPG, PNG, GIF)");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB");
        }

        // Tạo thư mục uploads external (ngoài project) để load ngay lập tức
        String uploadDir = "uploads/logos/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = ".jpg"; // default
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // Lưu file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

//        System.out.println("💾 File saved to: " + filePath.toAbsolutePath());
        
        // Trả về đường dẫn URL để truy cập file (sẽ được lưu vào database)
        return "/uploads/logos/" + newFilename;
    }
}

