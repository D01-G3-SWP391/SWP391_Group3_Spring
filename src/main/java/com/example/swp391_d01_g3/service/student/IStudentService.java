package com.example.swp391_d01_g3.service.student;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;

import java.util.List;

public interface IStudentService {
    Account save(Account account);
    List<Student> findAll();
    Student findByAccountUserId(Integer userId);
}
