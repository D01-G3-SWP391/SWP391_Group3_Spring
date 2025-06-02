package com.example.swp391_d01_g3.service.jobfield;

import com.example.swp391_d01_g3.model.JobField;

import java.util.List;
import java.util.Optional;

public interface IJobfieldService {
    List<JobField> findAll();
    Optional<JobField> findById(Integer id);


}
