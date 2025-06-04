package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.JobPost;

import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomePage {

   @Autowired
   private IJobpostService iJobpostService;

    @Autowired
    private IJobfieldService iJobfieldService;

    @GetMapping()
    public String showHomePage (Model model, Principal principal){
        List<JobPost> jobPosts = iJobpostService.findAll();
        List<JobField> jobField = iJobfieldService.findAll();
        model.addAttribute("jobField", jobField);
        model.addAttribute("jobPosts", jobPosts);
        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
        }
        return "homePage/homePage";
    }
}
