package com.example.swp391_d01_g3.service.blog;

import com.example.swp391_d01_g3.model.BlogPost;
import com.example.swp391_d01_g3.model.Resource;
import com.example.swp391_d01_g3.repository.IBlogPostRepository;
import com.example.swp391_d01_g3.repository.IResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements IBlogService {

    @Autowired
    private IBlogPostRepository blogPostRepository;

    @Autowired
    private IResourceRepository resourceRepository;

    @Override
    public List<BlogPost> getAllPublishedBlogPosts() {
        return blogPostRepository.findPublishedPostsOrderByDate();
    }

    @Override
    public Optional<BlogPost> getBlogPostById(Long id) {
        return blogPostRepository.findById(id);
    }

    @Override
    public List<BlogPost> searchBlogPosts(String keyword) {
        return blogPostRepository.findPublishedPostsByTitleContaining(keyword);
    }

    @Override
    public List<BlogPost> getBlogPostsByResourceType(String resourceType) {
        try {
            Resource.ResourceType type = Resource.ResourceType.valueOf(resourceType);
            List<Resource> resources = resourceRepository.findByResourceType(type);

            return blogPostRepository.findAll().stream()
                    .filter(post -> post.getStatus() == BlogPost.BlogStatus.PUBLISHED)
                    .filter(post -> resources.contains(post.getResource()))
                    .toList();
        } catch (IllegalArgumentException e) {
            return getAllPublishedBlogPosts();
        }
    }

    @Override
    public List<BlogPost> getLatestPublishedPosts(int limit) {
        return blogPostRepository.findLatestPublishedPosts()
                .stream()
                .limit(limit)
                .toList();
    }

    @Override
    public List<BlogPost> getBlogPostsByStatus(BlogPost.BlogStatus status) {
        return blogPostRepository.findByStatus(status);
    }

    @Override
    public List<BlogPost> getBlogPostsByResourceId(Long resourceId) {
        return blogPostRepository.findByResource_ResourceId(resourceId);
    }

    @Override
    public long countBlogPostsByStatus(BlogPost.BlogStatus status) {
        return blogPostRepository.countByStatus(status);
    }

    @Override
    public Page<BlogPost> findAllPublished(Pageable pageable) {
        return blogPostRepository.findAllPublishedWithPagination(pageable);
    }

    @Override
    public Page<BlogPost> searchBlogs(String keyword, Pageable pageable) {
        return blogPostRepository.searchPublishedPosts(keyword, pageable);
    }

    @Override
    public Page<BlogPost> findByCategory(String category, Pageable pageable) {
        try {
            Resource.ResourceType resourceType = Resource.ResourceType.valueOf(category);
            return blogPostRepository.findByResourceTypeWithPagination(resourceType, pageable);
        } catch (IllegalArgumentException e) {
            // If category is invalid, return all published posts
            return findAllPublished(pageable);
        }
    }

    @Override
    public Optional<BlogPost> findById(Long id) {
        return blogPostRepository.findById(id);
    }

    @Override
    public List<String> getAllCategories() {
        List<Resource.ResourceType> resourceTypes = blogPostRepository.findAllPublishedResourceTypes();
        return resourceTypes.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogPost> getFeaturedPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return blogPostRepository.findFeaturedPosts(pageable);
    }

    @Override
    public void incrementViewCount(Long id) {
        blogPostRepository.incrementViewCount(id);
    }

    @Override
    public List<BlogPost> getRelatedPosts(Resource.ResourceType resourceType, Long excludeId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return blogPostRepository.findRelatedPosts(resourceType, excludeId, pageable);
    }

    @Override
    public Optional<BlogPost> getNextPost(Long currentId) {
        Pageable limit = PageRequest.of(0, 1);
        List<BlogPost> nextPosts = blogPostRepository.findNextPostWithLimit(currentId, limit);
        return nextPosts.isEmpty() ? Optional.empty() : Optional.of(nextPosts.get(0));
    }

    @Override
    public Optional<BlogPost> getPreviousPost(Long currentId) {
        Pageable limit = PageRequest.of(0, 1);
        List<BlogPost> previousPosts = blogPostRepository.findPreviousPostWithLimit(currentId, limit);
        return previousPosts.isEmpty() ? Optional.empty() : Optional.of(previousPosts.get(0));
    }

    @Override
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    public void updateBlogStatus(Long blogPostId, BlogPost.BlogStatus newStatus) {
        BlogPost blog = blogPostRepository.findById(blogPostId).orElse(null);
        if (blog != null) {
            blog.setStatus(newStatus);
            blogPostRepository.save(blog);
        }
    }

    @Override
    public void updateBlog(Long id, BlogPost updatedBlog) {
        BlogPost blog = blogPostRepository.findById(id).orElseThrow();
        blog.setTitle(updatedBlog.getTitle());
        blog.setSummary(updatedBlog.getSummary());
        blog.setContent(updatedBlog.getContent());
        BlogPost.BlogStatus oldStatus = blog.getStatus();
        blog.setStatus(updatedBlog.getStatus());
        // Nếu status chuyển từ khác PUBLISHED sang PUBLISHED thì cập nhật lại publishedAt
        if (updatedBlog.getStatus() == BlogPost.BlogStatus.PUBLISHED && oldStatus != BlogPost.BlogStatus.PUBLISHED) {
            blog.setPublishedAt(LocalDateTime.now());
        }
        blog.setUpdatedAt(LocalDateTime.now());
        // Xử lý resource đúng cách
        if (updatedBlog.getResource() != null && updatedBlog.getResource().getResourceId() != null) {
            Resource resource = resourceRepository.findById(updatedBlog.getResource().getResourceId()).orElse(null);
            blog.setResource(resource);
        } else {
            blog.setResource(null);
        }
        blogPostRepository.save(blog);
    }

    @Override
    public void save(BlogPost blog) {
        blogPostRepository.save(blog);
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }


    // THÊM: Admin pagination methods
    @Override
    public Page<BlogPost> getAllBlogPostsWithPagination(Pageable pageable) {
        // Sắp xếp theo createdAt desc để hiển thị blog mới nhất trước
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return blogPostRepository.findAll(sortedPageable);
    }

    @Override
    public Page<BlogPost> searchBlogsByTitle(String title, Pageable pageable) {
        // Sắp xếp theo createdAt desc
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return blogPostRepository.findByTitleContainingIgnoreCase(title, sortedPageable);
    }

    @Override
    public Page<BlogPost> searchBlogsByTitleAndStatus(String title, BlogPost.BlogStatus status, Pageable pageable) {
        // Sắp xếp theo createdAt desc
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return blogPostRepository.findByTitleContainingIgnoreCaseAndStatus(title, status, sortedPageable);
    }

    @Override
    public Page<BlogPost> findByStatus(BlogPost.BlogStatus status, Pageable pageable) {
        // Sắp xếp theo createdAt desc
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return blogPostRepository.findByStatus(status, sortedPageable);
    }

    @Override
    public long getTotalBlogsCount() {
        return blogPostRepository.count();
    }

    @Override
    public long getDraftBlogsCount() {
        return blogPostRepository.countByStatus(BlogPost.BlogStatus.DRAFT);
    }

    @Override
    public long getPublishedBlogsCount() {
        return blogPostRepository.countByStatus(BlogPost.BlogStatus.PUBLISHED);
    }

    @Override
    public long getArchivedBlogsCount() {
        return blogPostRepository.countByStatus(BlogPost.BlogStatus.ARCHIVED);
    }

    @Override
    public BlogPost createBlog(BlogPost newBlog) {
        newBlog.setCreatedAt(LocalDateTime.now());
        newBlog.setUpdatedAt(LocalDateTime.now());
        return blogPostRepository.save(newBlog);
    }

    @Override
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }
}
