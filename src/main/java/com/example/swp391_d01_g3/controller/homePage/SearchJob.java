package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/searchJob")
public class SearchJob {

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
            @RequestParam(required = false) Integer salary,
            @RequestParam(required = false) String companyName,
            Model model) {

        List<JobPost> jobPosts = jobpostService.searchJobs(
                keyword,
                location,
                jobType,
                fieldId,
                salary,
                companyName
        );

        model.addAttribute("searchJob", jobPosts);
        return "homePage/homePage";
    }
}
