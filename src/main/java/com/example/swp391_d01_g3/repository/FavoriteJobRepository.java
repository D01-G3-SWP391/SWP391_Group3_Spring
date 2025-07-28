package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.FavoriteJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteJobRepository extends JpaRepository<FavoriteJob, Integer> {

    // Kiểm tra xem student đã favorite job này chưa
    boolean existsByStudentAndJobPost(com.example.swp391_d01_g3.model.Student student, com.example.swp391_d01_g3.model.JobPost jobPost);

    // Tìm favorite job theo studentId và jobPostId
    Optional<FavoriteJob> findByStudentAndJobPost(com.example.swp391_d01_g3.model.Student student, com.example.swp391_d01_g3.model.JobPost jobPost);

    // Lấy tất cả favorite jobs của một student
    List<FavoriteJob> findByStudentOrderByCreatedAtDesc(com.example.swp391_d01_g3.model.Student student);

    // Xóa favorite job theo studentId và jobPostId
    void deleteByStudentAndJobPost(com.example.swp391_d01_g3.model.Student student, com.example.swp391_d01_g3.model.JobPost jobPost);

    // Đếm số lượng favorite jobs của student
    long countByStudent(com.example.swp391_d01_g3.model.Student student);

    // Xóa method này vì không còn trường jobPostId và studentId
    // @Query("SELECT f.jobPostId FROM FavoriteJob f WHERE f.studentId = :studentId")
    // List<Integer> findJobPostIdsByStudentId(@Param("studentId") Integer studentId);
} 