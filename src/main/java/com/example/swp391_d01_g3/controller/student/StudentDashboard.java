package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.changePassword.ChangePassword;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.security.IAccountServiceImpl;
import com.example.swp391_d01_g3.service.student.IStudentService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

// Import the DTO
import com.example.swp391_d01_g3.dto.PasswordChangeDTO;
import com.example.swp391_d01_g3.dto.StudentProfileDTO;
import com.example.swp391_d01_g3.service.cloudinary.CloudinaryService;

@Controller
@RequestMapping("/Student")
public class StudentDashboard {

    @Autowired
    private IAccountServiceImpl iAccountServiceImpl;

    @Autowired
    private IAccountService IAccountService;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChangePassword changePassword;

    @Autowired
    private IJobApplicationService iJobApplicationService;

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("")
    public String showStudentDashboard(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Account account = IAccountService.findByEmail(email);
            model.addAttribute("account", account);
        }
        return "student/dashboardStudent";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model, Principal principal){
        if (principal != null){
            String email = principal.getName();
            Account account = IAccountService.findByEmail(email);
            if (account != null){
                Student student=studentService.findByAccountUserId(account.getUserId());
                if (student != null){
                    List<JobApplication> applications=iJobApplicationService.findByStudentId(student.getStudentId());
                    model.addAttribute("applications",applications);
                    model.addAttribute("student",student);
                    model.addAttribute("account",account);
                }
            }
        }
        return "student/my-applications";
    }
    @GetMapping("/Profile")
    public String showStudentProfile(Model model, Principal principal){
        if (principal != null) {
            String email = principal.getName();
            Account studentAccount = IAccountService.findByEmail(email);
            model.addAttribute("account", studentAccount);
            if (studentAccount != null) {
                Student studentDetails = studentService.findByAccountUserId(studentAccount.getUserId());
                System.out.println(studentDetails.getStudentId());
                model.addAttribute("studentDetails", studentDetails);
            }

            System.out.println(email);

        }
        return "student/profileStudent";
    }
    @GetMapping("/EditProfile")
    public String showEditForm(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Account studentAccount = IAccountService.findByEmail(email);
            Student studentDetails = null;
            if (studentAccount != null) {
                studentDetails = studentService.findByAccountUserId(studentAccount.getUserId());
            }
            StudentProfileDTO studentProfileDTO = new StudentProfileDTO();
            if (studentAccount != null) {
                studentProfileDTO.setFullName(studentAccount.getFullName());
                studentProfileDTO.setPhone(studentAccount.getPhone());
                studentProfileDTO.setAvatarUrl(studentAccount.getAvatarUrl());
            }
            if (studentDetails != null) {
                studentProfileDTO.setAddress(studentDetails.getAddress());
                studentProfileDTO.setUniversity(studentDetails.getUniversity());
                studentProfileDTO.setPreferredJobAddress(studentDetails.getPreferredJobAddress());
                studentProfileDTO.setProfileDescription(studentDetails.getProfileDescription());
                studentProfileDTO.setExperience(studentDetails.getExperience());
            }
            model.addAttribute("studentProfileDTO", studentProfileDTO);
            model.addAttribute("email",studentAccount.getEmail());
            model.addAttribute("account", studentAccount);
        }
        return "student/editStudentProfile";
    }

    @PostMapping("/EditProfile")
    public String saveStudentProfile(
            @Valid @ModelAttribute("studentProfileDTO") StudentProfileDTO studentProfileDTO,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "student/editStudentProfile";
        }
        if (principal == null) {
            return "redirect:/Login";
        }

        String email = principal.getName();
        Account currentAccount = IAccountService.findByEmail(email);
        Student currentStudent = studentService.findByAccountUserId(currentAccount.getUserId());

        // Handle avatar upload if a new file is provided
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Delete old avatar from Cloudinary if it exists
                String oldAvatarUrl = currentAccount.getAvatarUrl();
                if (oldAvatarUrl != null && oldAvatarUrl.contains("cloudinary.com")) {
                    String oldPublicId = cloudinaryService.extractPublicId(oldAvatarUrl);
                    if (oldPublicId != null) {
                        try {
                            cloudinaryService.deleteImage(oldPublicId);
                        } catch (Exception e) {
                            // Log but continue with upload
                        }
                    }
                }
                
                // Upload new avatar to Cloudinary and save to Account table
                String avatarUrl = cloudinaryService.uploadImage(avatarFile, "student-avatars");
                currentAccount.setAvatarUrl(avatarUrl);
                studentProfileDTO.setAvatarUrl(avatarUrl);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi upload ảnh: " + e.getMessage());
                return "student/editStudentProfile";
            }
        } else {
            // Keep existing avatar if no new file is uploaded
            currentAccount.setAvatarUrl(studentProfileDTO.getAvatarUrl());
        }

        // Update Account information
        currentAccount.setFullName(studentProfileDTO.getFullName());
        currentAccount.setPhone(studentProfileDTO.getPhone());

        // Update Student information
        currentStudent.setAddress(studentProfileDTO.getAddress());
        currentStudent.setUniversity(studentProfileDTO.getUniversity());
        currentStudent.setPreferredJobAddress(studentProfileDTO.getPreferredJobAddress());
        currentStudent.setProfileDescription(studentProfileDTO.getProfileDescription());
        currentStudent.setExperience(studentProfileDTO.getExperience());

        try {
            IAccountService.save(currentAccount);
            studentService.save(currentStudent);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
        }

        return "redirect:/Student/Profile";
    }
    @GetMapping("/ChangePassword")
    public String showChangePasswordForm(Model model, Principal principal) {
        if (principal != null) {
            String currentUserEmail = principal.getName();
            Account currentAccount = IAccountService.findByEmail(currentUserEmail);

            if (currentAccount != null) {
                Student student = studentService.findByAccountUserId(currentAccount.getUserId());
                model.addAttribute("currentAccount", currentAccount);
                model.addAttribute("account", currentAccount);
                model.addAttribute("student",student);
                model.addAttribute("passwordChangeDTO", new PasswordChangeDTO());
            }
        }
        return "student/changePassword";
    }

    @PostMapping("/ChangePassword")
    public String changePassword(@Valid @ModelAttribute("passwordChangeDTO") PasswordChangeDTO passwordChangeDTO,
                                 BindingResult bindingResult,
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
            return "redirect:/Student/Profile";
        }

        // Sử dụng validation service mới
        changePassword.validatePasswordWithOld(
            passwordChangeDTO.getCurrentPassword(),
            passwordChangeDTO.getNewPassword(),
            passwordChangeDTO.getConfirmPassword(),
            account.getPassword(),
            bindingResult
        );

        // Kiểm tra có lỗi validation không
        if (bindingResult.hasErrors()) {
            // Log errors for debugging
            System.out.println("Student change password validation errors found:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("- " + error.getDefaultMessage());
            });
            
            model.addAttribute("currentAccount", account);
            model.addAttribute("account", account);
            Student student = studentService.findByAccountUserId(account.getUserId());
            model.addAttribute("student", student);
            model.addAttribute("passwordChangeDTO", passwordChangeDTO);
            
            // Hiển thị lỗi đầu tiên
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            model.addAttribute("error", errorMessage);
            return "student/changePassword";
        }

        // Cập nhật mật khẩu
        account.setPassword(passwordEncoder.encode(changePassword.normalizePassword(passwordChangeDTO.getNewPassword())));
        iAccountServiceImpl.save(account);
        notificationService.createNotification(
                account,
                "Change password successful",
                "Ban da doi mk thanh cong",
                "CHANGE_PASSWORD",
                account.getUserId().longValue()
        );
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/Student/Profile";
    }

}
