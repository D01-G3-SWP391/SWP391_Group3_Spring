package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.JobField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IJobFieldRepository extends JpaRepository<JobField, Integer> {
}
