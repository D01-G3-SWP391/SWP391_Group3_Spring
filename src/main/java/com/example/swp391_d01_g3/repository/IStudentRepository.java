package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IStudentRepository extends JpaRepository<Student,Long> {
    Student findByAccount_UserId(Integer userId);
    Student findByAccount_Email(String email);
}
