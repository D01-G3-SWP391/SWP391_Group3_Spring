package com.example.swp391_d01_g3.service.jobapplication;

import com.example.swp391_d01_g3.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IJobApplicationService {
    List<JobApplication> findAllJobApplication();
    JobApplication save(JobApplication jobApplication);
    List<JobApplication> findByStudentId(Integer studentId);
    Page<JobApplication> getApplicationsByEmployerId(Integer employerId, Pageable pageable);
    Page<JobApplication> searchApplicationsByEmployerIdAndName(Integer employerId, String searchName, Pageable pageable);
    void updateApplicationStatus(Integer applicationId, JobApplication.ApplicationStatus status);
    Optional<JobApplication> findById(Integer applicationId);
    boolean hasStudentAppliedToJob(Integer studentId, Integer jobPostId);

    List<JobApplication> findByJobPostId(Integer jobPostId);
    List<JobApplication> findByJobPostIdAndNameAndExperience(Integer jobPostId, String searchName, String searchExperience);

    Page<JobApplication> searchApplicationsByEmployerIdAndKeyword(Integer employerId, String keyword, Pageable pageable);
}
