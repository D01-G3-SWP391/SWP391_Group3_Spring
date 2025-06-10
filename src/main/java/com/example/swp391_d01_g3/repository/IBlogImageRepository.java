//package com.example.swp391_d01_g3.repository;
//
//import com.example.swp391_d01_g3.model.BlogImage;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//
//@Repository
//public interface IBlogImageRepository extends JpaRepository<BlogImage, Long> {
//
//    // Tìm tất cả ảnh của một blog post
//    List<BlogImage> findByBlogPost_BlogPostId(Long blogPostId);
//
//    // Tìm ảnh theo type
//    List<BlogImage> findByImageType(BlogImage.ImageType imageType);
//
//    // Tìm ảnh featured của blog post
//    @Query("SELECT bi FROM BlogImage bi WHERE bi.blogPost.blogPostId = :blogPostId AND bi.imageType = 'FEATURED'")
//    List<BlogImage> findFeaturedImagesByBlogPost(@Param("blogPostId") Long blogPostId);
//
//    // Tìm ảnh inline của blog post theo thứ tự
//    @Query("SELECT bi FROM BlogImage bi WHERE bi.blogPost.blogPostId = :blogPostId AND bi.imageType = 'INLINE' ORDER BY bi.displayOrder ASC")
//    List<BlogImage> findInlineImagesByBlogPostOrdered(@Param("blogPostId") Long blogPostId);
//
//    // Xóa tất cả ảnh của một blog post
//    void deleteByBlogPost_BlogPostId(Long blogPostId);
//}