package com.example.swp391_d01_g3.service.jobpost;



import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Page<JobPost> findAll(Pageable pageable) {
        return iJobPostRepository.findAll(pageable);
    }
    @Override
    public Optional<JobPost> findById(Long id) {
        return iJobPostRepository.findById(id);
    }

    @Override
    public List<JobPost> findAllWithEmployer(Long id) {
        return iJobPostRepository.findAllWithEmployer(id);
    }

    @Override
    public List<JobPost> searchJobs(String keyword, String location, String jobType,
                                    Integer fieldId, Integer salary, String companyName) {
        return iJobPostRepository.searchJobs(
                (keyword == null || keyword.isEmpty()) ? null : keyword,
                (location == null || location.isEmpty()) ? null : location,
                (salary == null || salary == 0) ? null : salary,
                (jobType == null || jobType.isEmpty()) ? null : jobType,
                (fieldId == null || fieldId == 0) ? null : fieldId,
                (companyName == null || companyName.isEmpty()) ? null : companyName
        );
    }

    @Override
    public void save(JobPost jobPost) {
        iJobPostRepository.save(jobPost);
    }
}