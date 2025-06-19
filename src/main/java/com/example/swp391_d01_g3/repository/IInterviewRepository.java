package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface IInterviewRepository extends JpaRepository<Interview, Integer> {
    // Có thể bổ sung các phương thức custom nếu cần
} 