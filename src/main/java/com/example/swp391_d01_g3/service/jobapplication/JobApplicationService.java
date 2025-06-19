package com.example.swp391_d01_g3.service.jobapplication;

import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.repository.IJobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService implements IJobApplicationService {
    @Autowired
    private IJobApplicationRepository iJobApplicationRepository;

    @Override
    public List<JobApplication> findAllJobApplication() {
        return iJobApplicationRepository.findAll();
    }

    @Override
    public JobApplication save(JobApplication jobApplication) {
        return iJobApplicationRepository.save(jobApplication);
    }

    @Override
    public List<JobApplication> findByStudentId(Integer studentId) {
        return iJobApplicationRepository.findByStudentId(studentId);
    }

    @Override
    public Page<JobApplication> getApplicationsByEmployerId(Integer employerId, Pageable pageable) {
        return iJobApplicationRepository.findApplicationsByEmployerId(employerId,pageable);
    }

    @Override
    public Page<JobApplication> searchApplicationsByEmployerIdAndName(Integer employerId, String searchName, Pageable pageable) {
        return iJobApplicationRepository.findApplicationsByEmployerIdAndName(employerId,searchName,pageable);
    }

    @Override
    public void updateApplicationStatus(Integer applicationId, JobApplication.ApplicationStatus status) {
        JobApplication application = iJobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ứng tuyển"));
        application.setStatus(status);
        iJobApplicationRepository.save(application);
    }

    @Override
    public Optional<JobApplication> findById(Integer applicationId) {
        return iJobApplicationRepository.findById(applicationId);
    }
}
