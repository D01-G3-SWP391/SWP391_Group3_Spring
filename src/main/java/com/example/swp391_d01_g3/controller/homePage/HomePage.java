package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.JobPost;

import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.security.IAccountService;
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
    
    @Autowired
    private IAccountService accountService;

    @GetMapping()
    public String showHomePage (Model model, Principal principal,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "8") int size){
        
        Pageable pageable = PageRequest.of(page - 1, size);
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
            Account account = accountService.findByEmail(principal.getName());
            
            // Kiểm tra nếu account không tồn tại hoặc bị ban
            if (account == null) {
                // Account có thể bị ban hoặc không tồn tại, check với findByEmailAnyStatus
                Account anyStatusAccount = accountService.findByEmailAnyStatus(principal.getName());
                if (anyStatusAccount != null && anyStatusAccount.getStatus() == Account.Status.inactive) {
                    // Account bị ban, logout và redirect về login
                    return "redirect:/logout";
                }
            }
            
            model.addAttribute("account", account);
        }
        return "homePage/homePage";
    }


}
