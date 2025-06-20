package com.example.swp391_d01_g3.controller.employer;

import com.example.swp391_d01_g3.dto.EmployerEditDTO;
import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.service.changePassword.ChangePassword;
import com.example.swp391_d01_g3.service.cloudinary.CloudinaryService;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.interview.IInterViewService;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.security.IAccountServiceImpl;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Employer")
public class EmployerDashboard {
    @Autowired
    private IAccountServiceImpl iAccountServiceImpl;

    @Autowired
    private IEmployerService employerService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChangePassword changePassword;

    @Autowired
    private IJobfieldService jobfieldService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private IJobApplicationService iJobApplicationService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private IInterViewService iInterViewService;
    
    @Autowired
    private INotificationService notificationService;

    @GetMapping("")
    public String showEmployeeDashboard() {
        return "employee/dashboardEmployee";
    }
    
    @GetMapping("/Profile")
    public String showProfile(Model model, Principal principal) {
        if (principal != null) {
            String currentUserEmail = principal.getName();
            Account currentAccount = accountService.findByEmail(currentUserEmail);

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
            Account currentAccount = accountService.findByEmail(currentUserEmail);
            
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
        Account account = accountService.findByEmail(currentUserEmail);
        
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
            Account employerAccount = accountService.findByEmail(email);
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
        Account currentAccount = accountService.findByEmail(currentUserEmail);

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
        accountService.save(currentAccount);

        // Cập nhật thông tin Employer
        Employer employer = employerService.findByUserId(currentAccount.getUserId());
        if (employer != null) {
            employer.setCompanyName(employerEditDTO.getCompanyName());
            employer.setCompanyAddress(employerEditDTO.getCompanyAddress());
            

            String description = employerEditDTO.getCompanyDescription();
            employer.setCompanyDescription(description);
            
            // Xử lý upload logo nếu có file mới
            System.out.println("🔍 Checking logo file...");
            if (logoFile != null && !logoFile.isEmpty()) {
//                System.out.println("Logo file detected: " + logoFile.getOriginalFilename());
                try {
                    // Xóa logo cũ từ Cloudinary nếu tồn tại
                    String oldLogoUrl = employer.getLogoUrl();
                    if (oldLogoUrl != null && oldLogoUrl.contains("cloudinary.com")) {
                        String oldPublicId = cloudinaryService.extractPublicId(oldLogoUrl);
                        if (oldPublicId != null) {
                            try {
                                cloudinaryService.deleteImage(oldPublicId);
                            } catch (Exception e) {
                                // Log but don't fail - continue with upload
//                                System.out.println("⚠️ Warning: Could not delete old logo: " + e.getMessage());
                            }
                        }
                    }
                    
                    // Upload logo mới lên Cloudinary
//                    System.out.println("🚀 Starting Cloudinary upload...");
                    String logoUrl = cloudinaryService.uploadImage(logoFile, "employer-logos");
                    employer.setLogoUrl(logoUrl);
//                    System.out.println("✅ Logo uploaded successfully to Cloudinary: " + logoUrl);
                } catch (Exception e) {
//                    System.out.println("❌ Upload error in controller: " + e.getMessage());
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "Lỗi khi upload logo: " + e.getMessage());
                    model.addAttribute("jobFields", jobfieldService.findAll());
                    return "employee/editEmployerProfile";
                }
            } else {
//                System.out.println("📝 No new logo file, keeping existing: " + employerEditDTO.getLogoUrl());
                // Giữ nguyên logo cũ nếu không upload file mới
                employer.setLogoUrl(employerEditDTO.getLogoUrl());
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

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/Employer/Profile";
    }
    // **CONTROLLER ĐỚN GIẢN VỚI TÌM KIẾM**
    @GetMapping("/Applications")
    public String viewApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String searchName,
            Model model,
            Authentication authentication) {

        // Lấy thông tin employer đang đăng nhập
        String employerEmail = authentication.getName();
        Employer employer = employerService.findByEmail(employerEmail);

        if (employer == null) {
            return "redirect:/login";
        }

        // Tạo Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<JobApplication> applications;

        // Tìm kiếm hoặc hiển thị tất cả
        if (searchName != null && !searchName.trim().isEmpty()) {
            applications = iJobApplicationService.searchApplicationsByEmployerIdAndName(
                    employer.getEmployerId(), searchName.trim(), pageable);
            model.addAttribute("searchName", searchName);
        } else {
            applications = iJobApplicationService.getApplicationsByEmployerId(employer.getEmployerId(), pageable);
        }
        model.addAttribute("applications", applications);
        model.addAttribute("statuses", JobApplication.ApplicationStatus.values());
        
        // Add authentication status for chat system
        model.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());

        return "employee/viewListApplications";
    }




