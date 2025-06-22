package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.service.admin.IAdminEmployerService;
import com.example.swp391_d01_g3.service.admin.IAdminStudentService;
import com.example.swp391_d01_g3.service.blog.IBlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/Admin")
public class Dashboard {

    @Autowired
    private IAdminStudentService adminStudentService;
    @Autowired
    private IAdminEmployerService adminEmployerService;
    private static final Logger logger = LoggerFactory.getLogger(Dashboard.class);

    @Autowired
    private IBlogService blogService;

    @GetMapping("")
    public String showDashboard() {
        return "admin/dashboardPage";
    }

    @GetMapping("/blogs/{id}/edit")
    public String editBlogForm(@PathVariable Long id, Model model) {
        BlogPost blog = blogService.getBlogPostById(id).orElseThrow();
        model.addAttribute("blog", blog);
        model.addAttribute("resourceTypes", Resource.ResourceType.values());
        model.addAttribute("statuses", BlogPost.BlogStatus.values());
        model.addAttribute("allResources", blogService.getAllResources());
        return "blog/editBlog";
    }

    @PostMapping("/blogs/{id}/edit")
    public String updateBlog(@PathVariable Long id,
                             @ModelAttribute BlogPost updatedBlog,
                             RedirectAttributes redirectAttributes) {
        blogService.updateBlog(id, updatedBlog);
        redirectAttributes.addFlashAttribute("success", "Cập nhật blog thành công!");
        return "redirect:/Admin/blogs";
    }


    // Trang list blog với pagination
    @GetMapping("/blogs")
    public String listBlogs(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status,
                            Model model) {
        try {
            Page<BlogPost> blogPage;
            BlogPost.BlogStatus blogStatus = null;

            // Validate parameters
            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 10;

            Pageable pageable = PageRequest.of(page, size);

            // Parse status parameter
            if (status != null && !status.isEmpty()) {
                try {
                    blogStatus = BlogPost.BlogStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status parameter: {}", status);
                }
            }

            // Search logic với pagination
            if (keyword != null && !keyword.trim().isEmpty() && blogStatus != null) {
                // Search by keyword AND status
                blogPage = (Page<BlogPost>) blogService.searchBlogPosts(keyword);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                // Search by keyword only
                blogPage = blogService.searchBlogs(keyword, pageable);
            } else if (blogStatus != null) {
                // Filter by status only
                blogPage = blogService.findByStatus(blogStatus, pageable);
            } else {
                // Get all blogs with pagination
                blogPage = blogService.getAllBlogPostsWithPagination(pageable);
            }

            // Add pagination attributes to model
            model.addAttribute("blogs", blogPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", blogPage.getTotalPages());
            model.addAttribute("totalItems", blogPage.getTotalElements());
            model.addAttribute("hasNext", blogPage.hasNext());
            model.addAttribute("hasPrevious", blogPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);

            return "blog/managementBlog";

        } catch (Exception e) {
            logger.error("Error loading blogs: ", e);
            model.addAttribute("error", "Error loading blogs: " + e.getMessage());

            // Safe defaults for error case
            model.addAttribute("blogs", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("hasNext", false);
            model.addAttribute("hasPrevious", false);
            model.addAttribute("selectedStatus", "");
            model.addAttribute("keyword", "");

            return "blog/managementBlog";
        }
    }

    // Trang chi tiết blog
    @GetMapping("/blogs/{id}")
    public String blogDetail(@PathVariable Long id, Model model) {
        BlogPost blog = blogService.getBlogPostById(id).orElse(null);
        if (blog == null) return "redirect:/admin/blogs";
        List<BlogImage> images = blogService.getImagesForBlog(id);
        model.addAttribute("blog", blog);
        model.addAttribute("images", images);
        return "blog/detailBlog";
    }

    // Đổi status (POST)
    @PostMapping("/blogs/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        BlogPost.BlogStatus newStatus = BlogPost.BlogStatus.valueOf(status);
        blogService.updateBlogStatus(id, newStatus);
        return "redirect:/Admin/blogs";
    }

    @GetMapping("/ListStudent")
    public String showListStudent(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size,
                                  @RequestParam(required = false) String keyword,
                                  Model model) {
        try {
            Page<Account> studentPage;

            if (keyword != null && !keyword.trim().isEmpty()) {
                studentPage = adminStudentService.searchStudents(keyword.trim(), page, size);
                model.addAttribute("keyword", keyword);
            } else {
                studentPage = adminStudentService.getStudentsWithPagination(page, size);
            }

            model.addAttribute("studentList", studentPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", studentPage.getTotalPages());
            model.addAttribute("totalItems", studentPage.getTotalElements());
            model.addAttribute("hasNext", studentPage.hasNext());
            model.addAttribute("hasPrevious", studentPage.hasPrevious());

            return "admin/viewListStudent";
        } catch (Exception e) {
            logger.error("Error loading student list: ", e);
            model.addAttribute("error", "Error loading student list: " + e.getMessage());
            return "admin/error";
        }
    }

    @GetMapping("/ListEmployer")
    public String showListEmployer(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "6") int size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String status,
                                   Model model) {
        try {
            Page<Account> employerPage;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 6;

            // Filter logic
            if (keyword != null && !keyword.trim().isEmpty() && status != null && !status.isEmpty()) {
                employerPage = adminEmployerService.searchByKeywordAndStatus(keyword.trim(), status, page, size);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                employerPage = adminEmployerService.searchEmployers(keyword.trim(), page, size);
            } else if (status != null && !status.isEmpty()) {
                employerPage = adminEmployerService.findByStatus(status, page, size);
            } else {
                employerPage = adminEmployerService.getEmployersWithPagination(page, size);
            }

            // THÊM: Lấy số lượng cho badges
            long totalEmployers = adminEmployerService.countAllEmployers();
            long activeEmployers = adminEmployerService.countEmployersByStatus("active");
            long bannedEmployers = adminEmployerService.countEmployersByStatus("inactive");

            model.addAttribute("employerList", employerPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", employerPage.getTotalPages());
            model.addAttribute("totalItems", employerPage.getTotalElements());
            model.addAttribute("hasNext", employerPage.hasNext());
            model.addAttribute("hasPrevious", employerPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);

            // THÊM: Số lượng cho badges
            model.addAttribute("totalEmployers", totalEmployers);
            model.addAttribute("activeEmployers", activeEmployers);
            model.addAttribute("bannedEmployers", bannedEmployers);

            return "admin/viewListEmployer";
        } catch (Exception e) {
            logger.error("Error loading employer list: ", e);
            model.addAttribute("error", "Error loading employer list: " + e.getMessage());
            return "admin/error";
        }
    }


    // Student Ban/Unban Methods
    @PostMapping("/banStudent/{id}")
    public String banStudent(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to ban student with ID: {}", userId);
            adminStudentService.banStudent(userId);
            redirectAttributes.addFlashAttribute("success", "Student banned successfully");
            return "redirect:/Admin/ListStudent";
        } catch (Exception e) {
            logger.error("Error banning student: ", e);
            redirectAttributes.addFlashAttribute("error", "Error banning student: " + e.getMessage());
            return "redirect:/Admin/ListStudent";
        }
    }

    @PostMapping("/unbanStudent/{id}")
    public String unbanStudent(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to unban student with ID: {}", userId);
            adminStudentService.unbanStudent(userId);
            redirectAttributes.addFlashAttribute("success", "Student unbanned successfully");
            return "redirect:/Admin/ListStudent";
        } catch (Exception e) {
            logger.error("Error unbanning student: ", e);
            redirectAttributes.addFlashAttribute("error", "Error unbanning student: " + e.getMessage());
            return "redirect:/Admin/ListStudent";
        }
    }

    // Employer Ban/Unban Methods
    @PostMapping("/banEmployer/{id}")
    public String banEmployer(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to ban employer with ID: {}", userId);
            adminEmployerService.banEmployer(userId);
            redirectAttributes.addFlashAttribute("success", "Employer banned successfully");
            return "redirect:/Admin/ListEmployer";
        } catch (Exception e) {
            logger.error("Error banning employer: ", e);
            redirectAttributes.addFlashAttribute("error", "Error banning employer: " + e.getMessage());
            return "redirect:/Admin/ListEmployer";
        }
    }

