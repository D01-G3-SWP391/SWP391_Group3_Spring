package com.example.swp391_d01_g3.service.jobinvitation;

import com.example.swp391_d01_g3.model.JobInvitation;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobInvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobInvitationServiceImpl implements IJobInvitationService {
    
    @Autowired
    private IJobInvitationRepository jobInvitationRepository;
    
    @Override
    public JobInvitation createInvitation(JobPost jobPost, Student student, Employer employer, String message) {
        // Check if invitation already exists
        if (isInvitationExists(employer, student, jobPost.getJobPostId())) {
            throw new IllegalArgumentException("Invitation already exists for this job and student");
        }
        
        JobInvitation invitation = new JobInvitation(jobPost, student, employer, message);
        return save(invitation);
    }
    
    @Override
    public JobInvitation save(JobInvitation jobInvitation) {
        return jobInvitationRepository.save(jobInvitation);
    }
    
    @Override
    public Optional<JobInvitation> findById(Long invitationId) {
        return jobInvitationRepository.findById(invitationId);
    }
    
    @Override
    public List<JobInvitation> findByEmployer(Employer employer) {
        return jobInvitationRepository.findByEmployer(employer);
    }
    
    @Override
    public List<JobInvitation> findByEmployerWithRelationships(Employer employer) {
        return jobInvitationRepository.findByEmployerWithRelationships(employer);
    }
    
    @Override
    public List<JobInvitation> findByStudent(Student student) {
        return jobInvitationRepository.findByStudent(student);
    }
    
    @Override
    public List<JobInvitation> findByStatus(JobInvitation.InvitationStatus status) {
        return jobInvitationRepository.findByStatus(status);
    }
    
    @Override
    public boolean isInvitationExists(Employer employer, Student student, Integer jobPostId) {
        Optional<JobInvitation> existingInvitation = jobInvitationRepository
                .findByEmployerAndStudentAndJobPost(employer, student, jobPostId);
        return existingInvitation.isPresent();
    }
    
    @Override
    public JobInvitation updateInvitationStatus(Long invitationId, JobInvitation.InvitationStatus status, String responseMessage) {
        Optional<JobInvitation> optionalInvitation = findById(invitationId);
        if (optionalInvitation.isPresent()) {
            JobInvitation invitation = optionalInvitation.get();
            invitation.setStatus(status);
            invitation.setResponseMessage(responseMessage);
            invitation.setRespondedAt(LocalDateTime.now());
            return save(invitation);
        }
        throw new IllegalArgumentException("Invitation not found with ID: " + invitationId);
    }
    
    @Override
    public List<JobInvitation> findByEmployerAndStatus(Employer employer, JobInvitation.InvitationStatus status) {
        return jobInvitationRepository.findByEmployerAndStatus(employer, status);
    }
    
    @Override
    public List<JobInvitation> findByStudentAndStatus(Student student, JobInvitation.InvitationStatus status) {
        return jobInvitationRepository.findByStudentAndStatus(student, status);
    }
    
    @Override
    public Long countPendingInvitationsByStudent(Student student) {
        return jobInvitationRepository.countPendingInvitationsByStudent(student);
    }
    
    @Override
    public Long countInvitationsByEmployer(Employer employer) {
        return jobInvitationRepository.countInvitationsByEmployer(employer);
    }
    
    @Override
    public void deleteInvitation(Long invitationId) {
        if (jobInvitationRepository.existsById(invitationId)) {
            jobInvitationRepository.deleteById(invitationId);
        } else {
            throw new IllegalArgumentException("Invitation not found with ID: " + invitationId);
        }
    }
} 