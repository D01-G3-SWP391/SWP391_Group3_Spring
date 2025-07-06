package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.JobInvitation;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IJobInvitationRepository extends JpaRepository<JobInvitation, Long> {
    
    // Find invitations sent by a specific employer
    List<JobInvitation> findByEmployer(Employer employer);
    
    // Find invitations by employer with all relationships loaded
    @Query("SELECT ji FROM JobInvitation ji " +
           "LEFT JOIN FETCH ji.student s " +
           "LEFT JOIN FETCH s.account sa " +
           "LEFT JOIN FETCH ji.jobPost jp " +
           "LEFT JOIN FETCH ji.employer e " +
           "WHERE ji.employer = :employer " +
           "ORDER BY ji.sentAt DESC")
    List<JobInvitation> findByEmployerWithRelationships(@Param("employer") Employer employer);
    
    // Find invitations received by a specific student
    List<JobInvitation> findByStudent(Student student);
    
    // Find invitations by status
    List<JobInvitation> findByStatus(JobInvitation.InvitationStatus status);
    
    // Check if employer has already sent invitation to student for a specific job
    @Query("SELECT ji FROM JobInvitation ji WHERE ji.employer = :employer AND ji.student = :student AND ji.jobPost.jobPostId = :jobPostId")
    Optional<JobInvitation> findByEmployerAndStudentAndJobPost(@Param("employer") Employer employer, 
                                                                @Param("student") Student student, 
                                                                @Param("jobPostId") Integer jobPostId);
    
    // Find invitations by employer and status
    @Query("SELECT ji FROM JobInvitation ji WHERE ji.employer = :employer AND ji.status = :status ORDER BY ji.sentAt DESC")
    List<JobInvitation> findByEmployerAndStatus(@Param("employer") Employer employer, 
                                               @Param("status") JobInvitation.InvitationStatus status);
    
    // Find invitations by student and status
    @Query("SELECT ji FROM JobInvitation ji WHERE ji.student = :student AND ji.status = :status ORDER BY ji.sentAt DESC")
    List<JobInvitation> findByStudentAndStatus(@Param("student") Student student, 
                                              @Param("status") JobInvitation.InvitationStatus status);
    
    // Count pending invitations for a student
    @Query("SELECT COUNT(ji) FROM JobInvitation ji WHERE ji.student = :student AND ji.status = 'PENDING'")
    Long countPendingInvitationsByStudent(@Param("student") Student student);
    
    // Count sent invitations by employer
    @Query("SELECT COUNT(ji) FROM JobInvitation ji WHERE ji.employer = :employer")
    Long countInvitationsByEmployer(@Param("employer") Employer employer);
} 