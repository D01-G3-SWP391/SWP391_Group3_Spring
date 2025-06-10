package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;

import java.util.List;
import java.util.Optional;

public interface IJobpostService  {
    List<JobPost> findAll();
    void save(JobPost jobPost);
    List<JobPost> findByEmployerId(Integer employerId);
    List<JobPost> findJobPostsByEmployerEmail(String email);
    Optional<JobPost> findById(Integer jobPostId);
    void deleteById(Integer jobPostId);

}
