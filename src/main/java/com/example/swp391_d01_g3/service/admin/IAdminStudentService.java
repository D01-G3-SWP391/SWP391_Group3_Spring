package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Student;
import org.springframework.data.domain.Page;

import java.util.List;

public interface  IAdminStudentService {
    List<Account> getStudents();
    // Ban sinh viên (thay đổi trạng thái thành INACTIVE)
    Page<Account> getStudentsWithPagination(int page, int size);
    Page<Account> searchStudents(String keyword, int page, int size);

    void banStudent(Integer userId);

    // Unban sinh viên (thay đổi trạng thái thành ACTIVE)
    void unbanStudent(Integer userId);
    Account getStudentById(Integer userId);
    Student getStudentDetailsById(Integer userId);

}
