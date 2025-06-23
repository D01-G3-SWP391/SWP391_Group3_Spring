package com.example.swp391_d01_g3.repository;


import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmployerRepository extends JpaRepository<Employer, Integer> {
    Employer findByAccount_UserId(Integer userId);
    
    // 🆕 New method for chat system to handle duplicates  
    @Query("SELECT e FROM Employer e WHERE e.account.userId = :userId ORDER BY e.employerId ASC")
    List<Employer> findByAccount_UserIdForChat(@Param("userId") Integer userId, Pageable pageable);
    
    // Helper method for chat system
    default Employer findFirstByAccount_UserId(Integer userId) {
        List<Employer> results = findByAccount_UserIdForChat(userId, Pageable.ofSize(1));
        return results.isEmpty() ? null : results.get(0);
    }

    Employer findByAccount(Account account);
    Employer findByAccount_Email(String email);
}
