package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStudentRepository extends JpaRepository<Student,Long> {
    Student findByAccount_UserId(Integer userId);
    
    // 🆕 New method for chat system to handle duplicates
    @Query("SELECT s FROM Student s WHERE s.account.userId = :userId ORDER BY s.studentId ASC")
    List<Student> findByAccount_UserIdForChat(@Param("userId") Integer userId, Pageable pageable);
    
    // Helper method for chat system
    default Student findFirstByAccount_UserId(Integer userId) {
        List<Student> results = findByAccount_UserIdForChat(userId, Pageable.ofSize(1));
        return results.isEmpty() ? null : results.get(0);
    }
    
    Student findByAccount_Email(String email);

    /**
     * Lấy danh sách student kết hợp account và filter, chỉ lấy account.role = 'student', hỗ trợ tìm kiếm LIKE cho các trường và thêm filter ngành nghề.
     */
    @Query("SELECT s FROM Student s WHERE s.account.role = com.example.swp391_d01_g3.model.Account.Role.student " +
            "AND (:address IS NULL OR LOWER(s.preferredJobAddress) LIKE LOWER(CONCAT('%', :address, '%'))) " +
            "AND (:university IS NULL OR LOWER(s.university) LIKE LOWER(CONCAT('%', :university, '%'))) " +
            "AND (:experience IS NULL OR LOWER(s.experience) LIKE LOWER(CONCAT('%', :experience, '%'))) " +
            "AND (:jobFieldName IS NULL OR LOWER(s.jobField.jobFieldName) LIKE LOWER(CONCAT('%', :jobFieldName, '%')))")
    List<Student> searchStudents(@Param("address") String address, @Param("university") String university, @Param("experience") String experience, @Param("jobFieldName") String jobFieldName);
}
