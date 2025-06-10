//package com.example.swp391_d01_g3.controller;
//
//import com.example.swp391_d01_g3.dto.BlogPostCreateDTO;
//import com.example.swp391_d01_g3.model.Account;
//import com.example.swp391_d01_g3.model.BlogPost;
//import com.example.swp391_d01_g3.model.Resource;
//import com.example.swp391_d01_g3.service.blog.BlogServiceImpl;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/blog")
//public class BlogController {
//
//    @Autowired
//    private BlogServiceImpl blogServiceImpl;
//
//    // ===========================================
//    // PUBLIC BLOG VIEWS (Dành cho người dùng)
//    // ===========================================
//
//    /**
//     * Hiển thị danh sách blog posts cho public
//     */
//    @GetMapping("")
//    public String viewPublicBlogList(Model model, @RequestParam(defaultValue = "") String search) {
//        List<BlogPost> blogPosts;
//
//        if (search.trim().isEmpty()) {
//            blogPosts = blogServiceImpl.getAllPublishedBlogPosts();
//        } else {
//            blogPosts = blogServiceImpl.searchBlogPosts(search);
//        }
//
//        model.addAttribute("blogPosts", blogPosts);
//        model.addAttribute("searchKeyword", search);
//        model.addAttribute("pageTitle", "Cẩm nang nghề nghiệp");
//
//        return "blog/public-blog-list";
//    }
//
//    /**
//     * Hiển thị chi tiết blog post
//     */
//    @GetMapping("/{id}")
//    public String viewBlogDetail(@PathVariable Long id, Model model) {
//        Optional<BlogPost> blogPost = blogServiceImpl.getBlogPostById(id);
//
//        if (blogPost.isEmpty() || blogPost.get().getStatus() != BlogPost.BlogStatus.PUBLISHED) {
//            return "error/404";
//        }
//
//        model.addAttribute("blogPost", blogPost.get());
//        model.addAttribute("pageTitle", blogPost.get().getTitle());
//
//        // Lấy các bài viết liên quan (cùng resource type)
//        List<BlogPost> relatedPosts = blogServiceImpl.getAllPublishedBlogPosts()
//                .stream()
//                .filter(post -> !post.getBlogPostId().equals(id))
//                .limit(5)
//                .toList();
//        model.addAttribute("relatedPosts", relatedPosts);
//
//        return "blog/blog-detail";
//    }
//
//    // ===========================================
//    // ADMIN BLOG MANAGEMENT
//    // ===========================================
//
//    /**
//     * Admin - Hiển thị danh sách tất cả blog posts
//     */
//    @GetMapping("/admin")
//    public String adminBlogList(Model model, HttpSession session) {
//        // Kiểm tra quyền admin
//        Account currentUser = (Account) session.getAttribute("account");
//        if (currentUser == null || !isAdmin(currentUser)) {
//            return "redirect:/login";
//        }
//
//        List<BlogPost> allBlogPosts = blogServiceImpl.getAllBlogPosts();
//        model.addAttribute("blogPosts", allBlogPosts);
//        model.addAttribute("pageTitle", "Quản lý Blog Posts");
//
//        return "admin/blog/blog-list";
//    }
//
//    /**
//     * Admin - Hiển thị form tạo blog post mới
//     */
//    @GetMapping("/admin/create")
//    public String showCreateForm(Model model, HttpSession session) {
//        Account currentUser = (Account) session.getAttribute("account");
//        if (currentUser == null || !isAdmin(currentUser)) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("blogPostDTO", new BlogPostCreateDTO());
//        model.addAttribute("resourceTypes", Resource.ResourceType.values());
//        model.addAttribute("pageTitle", "Tạo Blog Post Mới");
//
//        return "admin/blog/create-blog";
//    }
//
//    /**
//     * Admin - Xử lý tạo blog post mới
//     */
//    @PostMapping("/admin/create")
//    public String createBlogPost(@Valid @ModelAttribute("blogPostDTO") BlogPostCreateDTO dto,
//                                BindingResult result,
//                                HttpSession session,
//                                RedirectAttributes redirectAttributes,
//                                Model model) {
//
//        Account currentUser = (Account) session.getAttribute("account");
//        if (currentUser == null || !isAdmin(currentUser)) {
//            return "redirect:/login";
//        }
//
//        if (result.hasErrors()) {
//            model.addAttribute("resourceTypes", Resource.ResourceType.values());
//            model.addAttribute("pageTitle", "Tạo Blog Post Mới");
//            return "admin/blog/create-blog";
//        }
//
//        try {
//            BlogPost createdPost = blogServiceImpl.createBlogPost(dto, currentUser);
//            redirectAttributes.addFlashAttribute("successMessage",
//                "Tạo blog post thành công! ID: " + createdPost.getBlogPostId());
//            return "redirect:/blog/admin";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage",
//                "Có lỗi xảy ra: " + e.getMessage());
//            return "redirect:/blog/admin/create";
//        }
//    }
//
//    /**
//     * Admin - Hiển thị form chỉnh sửa blog post
//     */
//    @GetMapping("/admin/edit/{id}")
//    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
//        Account currentUser = (Account) session.getAttribute("account");
//        if (currentUser == null || !isAdmin(currentUser)) {
//            return "redirect:/login";
//        }
//
//        Optional<BlogPost> blogPost = blogServiceImpl.getBlogPostById(id);
//        if (blogPost.isEmpty()) {
//            return "error/404";
//        }
//
//        // Chuyển đổi BlogPost thành DTO để hiển thị form
//        BlogPostCreateDTO dto = convertToDTO(blogPost.get());
//
//        model.addAttribute("blogPostDTO", dto);
//        model.addAttribute("blogPostId", id);
//        model.addAttribute("resourceTypes", Resource.ResourceType.values());
//        model.addAttribute("pageTitle", "Chỉnh sửa Blog Post");
//
//        return "admin/blog/edit-blog";
//    }
//
//    /**
//     * Admin - Xử lý cập nhật blog post
//     */
//    @PostMapping("/admin/edit/{id}")
//    public String updateBlogPost(@PathVariable Long id,
//                                @Valid @ModelAttribute("blogPostDTO") BlogPostCreateDTO dto,
//                                BindingResult result,
//                                HttpSession session,
//                                RedirectAttributes redirectAttributes,
//                                Model model) {
//
//        Account currentUser = (Account) session.getAttribute("account");
//        if (currentUser == null || !isAdmin(currentUser)) {
//            return "redirect:/login";
//        }
//
//        if (result.hasErrors()) {
//            model.addAttribute("blogPostId", id);
//            model.addAttribute("resourceTypes", Resource.ResourceType.values());
//            model.addAttribute("pageTitle", "Chỉnh sửa Blog Post");
//            return "admin/blog/edit-blog";
//        }
//
//        try {
//            blogServiceImpl.updateBlogPost(id, dto);
//            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật blog post thành công!");
//            return "redirect:/blog/admin";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
//            return "redirect:/blog/admin/edit/" + id;
//        }
//    }
//
//    /**
//     * Admin - Publish blog post
//     */
//    @PostMapping("/admin/publish/{id}")
//    public String publishBlogPost(@PathVariable Long id,
//                                 HttpSession session,
//                                 RedirectAttributes redirectAttributes) {
//
//        Account currentUser = (Account) session.getAttribute("account");
//        if (currentUser == null || !isAdmin(currentUser)) {
//            return "redirect:/login";
//        }
//
//        try {
//            blogServiceImpl.publishBlogPost(id);
//            redirectAttributes.addFlashAttribute("successMessage", "Đã publish blog post thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
//        }
//
//        return "redirect:/blog/admin";
//    }
//
//    /**
//     * Admin - Xóa blog post
//     */
//    @PostMapping("/admin/delete/{id}")
//    public String deleteBlogPost(@PathVariable Long id,
//                                HttpSession session,
//                                RedirectAttributes redirectAttributes) {
//
//        Account currentUser = (Account) session.getAttribute("account");
//        if (currentUser == null || !isAdmin(currentUser)) {
//            return "redirect:/login";
//        }
//
//        try {
//            blogServiceImpl.deleteBlogPost(id);
//            redirectAttributes.addFlashAttribute("successMessage", "Xóa blog post thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
//        }
//
//        return "redirect:/blog/admin";
//    }
//
//    // ===========================================
//    // HELPER METHODS
//    // ===========================================
//
//    /**
//     * Kiểm tra user có phải admin không
//     */
//    private boolean isAdmin(Account account) {
//        return account != null && "admin".equals(account.getRole());
//    }
//
//    /**
//     * Chuyển đổi BlogPost entity thành DTO
//     */
//    private BlogPostCreateDTO convertToDTO(BlogPost blogPost) {
//        BlogPostCreateDTO dto = new BlogPostCreateDTO();
//        dto.setResourceId(blogPost.getResource().getResourceId());
//        dto.setTitle(blogPost.getTitle());
//        dto.setContent(blogPost.getContent());
//        dto.setSummary(blogPost.getSummary());
//        dto.setFeaturedImageUrl(blogPost.getFeaturedImageUrl());
//        dto.setMetaDescription(blogPost.getMetaDescription());
//
//        // Resource info
//        Resource resource = blogPost.getResource();
//        dto.setResourceType(resource.getResourceType());
//        dto.setResourceTitle(resource.getResourceTitle());
//        dto.setResourceContent(resource.getResourceContent());
//        dto.setResourceImageUrl(resource.getImageUrl());
//
//        return dto;
//    }
//}