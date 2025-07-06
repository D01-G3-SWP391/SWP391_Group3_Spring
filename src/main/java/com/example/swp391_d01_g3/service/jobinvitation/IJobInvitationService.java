package com.example.swp391_d01_g3.service.jobinvitation;

import com.example.swp391_d01_g3.model.JobInvitation;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.model.JobPost;

import java.util.List;
import java.util.Optional;

public interface IJobInvitationService {
    
    // Create a new job invitation
    JobInvitation createInvitation(JobPost jobPost, Student student, Employer employer, String message);
    
    // Save job invitation
    JobInvitation save(JobInvitation jobInvitation);
    
    // Find invitation by ID
    Optional<JobInvitation> findById(Long invitationId);
    
    // Find all invitations sent by employer
    List<JobInvitation> findByEmployer(Employer employer);
    
    // Find all invitations sent by employer with relationships loaded
    List<JobInvitation> findByEmployerWithRelationships(Employer employer);
    
    // Find all invitations received by student
    List<JobInvitation> findByStudent(Student student);
    
    // Find invitations by status
    List<JobInvitation> findByStatus(JobInvitation.InvitationStatus status);
    
    // Check if invitation already exists
    boolean isInvitationExists(Employer employer, Student student, Integer jobPostId);
    
    // Update invitation status
    JobInvitation updateInvitationStatus(Long invitationId, JobInvitation.InvitationStatus status, String responseMessage);
    
    // Find invitations by employer and status
    List<JobInvitation> findByEmployerAndStatus(Employer employer, JobInvitation.InvitationStatus status);
    
    // Find invitations by student and status
    List<JobInvitation> findByStudentAndStatus(Student student, JobInvitation.InvitationStatus status);
    
    // Count pending invitations for student
    Long countPendingInvitationsByStudent(Student student);
    
    // Count sent invitations by employer
    Long countInvitationsByEmployer(Employer employer);
    
    // Delete invitation
    void deleteInvitation(Long invitationId);
} 