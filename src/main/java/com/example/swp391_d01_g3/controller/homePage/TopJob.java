package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
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

@Controller
@RequestMapping("/top-jobs")
public class TopJob {

    @Autowired
    private IJobpostService jobpostService;

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
        }
        return "homePage/topJobs";
    }
}
