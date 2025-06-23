package com.example.swp391_d01_g3.controller.blog;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.service.blog.IBlogService;
import com.example.swp391_d01_g3.model.BlogPost;
import com.example.swp391_d01_g3.model.Resource;
import com.example.swp391_d01_g3.service.security.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/Blog")
public class BlogController {

    @Autowired
    private IBlogService blogService;
    
    @Autowired
    private IAccountService accountService;

    /**
     * Hiển thị danh sách blog posts với phân trang và lọc
     */
    @GetMapping
    public String blogList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            Model model, Principal principal) {
        
        // Convert 1-based page to 0-based for Spring Data
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<BlogPost> blogPosts;
        
        // Lọc theo category và search
        if (category != null && !category.isEmpty()) {
            blogPosts = blogService.findByCategory(category, pageable);
            model.addAttribute("selectedCategory", category);
        } else if (search != null && !search.isEmpty()) {
            blogPosts = blogService.searchBlogs(search, pageable);
            model.addAttribute("searchKeyword", search);
        } else {
            blogPosts = blogService.findAllPublished(pageable);
        }

        // Phân loại bài viết theo resource type
        var interviewPosts = blogPosts.getContent().stream()
                .filter(post -> post.getResource().getResourceType() == Resource.ResourceType.interview_guide)
                .toList();
        
        var quotePosts = blogPosts.getContent().stream()
                .filter(post -> post.getResource().getResourceType() == Resource.ResourceType.quotes)
                .toList();
        
        var applicationPosts = blogPosts.getContent().stream()
                .filter(post -> post.getResource().getResourceType() == Resource.ResourceType.application_tips)
                .toList();
        
        // Lấy các category để hiển thị filter
        var categories = blogService.getAllCategories();
        
        // Lấy blog posts nổi bật (featured)
        var featuredPosts = blogService.getFeaturedPosts(4);
        
        model.addAttribute("blogPosts", blogPosts);
        model.addAttribute("interviewPosts", interviewPosts);
        model.addAttribute("quotePosts", quotePosts);
        model.addAttribute("applicationPosts", applicationPosts);
        model.addAttribute("categories", categories);
        model.addAttribute("featuredPosts", featuredPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPosts.getTotalPages());
        model.addAttribute("totalElements", blogPosts.getTotalElements());
        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
            Account account = accountService.findByEmail(principal.getName());
            model.addAttribute("account", account);
        }
        return "blog/blogPost";
    }

    /**
     * Hiển thị chi tiết blog post
     */
    @GetMapping("/{id}")
    public String blogDetail(@PathVariable Long id, Model model, Principal principal) {
        Optional<BlogPost> blogPost = blogService.findById(id);
        
        if (blogPost.isEmpty()) {
            return "error/404";
        }
        
        BlogPost post = blogPost.get();
        
        // Tăng view count
        blogService.incrementViewCount(id);
        
        // Lấy các bài viết liên quan
        var relatedPosts = blogService.getRelatedPosts(post.getResource().getResourceType(), id, 4);
        
        // Lấy bài viết tiếp theo và trước đó
        var nextPost = blogService.getNextPost(id);
        var previousPost = blogService.getPreviousPost(id);
        
        model.addAttribute("blogPost", post);
        model.addAttribute("relatedPosts", relatedPosts);
        model.addAttribute("nextPost", nextPost.orElse(null));
        model.addAttribute("previousPost", previousPost.orElse(null));
        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
            Account account = accountService.findByEmail(principal.getName());
            model.addAttribute("account", account);
        }
        
        return "blog/blog-detail";
    }

    /**
     * API endpoint để load more posts (AJAX)
     */
    @GetMapping("/api/load-more")
    @ResponseBody
    public Page<BlogPost> loadMorePosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String category) {
        
        // Convert 1-based page to 0-based for Spring Data
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        
        if (category != null && !category.isEmpty()) {
            return blogService.findByCategory(category, pageable);
        }
        return blogService.findAllPublished(pageable);
    }
} 