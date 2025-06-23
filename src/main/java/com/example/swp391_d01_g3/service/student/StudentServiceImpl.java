package com.example.swp391_d01_g3.service.student;

import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Student findByEmail(String email) {
        return iStudentRepository.findByAccount_Email(email);
    }

    public Optional<Student> findById(Long id) {
        return iStudentRepository.findById(id);
    }

    @Override
    public List<Student> searchStudents(String address, String university, String experience, String jobFieldName) {
        return iStudentRepository.searchStudents(address, university, experience, jobFieldName);
    }
}
