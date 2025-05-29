package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.JobPost;

import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomePage {

   @Autowired
   private IJobpostService iJobpostService;

    @GetMapping()
    public String showHomePage (Model model, Principal principal){
        List<JobPost> jobPosts = iJobpostService.findAll();
        model.addAttribute("jobPosts", jobPosts);
        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
        }
        return "homePage/homePage";
    }
}
