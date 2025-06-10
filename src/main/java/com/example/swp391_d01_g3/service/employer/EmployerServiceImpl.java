package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployerServiceImpl implements IEmployerService {
    @Autowired
    IEmployerRepository iEmployerRepository;
    @Autowired
    IAccountRepository iAccountRepository;
    @Autowired
    IJobPostRepository iJobPostRepository;

    @Override
    public void saveEmployer(Employer employer) {
         iEmployerRepository.save(employer);
    }


    @Override
    public Employer findByUserId(Integer userId) {
        return iEmployerRepository.findByAccount_UserId(userId);
    }



    @Override
    public Employer findByAccount(Account account) {
        return iEmployerRepository.findByAccount(account);
    }

    @Override
    public Employer findByEmail(String email) {
        return iEmployerRepository.findByAccount_Email(email);
    }



    @Override
    public void updateEmployer(Employer employer) {
        iEmployerRepository.save(employer);
    }
}
