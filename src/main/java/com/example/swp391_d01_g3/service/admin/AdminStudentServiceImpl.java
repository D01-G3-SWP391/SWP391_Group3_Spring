package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.IAdminRepository;
import com.example.swp391_d01_g3.repository.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminStudentServiceImpl implements IAdminStudentService {

    @Autowired
    private IAdminRepository iAdminRepository;

    @Autowired
    private IStudentRepository studentRepository;

    @Override
    public List<Account> getStudents() {
        return iAdminRepository.getAllStudent();
    }

    @Override
    public void banStudent(Integer userId) {
        try {
            Optional<Account> optionalUser = iAdminRepository.findById(userId);
            if (optionalUser.isPresent()) {
                Account user = optionalUser.get();
                user.setStatus(Account.Status.inactive);
                iAdminRepository.save(user);
            } else {
                throw new RuntimeException("Student not found with ID: " + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error banning student: " + e.getMessage());
        }
    }

    @Override
    public void unbanStudent(Integer userId) {
        try {
            Optional<Account> optionalUser = iAdminRepository.findById(userId);
            if (optionalUser.isPresent()) {
                Account user = optionalUser.get();
                user.setStatus(Account.Status.active);
                iAdminRepository.save(user);
            } else {
                throw new RuntimeException("Student not found with ID: " + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error unbanning student: " + e.getMessage());
        }
    }

    @Override
    public Account getStudentById(Integer userId) {
        try {
            Optional<Account> optionalUser = iAdminRepository.findById(userId);
            if (optionalUser.isPresent() && optionalUser.get().getRole() == Account.Role.student) {
                return optionalUser.get();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error getting student by ID: " + e.getMessage());
        }
    }

    @Override
    public Student getStudentDetailsById(Integer userId) {
        try {
            Student student = studentRepository.findByAccount_UserId(userId);
            return student;
        } catch (Exception e) {
            throw new RuntimeException("Error getting student details by ID: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> getStudentsWithPagination(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return iAdminRepository.getAllStudentWithPagination(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error getting students with pagination: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> searchStudents(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return iAdminRepository.searchStudents(keyword, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error searching students: " + e.getMessage());
        }
    }

    // THÊM: Filter by status methods
    @Override
    public Page<Account> findByStatus(String status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.findStudentsByStatus(accountStatus, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error finding students by status: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> searchByKeywordAndStatus(String keyword, String status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.searchStudentsByKeywordAndStatus(keyword, accountStatus, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error searching students by keyword and status: " + e.getMessage());
        }
    }

    // THÊM: Count methods
    @Override
    public long countAllStudents() {
        try {
            return iAdminRepository.countByRole(Account.Role.student);
        } catch (Exception e) {
            throw new RuntimeException("Error counting students: " + e.getMessage());
        }
    }

    @Override
    public long countStudentsByStatus(String status) {
        try {
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.countByRoleAndStatus(Account.Role.student, accountStatus);
        } catch (Exception e) {
            throw new RuntimeException("Error counting students by status: " + e.getMessage());
        }
    }
}
