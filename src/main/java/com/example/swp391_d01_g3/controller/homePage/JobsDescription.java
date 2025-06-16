package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.dto.JobApplicationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.IJobApplicationRepository;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/JobDescription")
public class JobsDescription {
    @Autowired
    private IJobpostService iJobpostService;
    @Autowired
    private IStudentService iStudentService;
    @Autowired
    private IAccountService iAccountService;

    @GetMapping("/JobPost")
    public String showDescription(@RequestParam("id") Integer id, 
                                @RequestParam(value = "lang", required = false) String lang,
                                Model model, Principal principal) {
        // Lấy thông tin job post
        List<JobPost> jobPosts = iJobpostService.findAllWithEmployer(id);
        model.addAttribute("jobPosts", jobPosts);

        // Tạo DTO cho modal form
        JobApplicationDTO jobApplicationDTO = new JobApplicationDTO();
        model.addAttribute("jobApplicationDTO", jobApplicationDTO);

        // Lấy thông tin student nếu đã đăng nhập
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("userEmail", email); // Thêm userEmail cho navbar
            
            Account studentAccount = iAccountService.findByEmail(email);
            model.addAttribute("account", studentAccount);

            if (studentAccount != null) {
                Student studentDetails = iStudentService.findByAccountUserId(studentAccount.getUserId());
                model.addAttribute("studentDetails", studentDetails);
                System.out.println("Student ID: " + studentDetails.getStudentId());
            }
//            System.out.println(email);
        }

        return "homePage/descriptionJob";
    }
}
