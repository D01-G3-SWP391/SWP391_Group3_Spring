package com.example.swp391_d01_g3.service.security;


import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service()
public class AccountServiceImpl implements AccountService {
    @Autowired
    IAccountRepository accountRepository;

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Account findByRole(Account.Role role) {
        return accountRepository.findByRole(role);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }
}
