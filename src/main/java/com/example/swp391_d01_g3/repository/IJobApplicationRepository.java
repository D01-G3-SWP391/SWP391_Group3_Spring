package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IJobApplicationRepository extends JpaRepository<JobApplication, Integer> {

    @Query("SELECT ja FROM JobApplication ja WHERE ja.student.studentId = :studentId")
    List<JobApplication> findByStudentId(@Param("studentId") Integer studentId);

    @Query("SELECT ja FROM JobApplication ja " +
            "JOIN FETCH ja.student s " +
            "JOIN FETCH ja.jobPost jp " +
            "WHERE jp.employer.employerId = :employerId " +
            "ORDER BY ja.appliedAt DESC")
    Page<JobApplication> findApplicationsByEmployerId(@Param("employerId") Integer employerId, Pageable pageable);

    // 2. Tìm kiếm ứng viên theo tên (có phân trang)
    @Query("SELECT ja FROM JobApplication ja " +
            "JOIN FETCH ja.student s " +
            "JOIN FETCH ja.jobPost jp " +
            "WHERE jp.employer.employerId = :employerId " +
            "AND (LOWER(ja.fullName) LIKE LOWER(CONCAT('%', :searchName, '%'))) " +
            "ORDER BY ja.appliedAt DESC")
    Page<JobApplication> findApplicationsByEmployerIdAndName(@Param("employerId") Integer employerId,
                                                             @Param("searchName") String searchName,
                                                             Pageable pageable);

    // 3. Kiểm tra xem student đã apply vào job này chưa
    @Query("SELECT COUNT(ja) > 0 FROM JobApplication ja " +
            "WHERE ja.student.studentId = :studentId " +
            "AND ja.jobPost.jobPostId = :jobPostId")
    boolean existsByStudentIdAndJobPostId(@Param("studentId") Integer studentId, 
                                        @Param("jobPostId") Integer jobPostId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobPost.jobPostId = :jobPostId ORDER BY ja.appliedAt DESC")
    List<JobApplication> findByJobPostId(@Param("jobPostId") Integer jobPostId);

    @Query("SELECT ja FROM JobApplication ja " +
           "JOIN FETCH ja.student s " +
           "WHERE ja.jobPost.jobPostId = :jobPostId " +
           "AND (:searchName IS NULL OR LOWER(ja.fullName) LIKE LOWER(CONCAT('%', :searchName, '%'))) " +
           "ORDER BY ja.appliedAt DESC")
    List<JobApplication> findByJobPostIdAndNameAndExperience(
        @Param("jobPostId") Integer jobPostId,
        @Param("searchName") String searchName,
        @Param("searchExperience") String searchExperience
    );

    // Tìm kiếm ứng viên theo từ khóa (tên, email, kinh nghiệm) cho employer
    @Query("SELECT ja FROM JobApplication ja " +
           "JOIN FETCH ja.student s " +
           "JOIN FETCH ja.jobPost jp " +
           "WHERE jp.employer.employerId = :employerId " +
           "AND (LOWER(ja.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(ja.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(ja.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY ja.appliedAt DESC")
    Page<JobApplication> searchApplicationsByEmployerIdAndKeyword(@Param("employerId") Integer employerId,
                                                                 @Param("keyword") String keyword,
                                                                 Pageable pageable);

}
