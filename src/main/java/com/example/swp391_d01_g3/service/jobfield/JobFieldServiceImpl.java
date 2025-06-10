package com.example.swp391_d01_g3.service.jobfield;

import com.example.swp391_d01_g3.model.JobField;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobFieldServiceImpl implements IJobfieldService {

    @Autowired
    private IJobFieldRepository jobFieldRepository;

    @Override
    public List<JobField> findAll() {
        return jobFieldRepository.findAll();
    }

    @Override
    public Optional<JobField> findById(Integer id) {
        return jobFieldRepository.findById(id);
    }


}
