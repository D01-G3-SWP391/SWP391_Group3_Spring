package com.example.swp391_d01_g3.repository;


import com.example.swp391_d01_g3.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAccountRepository extends JpaRepository<Account,Integer> {
    @Query("SELECT a FROM Account a WHERE a.email = :email AND a.status = 'active'")
    Account findByEmail(@Param("email") String email);

    @Query("SELECT a FROM Account a WHERE a.email = :email AND a.status = 'inactive'")
    Account findByEmailBan(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.phone = :phone AND a.status = 'active'")
    boolean existsByPhone(@Param("phone") String phone);

    List<Account> findByRole(Account.Role role);
    List<Account> findAll();
}