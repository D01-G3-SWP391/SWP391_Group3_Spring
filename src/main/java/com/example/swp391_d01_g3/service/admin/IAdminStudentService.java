package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAdminStudentService {
    List<Account> getStudents();

    // Pagination và search
    Page<Account> getStudentsWithPagination(int page, int size);
    Page<Account> searchStudents(String keyword, int page, int size);

    // THÊM: Filter methods
    Page<Account> findByStatus(String status, int page, int size);
    Page<Account> searchByKeywordAndStatus(String keyword, String status, int page, int size);

    // THÊM: Count methods cho badges
    long countAllStudents();
    long countStudentsByStatus(String status);

    // Ban/Unban methods
    void banStudent(Integer userId);
    void unbanStudent(Integer userId);

    // Get student info
    Account getStudentById(Integer userId);
    Student getStudentDetailsById(Integer userId);
}
