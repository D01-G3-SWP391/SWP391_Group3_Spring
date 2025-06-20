package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.dto.JobApplicationDTO;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private INotificationService notificationService;

    @PostMapping("/JobDescription/Apply")
    public String submitApplication(@Valid @ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
                                  BindingResult bindingResult,
                                  @RequestParam("cvFile") MultipartFile cv,
                                  Model model,
                                  RedirectAttributes redirectAttributes) throws IOException {
        
        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            // Thêm lại dữ liệu cần thiết cho form
            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
            model.addAttribute("jobPosts", Arrays.asList(iJobpostService.findByJobPostId(jobApplicationDTO.getJobPostId()).orElse(null)));
            model.addAttribute("studentDetails", studentService.findById(jobApplicationDTO.getStudentId()).orElse(null));
            
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin đã nhập.");
            return "homePage/descriptionJob";
        }

        // Validate file CV
        if (cv != null && !cv.isEmpty()) {
            String originalFilename = cv.getOriginalFilename();
            if (originalFilename != null) {
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                String[] allowedExtensions = {".pdf", ".png", ".jpg", ".jpeg", ".gif"};
                
                if (!Arrays.asList(allowedExtensions).contains(fileExtension)) {
                    model.addAttribute("jobApplicationDTO", jobApplicationDTO);
                    model.addAttribute("jobPosts", Arrays.asList(iJobpostService.findByJobPostId(jobApplicationDTO.getJobPostId()).orElse(null)));
                    model.addAttribute("studentDetails", studentService.findById(jobApplicationDTO.getStudentId()).orElse(null));
                    model.addAttribute("cvError", "Chỉ chấp nhận file PDF và ảnh (PNG, JPG, JPEG, GIF)");
                    return "homePage/descriptionJob";
                }
                
                if (cv.getSize() > 2048 * 1024) { // 2048KB
                    model.addAttribute("jobApplicationDTO", jobApplicationDTO);
                    model.addAttribute("jobPosts", Arrays.asList(iJobpostService.findByJobPostId(jobApplicationDTO.getJobPostId()).orElse(null)));
                    model.addAttribute("studentDetails", studentService.findById(jobApplicationDTO.getStudentId()).orElse(null));
                    model.addAttribute("cvError", "Kích thước file không được vượt quá 2048KB");
                    return "homePage/descriptionJob";
                }
            }
        }

        JobApplication jobApplication = new JobApplication();

        // Handle CV file upload
        if (cv != null && !cv.isEmpty()) {
            String originalFilename = cv.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            String uploadDir = "uploads/cv/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(cv.getInputStream(), filePath);
            String cvUrl = uploadDir + newFileName;
            jobApplication.setCvUrl(cvUrl);
        }

        // Set các thuộc tính từ DTO vào JobApplication
        jobApplication.setFullName(jobApplicationDTO.getFullname());
        jobApplication.setEmail(jobApplicationDTO.getEmail());
        jobApplication.setPhone(jobApplicationDTO.getPhoneNumber());
        jobApplication.setDescription(jobApplicationDTO.getDescription());

        // Truy xuất student và jobPost từ cơ sở dữ liệu
        Optional<Student> student = studentService.findById(jobApplicationDTO.getStudentId());
        Optional<JobPost> jobPost = iJobpostService.findByJobPostId(jobApplicationDTO.getJobPostId());

        if (student.isEmpty() || jobPost.isEmpty()) {
            model.addAttribute("error", "Thông tin sinh viên hoặc công việc không hợp lệ.");
            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
            model.addAttribute("jobPosts", Arrays.asList(jobPost.orElse(null)));
            model.addAttribute("studentDetails", student.orElse(null));
            return "homePage/descriptionJob";
        }

        // Set mối quan hệ
        jobApplication.setStudent(student.get());
        jobApplication.setJobPost(jobPost.get());

        try {
            // Lưu JobApplication vào cơ sở dữ liệu
            jobApplication = jobApplicationService.save(jobApplication);
            
            // Cập nhật số lượng ứng viên
            JobPost updatedJobPost = jobPost.get();
            updatedJobPost.setAppliedQuality(updatedJobPost.getAppliedQuality() + 1);
            iJobpostService.save(updatedJobPost);

            // Create notification for student
            notificationService.createNotification(
                student.get().getAccount(),
                "Job Application Submitted",
                "Your application for " + jobPost.get().getJobTitle() + " has been submitted successfully.",
                "JOB_APPLICATION",
                jobApplication.getApplicationId().longValue()
            );

            // Create notification for employer
            notificationService.createNotification(
                jobPost.get().getEmployer().getAccount(),
                "New Job Application",
                "A new application has been received for " + jobPost.get().getJobTitle() + " from " + student.get().getAccount().getFullName(),
                "NEW_APPLICATION",
                jobApplication.getApplicationId().longValue()
            );

            redirectAttributes.addFlashAttribute("success", "Đơn ứng tuyển đã được gửi thành công!");
            return "redirect:/";
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi gửi đơn ứng tuyển. Vui lòng thử lại.");
            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
            model.addAttribute("jobPosts", Arrays.asList(jobPost.get()));
            model.addAttribute("studentDetails", student.get());
            return "homePage/descriptionJob";
        }
    }
}
