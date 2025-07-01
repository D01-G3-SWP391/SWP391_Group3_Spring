package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAdminRepository extends JpaRepository<Account, Integer> {

    @Query("SELECT a FROM Account a WHERE a.role = 'student'")
    List<Account> getAllStudent();

    @Query("SELECT a FROM Account a WHERE a.role = 'employer'")
    List<Account> getAllEmployer();

    @Query("SELECT a FROM Account a WHERE a.status = 'active'")
    List<Account> getAllActiveAccounts();

    @Query("SELECT a FROM Account a WHERE a.status = 'inactive'")
    List<Account> getAllInactiveAccounts();

    // Pagination cho Student
    @Query("SELECT a FROM Account a WHERE a.role = 'student'")
    Page<Account> getAllStudentWithPagination(Pageable pageable);

    // Search Student theo name, email, phone với pagination
    @Query("SELECT a FROM Account a WHERE a.role = 'student' AND (a.fullName LIKE %:keyword% OR a.email LIKE %:keyword% OR a.phone LIKE %:keyword%)")
    Page<Account> searchStudents(@Param("keyword") String keyword, Pageable pageable);

    // Pagination cho Employer
    @Query("SELECT a FROM Account a WHERE a.role = 'employer'")
    Page<Account> getAllEmployerWithPagination(Pageable pageable);

    // Search Employer theo name, email, phone với pagination
    @Query("SELECT a FROM Account a WHERE a.role = 'employer' AND (a.fullName LIKE %:keyword% OR a.email LIKE %:keyword% OR a.phone LIKE %:keyword%)")
    Page<Account> searchEmployers(@Param("keyword") String keyword, Pageable pageable);

    // THÊM: Tìm employers theo status với phân trang
    @Query("SELECT a FROM Account a WHERE a.role = 'employer' AND a.status = :status")
    Page<Account> findEmployersByStatus(@Param("status") Account.Status status, Pageable pageable);

    // THÊM: Tìm kiếm employers theo keyword và status với phân trang
    @Query("SELECT a FROM Account a WHERE a.role = 'employer' AND a.status = :status AND (a.fullName LIKE %:keyword% OR a.email LIKE %:keyword% OR a.phone LIKE %:keyword%)")
    Page<Account> searchEmployersByKeywordAndStatus(@Param("keyword") String keyword,
                                                    @Param("status") Account.Status status,
                                                    Pageable pageable);

    // THÊM: Tìm students theo status với phân trang
    @Query("SELECT a FROM Account a WHERE a.role = 'student' AND a.status = :status")
    Page<Account> findStudentsByStatus(@Param("status") Account.Status status, Pageable pageable);

    // THÊM: Tìm kiếm students theo keyword và status với phân trang
    @Query("SELECT a FROM Account a WHERE a.role = 'student' AND a.status = :status AND (a.fullName LIKE %:keyword% OR a.email LIKE %:keyword% OR a.phone LIKE %:keyword%)")
    Page<Account> searchStudentsByKeywordAndStatus(@Param("keyword") String keyword,
                                                   @Param("status") Account.Status status,
                                                   Pageable pageable);

    // THÊM: Count methods cho filter badges
    // Spring Data JPA naming convention methods
    long countByRole(Account.Role role);
    long countByRoleAndStatus(Account.Role role, Account.Status status);
    long countByStatus(Account.Status status);
}
