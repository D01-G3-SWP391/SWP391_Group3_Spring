package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/TopJob")
public class TopJob {

    @Autowired
    private IJobpostService jobpostService;

    @GetMapping("/View")
    public String showTopJobs(@RequestParam(defaultValue = "20") int limit, Model model, Principal principal) {
        List<JobPost> topJobs = jobpostService.getTopJobsLimit(limit);
        model.addAttribute("jobs", topJobs);
        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
        }
        return "homePage/topJobs"; // Sử dụng template list jobs có sẵn
    }
}
