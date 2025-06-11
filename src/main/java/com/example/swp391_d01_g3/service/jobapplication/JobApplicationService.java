package com.example.swp391_d01_g3.service.jobapplication;

import com.example.swp391_d01_g3.model.JobApplication;
import com.example.swp391_d01_g3.repository.IJobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
