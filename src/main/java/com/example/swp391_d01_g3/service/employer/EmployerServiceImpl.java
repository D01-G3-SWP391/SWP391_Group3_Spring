package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployerServiceImpl implements IEmployerService {
    @Autowired
    private IAccountRepository iAccountRepository;
    @Autowired
    IEmployerRepository iEmployerRepository;
    @Override
    public Account saveAccount(Account account) {

        return iAccountRepository.save(account);
    }

    @Override
    public Employer saveEmployer(Employer employer) {
        return iEmployerRepository.save(employer);
    }
}
