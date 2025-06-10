//package com.example.swp391_d01_g3.repository;
//
//import com.example.swp391_d01_g3.model.Resource;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface IResourceRepository extends JpaRepository<Resource, Long> {
//
//    // Tìm resource theo type
//    List<Resource> findByResourceType(Resource.ResourceType resourceType);
//
//    // Tìm resource theo title
//    Optional<Resource> findByResourceTitle(String resourceTitle);
//
//    // Tìm resource được tạo bởi user nào đó
//    List<Resource> findByCreatedBy_AccountId(Long accountId);
//
//    // Tìm resource theo title chứa keyword
//    @Query("SELECT r FROM Resource r WHERE r.resourceTitle LIKE %:keyword%")
//    List<Resource> findByResourceTitleContaining(@Param("keyword") String keyword);
//
//    // Lấy các resource mới nhất
//    @Query("SELECT r FROM Resource r ORDER BY r.createdAt DESC")
//    List<Resource> findAllOrderByCreatedAtDesc();
//}