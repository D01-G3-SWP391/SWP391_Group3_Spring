package com.example.swp391_d01_g3.controller.blog;

import com.example.swp391_d01_g3.service.blog.IBlogService;
import com.example.swp391_d01_g3.model.BlogPost;
import com.example.swp391_d01_g3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/Blog")
public class BlogController {

    @Autowired
    private IBlogService blogService;

    /**
     * Hiển thị danh sách blog posts với phân trang và lọc
     */
    @GetMapping
    public String blogList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
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
        
        // Lấy các category để hiển thị filter
        var categories = blogService.getAllCategories();
        
        // Lấy blog posts nổi bật (featured)
        var featuredPosts = blogService.getFeaturedPosts(3);
        
        model.addAttribute("blogPosts", blogPosts);
        model.addAttribute("categories", categories);
        model.addAttribute("featuredPosts", featuredPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPosts.getTotalPages());
        model.addAttribute("totalElements", blogPosts.getTotalElements());
        
        return "blog/blog-list";
    }

    /**
     * Hiển thị chi tiết blog post
     */
    @GetMapping("/{id}")
    public String blogDetail(@PathVariable Long id, Model model) {
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
        
        return "blog/blog-detail";
    }

    /**
     * API endpoint để load more posts (AJAX)
     */
    @GetMapping("/api/load-more")
    @ResponseBody
    public Page<BlogPost> loadMorePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String category) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if (category != null && !category.isEmpty()) {
            return blogService.findByCategory(category, pageable);
        }
        return blogService.findAllPublished(pageable);
    }
} 