package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IJobApplicationRepository extends JpaRepository<JobApplication, Integer> {

    @Query("SELECT ja FROM JobApplication ja WHERE ja.student.studentId = :studentId")
    List<JobApplication> findByStudentId(@Param("studentId") Integer studentId);


}
