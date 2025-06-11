package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.dto.JobApplicationDTO;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/Student")
public class AddJobApplication {
    @Autowired
    private IJobApplicationService jobApplicationService;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private IAccountService iAccountService;

    @Autowired
    private IJobpostService iJobpostService;

    @GetMapping("/JobDescription/Apply")
    public String showApplyForm(@RequestParam("id") Integer jobPostId, Model model) {
        Optional<JobPost> jobPost = iJobpostService.findByJobPostId(jobPostId);
        if (jobPost.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("jobApplicationDTO", new JobApplicationDTO());
        model.addAttribute("jobPosts", Arrays.asList(jobPost.get()));

        return "homePage/applyForm";
    }
    @PostMapping("/addJobApplication")
    public String addJobApplication(@ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
                                    @RequestParam("cv") MultipartFile cvFile,
                                    Model model, RedirectAttributes redirectAttributes) {
        // Kiểm tra trực tiếp các giá trị nhập vào
        if (jobApplicationDTO.getStudentId() == null || jobApplicationDTO.getJobPostId() == null) {
            model.addAttribute("error", "Student ID and Job Post ID cannot be null.");
            return "homePage/applyForm"; // Trả lại form nếu có lỗi
        }

//        System.out.println("JobPost ID: " + jobApplicationDTO.getJobPostId());
//        System.out.println("Student ID: " + jobApplicationDTO.getStudentId());
        // Xử lý file upload
        String cvUrl = null;
        if (cvFile != null && !cvFile.isEmpty()) {
            try {
                // Tạo thư mục uploads nếu chưa có
                String uploadDir = "uploads/cv/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Tạo tên file unique
                String fileName = UUID.randomUUID().toString() + "_" + cvFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                
                // Lưu file
                Files.copy(cvFile.getInputStream(), filePath);
                cvUrl = uploadDir + fileName;
                
            } catch (IOException e) {
                model.addAttribute("error", "Lỗi khi tải lên file CV: " + e.getMessage());
                return "homePage/applyForm";
            }
        }

        // Tạo đối tượng JobApplication từ DTO
        JobApplication jobApplication = new JobApplication();

        // Set các thuộc tính từ DTO vào JobApplication
        jobApplication.setFullName(jobApplicationDTO.getFullname());
        jobApplication.setEmail(jobApplicationDTO.getEmail());
        jobApplication.setPhone(jobApplicationDTO.getPhoneNumber());
        jobApplication.setCvUrl(cvUrl); // Sử dụng path file đã upload
        jobApplication.setDescription(jobApplicationDTO.getDescription());

        //jobApplication.setUniversity(jobApplicationDTO.getUniversity());

        // Truy xuất student và jobPost từ cơ sở dữ liệu
        Optional<Student> student = studentService.findById(jobApplicationDTO.getStudentId());
        Optional<JobPost> jobPost = iJobpostService.findByJobPostId(jobApplicationDTO.getJobPostId());

        System.out.println(student);
        System.out.println(jobPost);

        if (student == null || jobPost.isEmpty()) {
            model.addAttribute("error", "Invalid student or job post.");
            return "homePage/appplyForm";  // Trả lại form nếu không tìm thấy student hoặc jobPost
        }

        // Set mối quan hệ
        jobApplication.setStudent(student.get());
        jobApplication.setJobPost(jobPost.get());

        // Lưu JobApplication vào cơ sở dữ liệu
        jobApplicationService.save(jobApplication);
        JobPost updatedJobPost = jobPost.get();
        updatedJobPost.setAppliedQuality(updatedJobPost.getAppliedQuality() + 1);  // Tăng applied_quality lên 1
        iJobpostService.save(updatedJobPost);  // Lưu lại JobPost với giá trị đã cập nhật


        redirectAttributes.addFlashAttribute("success", "Job Application has been saved successfully.");
        return "redirect:/";
    }


}
