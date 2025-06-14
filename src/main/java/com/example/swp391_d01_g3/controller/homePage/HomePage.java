package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.JobPost;

import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomePage {

   @Autowired
   private IJobpostService iJobpostService;

    @Autowired
    private IJobfieldService iJobfieldService;

    @GetMapping()
    public String showHomePage (Model model, Principal principal,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "8") int size){
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobPostsPage = iJobpostService.findAll(pageable);
        
        model.addAttribute("jobPostsPage", jobPostsPage);
        model.addAttribute("jobPosts", jobPostsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPostsPage.getTotalPages());
        model.addAttribute("totalElements", jobPostsPage.getTotalElements());
        model.addAttribute("size", size);
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
