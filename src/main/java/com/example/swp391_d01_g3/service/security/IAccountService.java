package com.example.swp391_d01_g3.service.security;


import com.example.swp391_d01_g3.model.Account;

import java.util.List;


public interface IAccountService {
    Account findByEmail(String email);

    Account findByRole(Account.Role role);

    List<Account> findAll();
    
    Account save(Account account);
}
