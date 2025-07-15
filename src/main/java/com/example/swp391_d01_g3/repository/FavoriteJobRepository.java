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
    boolean existsByStudentIdAndJobPostId(Integer studentId, Integer jobPostId);

    // Tìm favorite job theo studentId và jobPostId
    Optional<FavoriteJob> findByStudentIdAndJobPostId(Integer studentId, Integer jobPostId);

    // Lấy tất cả favorite jobs của một student
    List<FavoriteJob> findByStudentIdOrderByCreatedAtDesc(Integer studentId);

    // Xóa favorite job theo studentId và jobPostId
    void deleteByStudentIdAndJobPostId(Integer studentId, Integer jobPostId);

    // Đếm số lượng favorite jobs của student
    long countByStudentId(Integer studentId);

    // Lấy danh sách jobPostId của student
    @Query("SELECT f.jobPostId FROM FavoriteJob f WHERE f.studentId = :studentId")
    List<Integer> findJobPostIdsByStudentId(@Param("studentId") Integer studentId);
} 