    // Cập nhật trạng thái ứng viên
    @PostMapping("/Applications/{applicationId}/updateStatus")
    public String updateApplicationStatus(
            @PathVariable Integer applicationId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        try {
            String employerEmail = authentication.getName();
            Employer employer = employerService.findByEmail(employerEmail);

            JobApplication application = iJobApplicationService.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ứng tuyển"));

            // Kiểm tra quyền
            if (!application.getJobPost().getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện hành động này!");
                return "redirect:/Employer/Applications";
            }

            JobApplication.ApplicationStatus newStatus = JobApplication.ApplicationStatus.valueOf(status);
            iJobApplicationService.updateApplicationStatus(applicationId, newStatus);
            
            // Gửi email cho ACCEPTED và REJECTED (không gửi cho INTERVIEW qua dropdown)
            if (newStatus == JobApplication.ApplicationStatus.ACCEPTED || newStatus == JobApplication.ApplicationStatus.REJECTED) {
                String candidateEmail = application.getEmail();
                String candidateName = application.getFullName();
                String jobTitle = application.getJobPost().getJobTitle();
                String companyName = employer.getCompanyName();
                
                if (newStatus == JobApplication.ApplicationStatus.ACCEPTED) {
                    emailService.sendApplicationAcceptedEmail(candidateEmail, candidateName, jobTitle, companyName);
                } else if (newStatus == JobApplication.ApplicationStatus.REJECTED) {
                    emailService.sendApplicationRejectedEmail(candidateEmail, candidateName, jobTitle, companyName);
                }
            }
            
            // Tạo thông báo cho ứng viên về việc thay đổi trạng thái
            String notificationTitle = "Cập nhật trạng thái ứng tuyển";
            String notificationMessage = "";
            String notificationType = "APPLICATION_STATUS_UPDATE";
            
            switch (newStatus) {
                case SUBMITTED:
                    notificationMessage = "Đơn ứng tuyển của bạn cho vị trí " + application.getJobPost().getJobTitle() + 
                                        " đã được gửi thành công và đang chờ xét duyệt.";
                    break;
                case INTERVIEW:
                    notificationMessage = "Đơn ứng tuyển của bạn cho vị trí " + application.getJobPost().getJobTitle() + 
                                        " đã chuyển sang giai đoạn phỏng vấn.";
                    notificationType = "INTERVIEW_SCHEDULE";
                    break;
                case ACCEPTED:
                    notificationMessage = "Chúc mừng! Bạn đã vượt qua phỏng vấn cho vị trí " + application.getJobPost().getJobTitle() + 
                                        " tại " + employer.getCompanyName() + ".";
                    break;
                case REJECTED:
                    notificationMessage = "Rất tiếc, đơn ứng tuyển của bạn cho vị trí " + application.getJobPost().getJobTitle() + 
                                        " tại " + employer.getCompanyName() + " chưa phù hợp lần này.";
                    break;
            }
            
            // Gửi thông báo cho student
            notificationService.createNotification(
                application.getStudent().getAccount(),
                notificationTitle,
                notificationMessage,
                notificationType,
                application.getApplicationId().longValue()
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái!");
        }

        return "redirect:/Employer/Applications";
    }

    @PostMapping("/Applications/{applicationId}/sendInterviewMail")
    public String sendInterviewMail(
            @PathVariable Integer applicationId,
            @RequestParam String interviewTime,
            @RequestParam String interviewType,
            @RequestParam(required = false) String meetingLink,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        try {
            String employerEmail = authentication.getName();
            Employer employer = employerService.findByEmail(employerEmail);
            JobApplication application = iJobApplicationService.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ứng tuyển"));
            // Kiểm tra quyền
            if (!application.getJobPost().getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện hành động này!");
                return "redirect:/Employer/Applications";
            }
            // Lưu lịch phỏng vấn vào bảng Interview
            Interview interview = new com.example.swp391_d01_g3.model.Interview();
            interview.setJobApplication(application);
            interview.setInterviewType(interviewType);
            interview.setMeetingLink(meetingLink);
            interview.setNote(note);
            interview.setInterviewStatus("SCHEDULED");
            java.time.LocalDateTime interviewDate = java.time.LocalDateTime.parse(interviewTime);
            interview.setInterviewDate(interviewDate);
            iInterViewService.save(interview);
            // Lấy thông tin ứng viên
            String candidateEmail = application.getEmail();
            String candidateName = application.getFullName();
            String candidatePhone = application.getPhone(); // nếu muốn dùng
            String jobTitle = application.getJobPost().getJobTitle();
            // Gọi service gửi mail
            emailService.sendInterviewScheduleEmail(candidateEmail, candidateName, jobTitle, interviewTime, interviewType, meetingLink, note);
            // Cập nhật trạng thái ứng viên sang INTERVIEW
            iJobApplicationService.updateApplicationStatus(applicationId, JobApplication.ApplicationStatus.INTERVIEW);
            
            // Tạo thông báo cho ứng viên về lịch phỏng vấn
            notificationService.createNotification(
                    application.getStudent().getAccount(),
                    "Lịch phỏng vấn mới",
                    "Bạn có lịch phỏng vấn cho vị trí " + jobTitle +
                            " vào " + interviewTime +
                            " theo hình thức " + interviewType + "." +
                    " Link meeting: " + meetingLink +
                    " Ghi chú: " + note,
                    "NEW_APPLICATION",
                    application.getApplicationId().longValue()
            );

            
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lịch phỏng vấn cho ứng viên!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi gửi lịch phỏng vấn!");
        }
        return "redirect:/Employer/Applications";
    }
}

