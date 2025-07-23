package com.example.swp391_d01_g3.controller.employer;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.swp391_d01_g3.dto.EmployerEditDTO;
import com.example.swp391_d01_g3.dto.PasswordChangeDTO;
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
import com.example.swp391_d01_g3.service.student.IStudentService;
import com.example.swp391_d01_g3.service.jobinvitation.IJobInvitationService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.employer.IDashboardService;
import com.example.swp391_d01_g3.dto.EmployerDashboardDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private IStudentService iStudentService;

    @Autowired
    private IJobInvitationService jobInvitationService;

    @Autowired
    private IJobpostService jobPostService;

    @Autowired
    private IDashboardService dashboardService;

    @GetMapping("")
    public String showEmployeeDashboard(Model model, Principal principal) {
        if (principal != null) {
            String currentUserEmail = principal.getName();
            Account currentAccount = accountService.findByEmail(currentUserEmail);
            if (currentAccount != null) {
                Employer employer = employerService.findByUserId(currentAccount.getUserId());
                model.addAttribute("account", currentAccount);
                model.addAttribute("employer", employer);
                
                // Get dashboard statistics
                if (employer != null) {
                    EmployerDashboardDTO dashboardData = dashboardService.getEmployerDashboard(employer.getEmployerId());
                    model.addAttribute("dashboardData", dashboardData);
                }
            }
        }
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
                model.addAttribute("account", currentAccount);  // Thêm account để template có thể truy cập avatarUrl
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
                model.addAttribute("account", currentAccount);  // Thêm account cho navbar
                model.addAttribute("employer", employer);
                model.addAttribute("passwordChangeDTO", new PasswordChangeDTO());
            }
        }
        return "employee/changePassword";
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
        Account account = accountService.findByEmail(currentUserEmail);
        
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/Employer/Profile";
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
            System.out.println("Employer change password validation errors found:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("- " + error.getDefaultMessage());
            });
            
            model.addAttribute("currentAccount", account);
            model.addAttribute("account", account);  // Thêm account cho navbar
            Employer employer = employerService.findByUserId(account.getUserId());
            model.addAttribute("employer", employer);
            model.addAttribute("passwordChangeDTO", passwordChangeDTO);
            
            // Hiển thị lỗi đầu tiên
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            model.addAttribute("error", errorMessage);
            return "employee/changePassword";
        }
        
        // Cập nhật mật khẩu
        account.setPassword(passwordEncoder.encode(changePassword.normalizePassword(passwordChangeDTO.getNewPassword())));
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
            model.addAttribute("account", employerAccount);  // Thêm account để hiển thị avatar
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
            model.addAttribute("account", currentAccount);  // Thêm account cho trường hợp error
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
            
            // Xử lý upload logo nếu có file mới - LưU VÀO ACCOUNT.AVATARURL
            System.out.println("🔍 Checking logo file...");
            Account employerAccount = employer.getAccount();
            if (logoFile != null && !logoFile.isEmpty()) {
//                System.out.println("Logo file detected: " + logoFile.getOriginalFilename());
                try {
                    // Xóa logo cũ từ Cloudinary nếu tồn tại (từ Account)
                    String oldLogoUrl = employerAccount.getAvatarUrl();
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
                    employerAccount.setAvatarUrl(logoUrl);  // Lưu vào Account thay vì Employer
                    accountService.save(employerAccount);  // Cập nhật Account
//                    System.out.println("✅ Logo uploaded successfully to Account.avatarUrl: " + logoUrl);
                } catch (Exception e) {
//                    System.out.println("❌ Upload error in controller: " + e.getMessage());
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "Lỗi khi upload logo: " + e.getMessage());
                    model.addAttribute("jobFields", jobfieldService.findAll());
                    return "employee/editEmployerProfile";
                }
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

    @GetMapping("/SearchCandidate")
    public String searchCandidate(
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "university", required = false) String university,
            @RequestParam(value = "experience", required = false) String experience,
            @RequestParam(value = "jobFieldName", required = false) String jobFieldName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model,
            Principal principal) {
        
        // Thêm account cho navbar
        if (principal != null) {
            model.addAttribute("account", accountService.findByEmail(principal.getName()));
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentsPage;
        
        // Nếu có filter thì search, không thì hiển thị tất cả
        if ((address != null && !address.trim().isEmpty()) ||
            (university != null && !university.trim().isEmpty()) ||
            (experience != null && !experience.trim().isEmpty()) ||
            (jobFieldName != null && !jobFieldName.trim().isEmpty())) {
            
            // Tạo page từ search results
            List<Student> searchResults = iStudentService.searchStudents(address, university, experience, jobFieldName);
            int start = Math.min((int) pageable.getOffset(), searchResults.size());
            int end = Math.min(start + pageable.getPageSize(), searchResults.size());
            List<Student> pageContent = searchResults.subList(start, end);
            studentsPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, searchResults.size());
        } else {
            // Hiển thị tất cả students
            studentsPage = iStudentService.getStudent(pageable);
        }
        
        // Tính toán statistics
        long totalStudents = studentsPage.getTotalElements();
        long activeStudents = totalStudents; // Có thể thêm logic filter active students
        
        model.addAttribute("studentsPage", studentsPage);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("activeStudents", activeStudents);
        model.addAttribute("address", address);
        model.addAttribute("university", university);
        model.addAttribute("experience", experience);
        model.addAttribute("jobFieldName", jobFieldName);
        
        return "employee/potentialCandidates";
    }

    // DEPRECATED: Cập nhật trạng thái ứng viên - Đã chuyển sang JobPostController 
    // @PostMapping("/Applications/{applicationId}/updateStatus")
    /*
    public String updateApplicationStatus(
            @PathVariable Integer applicationId,
            @RequestParam String status,
            @RequestParam(required = false) Integer jobPostId,
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

        if (jobPostId != null) {
            return "redirect:/Employer/JobPosts/" + jobPostId + "/applications";
        }
        return "redirect:/Employer/Applications";
    }
    */

    // DEPRECATED: Gửi lịch phỏng vấn - Đã chuyển sang JobPostController
    // @PostMapping("/Applications/{applicationId}/sendInterviewMail") 
    /*
    public String sendInterviewMail(
            @PathVariable Integer applicationId,
            @RequestParam String interviewTime,
            @RequestParam String interviewType,
            @RequestParam(required = false) String meetingLink,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) Integer jobPostId,
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
        if (jobPostId != null) {
            return "redirect:/Employer/JobPosts/" + jobPostId + "/applications";
        }
        return "redirect:/Employer/Applications";
    }
    */

    @GetMapping("/CandidateDetail/{id}")
    public String candidateDetail(@PathVariable("id") Long studentId, Model model, Principal principal) {
        Student student = iStudentService.findById(studentId).orElse(null);
        if (principal != null) {
            model.addAttribute("account", accountService.findByEmail(principal.getName()));
            
            // Load job posts of current employer for job invitation
            String employerEmail = principal.getName();
            Employer employer = employerService.findByEmail(employerEmail);
            if (employer != null) {
                List<JobPost> jobPosts = jobPostService.findJobPostsByEmployerEmail(employerEmail);
                // Filter only approved and active job posts
                List<JobPost> activeJobPosts = jobPosts.stream()
                    .filter(jp -> jp.getApprovalStatus() == JobPost.ApprovalStatus.APPROVED 
                                && jp.getDisplayStatus() == JobPost.DisplayStatus.ACTIVE)
                    .toList();
                model.addAttribute("employerJobPosts", activeJobPosts);
            }
        }
        if (student == null) {
            return "redirect:/Employer/SearchCandidate";
        }
        model.addAttribute("student", student);
        return "employee/candidateDetail";
    }

    @PostMapping("/SendJobInvitation")
    @ResponseBody
    public ResponseEntity<String> sendJobInvitation(
            @RequestParam("studentId") Long studentId,
            @RequestParam("jobPostId") Integer jobPostId,
            @RequestParam("message") String message,
            Principal principal) {
        
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập để thực hiện chức năng này");
            }
            
            String employerEmail = principal.getName();
            Employer employer = employerService.findByEmail(employerEmail);
            
            if (employer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin nhà tuyển dụng");
            }
            
            Student student = iStudentService.findById(studentId).orElse(null);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin ứng viên");
            }
            
            JobPost jobPost = jobPostService.findByJobPostId(jobPostId).orElse(null);
            if (jobPost == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tin tuyển dụng");
            }
            
            // Check if job post belongs to the employer
            if (!jobPost.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền gửi lời mời cho tin tuyển dụng này");
            }
            
            // Check if invitation already exists
            if (jobInvitationService.isInvitationExists(employer, student, jobPostId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Bạn đã gửi lời mời cho ứng viên này rồi");
            }
            
            // Create and save job invitation
            JobInvitation invitation = jobInvitationService.createInvitation(jobPost, student, employer, message);
            
            // Send notification to student
            String notificationTitle = "Lời mời việc làm mới";
            String notificationMessage = "Bạn nhận được lời mời ứng tuyển cho vị trí " + jobPost.getJobTitle() + 
                                       " tại " + employer.getCompanyName();
            
            notificationService.createNotification(
                student.getAccount(),
                notificationTitle,
                notificationMessage,
                "JOB_INVITATION",
                invitation.getInvitationId()
            );
            
            return ResponseEntity.ok("Gửi lời mời thành công!");
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi gửi lời mời: " + e.getMessage());
        }
    }

    @GetMapping("/api/jobposts")
    @ResponseBody
    public ResponseEntity<List<JobPost>> getEmployerJobPosts(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            String employerEmail = principal.getName();
            List<JobPost> jobPosts = jobPostService.findJobPostsByEmployerEmail(employerEmail);
            
            // Filter only approved and active job posts
            List<JobPost> activeJobPosts = jobPosts.stream()
                .filter(jp -> jp.getApprovalStatus() == JobPost.ApprovalStatus.APPROVED 
                            && jp.getDisplayStatus() == JobPost.DisplayStatus.ACTIVE)
                .toList();
            
            return ResponseEntity.ok(activeJobPosts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/JobInvitations")
    @Transactional
    public String viewJobInvitations(Model model, Principal principal, HttpServletRequest request) {
        try {
            System.out.println("🔍 Loading JobInvitations page...");
            
            if (principal == null) {
                System.err.println("❌ Principal is null - user not authenticated");
                model.addAttribute("errorMessage", "Bạn cần đăng nhập để xem trang này");
                return "redirect:/login";
            }
            
            String email = principal.getName();
            System.out.println("👤 User email: " + email);
            
            Account account = accountService.findByEmail(email);
            if (account == null) {
                System.err.println("❌ Account not found for email: " + email);
                model.addAttribute("errorMessage", "Không tìm thấy tài khoản");
                return "redirect:/login";
            }
            
            System.out.println("✅ Found account: " + account.getUserId());
            
            Employer employer = employerService.findByUserId(account.getUserId());
            if (employer == null) {
                System.err.println("❌ Employer not found for account: " + account.getUserId());
                model.addAttribute("errorMessage", "Không tìm thấy thông tin nhà tuyển dụng");
                return "redirect:/Employer";
            }
            
            System.out.println("✅ Found employer: " + employer.getEmployerId());
            
            // Get all job invitations sent by this employer with relationships loaded
            List<JobInvitation> allInvitations = jobInvitationService.findByEmployerWithRelationships(employer);
            System.out.println("📨 Found " + allInvitations.size() + " invitations");
            
            // Separate invitations by status
            List<JobInvitation> pendingInvitations = allInvitations.stream()
                .filter(inv -> inv.getStatus() == JobInvitation.InvitationStatus.PENDING)
                .toList();
            
            List<JobInvitation> acceptedInvitations = allInvitations.stream()
                .filter(inv -> inv.getStatus() == JobInvitation.InvitationStatus.ACCEPTED)
                .toList();
            
            List<JobInvitation> declinedInvitations = allInvitations.stream()
                .filter(inv -> inv.getStatus() == JobInvitation.InvitationStatus.DECLINED)
                .toList();
            
            System.out.println("📊 Pending: " + pendingInvitations.size() + 
                             ", Accepted: " + acceptedInvitations.size() + 
                             ", Declined: " + declinedInvitations.size());
            
            model.addAttribute("pendingInvitations", pendingInvitations);
            model.addAttribute("acceptedInvitations", acceptedInvitations);
            model.addAttribute("declinedInvitations", declinedInvitations);
            model.addAttribute("employer", employer);
            model.addAttribute("account", account);
            
            // Add CSRF token to model
            org.springframework.security.web.csrf.CsrfToken csrfToken = 
                (org.springframework.security.web.csrf.CsrfToken) request.getAttribute(org.springframework.security.web.csrf.CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("_csrf", csrfToken);
            }
            
            System.out.println("✅ Successfully loaded JobInvitations page");
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error in viewJobInvitations: " + e.getMessage());
            e.printStackTrace();
            // Add error message to model
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải danh sách lời mời: " + e.getMessage());
            // Return error page or handle gracefully
            model.addAttribute("pendingInvitations", List.of());
            model.addAttribute("acceptedInvitations", List.of());
            model.addAttribute("declinedInvitations", List.of());
        }
        return "employee/jobInvitations";
    }

    @PostMapping("/JobInvitations/{invitationId}/sendInterview")
    public String sendInterviewForInvitation(
            @PathVariable Long invitationId,
            @RequestParam String interviewTime,
            @RequestParam String interviewType,
            @RequestParam(required = false) String meetingLink,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes,
            Principal principal) {
        
        try {
            if (principal == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để thực hiện chức năng này");
                return "redirect:/login";
            }
            
            String employerEmail = principal.getName();
            Employer employer = employerService.findByEmail(employerEmail);
            
            // Get job invitation
            JobInvitation invitation = jobInvitationService.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lời mời việc làm"));
            
            // Check if invitation belongs to this employer
            if (!invitation.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện hành động này");
                return "redirect:/Employer/JobInvitations";
            }
            
            // Check if invitation is accepted
            if (invitation.getStatus() != JobInvitation.InvitationStatus.ACCEPTED) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể gửi lịch phỏng vấn cho lời mời đã được chấp nhận");
                return "redirect:/Employer/JobInvitations";
            }
            
            // Create interview record
            Interview interview = new Interview();
            interview.setJobApplication(null); // For job invitations, we don't have JobApplication
            interview.setInterviewType(interviewType);
            interview.setMeetingLink(meetingLink);
            interview.setNote(note);
            interview.setInterviewStatus("SCHEDULED");
            java.time.LocalDateTime interviewDate = java.time.LocalDateTime.parse(interviewTime);
            interview.setInterviewDate(interviewDate);
            iInterViewService.save(interview);
            
            // Send email to student
            String candidateEmail = invitation.getStudent().getAccount().getEmail();
            String candidateName = invitation.getStudent().getAccount().getFullName();
            String jobTitle = invitation.getJobPost().getJobTitle();
            
            emailService.sendInterviewScheduleEmail(candidateEmail, candidateName, jobTitle, interviewTime, interviewType, meetingLink, note);
            
            // Create notification for student
            notificationService.createNotification(
                invitation.getStudent().getAccount(),
                "Lịch phỏng vấn mới",
                "Bạn có lịch phỏng vấn cho vị trí " + jobTitle +
                        " vào " + interviewTime +
                        " theo hình thức " + interviewType + "." +
                        (meetingLink != null ? " Link meeting: " + meetingLink : "") +
                        (note != null ? " Ghi chú: " + note : ""),
                "INTERVIEW_SCHEDULED",
                invitationId
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lịch phỏng vấn thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi gửi lịch phỏng vấn: " + e.getMessage());
        }
        
        return "redirect:/Employer/JobInvitations";
    }
}

