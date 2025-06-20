package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.dto.JobApplicationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.cloudinary.CloudinaryService;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.notification.INotificationService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import com.example.swp391_d01_g3.service.cloudinary.CloudinaryService;
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
import java.security.Principal;
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
    public String submitApplication(@Valid @ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
                                  BindingResult bindingResult,
                                  @RequestParam("cvFile") MultipartFile cv,
                                  @RequestParam("jobPostId") Integer jobPostId,
                                  Principal principal,
                                  Model model,
                                  RedirectAttributes redirectAttributes) throws IOException {

        // Lấy thông tin student từ principal (session)
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để ứng tuyển.");
            return "redirect:/Login";
        }

        String email = principal.getName();
        Account account = iAccountService.findByEmail(email);
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin tài khoản.");
            return "redirect:/Login";
        }

        Student student = studentService.findByAccountUserId(account.getUserId());
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Chỉ sinh viên mới có thể ứng tuyển.");
            return "redirect:/Login";
        }

        Long studentId = student.getStudentId().longValue();

        // Kiểm tra validation errors (chỉ validate dữ liệu form, không validate ID)
        if (bindingResult.hasErrors()) {
            // Thêm lại dữ liệu cần thiết cho form
            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
            model.addAttribute("jobPosts", Arrays.asList(iJobpostService.findByJobPostId(jobPostId).orElse(null)));
            model.addAttribute("studentDetails", student);

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
                    model.addAttribute("jobPosts", Arrays.asList(iJobpostService.findByJobPostId(jobPostId).orElse(null)));
                    model.addAttribute("studentDetails", student);
                    model.addAttribute("cvError", "Chỉ chấp nhận file PDF và ảnh (PNG, JPG, JPEG, GIF)");
                    return "homePage/descriptionJob";
                }

                if (cv.getSize() > 2048 * 1024) { // 2048KB
                    model.addAttribute("jobApplicationDTO", jobApplicationDTO);
                    model.addAttribute("jobPosts", Arrays.asList(iJobpostService.findByJobPostId(jobPostId).orElse(null)));
                    model.addAttribute("studentDetails", student);
                    model.addAttribute("cvError", "Kích thước file không được vượt quá 2048KB");
                    return "homePage/descriptionJob";
                }
            }
        }

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

        // Truy xuất jobPost từ cơ sở dữ liệu
        Optional<JobPost> jobPost = iJobpostService.findByJobPostId(jobPostId);

        if (jobPost.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin công việc.");
            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
            model.addAttribute("studentDetails", student);
            return "homePage/descriptionJob";
        }

        // Set mối quan hệ
        jobApplication.setStudent(student);
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
                student.getAccount(),
                "Job Application Submitted",
                "Your application for " + jobPost.get().getJobTitle() + " has been submitted successfully.",
                "JOB_APPLICATION",
                jobApplication.getApplicationId().longValue()
            );

            // Create notification for employer
            notificationService.createNotification(
                jobPost.get().getEmployer().getAccount(),
                "New Job Application",
                "A new application has been received for " + jobPost.get().getJobTitle() + " from " + student.getAccount().getFullName(),
                "NEW_APPLICATION",
                jobApplication.getApplicationId().longValue()
            );

            redirectAttributes.addFlashAttribute("success", "Đơn ứng tuyển đã được gửi thành công!");
            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi gửi đơn ứng tuyển. Vui lòng thử lại.");
            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
            model.addAttribute("jobPosts", Arrays.asList(jobPost.get()));
            model.addAttribute("studentDetails", student);
            return "homePage/descriptionJob";
        }
    }
}
