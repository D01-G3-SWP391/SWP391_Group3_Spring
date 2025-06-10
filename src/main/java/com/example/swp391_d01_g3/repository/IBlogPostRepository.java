//package com.example.swp391_d01_g3.repository;
//
//import com.example.swp391_d01_g3.model.BlogPost;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface IBlogPostRepository extends JpaRepository<BlogPost, Long> {
//
//    // Tìm blog post theo status
//    List<BlogPost> findByStatus(BlogPost.BlogStatus status);
//
//    // Tìm blog post theo resource
//    List<BlogPost> findByResource_ResourceId(Long resourceId);
//
//    // Tìm blog post đã published
//    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' ORDER BY bp.publishedAt DESC")
//    List<BlogPost> findPublishedPostsOrderByDate();
//
//    // Tìm blog post theo title
//    @Query("SELECT bp FROM BlogPost bp WHERE bp.title LIKE %:keyword% AND bp.status = 'PUBLISHED'")
//    List<BlogPost> findPublishedPostsByTitleContaining(@Param("keyword") String keyword);
//
//    // Lấy các blog post mới nhất
//    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' ORDER BY bp.createdAt DESC")
//    List<BlogPost> findLatestPublishedPosts();
//
//    // Đếm số blog post theo status
//    long countByStatus(BlogPost.BlogStatus status);
//}