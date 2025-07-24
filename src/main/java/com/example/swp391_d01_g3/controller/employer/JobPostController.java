package com.example.swp391_d01_g3.controller.employer;

import com.example.swp391_d01_g3.dto.JobPostDTO;
import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.interview.IInterViewService;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.email.EmailService;
import com.example.swp391_d01_g3.service.interview.IInterViewService;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Employer")
public class JobPostController {

    @Autowired
    private IJobfieldService iJobfieldService;

    @Autowired
    private IJobpostService iJobpostService;

    @Autowired
    private IEmployerService iEmployerService;

    @Autowired
    private IJobApplicationService iJobApplicationService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IInterViewService iInterViewService;

    @Autowired
    private INotificationService notificationService;

    @PostMapping("/CreateJobPost")
    public String createJobPost(
            @ModelAttribute("jobPostDTO") @Valid JobPostDTO dto,
            BindingResult result,
            Model model,
            Principal principal,
            RedirectAttributes ra
    ) {
        if (result.hasErrors()) {
            // Thêm account cho navbar trong trường hợp lỗi
            model.addAttribute("account", accountService.findByEmail(principal.getName()));
            model.addAttribute("jobFields", iJobfieldService.findAll());
            model.addAttribute("jobTypes", JobPost.JobType.values());
            return "employee/createJobPost";
        }

        Employer emp = iEmployerService.findByEmail(principal.getName());
        JobField field = iJobfieldService.findById(dto.getJobFieldId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid JobField Id: " + dto.getJobFieldId()));

        JobPost job = new JobPost();
        job.setEmployer(emp);
        job.setJobField(field);
        job.setJobTitle(dto.getJobTitle());
        job.setJobLocation(dto.getJobLocation());
        job.setJobSalary(dto.getJobSalary());
        job.setJobRequirements(dto.getJobRequirements());
        job.setJobDescription(dto.getJobDescription());
        job.setJobType(JobPost.JobType.valueOf(dto.getJobType()));
        job.setAppliedQuality(0);
        // createdAt, approvalStatus, displayStatus dùng mặc định

        // 4) Lưu và thông báo thành công
        iJobpostService.save(job);
        ra.addFlashAttribute("successMsg", "Đăng việc thành công!");
        return "redirect:/Employer/JobPosts";
    }
    @GetMapping("/CreateJobPost")
    public String showCreateForm(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("account", accountService.findByEmail(principal.getName()));
        }
        model.addAttribute("jobPostDTO", new JobPostDTO());
        model.addAttribute("jobFields", iJobfieldService.findAll());
        model.addAttribute("jobTypes", JobPost.JobType.values());
        return "employee/createJobPost";
    }

