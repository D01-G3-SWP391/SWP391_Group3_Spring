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
import com.example.swp391_d01_g3.service.cloudinary.CloudinaryService;
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

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/JobDescription/Apply")
    public String submitApplication(@ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
                                    @RequestParam("cv") MultipartFile cv,
                                    Model model,
                                    RedirectAttributes redirectAttributes) throws IOException {
        JobApplication jobApplication = new JobApplication();

        // Handle CV file upload
        if (!cv.isEmpty()) {
            // Upload file lên Cloudinary
            String cvUrl = cloudinaryService.uploadFile(cv, "cv-uploads");
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

        if (student == null || jobPost.isEmpty()) {
            model.addAttribute("error", "Invalid student or job post.");
            return "homePage/appplyForm";
        }

        // Set mối quan hệ
        jobApplication.setStudent(student.get());
        jobApplication.setJobPost(jobPost.get());

        // Lưu JobApplication vào cơ sở dữ liệu
        jobApplication = jobApplicationService.save(jobApplication);
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

        redirectAttributes.addFlashAttribute("success", "Job Application has been saved successfully.");
        return "redirect:/";
    }
}
