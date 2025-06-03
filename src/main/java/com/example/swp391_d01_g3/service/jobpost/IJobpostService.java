package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;

import java.util.List;

public interface IJobpostService  {
    List<JobPost> findAll();
    void save(JobPost jobPost);
}
