package com.example.swp391_d01_g3.service.blog;

import com.example.swp391_d01_g3.model.BlogPost;
import com.example.swp391_d01_g3.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IBlogService {
    
    // Lấy tất cả blog posts đã published
    List<BlogPost> getAllPublishedBlogPosts();
    
    // Lấy blog post theo ID
    Optional<BlogPost> getBlogPostById(Long id);
    
    // Tìm kiếm blog posts theo title
    List<BlogPost> searchBlogPosts(String keyword);
    
    // Lấy blog posts theo resource type
    List<BlogPost> getBlogPostsByResourceType(String resourceType);
    
    // Lấy các blog posts mới nhất với giới hạn số lượng
    List<BlogPost> getLatestPublishedPosts(int limit);
    
    // Lấy blog posts theo status
    List<BlogPost> getBlogPostsByStatus(BlogPost.BlogStatus status);
    
    // Lấy blog posts theo resource ID
    List<BlogPost> getBlogPostsByResourceId(Long resourceId);
    
    // Đếm số lượng blog posts theo status
    long countBlogPostsByStatus(BlogPost.BlogStatus status);
    
    // Phân trang cho tất cả blog posts đã publish
    Page<BlogPost> findAllPublished(Pageable pageable);
    
    // Tìm kiếm blog với pagination
    Page<BlogPost> searchBlogs(String keyword, Pageable pageable);
    
    // Lọc theo category với pagination
    Page<BlogPost> findByCategory(String category, Pageable pageable);
    
    // Lấy blog post theo ID (alias cho getBlogPostById)
    Optional<BlogPost> findById(Long id);
    
    // Lấy tất cả categories (resource types)
    List<String> getAllCategories();
    
    // Lấy các bài viết nổi bật
    List<BlogPost> getFeaturedPosts(int limit);
    
    // Tăng view count cho blog post
    void incrementViewCount(Long id);
    
    // Lấy bài viết liên quan theo resource type
    List<BlogPost> getRelatedPosts(Resource.ResourceType resourceType, Long excludeId, int limit);
    
    // Lấy bài viết tiếp theo
    Optional<BlogPost> getNextPost(Long currentId);
    
    // Lấy bài viết trước đó
    Optional<BlogPost> getPreviousPost(Long currentId);
} 