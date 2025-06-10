package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.Employer;


import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
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
    @Autowired
    private IEmployerService iEmployerService;

    @Override
    public List<JobPost> findAll() {
        return iJobPostRepository.findAll();
    }

    @Override
    public Page<JobPost> findAll(Pageable pageable) {
        return iJobPostRepository.findAll(pageable);
    }
    @Override
    public Optional<JobPost> findByIdJobPost(Long id) {
        return iJobPostRepository.findByIdJobPost(id);
    }

    @Override
    public List<JobPost> findAllWithEmployer(Long id) {
        return iJobPostRepository.findAllWithEmployer(id);
    }
    @Override
    public void save(JobPost jobPost) {
        iJobPostRepository.save(jobPost);
    }

    @Override
    public List<JobPost> findByEmployerId(Integer employerId) {
        return iJobPostRepository.findByEmployerId(employerId);
    }

    @Override
    public List<JobPost> findJobPostsByEmployerEmail(String email) {
        Employer employer = iEmployerService.findByEmail(email);
        return iJobPostRepository.findByEmployer(employer);
    }

    @Override
    public Optional<JobPost> findById(Integer jobPostId) {
        return iJobPostRepository.findById(jobPostId);
    }


    @Override
    public void deleteById(Integer jobPostId) {
        iJobPostRepository.deleteById(jobPostId);
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


}