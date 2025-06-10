package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IJobpostService  {
    List<JobPost> findAll();
    Optional<JobPost> findByIdJobPost(Long id);
    List<JobPost> findAllWithEmployer(Long id);

    // Hàm search với đầy đủ các tiêu chí
    List<JobPost> searchJobs(String keyword, String location, String jobType,
                             Integer fieldId, Integer salary,
                             String companyName);

    void save(JobPost jobPost);


}
