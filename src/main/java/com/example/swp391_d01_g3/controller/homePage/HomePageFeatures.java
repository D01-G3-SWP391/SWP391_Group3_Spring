package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.security.IAccountService;
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
    private IJobfieldService jobfieldService;
    
    @Autowired
    private IAccountService accountService;

    @PostMapping("/search")
    public String searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(name = "fieldId", required = false) Integer fieldId,
            @RequestParam(required = false) String salaryRange,
            @RequestParam(required = false) String companyName,
            Model model,
            Principal principal) {

        Double minSalary = null;
        Double maxSalary = null;

        if (salaryRange != null && !salaryRange.isEmpty()) {
            try {
                if (salaryRange.startsWith(">")) {
                    minSalary = Double.parseDouble(salaryRange.substring(1).trim());
                } else if (salaryRange.contains("-")) {
                    String[] parts = salaryRange.split("-");
                    minSalary = Double.parseDouble(parts[0].trim());
                    maxSalary = Double.parseDouble(parts[1].trim());
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Định dạng khoảng lương không hợp lệ: " + e.getMessage());
                model.addAttribute("jobField", jobfieldService.findAll());
                if (principal != null) {
                    model.addAttribute("userEmail", principal.getName());
                    Account account = accountService.findByEmail(principal.getName());
                    model.addAttribute("account", account);
                }
                return "homePage/showSearchJob";
            }
        }

        try {
            List<JobPost> jobPosts = jobpostService.searchJobs(
                    keyword,
                    location,
                    jobType,
                    fieldId,
                    minSalary,
                    maxSalary,
                    companyName
            );

            model.addAttribute("searchJob", jobPosts);
            model.addAttribute("jobField", jobfieldService.findAll());
            model.addAttribute("searchKeyword", keyword);
            model.addAttribute("searchLocation", location);
            model.addAttribute("searchJobType", jobType);
            model.addAttribute("searchFieldId", fieldId);
            model.addAttribute("searchSalary", salaryRange);
            model.addAttribute("searchCompanyName", companyName);

            if (principal != null) {
                model.addAttribute("userEmail", principal.getName());
                Account account = accountService.findByEmail(principal.getName());
                model.addAttribute("account", account);
            }

            if (jobPosts != null && !jobPosts.isEmpty()) {
                model.addAttribute("success", "Tìm thấy " + jobPosts.size() + " việc làm phù hợp");
            } else {
                model.addAttribute("info", "Không tìm thấy việc làm phù hợp với tiêu chí tìm kiếm");
            }

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tìm kiếm: " + e.getMessage());
            model.addAttribute("jobField", jobfieldService.findAll());
            if (principal != null) {
                model.addAttribute("userEmail", principal.getName());
                Account account = accountService.findByEmail(principal.getName());
                model.addAttribute("account", account);
            }
        }

        return "homePage/showSearchJob";
    }
}

