package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.dto.JobApplicationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.IJobApplicationRepository;
import com.example.swp391_d01_g3.service.favorite.IFavoriteJobService;
import com.example.swp391_d01_g3.service.jobapplication.IJobApplicationService;
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
    @Autowired
    private IJobApplicationService jobApplicationService;
    @Autowired
    private IFavoriteJobService favoriteJobService;

    @GetMapping("/JobPost")
    public String showDescription(@RequestParam("id") Integer id, 
                                @RequestParam(value = "lang", required = false) String lang,
                                Model model, Principal principal) {
        // Lấy thông tin job post
        List<JobPost> jobPosts = iJobpostService.findAllWithEmployer(id);
        model.addAttribute("jobPosts", jobPosts);

        // Tạo DTO cho modal form
        JobApplicationDTO jobApplicationDTO = new JobApplicationDTO();
        
        // Lấy thông tin user nếu đã đăng nhập
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("userEmail", email); // Thêm userEmail cho navbar
            
            Account userAccount = iAccountService.findByEmail(email);
            model.addAttribute("account", userAccount);

            if (userAccount != null) {
                // Auto-fill họ tên, email và phone từ database vào JobApplicationDTO
                jobApplicationDTO.setFullname(userAccount.getFullName());
                jobApplicationDTO.setEmail(userAccount.getEmail());
                jobApplicationDTO.setPhoneNumber(userAccount.getPhone());
                
                // Chỉ lấy thông tin student nếu user có role student
                Student studentDetails = iStudentService.findByAccountUserId(userAccount.getUserId());
                if (studentDetails != null){
                    model.addAttribute("studentDetails", studentDetails);
                    
                    // Kiểm tra xem student đã apply vào job này chưa
                    boolean hasApplied = jobApplicationService.hasStudentAppliedToJob(studentDetails.getStudentId(), id);
                    model.addAttribute("hasApplied", hasApplied);
                    
                    // Add favorite job checking for students
                    model.addAttribute("studentId", studentDetails.getStudentId());
                    // Add a utility object to check favorites in templates
                    model.addAttribute("favoriteChecker", new FavoriteChecker(favoriteJobService, studentDetails.getStudentId()));
                    // Add favorite jobs list for navbar dropdown
                    List<JobPost> favoriteJobs = favoriteJobService.getFavoriteJobPostsByStudent(studentDetails.getStudentId());
                    model.addAttribute("favoriteJobs", favoriteJobs);
                    model.addAttribute("favoriteCount", favoriteJobs.size());
                }
            } else {
                return "redirect:/Login";
            }
        }
        // Cho phép user chưa đăng nhập cũng xem được (theo permitAll)
        
        model.addAttribute("jobApplicationDTO", jobApplicationDTO);
        return "homePage/descriptionJob";
    }
    
    // Helper class for checking favorites in templates
    public static class FavoriteChecker {
        private final IFavoriteJobService favoriteJobService;
        private final Integer studentId;
        
        public FavoriteChecker(IFavoriteJobService favoriteJobService, Integer studentId) {
            this.favoriteJobService = favoriteJobService;
            this.studentId = studentId;
        }
        
        public boolean isFavorited(Integer jobPostId) {
            return favoriteJobService.isFavorited(studentId, jobPostId);
        }
    }
}
