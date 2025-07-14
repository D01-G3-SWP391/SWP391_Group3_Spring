package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.dto.BlogCreateDTO;
import com.example.swp391_d01_g3.dto.BanRequestDTO;
import com.example.swp391_d01_g3.model.*;
import com.example.swp391_d01_g3.service.admin.IAdminEmployerService;
import com.example.swp391_d01_g3.service.admin.IAdminStudentService;
import com.example.swp391_d01_g3.service.blog.IBlogService;
import com.example.swp391_d01_g3.service.cloudinary.CloudinaryService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class Dashboard {

    @Autowired
    private IAdminStudentService adminStudentService;
    @Autowired
    private IAdminEmployerService adminEmployerService;
    @Autowired
    private IAccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(Dashboard.class);

    @Autowired
    private IBlogService blogService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("")
    public String showDashboard(Model model) {
        // Dashboard statistics
        try {
            // Get user counts
            long totalStudents = adminStudentService.countAllStudents();
            long activeStudents = adminStudentService.countStudentsByStatus("active");
            long bannedStudents = adminStudentService.countStudentsByStatus("inactive");
            
            long totalEmployers = adminEmployerService.countAllEmployers();
            long activeEmployers = adminEmployerService.countEmployersByStatus("active");
            long bannedEmployers = adminEmployerService.countEmployersByStatus("inactive");
            
            // Add to model
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("activeStudents", activeStudents);
            model.addAttribute("bannedStudents", bannedStudents);
            
            model.addAttribute("totalEmployers", totalEmployers);
            model.addAttribute("activeEmployers", activeEmployers);
            model.addAttribute("bannedEmployers", bannedEmployers);
            
            // Calculate percentages
            model.addAttribute("studentActivePercentage", totalStudents > 0 ? (activeStudents * 100 / totalStudents) : 0);
            model.addAttribute("employerActivePercentage", totalEmployers > 0 ? (activeEmployers * 100 / totalEmployers) : 0);
            
        } catch (Exception e) {
            logger.error("Error loading dashboard statistics: ", e);
        }
        
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

    // CREATE NEW BLOG - GET Form
    @GetMapping("/blogs/create")
    public String createBlogForm(Model model) {
        model.addAttribute("blogCreateDTO", new BlogCreateDTO());
        model.addAttribute("resourceTypes", Resource.ResourceType.values());
        model.addAttribute("statuses", BlogPost.BlogStatus.values());
        model.addAttribute("allResources", blogService.getAllResources());
        return "blog/createBlog";
    }

    // CREATE NEW BLOG - POST Submit with Server-side Validation
    @PostMapping("/blogs/create")
    public String createBlog(@Valid @ModelAttribute("blogCreateDTO") BlogCreateDTO blogCreateDTO,
                           BindingResult bindingResult,
                           @RequestParam(value = "featureImage", required = false) MultipartFile featureImage,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        // Server-side validation
        if (bindingResult.hasErrors()) {
            // Return form with validation errors
            model.addAttribute("resourceTypes", Resource.ResourceType.values());
            model.addAttribute("statuses", BlogPost.BlogStatus.values());
            model.addAttribute("allResources", blogService.getAllResources());
            return "blog/createBlog";
        }
        
        try {
            // Handle feature image upload if provided
            if (featureImage != null && !featureImage.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(featureImage, "blog-images");
                blogCreateDTO.setFeaturedImageUrl(imageUrl);
            }
            
            // Convert DTO to entity
            BlogPost newBlog = blogCreateDTO.toBlogPost();
            
            // Set resource if provided
            if (blogCreateDTO.getResourceId() != null && blogCreateDTO.getResourceId() > 0) {
                Resource resource = blogService.getAllResources().stream()
                    .filter(r -> r.getResourceId().equals(blogCreateDTO.getResourceId()))
                    .findFirst()
                    .orElse(null);
                newBlog.setResource(resource);
            }
            
            // Create blog
            blogService.createBlog(newBlog);
            redirectAttributes.addFlashAttribute("success", "Tạo blog mới thành công!");
            return "redirect:/Admin/blogs";
            
        } catch (Exception e) {
            logger.error("Error creating blog: ", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tạo blog: " + e.getMessage());
            model.addAttribute("resourceTypes", Resource.ResourceType.values());
            model.addAttribute("statuses", BlogPost.BlogStatus.values());
            model.addAttribute("allResources", blogService.getAllResources());
            return "blog/createBlog";
        }
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

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 10;

            Pageable pageable = PageRequest.of(page, size);

            if (status != null && !status.isEmpty()) {
                try {
                    blogStatus = BlogPost.BlogStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status parameter: {}", status);
                }
            }

            // Search logic với pagination
            if (keyword != null && !keyword.trim().isEmpty() && blogStatus != null) {
                blogPage = blogService.searchBlogsByTitleAndStatus(keyword.trim(), blogStatus, pageable);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                blogPage = blogService.searchBlogsByTitle(keyword.trim(), pageable);
            } else if (blogStatus != null) {
                blogPage = blogService.findByStatus(blogStatus, pageable);
            } else {
                blogPage = blogService.getAllBlogPostsWithPagination(pageable);
            }

            // THÊM: Lấy số lượng cho badges
            long totalBlogs = blogService.getTotalBlogsCount();
            long draftBlogs = blogService.getDraftBlogsCount();
            long publishedBlogs = blogService.getPublishedBlogsCount();
            long archivedBlogs = blogService.getArchivedBlogsCount();

            model.addAttribute("blogs", blogPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", blogPage.getTotalPages());
            model.addAttribute("totalItems", blogPage.getTotalElements());
            model.addAttribute("hasNext", blogPage.hasNext());
            model.addAttribute("hasPrevious", blogPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);

            // THÊM: Số lượng cho badges
            model.addAttribute("totalBlogs", totalBlogs);
            model.addAttribute("draftBlogs", draftBlogs);
            model.addAttribute("publishedBlogs", publishedBlogs);
            model.addAttribute("archivedBlogs", archivedBlogs);

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
        if (blog == null) return "redirect:/Admin/blogs";
//        List<BlogImage> images = blogService.getImagesForBlog(id);
        model.addAttribute("blog", blog);
//        model.addAttribute("images", images);
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
                                  @RequestParam(required = false) String status,
                                  Model model) {
        try {
            Page<Account> studentPage;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 6;

            // Filter logic
            if (keyword != null && !keyword.trim().isEmpty() && status != null && !status.isEmpty()) {
                studentPage = adminStudentService.searchByKeywordAndStatus(keyword.trim(), status, page, size);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                studentPage = adminStudentService.searchStudents(keyword.trim(), page, size);
            } else if (status != null && !status.isEmpty()) {
                studentPage = adminStudentService.findByStatus(status, page, size);
            } else {
                studentPage = adminStudentService.getStudentsWithPagination(page, size);
            }

            // THÊM: Lấy số lượng cho badges
            long totalStudents = adminStudentService.countAllStudents();
            long activeStudents = adminStudentService.countStudentsByStatus("active");
            long bannedStudents = adminStudentService.countStudentsByStatus("inactive");

            model.addAttribute("studentList", studentPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", studentPage.getTotalPages());
            model.addAttribute("totalItems", studentPage.getTotalElements());
            model.addAttribute("hasNext", studentPage.hasNext());
            model.addAttribute("hasPrevious", studentPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);

            // THÊM: Số lượng cho badges
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("activeStudents", activeStudents);
            model.addAttribute("bannedStudents", bannedStudents);

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
    
    /**
     * Enhanced ban student with reason and email notification (Form-based)
     */
    @PostMapping("/banStudentWithReason")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> banStudentWithReason(@RequestParam Integer userId,
                                                                   @RequestParam String banReason,
                                                                   @RequestParam(required = false) String banDescription,
                                                                   @RequestParam String banDurationType,
                                                                   @RequestParam(required = false) Integer banDurationDays,
                                                                   Principal principal) {
        try {
            // Create BanRequestDTO from form data
            BanRequestDTO banRequest = new BanRequestDTO();
            banRequest.setUserId(userId);
            banRequest.setBanReason(BanRecord.BanReason.valueOf(banReason));
            banRequest.setBanDescription(banDescription);
            banRequest.setBanDurationType(BanRecord.BanDurationType.valueOf(banDurationType));
            banRequest.setBanDurationDays(banDurationDays);
            
            logger.info("🔍 DEBUG: Received ban request - userId: {}, reason: {}, durationType: {}, days: {}", 
                       userId, banReason, banDurationType, banDurationDays);
            // Get admin user ID
            Account adminAccount = accountService.findByEmail(principal.getName());
            if (adminAccount == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Admin account not found"));
            }
            
            // Validate ban request
            if (!banRequest.isValid()) {
                logger.error("❌ Ban request validation failed: {}", banRequest);
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Dữ liệu ban request không hợp lệ. Vui lòng kiểm tra lại."));
            }
            
            logger.info("✅ Admin {} banning student {} with reason: {}, duration: {} {}", 
                       adminAccount.getEmail(), banRequest.getUserId(), banRequest.getBanReason(),
                       banRequest.getBanDurationType(), 
                       banRequest.getBanDurationDays() != null ? banRequest.getBanDurationDays() + " days" : "");
            
            // Ban student with reason and send email
            adminStudentService.banStudentWithReason(banRequest, adminAccount.getUserId());
            
            logger.info("✅ Student {} banned successfully by admin {}", banRequest.getUserId(), adminAccount.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Sinh viên đã được ban thành công và email thông báo đã được gửi!"
            ));
            
        } catch (Exception e) {
            logger.error("Error banning student with reason: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Lỗi khi ban sinh viên: " + e.getMessage()));
        }
    }
    
    /**
     * Enhanced unban student with notification
     */
    @PostMapping("/unbanStudentWithNotification/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unbanStudentWithNotification(@PathVariable("id") Integer userId, 
                                                                           Principal principal) {
        try {
            // Get admin user ID
            Account adminAccount = accountService.findByEmail(principal.getName());
            if (adminAccount == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Admin account not found"));
            }
            
            logger.info("Admin {} unbanning student {}", adminAccount.getEmail(), userId);
            
            // Unban student and send email notification
            adminStudentService.unbanStudentWithNotification(userId, adminAccount.getUserId());
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Sinh viên đã được unban thành công và email thông báo đã được gửi!"
            ));
            
        } catch (Exception e) {
            logger.error("Error unbanning student with notification: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Lỗi khi unban sinh viên: " + e.getMessage()));
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
    
    /**
     * Enhanced ban employer with reason and email notification (Form-based)
     */
    @PostMapping("/banEmployerWithReason")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> banEmployerWithReason(@RequestParam Integer userId,
                                                                    @RequestParam String banReason,
                                                                    @RequestParam(required = false) String banDescription,
                                                                    @RequestParam String banDurationType,
                                                                    @RequestParam(required = false) Integer banDurationDays,
                                                                    Principal principal) {
        try {
            // Create BanRequestDTO from form data
            BanRequestDTO banRequest = new BanRequestDTO();
            banRequest.setUserId(userId);
            banRequest.setBanReason(BanRecord.BanReason.valueOf(banReason));
            banRequest.setBanDescription(banDescription);
            banRequest.setBanDurationType(BanRecord.BanDurationType.valueOf(banDurationType));
            banRequest.setBanDurationDays(banDurationDays);
            
            logger.info("🔍 DEBUG: Received employer ban request - userId: {}, reason: {}, durationType: {}, days: {}", 
                       userId, banReason, banDurationType, banDurationDays);
            // Get admin user ID
            Account adminAccount = accountService.findByEmail(principal.getName());
            if (adminAccount == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Admin account not found"));
            }
            
            // Validate ban request
            if (!banRequest.isValid()) {
                logger.error("❌ Employer ban request validation failed: {}", banRequest);
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Dữ liệu ban request không hợp lệ. Vui lòng kiểm tra lại."));
            }
            
            logger.info("✅ Admin {} banning employer {} with reason: {}, duration: {} {}", 
                       adminAccount.getEmail(), banRequest.getUserId(), banRequest.getBanReason(),
                       banRequest.getBanDurationType(), 
                       banRequest.getBanDurationDays() != null ? banRequest.getBanDurationDays() + " days" : "");
            
            // Ban employer with reason and send email
            adminEmployerService.banEmployerWithReason(banRequest, adminAccount.getUserId());
            
            logger.info("✅ Employer {} banned successfully by admin {}", banRequest.getUserId(), adminAccount.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Nhà tuyển dụng đã được ban thành công và email thông báo đã được gửi!"
            ));
            
        } catch (Exception e) {
            logger.error("Error banning employer with reason: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Lỗi khi ban nhà tuyển dụng: " + e.getMessage()));
        }
    }
    
    /**
     * Enhanced unban employer with notification
     */
    @PostMapping("/unbanEmployerWithNotification/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unbanEmployerWithNotification(@PathVariable("id") Integer userId, 
                                                                            Principal principal) {
        try {
            // Get admin user ID
            Account adminAccount = accountService.findByEmail(principal.getName());
            if (adminAccount == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Admin account not found"));
            }
            
            logger.info("Admin {} unbanning employer {}", adminAccount.getEmail(), userId);
            
            // Unban employer and send email notification
            adminEmployerService.unbanEmployerWithNotification(userId, adminAccount.getUserId());
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Nhà tuyển dụng đã được unban thành công và email thông báo đã được gửi!"
            ));
            
        } catch (Exception e) {
            logger.error("Error unbanning employer with notification: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Lỗi khi unban nhà tuyển dụng: " + e.getMessage()));
        }
    }
    
    // API endpoint đã được loại bỏ - không cần thiết cho server-side rendering
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
