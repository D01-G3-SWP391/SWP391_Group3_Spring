package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword,Integer> {

    @Query("select fp from ForgotPassword fp where fp.otp = ?1 ")
    Optional<ForgotPassword> findByOtpAndAccount(Integer otpAccount);

}
