package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IJobPostRepository extends JpaRepository<JobPost,Integer> {

    @Query("SELECT jp FROM JobPost jp WHERE jp.employer = ?1")
    List<JobPost> findByEmployerId(Integer employerId);
    List<JobPost> findByEmployer(Employer employer);
    Optional<JobPost> findById(Integer jobPostId);

    Optional<JobPost> findByJobPostId(Integer jobPostId);

    @Query("SELECT jp FROM JobPost jp JOIN FETCH jp.employer WHERE jp.jobPostId = ?1")
    List<JobPost> findAllWithEmployer(Integer id);


    @Query("SELECT jp FROM JobPost jp " +
            "JOIN jp.employer e " +
            "JOIN e.jobField jf " +
            "WHERE (:keyword IS NULL OR jp.jobTitle LIKE %:keyword%) " +
            "AND (:location IS NULL OR jp.jobLocation = :location) " +
            "AND (:salary IS NULL OR jp.jobSalary >= :salary) " +
            "AND (:jobType IS NULL OR jp.jobType = :jobType) " +
            "AND (:jobFieldId IS NULL OR jf.jobFieldId = :jobFieldId) " +
            "AND (:companyName IS NULL OR e.companyName = :companyName)")
    List<JobPost> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("salary") Integer salary,
            @Param("jobType") String jobType,
            @Param("jobFieldId") Integer jobFieldId,
            @Param("companyName") String companyName);
}
