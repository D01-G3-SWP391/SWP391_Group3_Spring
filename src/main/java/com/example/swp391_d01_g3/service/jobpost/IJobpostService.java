package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IJobpostService  {
    List<JobPost> findAll();
    void save(JobPost jobPost);
    List<JobPost> findByEmployerId(Integer employerId);
    List<JobPost> findJobPostsByEmployerEmail(String email);
    Optional<JobPost> findById(Integer jobPostId);
    void deleteById(Integer jobPostId);

    Page<JobPost> findAll(Pageable pageable);
    Optional<JobPost> findByJobPostId(Integer id);
    List<JobPost> findAllWithEmployer(Integer id);

    // Hàm search với đầy đủ các tiêu chí
    List<JobPost> searchJobs(String keyword, String location, String jobType,
                             Integer fieldId, Integer salary,
                             String companyName);

    List<JobPost> getTopJobs();
    List<JobPost> getTopJobsLimit(int limit);

}
