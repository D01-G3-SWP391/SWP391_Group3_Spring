package com.example.swp391_d01_g3.service.jobpost;

import com.example.swp391_d01_g3.model.Employer;


import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
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
        return iJobPostRepository.findAllPendingJobs(pageable);
    }

    @Override
    public Optional<JobPost> findByJobPostId(Integer id) {
        return iJobPostRepository.findByJobPostId(id);
    }

    @Override
    public List<JobPost> findAllWithEmployer(Integer id) {
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
    public List<JobPost> searchJobs(String keyword, String location, String jobTypeStr,
                                    Integer fieldId, Double minSalary, Double maxSalary,
                                    String companyName) {

        // Chuyển đổi jobType từ String sang Enum
        JobPost.JobType jobType = null;
        if (jobTypeStr != null && !jobTypeStr.isEmpty()) {
            try {
                jobType = JobPost.JobType.valueOf(jobTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Xử lý nếu giá trị enum không hợp lệ
                System.err.println("Invalid job type: " + jobTypeStr);
            }
        }

        return iJobPostRepository.searchJobs(
                (keyword == null || keyword.isEmpty()) ? null : keyword,
                (location == null || location.isEmpty()) ? null : location,
                jobType,  // Truyền Enum, không phải String
                fieldId,
                minSalary,
                maxSalary,
                (companyName == null || companyName.isEmpty()) ? null : companyName
        );
    }

    @Override
    public List<JobPost> getTopJobs() {
        return iJobPostRepository.findTopJobsByAppliedQuality();
    }

    @Override
    public Page<JobPost> getTopJobsPaginated(Pageable pageable) {
        return iJobPostRepository.findTopJobsByAppliedQuality(pageable);
    }

    //    pendding
    @Override
    public long countJobPostsByEmployerEmail(String employerEmail) {
        Employer employer = iEmployerService.findByEmail(employerEmail);
        if (employer == null) {
            return 0;
        }
        return iJobPostRepository.countByEmployer(employer);
    }

    @Override
    public long countJobPostsByEmployerEmailAndStatus(String employerEmail, String status) {
        Employer employer = iEmployerService.findByEmail(employerEmail);
        if (employer == null) {
            return 0;
        }

        // ✅ Convert String to JobPost.ApprovalStatus enum
        JobPost.ApprovalStatus approvalStatus;
        try {
            approvalStatus = JobPost.ApprovalStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid approval status: " + status + ". Valid values are: " +
                    Arrays.toString(JobPost.ApprovalStatus.values()));
            return 0;
        }

        return iJobPostRepository.countByEmployerAndApprovalStatus(employer, approvalStatus);
    }

    //phan trang JobPost
    @Override
    public Page<JobPost> findJobPostsByEmployerEmail(String email, Pageable pageable) {
        Employer employer = iEmployerService.findByEmail(email); // Tìm employer theo email
        if (employer == null) {
            return Page.empty(pageable);
        }
        return iJobPostRepository.findByEmployerOrderByCreatedAtDesc(employer, pageable); // Lọc JobPost theo employer đó
    }


}