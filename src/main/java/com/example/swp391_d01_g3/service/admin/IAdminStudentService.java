package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Student;

import java.util.List;

public interface  IAdminStudentService {
    List<Account> getStudents();
    // Ban sinh viên (thay đổi trạng thái thành INACTIVE)
    void banStudent(Integer userId);

    // Unban sinh viên (thay đổi trạng thái thành ACTIVE)
    void unbanStudent(Integer userId);
    Account getStudentById(Integer userId);
    Student getStudentDetailsById(Integer userId);
}
