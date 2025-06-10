//package com.example.swp391_d01_g3.service.blog;
//
//import com.example.swp391_d01_g3.dto.BlogPostCreateDTO;
//import com.example.swp391_d01_g3.model.Account;
//import com.example.swp391_d01_g3.model.BlogPost;
//import com.example.swp391_d01_g3.model.Resource;
//import com.example.swp391_d01_g3.repository.IBlogPostRepository;
//import com.example.swp391_d01_g3.repository.IResourceRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Transactional
//public class BlogServiceImpl {
//
//    @Autowired
//    private IBlogPostRepository blogPostRepository;
//
//    @Autowired
//    private IResourceRepository resourceRepository;
//
//    /**
//     * Tạo blog post mới
//     */
//    public BlogPost createBlogPost(BlogPostCreateDTO dto, Account author) {
//        // Tìm hoặc tạo resource
//        Resource resource = findOrCreateResource(dto, author);
//
//        // Tạo blog post
//        BlogPost blogPost = new BlogPost();
//        blogPost.setResource(resource);
//        blogPost.setTitle(dto.getTitle());
//        blogPost.setContent(dto.getContent());
//        blogPost.setSummary(dto.getSummary());
//        blogPost.setFeaturedImageUrl(dto.getFeaturedImageUrl());
//        blogPost.setMetaDescription(dto.getMetaDescription());
//        blogPost.setStatus(BlogPost.BlogStatus.DRAFT);
//        blogPost.setCreatedAt(LocalDateTime.now());
//        blogPost.setUpdatedAt(LocalDateTime.now());
//
//        return blogPostRepository.save(blogPost);
//    }
//
//    /**
//     * Tìm hoặc tạo resource
//     */
//    private Resource findOrCreateResource(BlogPostCreateDTO dto, Account author) {
//        if (dto.getResourceId() != null) {
//            Optional<Resource> existingResource = resourceRepository.findById(dto.getResourceId());
//            if (existingResource.isPresent()) {
//                return existingResource.get();
//            }
//        }
//
//        // Tạo resource mới
//        Resource resource = new Resource();
//        resource.setResourceTitle(dto.getResourceTitle() != null ? dto.getResourceTitle() : dto.getTitle());
//        resource.setResourceContent(dto.getResourceContent() != null ? dto.getResourceContent() : dto.getSummary());
//        resource.setImageUrl(dto.getResourceImageUrl());
//        resource.setResourceType(dto.getResourceType() != null ? dto.getResourceType() : Resource.ResourceType.application_tips);
//        resource.setCreatedBy(author);
//        resource.setCreatedAt(LocalDateTime.now());
//        resource.setUpdatedAt(LocalDateTime.now());
//
//        return resourceRepository.save(resource);
//    }
//
//    /**
//     * Cập nhật blog post
//     */
//    public BlogPost updateBlogPost(Long blogPostId, BlogPostCreateDTO dto) {
//        Optional<BlogPost> optionalBlogPost = blogPostRepository.findById(blogPostId);
//        if (optionalBlogPost.isEmpty()) {
//            throw new RuntimeException("Blog post không tồn tại");
//        }
//
//        BlogPost blogPost = optionalBlogPost.get();
//        blogPost.setTitle(dto.getTitle());
//        blogPost.setContent(dto.getContent());
//        blogPost.setSummary(dto.getSummary());
//        blogPost.setFeaturedImageUrl(dto.getFeaturedImageUrl());
//        blogPost.setMetaDescription(dto.getMetaDescription());
//        blogPost.setUpdatedAt(LocalDateTime.now());
//
//        return blogPostRepository.save(blogPost);
//    }
//
//    /**
//     * Publish blog post
//     */
//    public BlogPost publishBlogPost(Long blogPostId) {
//        Optional<BlogPost> optionalBlogPost = blogPostRepository.findById(blogPostId);
//        if (optionalBlogPost.isEmpty()) {
//            throw new RuntimeException("Blog post không tồn tại");
//        }
//
//        BlogPost blogPost = optionalBlogPost.get();
//        blogPost.setStatus(BlogPost.BlogStatus.PUBLISHED);
//        blogPost.setPublishedAt(LocalDateTime.now());
//        blogPost.setUpdatedAt(LocalDateTime.now());
//
//        return blogPostRepository.save(blogPost);
//    }
//
//    /**
//     * Lấy tất cả blog posts đã published
//     */
//    public List<BlogPost> getAllPublishedBlogPosts() {
//        return blogPostRepository.findPublishedPostsOrderByDate();
//    }
//
//    /**
//     * Lấy blog post theo ID
//     */
//    public Optional<BlogPost> getBlogPostById(Long id) {
//        return blogPostRepository.findById(id);
//    }
//
//    /**
//     * Tìm kiếm blog posts
//     */
//    public List<BlogPost> searchBlogPosts(String keyword) {
//        return blogPostRepository.findPublishedPostsByTitleContaining(keyword);
//    }
//
//    /**
//     * Xóa blog post
//     */
//    public void deleteBlogPost(Long blogPostId) {
//        if (!blogPostRepository.existsById(blogPostId)) {
//            throw new RuntimeException("Blog post không tồn tại");
//        }
//        blogPostRepository.deleteById(blogPostId);
//    }
//
//    /**
//     * Lấy tất cả blog posts (bao gồm draft)
//     */
//    public List<BlogPost> getAllBlogPosts() {
//        return blogPostRepository.findAll();
//    }
//
//    /**
//     * Lấy blog posts theo status
//     */
//    public List<BlogPost> getBlogPostsByStatus(BlogPost.BlogStatus status) {
//        return blogPostRepository.findByStatus(status);
//    }
//}