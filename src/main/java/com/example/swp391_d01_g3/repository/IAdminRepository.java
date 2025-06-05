package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAdminRepository extends JpaRepository<Account, Integer> {

    @Query("select student from Account student where student.role = student")
    List<Account> getAllStudent();

    @Query("select employer from Account employer where employer.role = employer")
    List<Account> getAllEmployer();
}
