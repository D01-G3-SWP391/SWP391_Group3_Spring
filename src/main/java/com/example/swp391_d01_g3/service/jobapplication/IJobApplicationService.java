package com.example.swp391_d01_g3.service.jobapplication;

import com.example.swp391_d01_g3.model.JobApplication;

import java.util.List;

public interface IJobApplicationService {
    List<JobApplication> findAllJobApplication();
    JobApplication save(JobApplication jobApplication);
    List<JobApplication> findByStudentId(Integer studentId);
}
