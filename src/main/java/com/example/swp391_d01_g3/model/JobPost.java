package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Jobs_post")
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_post_id")
    private Integer jobPostId;

    @ManyToOne
    @JoinColumn(name = "job_field_id", nullable = false)
    private JobField jobField;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @Column(name = "job_title", length = 255, nullable = false)
    private String jobTitle;

    @Column(name = "job_description", columnDefinition = "LONGTEXT", nullable = false)
    private String jobDescription;

    @Column(name = "job_salary", length = 50)
    private String jobSalary;

    @Column(name = "job_requirements", columnDefinition = "TEXT")
    private String jobRequirements;

    @Column(name = "job_location", length = 255)
    private String jobLocation;

    @Column(name = "applied_quality")
    private Integer appliedQuality;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", length = 20)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "display_status", length = 20)
    private DisplayStatus displayStatus = DisplayStatus.ACTIVE;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum JobType {
        PART_TIME,
        FULL_TIME
    }

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public enum DisplayStatus {
        ACTIVE,
        INACTIVE
    }

    public JobPost(Integer jobPostId, JobField jobField, Employer employer, String jobTitle, String jobDescription, String jobSalary, String jobRequirements, String jobLocation, Integer appliedQuality, JobType jobType, ApprovalStatus approvalStatus, DisplayStatus displayStatus, LocalDateTime createdAt) {
        this.jobPostId = jobPostId;
        this.jobField = jobField;
        this.employer = employer;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobSalary = jobSalary;
        this.jobRequirements = jobRequirements;
        this.jobLocation = jobLocation;
        this.appliedQuality = appliedQuality;
        this.jobType = jobType;
        this.approvalStatus = approvalStatus;
        this.displayStatus = displayStatus;
        this.createdAt = createdAt;
    }

    public JobPost() {
    }

    // Getter and Setter methods
    public Integer getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
    }

    public JobField getJobField() {
        return jobField;
    }

    public void setJobField(JobField jobField) {
        this.jobField = jobField;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobSalary() {
        return jobSalary;
    }

    public void setJobSalary(String jobSalary) {
        this.jobSalary = jobSalary;
    }

    public String getJobRequirements() {
        return jobRequirements;
    }

    public void setJobRequirements(String jobRequirements) {
        this.jobRequirements = jobRequirements;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public Integer getAppliedQuality() {
        return appliedQuality;
    }

    public void setAppliedQuality(Integer appliedQuality) {
        this.appliedQuality = appliedQuality;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
