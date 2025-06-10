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
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping("/search")
    public String searchJobs(Model model, Principal principal,
                           @RequestParam(value = "keyword", required = false) String keyword,
                           @RequestParam(value = "location", required = false) String location,
                           @RequestParam(value = "jobType", required = false) String jobType,
                           @RequestParam(value = "fieldId", required = false) Integer fieldId,
                           @RequestParam(value = "salary", required = false) Integer salary,
                           @RequestParam(value = "companyName", required = false) String companyName,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "8") int size) {
        
        try {
            // Search for jobs based on criteria
            List<JobPost> searchResults = iJobpostService.searchJobs(keyword, location, jobType, fieldId, salary, companyName);
            
            // For pagination of search results, we'll create a simple pagination logic
            int start = page * size;
            int end = Math.min(start + size, searchResults.size());
            List<JobPost> pagedResults = searchResults.subList(start, end);
            
            int totalPages = (int) Math.ceil((double) searchResults.size() / size);
            
            model.addAttribute("jobPosts", pagedResults);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", searchResults.size());
            model.addAttribute("size", size);
            
            // Add search parameters back to model for form persistence
            model.addAttribute("searchKeyword", keyword);
            model.addAttribute("searchLocation", location);
            model.addAttribute("searchJobType", jobType);
            model.addAttribute("searchFieldId", fieldId);
            model.addAttribute("searchSalary", salary);
            model.addAttribute("searchCompanyName", companyName);
            
            List<JobField> jobField = iJobfieldService.findAll();
            model.addAttribute("jobField", jobField);
            
            if (principal != null) {
                model.addAttribute("userEmail", principal.getName());
            }
            
            model.addAttribute("success", "Tìm thấy " + searchResults.size() + " việc làm phù hợp");
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tìm kiếm: " + e.getMessage());
            // Fallback to show all jobs
            return showHomePage(model, principal, page, size);
        }
        
        return "homePage/homePage";
    }
}
