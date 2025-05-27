package com.example.swp391_d01_g3.service.student;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service()
public class StudentServiceImpl implements IStudentService {
    @Autowired
    private IAccountRepository iAccountRepository;

    @Override
    public Account save(Account account) {
        return iAccountRepository.save(account);

    }
}
