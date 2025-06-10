package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.changePassword.ChangePassword;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

// Import the DTO
import com.example.swp391_d01_g3.dto.StudentProfileDTO;

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
    private IJobApplicationService  iJobApplicationService;

    @GetMapping("")
    public String showStudentDashboard(){
        return "student/dashboardStudent";
    }

    // Chau them

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
                model.addAttribute("studentDetails", studentDetails);
            }
            System.out.println(email);

        }
        return "student/profileStudent";
    }
    @GetMapping("/EditProfile")
    public String showEditForm(Model model, Principal principal){
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
            }
            if (studentDetails != null) {
                studentProfileDTO.setAddress(studentDetails.getAddress());
                studentProfileDTO.setUniversity(studentDetails.getUniversity());
                studentProfileDTO.setPreferredJobAddress(studentDetails.getPreferredJobAddress());
                studentProfileDTO.setProfileDescription(studentDetails.getProfileDescription());
                studentProfileDTO.setExperience(studentDetails.getExperience());
            }
            model.addAttribute("studentProfileDTO", studentProfileDTO);
        }
        return "student/editStudentProfile";
    }

    @PostMapping("/EditProfile")
    public String saveStudentProfile(
            @Valid @ModelAttribute("studentProfileDTO") StudentProfileDTO studentProfileDTO,
            BindingResult bindingResult,
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
        BeanUtils.copyProperties(studentProfileDTO, currentAccount, "address", "university", "preferredJobAddress", "profileDescription", "experience");
        BeanUtils.copyProperties(studentProfileDTO, currentStudent, "fullName", "phone");
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
                model.addAttribute("student",student);
            }
        }
        return "student/changePassword";
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
            return "redirect:/Student/Profile";
        }

        // Sử dụng service để kiểm tra mật khẩu hiện tại
        if (!changePassword.isCurrentPasswordValid(currentPassword, account.getPassword())) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
            model.addAttribute("currentAccount", account);
            Student student = studentService.findByAccountUserId(account.getUserId());
            model.addAttribute("student", student);
            return "student/changePassword";
        }

        // Sử dụng service để kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!changePassword.isNewPasswordConfirmed(newPassword, confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            model.addAttribute("currentAccount", account);
            Student student = studentService.findByAccountUserId(account.getUserId());
            model.addAttribute("student", student);
            return "student/changePassword";
        }

        // Sử dụng service để kiểm tra độ dài mật khẩu
        if (!changePassword.isNewPasswordValidLength(newPassword, 6)) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            model.addAttribute("currentAccount", account);
            Student student = studentService.findByAccountUserId(account.getUserId());
            model.addAttribute("student", student);
            return "student/changePassword";
        }

        // Sử dụng service để kiểm tra mật khẩu mới không giống mật khẩu cũ
        if (!changePassword.isNewPasswordDifferent(newPassword, account.getPassword())) {
            model.addAttribute("error", "Mật khẩu mới phải khác mật khẩu hiện tại.");
            model.addAttribute("currentAccount", account);
            Student student = studentService.findByAccountUserId(account.getUserId());
            model.addAttribute("student", student);
            return "student/changePassword";
        }

        // Cập nhật mật khẩu
        account.setPassword(passwordEncoder.encode(newPassword));
        iAccountServiceImpl.save(account);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/Student/Profile";
    }

}
