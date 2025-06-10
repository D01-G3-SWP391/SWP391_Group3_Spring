package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobPostImpl implements IJobpostService {

    @Autowired
    private IJobPostRepository iJobPostRepository;

    @Override
    public List<JobPost> findAll() {
        return iJobPostRepository.findAll();
    }

    @Override
    public Page<JobPost> findAll(Pageable pageable) {
        return iJobPostRepository.findAll(pageable);
    }
}
