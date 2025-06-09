package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAdminRepository extends JpaRepository<Account, Integer> {

    @Query("SELECT a FROM Account a WHERE a.role = 'student'")
    List<Account> getAllStudent();

    @Query("SELECT a FROM Account a WHERE a.role = 'employer'")
    List<Account> getAllEmployer();

    @Query("SELECT a FROM Account a WHERE a.status = 'active'")
    List<Account> getAllActiveAccounts();

    @Query("SELECT a FROM Account a WHERE a.status = 'inactive'")
    List<Account> getAllInactiveAccounts();
}

