package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStudentRepository extends JpaRepository<Student,Long> {
    Student findByAccount_UserId(Integer userId);
    
    // 🆕 New method for chat system to handle duplicates
    @Query("SELECT s FROM Student s WHERE s.account.userId = :userId ORDER BY s.studentId ASC")
    List<Student> findByAccount_UserIdForChat(@Param("userId") Integer userId, Pageable pageable);
    
    // Helper method for chat system
    default Student findFirstByAccount_UserId(Integer userId) {
        List<Student> results = findByAccount_UserIdForChat(userId, Pageable.ofSize(1));
        return results.isEmpty() ? null : results.get(0);
    }
    
    Student findByAccount_Email(String email);
}
