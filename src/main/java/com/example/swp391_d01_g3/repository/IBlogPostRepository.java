package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.BlogPost;
import com.example.swp391_d01_g3.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBlogPostRepository extends JpaRepository<BlogPost, Long> {

    // Tìm blog post theo status
    List<BlogPost> findByStatus(BlogPost.BlogStatus status);

    // Tìm blog post theo resource
    List<BlogPost> findByResource_ResourceId(Long resourceId);

    // Tìm blog post đã published
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' ORDER BY bp.publishedAt DESC")
    List<BlogPost> findPublishedPostsOrderByDate();

    // Tìm blog post theo title
    @Query("SELECT bp FROM BlogPost bp WHERE bp.title LIKE %:keyword% AND bp.status = 'PUBLISHED'")
    List<BlogPost> findPublishedPostsByTitleContaining(@Param("keyword") String keyword);

    // Lấy các blog post mới nhất
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' ORDER BY bp.createdAt DESC")
    List<BlogPost> findLatestPublishedPosts();

    // Đếm số blog post theo status
    long countByStatus(BlogPost.BlogStatus status);

    // Phân trang cho blog posts đã published
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' ORDER BY bp.publishedAt DESC")
    Page<BlogPost> findAllPublishedWithPagination(Pageable pageable);

    // Tìm kiếm với phân trang
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' AND " +
            "(bp.title LIKE %:keyword% OR bp.summary LIKE %:keyword% OR bp.content LIKE %:keyword%)")
    Page<BlogPost> searchPublishedPosts(@Param("keyword") String keyword, Pageable pageable);

    // Lọc theo resource type với phân trang
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' AND bp.resource.resourceType = :resourceType")
    Page<BlogPost> findByResourceTypeWithPagination(@Param("resourceType") Resource.ResourceType resourceType, Pageable pageable);

    // Lấy tất cả resource types đã có blog posts
    @Query("SELECT DISTINCT bp.resource.resourceType FROM BlogPost bp WHERE bp.status = 'PUBLISHED'")
    List<Resource.ResourceType> findAllPublishedResourceTypes();

    // Lấy bài viết nổi bật (theo lượt xem hoặc ngày publish mới nhất)
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' ORDER BY bp.publishedAt DESC")
    List<BlogPost> findFeaturedPosts(Pageable pageable);

    // Lấy bài viết liên quan theo resource type (loại trừ bài hiện tại)
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' AND bp.resource.resourceType = :resourceType AND bp.blogPostId != :excludeId ORDER BY bp.publishedAt DESC")
    List<BlogPost> findRelatedPosts(@Param("resourceType") Resource.ResourceType resourceType, @Param("excludeId") Long excludeId, Pageable pageable);

    // Lấy bài viết tiếp theo (theo thứ tự publishedAt) với Pageable
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' AND bp.publishedAt > " +
            "(SELECT bp2.publishedAt FROM BlogPost bp2 WHERE bp2.blogPostId = :currentId) " +
            "ORDER BY bp.publishedAt ASC")
    List<BlogPost> findNextPostWithLimit(@Param("currentId") Long currentId, Pageable pageable);

    // Lấy bài viết trước đó (theo thứ tự publishedAt) với Pageable
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' AND bp.publishedAt < " +
            "(SELECT bp2.publishedAt FROM BlogPost bp2 WHERE bp2.blogPostId = :currentId) " +
            "ORDER BY bp.publishedAt DESC")
    List<BlogPost> findPreviousPostWithLimit(@Param("currentId") Long currentId, Pageable pageable);

    // Tăng view count (giả sử có field viewCount)
    @Modifying
    @Transactional
    @Query("UPDATE BlogPost bp SET bp.updatedAt = CURRENT_TIMESTAMP WHERE bp.blogPostId = :id")
    void incrementViewCount(@Param("id") Long id);
    // Thêm vào IBlogPostRepository.java
    @Query("SELECT b FROM BlogPost b WHERE b.title LIKE %:title% ORDER BY b.createdAt DESC")
    Page<BlogPost> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE b.title LIKE %:title% AND b.status = :status ORDER BY b.createdAt DESC")
    Page<BlogPost> findByTitleContainingIgnoreCaseAndStatus(@Param("title") String title,
                                                            @Param("status") BlogPost.BlogStatus status,
                                                            Pageable pageable);

    Page<BlogPost> findByStatus(BlogPost.BlogStatus status, Pageable pageable);


}