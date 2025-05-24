package com.example.swp391_d01_g3.repository;


import com.example.swp391_d01_g3.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByEmail (String email);
    Account findByRole (Account.Role role);
    List<Account> findAll();

}