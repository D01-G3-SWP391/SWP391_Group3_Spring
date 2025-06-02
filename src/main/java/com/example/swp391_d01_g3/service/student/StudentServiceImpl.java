package com.example.swp391_d01_g3.service.student;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import com.example.swp391_d01_g3.repository.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements IStudentService {
    @Autowired
    private IStudentRepository iStudentRepository;

    @Override
    public Student save(Student student) {
        return iStudentRepository.save(student);
    }

    @Override
    public List<Student> findAll() {
        return iStudentRepository.findAll();
    }


    @Override
    public Student findByAccountUserId(Integer userId) {
        return iStudentRepository.findByAccount_UserId(userId);
    }
}