        @GetMapping("/JobPosts")
        public String viewJobPosts(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "5") int size,
                @RequestParam(defaultValue = "active") String filter,
                Model model,
                Authentication authentication) {

            String employerEmail = authentication.getName();
            Employer employer = iEmployerService.findByEmail(employerEmail);

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            
            Page<JobPost> jobPostPage;
            long currentCount;
            
            // Filter based on the parameter
            if ("hidden".equals(filter)) {
                jobPostPage = iJobpostService.findByEmployerAndDisplayStatusOrderByCreatedAtDesc(
                    employer, JobPost.DisplayStatus.INACTIVE, pageable);
                currentCount = iJobpostService.countHiddenJobPostsByEmployerEmail(employerEmail);
            } else {
                // Default: show active jobs
                jobPostPage = iJobpostService.findActiveJobPostsByEmployerEmail(employerEmail, pageable);
                currentCount = iJobpostService.countActiveJobPostsByEmployerEmail(employerEmail);
            }

            long totalActiveJobs = iJobpostService.countActiveJobPostsByEmployerEmail(employerEmail);
            long hiddenJobs = iJobpostService.countHiddenJobPostsByEmployerEmail(employerEmail);
            long pendingJobs = iJobpostService.countJobPostsByEmployerEmailAndStatus(employerEmail, "PENDING");

            // Thêm account cho navbar
            model.addAttribute("account", accountService.findByEmail(employerEmail));
            model.addAttribute("jobPostPage", jobPostPage);
            model.addAttribute("employerEmail", employerEmail);
            model.addAttribute("totalJobs", totalActiveJobs);
            model.addAttribute("hiddenJobs", hiddenJobs);
            model.addAttribute("pendingJobs", pendingJobs);
            model.addAttribute("currentFilter", filter);
            model.addAttribute("currentCount", currentCount);

            return "employee/viewJobPost";
        }

        // Hide job post (set display status to INACTIVE)
        @PostMapping("/JobPosts/Hide/{jobPostId}")
        public String hideJobPost(@PathVariable Integer jobPostId, 
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
            try {
                String employerEmail = authentication.getName();
                Employer employer = iEmployerService.findByEmail(employerEmail);
                
                // Verify that this job post belongs to the current employer
                JobPost jobPost = iJobpostService.findByJobPostId(jobPostId).orElse(null);
                if (jobPost == null || !jobPost.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                    redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy công việc hoặc bạn không có quyền truy cập.");
                    return "redirect:/Employer/JobPosts";
                }
                
                // Set display status to INACTIVE
                jobPost.setDisplayStatus(JobPost.DisplayStatus.INACTIVE);
                iJobpostService.save(jobPost);
                
                redirectAttributes.addFlashAttribute("successMsg", "Đã ẩn công việc thành công!");
                
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra khi ẩn công việc.");
            }
            
            return "redirect:/Employer/JobPosts";
        }

        // Unhide job post (set display status to ACTIVE)
        @PostMapping("/JobPosts/Unhide/{jobPostId}")
        public String unhideJobPost(@PathVariable Integer jobPostId, 
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
            try {
                String employerEmail = authentication.getName();
                Employer employer = iEmployerService.findByEmail(employerEmail);
                
                // Verify that this job post belongs to the current employer
                JobPost jobPost = iJobpostService.findByJobPostId(jobPostId).orElse(null);
                if (jobPost == null || !jobPost.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                    redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy công việc hoặc bạn không có quyền truy cập.");
                    return "redirect:/Employer/JobPosts";
                }
                
                // Set display status to ACTIVE
                jobPost.setDisplayStatus(JobPost.DisplayStatus.ACTIVE);
                iJobpostService.save(jobPost);
                
                redirectAttributes.addFlashAttribute("successMsg", "Đã hiện công việc thành công!");
                
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra khi hiện công việc.");
            }
            
            return "redirect:/Employer/JobPosts";
        }




    @GetMapping("/EditJobPost/{jobPostId}")
    public String showEditForm(@PathVariable("jobPostId") Integer jobPostId, Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("account", accountService.findByEmail(principal.getName()));
        }
        JobPost jobPost = iJobpostService.findById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid JobPost Id: " + jobPostId));

        List<JobField> jobFields = iJobfieldService.findAll();
        model.addAttribute("jobFields", jobFields);
        model.addAttribute("jobTypes", JobPost.JobType.values());

        model.addAttribute("jobPost", jobPost);

        return "employee/editJobPost";
    }

    @PostMapping("/EditJobPost/{jobPostId}")
    public String updateJobPost(
            @PathVariable("jobPostId") Integer jobPostId,
            @ModelAttribute("jobPost") JobPost jobPost,
            @RequestParam("jobFieldId") Integer jobFieldId,
            Authentication authentication,
            RedirectAttributes ra) {

        String employerEmail = authentication.getName();
        Employer employer = iEmployerService.findByEmail(employerEmail);
        jobPost.setEmployer(employer);

        JobField jobField = iJobfieldService.findById(jobFieldId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid JobField Id: " + jobFieldId));

        jobPost.setJobField(jobField);
        jobPost.setCreatedAt(LocalDateTime.now());

        if (jobPost.getApprovalStatus() == null) {
            jobPost.setApprovalStatus(JobPost.ApprovalStatus.PENDING);
        }
        iJobpostService.save(jobPost);
        ra.addFlashAttribute("successMsg", "Cập nhật công việc thành công!");
        return "redirect:/Employer/JobPosts";  // Redirect đến endpoint mới
    }

    @PostMapping("/DeleteJobPost/{jobPostId}")
    public String deleteJobPost(@PathVariable("jobPostId") Integer jobPostId, Authentication authentication, RedirectAttributes ra) {
        String employerEmail = authentication.getName();
        Employer employer = iEmployerService.findByEmail(employerEmail);

        JobPost jobPost = iJobpostService.findById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid JobPost Id: " + jobPostId));

        if (!jobPost.getEmployer().getAccount().getEmail().equals(employerEmail)) {
            throw new SecurityException("You do not have permission to delete this job post");
        }
        iJobpostService.deleteById(jobPostId);
        ra.addFlashAttribute("successMsg", "Xóa công việc thành công!");
        return "redirect:/Employer/JobPosts";  // Redirect đến endpoint mới
    }

    @GetMapping("/JobPosts/{jobPostId}/applications")
    public String viewJobPostApplications(
        @PathVariable Integer jobPostId,
        @RequestParam(required = false) String searchName,
        @RequestParam(required = false) String searchExperience,
        Model model, Principal principal) {
        Employer employer = iEmployerService.findByEmail(principal.getName());
        JobPost jobPost = iJobpostService.findByJobPostId(jobPostId).orElse(null);
        if (jobPost == null || !jobPost.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            return "redirect:/Employer/JobPosts";
        }
        if (searchName != null && searchName.trim().isEmpty()) searchName = null;
        List<JobApplication> applications = iJobApplicationService
            .findByJobPostIdAndNameAndExperience(jobPostId, searchName, null);
        List<JobApplication> applications1 = iJobApplicationService.findByJobPostId(jobPostId);
        // Thêm account cho navbar
        model.addAttribute("account", accountService.findByEmail(principal.getName()));
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("applications", applications1);
        model.addAttribute("totalApplications", applications1.size());
        model.addAttribute("statuses", JobApplication.ApplicationStatus.values());
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchExperience", searchExperience);
        return "employee/jobPostApplications";
    }



    // Update Application Status
    @PostMapping("/JobPosts/{jobPostId}/applications/{applicationId}/updateStatus")
    public String updateApplicationStatus(
            @PathVariable Integer jobPostId,
            @PathVariable Integer applicationId,
            @RequestParam(value = "status", required = false) String status,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        try {
            // Validate status parameter
            if (status == null || status.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Trạng thái không được để trống!");
                return "redirect:/Employer/JobPosts/" + jobPostId + "/applications";
            }

            String employerEmail = authentication.getName();
            Employer employer = iEmployerService.findByEmail(employerEmail);

            JobApplication application = iJobApplicationService.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ứng tuyển"));

            // Kiểm tra quyền
            if (!application.getJobPost().getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện hành động này!");
                return "redirect:/Employer/JobPosts/" + jobPostId + "/applications";
            }

            JobApplication.ApplicationStatus newStatus = JobApplication.ApplicationStatus.valueOf(status);
            iJobApplicationService.updateApplicationStatus(applicationId, newStatus);

            // Gửi email cho ACCEPTED và REJECTED
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

            // Tạo thông báo cho ứng viên
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

        return "redirect:/Employer/JobPosts/" + jobPostId + "/applications";
    }

    // Send Interview Mail
    @PostMapping("/JobPosts/{jobPostId}/applications/{applicationId}/sendInterviewMail")
    public String sendInterviewMail(
            @PathVariable Integer jobPostId,
            @PathVariable Integer applicationId,
            @RequestParam String interviewTime,
            @RequestParam String interviewType,
            @RequestParam(required = false) String meetingLink,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        try {
            String employerEmail = authentication.getName();
            Employer employer = iEmployerService.findByEmail(employerEmail);
            JobApplication application = iJobApplicationService.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ứng tuyển"));

            // Kiểm tra quyền
            if (!application.getJobPost().getEmployer().getEmployerId().equals(employer.getEmployerId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện hành động này!");
                return "redirect:/Employer/JobPosts/" + jobPostId + "/applications";
            }

            // Lưu lịch phỏng vấn
            Interview interview = new Interview();
            interview.setJobApplication(application);
            interview.setInterviewType(interviewType);
            interview.setMeetingLink(meetingLink);
            interview.setNote(note);
            interview.setInterviewStatus("SCHEDULED");
            LocalDateTime interviewDate = LocalDateTime.parse(interviewTime);
            interview.setInterviewDate(interviewDate);
            iInterViewService.save(interview);

            // Gửi email
            String candidateEmail = application.getEmail();
            String candidateName = application.getFullName();
            String jobTitle = application.getJobPost().getJobTitle();

            emailService.sendInterviewScheduleEmail(candidateEmail, candidateName, jobTitle, interviewTime, interviewType, meetingLink, note);

            // Cập nhật trạng thái
            iJobApplicationService.updateApplicationStatus(applicationId, JobApplication.ApplicationStatus.INTERVIEW);

            // Tạo thông báo
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

        return "redirect:/Employer/JobPosts/" + jobPostId + "/applications";
    }
}