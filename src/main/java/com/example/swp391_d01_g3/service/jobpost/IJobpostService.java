package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;

import java.util.List;
import java.util.Optional;

public interface IJobpostService  {
    List<JobPost> findAll();
    Optional<JobPost> findById(Long id);
    List<JobPost> findAllWithEmployer(Long id);

}