    @PostMapping("/unbanEmployer/{id}")
    public String unbanEmployer(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to unban employer with ID: {}", userId);
            adminEmployerService.unbanEmployer(userId);
            redirectAttributes.addFlashAttribute("success", "Employer unbanned successfully");
            return "redirect:/Admin/ListEmployer";
        } catch (Exception e) {
            logger.error("Error unbanning employer: ", e);
            redirectAttributes.addFlashAttribute("error", "Error unbanning employer: " + e.getMessage());
            return "redirect:/Admin/ListEmployer";
        }
    }
    @GetMapping("/employer/{id}")
    public String viewEmployerDetails(@PathVariable("id") Integer userId, Model model) {
        try {
            logger.info("Viewing employer details for ID: {}", userId);

            // SỬA: Sử dụng getEmployerDetailsById() để lấy object Employer
            Employer employer = adminEmployerService.getEmployerDetailsById(userId);
            if (employer != null) {
                model.addAttribute("employer", employer);
                return "admin/viewEmployerDetails";
            } else {
                logger.error("Employer details not found for user ID: {}", userId);
                model.addAttribute("error", "Employer details not found. This user may not have completed employer profile.");
                return "redirect:/Admin/ListEmployer";
            }
        } catch (Exception e) {
            logger.error("Error loading employer details for ID: {}", userId, e);
            model.addAttribute("error", "Error loading employer details: " + e.getMessage());
            return "redirect:/Admin/ListEmployer";
        }
    }


    // View Student Details
    @GetMapping("/student/{id}")
    public String viewStudentDetails(@PathVariable("id") Integer userId, Model model) {
        try {
            logger.info("Viewing student details for ID: {}", userId);

            // Lấy thông tin Student với relationship Account
            Student student = adminStudentService.getStudentDetailsById(userId);
            if (student != null) {
                model.addAttribute("student", student);
                return "admin/viewStudentDetails";
            } else {
                logger.error("Student details not found for user ID: {}", userId);
                model.addAttribute("error", "Student details not found. This user may not have completed student profile.");
                return "redirect:/Admin/ListStudent";
            }
        } catch (Exception e) {
            logger.error("Error loading student details: ", e);
            model.addAttribute("error", "Error loading student details: " + e.getMessage());
            return "redirect:/Admin/ListStudent";
        }
    }

}
