package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostImpl implements IJobpostService {

    @Autowired
    private IJobPostRepository iJobPostRepository;

    @Override
    public List<JobPost> findAll() {
        return iJobPostRepository.findAll();
    }

    @Override
    public Optional<JobPost> findById(Long id) {
        return iJobPostRepository.findById(id);
    }

    @Override
    public List<JobPost> findAllWithEmployer(Long id) {
        return iJobPostRepository.findAllWithEmployer(id);
    }
}
