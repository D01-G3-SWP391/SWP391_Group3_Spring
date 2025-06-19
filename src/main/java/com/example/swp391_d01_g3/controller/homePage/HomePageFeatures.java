package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/HomePage")
public class HomePageFeatures {

    @Autowired
    private IJobpostService jobpostService;

    @Autowired
    private IJobfieldService iJobfieldService;

    @PostMapping("/search")
    public String searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(name = "fieldId", required = false) Integer fieldId,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) String companyName,
            Model model,
            Principal principal) {

        try {
            // Search for jobs based on criteria
            List<JobPost> jobPosts = jobpostService.searchJobs(
                    keyword,
                    location,
                    jobType,
                    fieldId,
                    salary,
                    companyName
            );

            // Add search results to model
            model.addAttribute("searchJob", jobPosts);
            
            // Add job fields for the search form dropdown
            List<JobField> jobFields = iJobfieldService.findAll();
            model.addAttribute("jobField", jobFields);
            
            // Add search parameters back to model for form persistence  
            model.addAttribute("searchKeyword", keyword);
            model.addAttribute("searchLocation", location);
            model.addAttribute("searchJobType", jobType);
            model.addAttribute("searchFieldId", fieldId);
            model.addAttribute("searchSalary", salary);
            model.addAttribute("searchCompanyName", companyName);
            
            if (principal != null) {
                model.addAttribute("userEmail", principal.getName());
            }
            
            // Add success/info message
            if (jobPosts != null && !jobPosts.isEmpty()) {
                model.addAttribute("success", "Tìm thấy " + jobPosts.size() + " việc làm phù hợp");
            } else {
                model.addAttribute("info", "Không tìm thấy việc làm phù hợp với tiêu chí tìm kiếm");
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tìm kiếm: " + e.getMessage());
            
            // Still provide job fields for the form
            List<JobField> jobFields = iJobfieldService.findAll();
            model.addAttribute("jobField", jobFields);
            
            if (principal != null) {
                model.addAttribute("userEmail", principal.getName());
            }
        }

        return "homePage/showSearchJob";
    }
}
