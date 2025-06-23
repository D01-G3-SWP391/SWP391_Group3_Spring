package com.example.swp391_d01_g3.controller.employer;

import com.example.swp391_d01_g3.dto.JobPostDTO;
import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
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
                @RequestParam(defaultValue = "1") int size,
                Model model,
                Authentication authentication) {

            String employerEmail = authentication.getName();
            Employer employer = iEmployerService.findByEmail(employerEmail);

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<JobPost> jobPostPage = iJobpostService.findJobPostsByEmployerEmail(employerEmail, pageable);

            long totalJobs = iJobpostService.countJobPostsByEmployerEmail(employerEmail);

            long pendingJobs = iJobpostService.countJobPostsByEmployerEmailAndStatus(employerEmail, "PENDING");


            // Thêm account cho navbar
            model.addAttribute("account", accountService.findByEmail(employerEmail));
            model.addAttribute("jobPostPage", jobPostPage);
            model.addAttribute("employerEmail", employerEmail);
            model.addAttribute("totalJobs", totalJobs);
            model.addAttribute("pendingJobs", pendingJobs);

            return "employee/viewJobPost";
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
    public String viewJobPostApplications(@PathVariable Integer jobPostId, Model model, Principal principal) {
        Employer employer = iEmployerService.findByEmail(principal.getName());
        JobPost jobPost = iJobpostService.findByJobPostId(jobPostId).orElse(null);
        if (jobPost == null || !jobPost.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            return "redirect:/Employer/JobPosts";
        }
        List<JobApplication> applications = iJobApplicationService.findByJobPostId(jobPostId);
        // Thêm account cho navbar
        model.addAttribute("account", accountService.findByEmail(principal.getName()));
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("applications", applications);
        model.addAttribute("totalApplications", applications.size());
        model.addAttribute("statuses", com.example.swp391_d01_g3.model.JobApplication.ApplicationStatus.values());
        return "employee/jobPostApplications";
    }
}