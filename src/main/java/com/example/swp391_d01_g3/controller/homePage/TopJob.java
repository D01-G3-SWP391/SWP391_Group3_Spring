package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.favorite.IFavoriteJobService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/top-jobs")
public class TopJob {

    @Autowired
    private IJobpostService jobpostService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IFavoriteJobService favoriteJobService;
    @Autowired
    private IStudentService studentService;

    @GetMapping
    public String showTopJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model, Principal principal) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> topJobsPage = jobpostService.getTopJobsPaginated(pageable);

        model.addAttribute("topJobsPage", topJobsPage);

        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
            Account account = accountService.findByEmail(principal.getName());
            model.addAttribute("account", account);
            
            // Add favorite job checking for students
            if (account != null && account.getRole() == Account.Role.student) {
                Student student = studentService.findByAccountUserId(account.getUserId());
                if (student != null) {
                    model.addAttribute("studentId", student.getStudentId());
                    // Add a utility object to check favorites in templates
                    model.addAttribute("favoriteChecker", new HomePage.FavoriteChecker(favoriteJobService, student.getStudentId()));
                    // Add favorite jobs list for navbar dropdown
                    List<JobPost> favoriteJobs = favoriteJobService.getFavoriteJobPostsByStudent(student.getStudentId());
                    model.addAttribute("favoriteJobs", favoriteJobs);
                    model.addAttribute("favoriteCount", favoriteJobs.size());
                }
            }
        }
        return "homePage/topJobs";
    }
}
