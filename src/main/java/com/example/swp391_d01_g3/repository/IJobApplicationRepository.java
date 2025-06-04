package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IJobApplicationRepository extends JpaRepository<JobApplication,Long> {

}
