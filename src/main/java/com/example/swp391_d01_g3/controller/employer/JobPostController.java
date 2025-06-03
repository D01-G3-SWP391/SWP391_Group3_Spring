package com.example.swp391_d01_g3.controller.employer;

import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/employer/jobpost")
public class JobPostController {

    @Autowired
    private IJobfieldService jobfieldService;

    @Autowired
    private IJobpostService jobpostService;

    @Autowired
    private IEmployerRepository employerRepository;

    /**
     * Hiển thị trang form để Employer tạo bài đăng
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Lấy danh sách jobField để đổ vào dropdown
        List<JobField> jobFields = jobfieldService.findAll();
        model.addAttribute("jobFields", jobFields);

        // Lấy enum JobType (FULL_TIME, PART_TIME, v.v.) để đổ dropdown
        model.addAttribute("jobTypes", JobPost.JobType.values());

        // Tạo 1 đối tượng rỗng để bind form
        model.addAttribute("jobPost", new JobPost());

        return "employee/createJobPost";
    }

    /**
     * Xử lý POST khi Employer submit form
     */
    @PostMapping("/create")
    public String createJobPost(
            @ModelAttribute("jobPost") JobPost jobPost,
            @RequestParam("jobFieldId") Integer jobFieldId) {

        // 1. Thiết lập JobField (chọn từ dropdown)
        JobField selectedField = jobfieldService.findById(jobFieldId).orElse(null);
        jobPost.setJobField(selectedField);

        // 2. Lấy Employer hiện tại (dựa vào Account trong Context)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Giả sử Principal chính là instance của Account (được load bởi AccountDetailsServiceImpl)
        Account currentAccount = (Account) auth.getPrincipal();
        // Tìm Employer tương ứng
        Employer employer = employerRepository.findByAccount(currentAccount);
        jobPost.setEmployer(employer);

        // 3. Khởi tạo các trường mặc định
        jobPost.setAppliedQuality(0);
        jobPost.setApprovalStatus(JobPost.ApprovalStatus.PENDING);
        jobPost.setDisplayStatus(JobPost.DisplayStatus.ACTIVE);
        jobPost.setCreatedAt(LocalDateTime.now());

        // 4. Lưu vào cơ sở dữ liệu
        jobpostService.save(jobPost);

        // 5. Redirect về dashboard của employer hoặc trang danh sách job posts
        return "redirect:/employer/dashboard";
    }
}
