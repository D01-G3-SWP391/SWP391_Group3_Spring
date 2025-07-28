package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IJobPostRepository extends JpaRepository<JobPost, Integer> {

    @Query("select jp from JobPost jp where jp.approvalStatus = 'APPROVED' and jp.displayStatus = 'ACTIVE'")
    Page<JobPost> findAllPendingJobs(Pageable pageable);
    // Existing methods
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
            "WHERE jp.approvalStatus = 'APPROVED' " +
            "AND jp.displayStatus = 'ACTIVE' " +
            "AND (:keyword IS NULL OR jp.jobTitle LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:location IS NULL OR jp.jobLocation LIKE CONCAT('%', :location, '%')) " +
            "AND (:minSalary IS NULL OR jp.jobSalary >= :minSalary) " +
            "AND (:maxSalary IS NULL OR jp.jobSalary <= :maxSalary) " +
            "AND (:jobType IS NULL OR jp.jobType = :jobType) " +
            "AND (:jobFieldId IS NULL OR jf.jobFieldId = :jobFieldId) " +
            "AND (:companyName IS NULL OR e.companyName LIKE CONCAT('%', :companyName, '%'))")
    List<JobPost> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") JobPost.JobType jobType,
            @Param("jobFieldId") Integer jobFieldId,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            @Param("companyName") String companyName
    );





    @Query("SELECT jp FROM JobPost jp " +
            "WHERE jp.displayStatus = 'ACTIVE' " +
            "AND jp.appliedQuality > 0 " +
            "ORDER BY jp.appliedQuality DESC")
    List<JobPost> findTopJobsByAppliedQuality();

    // THÊM điều kiện appliedQuality > 0 cho query có limit
    @Query("SELECT jp FROM JobPost jp " +
            "WHERE jp.displayStatus = 'ACTIVE' " +
            "AND jp.appliedQuality > 0 " +
            "ORDER BY jp.appliedQuality DESC")
    Page<JobPost> findTopJobsByAppliedQuality(Pageable pageable);



    // Admin Job Post Management Methods
    Page<JobPost> findByApprovalStatusOrderByCreatedAtDesc(JobPost.ApprovalStatus status, Pageable pageable);

    @Query("SELECT jp FROM JobPost jp WHERE " +
            "jp.jobTitle LIKE %:keyword% OR jp.employer.companyName LIKE %:keyword% OR jp.jobLocation LIKE %:keyword% " +
            "ORDER BY jp.createdAt DESC")
    Page<JobPost> searchJobPostsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT jp FROM JobPost jp WHERE " +
            "(jp.jobTitle LIKE %:keyword% OR jp.employer.companyName LIKE %:keyword% OR jp.jobLocation LIKE %:keyword%) " +
            "AND jp.approvalStatus = :status " +
            "ORDER BY jp.createdAt DESC")
    Page<JobPost> searchJobPostsByKeywordAndStatus(@Param("keyword") String keyword,
                                                   @Param("status") JobPost.ApprovalStatus status,
                                                   Pageable pageable);

    // Get all job posts with pagination
    Page<JobPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Get job posts with employer details
    @Query("SELECT jp FROM JobPost jp JOIN FETCH jp.employer e ORDER BY jp.createdAt DESC")
    Page<JobPost> findAllWithEmployerDetails(Pageable pageable);

    // Get job posts with employer details by IDs (for favorite jobs)
    @Query("SELECT jp FROM JobPost jp JOIN FETCH jp.employer e WHERE jp.jobPostId IN :ids ORDER BY jp.createdAt DESC")
    List<JobPost> findByJobPostIdInWithEmployer(@Param("ids") List<Integer> ids);

    // Count job posts by status
    long countByApprovalStatus(JobPost.ApprovalStatus status);

    // Find deletable job posts
    @Query("SELECT jp FROM JobPost jp WHERE " +
            "jp.approvalStatus = :status OR jp.createdAt < :cutoffDate " +
            "ORDER BY jp.createdAt DESC")
    Page<JobPost> findDeletableJobPosts(@Param("status") JobPost.ApprovalStatus status,
                                        @Param("cutoffDate") LocalDateTime cutoffDate,
                                        Pageable pageable);
//    phan trang jobPost
    Page<JobPost> findByEmployerOrderByCreatedAtDesc(Employer employer, Pageable pageable);
    
    // Find job posts by employer with display status filter
    @Query("SELECT jp FROM JobPost jp WHERE jp.employer = :employer AND jp.displayStatus = :displayStatus ORDER BY jp.createdAt DESC")
    Page<JobPost> findByEmployerAndDisplayStatusOrderByCreatedAtDesc(@Param("employer") Employer employer, 
                                                                     @Param("displayStatus") JobPost.DisplayStatus displayStatus, 
                                                                     Pageable pageable);
    
    // Find all job posts by employer and display status (without pagination)
    @Query("SELECT jp FROM JobPost jp WHERE jp.employer = :employer AND jp.displayStatus = :displayStatus ORDER BY jp.createdAt DESC")
    List<JobPost> findByEmployerAndDisplayStatus(@Param("employer") Employer employer, 
                                                 @Param("displayStatus") JobPost.DisplayStatus displayStatus);
    
////    pendding
    long countByEmployer(Employer employer);
    long countByEmployerAndApprovalStatus(Employer employer, JobPost.ApprovalStatus approvalStatus);
    
    // Count job posts by employer and display status
    long countByEmployerAndDisplayStatus(Employer employer, JobPost.DisplayStatus displayStatus);

    // Dashboard Statistics Queries
    
    // Count total job posts for employer
    @Query("SELECT COUNT(jp) FROM JobPost jp WHERE jp.employer.employerId = :employerId")
    Long countJobPostsByEmployerId(@Param("employerId") Integer employerId);
    
    // Count job posts by approval status for employer
    @Query("SELECT COUNT(jp) FROM JobPost jp WHERE jp.employer.employerId = :employerId " +
           "AND jp.approvalStatus = :status")
    Long countJobPostsByEmployerIdAndApprovalStatus(@Param("employerId") Integer employerId,
                                                   @Param("status") JobPost.ApprovalStatus status);
    
    // Count job posts by display status for employer
    @Query("SELECT COUNT(jp) FROM JobPost jp WHERE jp.employer.employerId = :employerId " +
           "AND jp.displayStatus = :status")
    Long countJobPostsByEmployerIdAndDisplayStatus(@Param("employerId") Integer employerId,
                                                  @Param("status") JobPost.DisplayStatus status);
    
    // Get job posts with application count for employer
    @Query("SELECT jp.jobTitle, jp.approvalStatus, " +
           "(SELECT COUNT(ja) FROM JobApplication ja WHERE ja.jobPost = jp) " +
           "FROM JobPost jp " +
           "WHERE jp.employer.employerId = :employerId " +
           "ORDER BY jp.createdAt DESC")
    List<Object[]> getJobPostsWithApplicationCount(@Param("employerId") Integer employerId);

}
