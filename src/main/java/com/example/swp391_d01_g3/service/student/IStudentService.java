package com.example.swp391_d01_g3.service.student;

import com.example.swp391_d01_g3.model.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentService {
    Student save(Student student);
    List<Student> findAll();
    Student findByAccountUserId(Integer userId);
    Student findByEmail(String email);
    Optional<Student> findById(Long id);
    List<Student> searchStudents(String address, String university, String experience, String jobFieldName);
}
