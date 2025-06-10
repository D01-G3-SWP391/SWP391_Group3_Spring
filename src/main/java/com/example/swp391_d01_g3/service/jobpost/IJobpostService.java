package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IJobpostService  {
    List<JobPost> findAll();
    Page<JobPost> findAll(Pageable pageable);
}
