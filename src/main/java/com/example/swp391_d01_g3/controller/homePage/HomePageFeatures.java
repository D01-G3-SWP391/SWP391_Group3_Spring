package com.example.swp391_d01_g3.controller.homePage;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.favorite.IFavoriteJobService;
import com.example.swp391_d01_g3.service.jobfield.IJobfieldService;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
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
    
    @Autowired
    private IFavoriteJobService favoriteJobService;
    
    @Autowired
    private IStudentService studentService;

    @GetMapping("")
    public String redirectToHomePage() {
        return "redirect:/";
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    public String searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(name = "fieldId", required = false) Integer fieldId,
            @RequestParam(required = false) String salaryRange,
            @RequestParam(required = false) String companyName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
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
                    
                    // Add favorite job checking for students
                    if (account != null && account.getRole() == Account.Role.student) {
                        Student student = studentService.findByAccountUserId(account.getUserId());
                        if (student != null) {
                            model.addAttribute("studentId", student.getStudentId());
                            // Add a utility object to check favorites in templates
                            model.addAttribute("favoriteChecker", new FavoriteChecker(favoriteJobService, student.getStudentId()));
                        }
                    }
                }
                return "homePage/showSearchJob";
            }
        }

        try {
            List<JobPost> allJobPosts = jobpostService.searchJobs(
                    keyword,
                    location,
                    jobType,
                    fieldId,
                    minSalary,
                    maxSalary,
                    companyName
            );

            // Implement pagination
            int totalJobs = allJobPosts.size();
            int totalPages = totalJobs > 0 ? (int) Math.ceil((double) totalJobs / size) : 0;
            
            // Validate page number
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;
            
            List<JobPost> jobPosts;
            if (totalJobs == 0) {
                jobPosts = new ArrayList<>();
            } else {
                int startIndex = (page - 1) * size;
                int endIndex = Math.min(startIndex + size, totalJobs);
                jobPosts = allJobPosts.subList(startIndex, endIndex);
            }

            model.addAttribute("searchJob", jobPosts);
            model.addAttribute("jobField", jobfieldService.findAll());
            model.addAttribute("searchKeyword", keyword);
            model.addAttribute("searchLocation", location);
            model.addAttribute("searchJobType", jobType);
            model.addAttribute("searchFieldId", fieldId);
            model.addAttribute("searchSalary", salaryRange);
            model.addAttribute("searchCompanyName", companyName);
            
            // Pagination attributes
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", totalJobs);
            model.addAttribute("size", size);
            model.addAttribute("hasNext", page < totalPages);
            model.addAttribute("hasPrevious", page > 1);

            if (principal != null) {
                model.addAttribute("userEmail", principal.getName());
                Account account = accountService.findByEmail(principal.getName());
                model.addAttribute("account", account);
                
                // Add favorite job checking for students
                if (account != null && account.getRole() == Account.Role.student) {
                    Student student = studentService.findByAccountUserId(account.getUserId());
                    if (student != null) {
                        model.addAttribute("studentId", student.getStudentId());
                        // Add a utility object to check favorites in templates
                        model.addAttribute("favoriteChecker", new FavoriteChecker(favoriteJobService, student.getStudentId()));
                        // Add favorite jobs list for navbar dropdown
                        List<JobPost> favoriteJobs = favoriteJobService.getFavoriteJobPostsByStudent(student.getStudentId());
                        model.addAttribute("favoriteJobs", favoriteJobs);
                        model.addAttribute("favoriteCount", favoriteJobs.size());
                    }
                }
            }

            // Chỉ hiển thị thông báo search khi không có flash message từ favorite action
            if (!model.containsAttribute("success") && !model.containsAttribute("error")) {
                if (totalJobs > 0) {
                    model.addAttribute("success", "Tìm thấy " + totalJobs + " việc làm phù hợp. Hiển thị trang " + page + "/" + totalPages);
                } else {
                    model.addAttribute("info", "Không tìm thấy việc làm phù hợp với tiêu chí tìm kiếm");
                }
            }

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tìm kiếm: " + e.getMessage());
            model.addAttribute("jobField", jobfieldService.findAll());
            if (principal != null) {
                model.addAttribute("userEmail", principal.getName());
                Account account = accountService.findByEmail(principal.getName());
                model.addAttribute("account", account);
                
                // Add favorite job checking for students
                if (account != null && account.getRole() == Account.Role.student) {
                    Student student = studentService.findByAccountUserId(account.getUserId());
                    if (student != null) {
                        model.addAttribute("studentId", student.getStudentId());
                        // Add a utility object to check favorites in templates
                        model.addAttribute("favoriteChecker", new FavoriteChecker(favoriteJobService, student.getStudentId()));
                        // Add favorite jobs list for navbar dropdown
                        List<JobPost> favoriteJobs = favoriteJobService.getFavoriteJobPostsByStudent(student.getStudentId());
                        model.addAttribute("favoriteJobs", favoriteJobs);
                        model.addAttribute("favoriteCount", favoriteJobs.size());
                    }
                }
            }
        }

        return "homePage/showSearchJob";
    }
    
    // Helper class for checking favorites in templates
    public static class FavoriteChecker {
        private final IFavoriteJobService favoriteJobService;
        private final Integer studentId;
        
        public FavoriteChecker(IFavoriteJobService favoriteJobService, Integer studentId) {
            this.favoriteJobService = favoriteJobService;
            this.studentId = studentId;
        }
        
        public boolean isFavorited(Integer jobPostId) {
            return favoriteJobService.isFavorited(studentId, jobPostId);
        }
    }
} 