package com.example.swp391_d01_g3.service.blog;

import com.example.swp391_d01_g3.model.BlogPost;
import com.example.swp391_d01_g3.model.Resource;
import com.example.swp391_d01_g3.repository.IBlogPostRepository;
import com.example.swp391_d01_g3.repository.IResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
} 