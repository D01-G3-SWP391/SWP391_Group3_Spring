package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.dto.JobApplicationDTO;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Optional;

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
    @PostMapping("/addJobApplication")
    public String addJobApplication(@ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
                                    Model model, RedirectAttributes redirectAttributes) {
        // Kiểm tra trực tiếp các giá trị nhập vào
        if (jobApplicationDTO.getStudentId() == null || jobApplicationDTO.getJobPostId() == null) {
            model.addAttribute("error", "Student ID and Job Post ID cannot be null.");
            return "homePage/applyForm"; // Trả lại form nếu có lỗi
        }

//        System.out.println("JobPost ID: " + jobApplicationDTO.getJobPostId());
//        System.out.println("Student ID: " + jobApplicationDTO.getStudentId());
        // Tạo đối tượng JobApplication từ DTO
        JobApplication jobApplication = new JobApplication();

        // Set các thuộc tính từ DTO vào JobApplication
        jobApplication.setFullName(jobApplicationDTO.getFullname());
        jobApplication.setEmail(jobApplicationDTO.getEmail());
        jobApplication.setPhone(jobApplicationDTO.getPhoneNumber());
        jobApplication.setCvUrl(jobApplicationDTO.getCv_url());
        jobApplication.setDescription(jobApplicationDTO.getDescription());

        //jobApplication.setUniversity(jobApplicationDTO.getUniversity());

        // Truy xuất student và jobPost từ cơ sở dữ liệu
        Optional<Student> student = studentService.findById(jobApplicationDTO.getStudentId());
        Optional<JobPost> jobPost = iJobpostService.findByIdJobPost(jobApplicationDTO.getJobPostId());

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
