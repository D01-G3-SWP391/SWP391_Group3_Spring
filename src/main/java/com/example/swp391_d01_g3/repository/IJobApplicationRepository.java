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

}
