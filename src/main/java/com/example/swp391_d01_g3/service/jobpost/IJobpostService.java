package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.swp391_d01_g3.model.Employer;

public interface IJobpostService  {
    List<JobPost> findAll();
    void save(JobPost jobPost);
    List<JobPost> findByEmployerId(Integer employerId);
    List<JobPost> findJobPostsByEmployerEmail(String email);
    //phan trang JobPost
    Page<JobPost> findJobPostsByEmployerEmail(String email, Pageable pageable);
    Optional<JobPost> findById(Integer jobPostId);
    void deleteById(Integer jobPostId);

    Page<JobPost> findAll(Pageable pageable);
    Optional<JobPost> findByJobPostId(Integer id);
    List<JobPost> findAllWithEmployer(Integer id);


    List<JobPost> searchJobs(
            String keyword,
            String location,
            String jobType,
            Integer jobFieldId,
            Double minSalary,
            Double maxSalary,
            String companyName
    );



    List<JobPost> getTopJobs();
    Page<JobPost> getTopJobsPaginated(Pageable pageable);
//    pendding
    long countJobPostsByEmployerEmail(String employerEmail);
    long countJobPostsByEmployerEmailAndStatus(String employerEmail, String status);

    // Methods for hide/show feature
    Page<JobPost> findActiveJobPostsByEmployerEmail(String email, Pageable pageable);
    Page<JobPost> findByEmployerAndDisplayStatusOrderByCreatedAtDesc(Employer employer, JobPost.DisplayStatus displayStatus, Pageable pageable);
    long countActiveJobPostsByEmployerEmail(String employerEmail);
    long countHiddenJobPostsByEmployerEmail(String employerEmail);

}
