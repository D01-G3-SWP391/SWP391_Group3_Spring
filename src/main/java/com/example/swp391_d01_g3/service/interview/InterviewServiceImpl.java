package com.example.swp391_d01_g3.service.interview;

import com.example.swp391_d01_g3.model.Interview;
import com.example.swp391_d01_g3.repository.IInterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InterviewServiceImpl implements IInterViewService {
    @Autowired
    private IInterviewRepository interviewRepository;
    @Override
    public Interview save(Interview interview) {
        return interviewRepository.save(interview);
    }
}
