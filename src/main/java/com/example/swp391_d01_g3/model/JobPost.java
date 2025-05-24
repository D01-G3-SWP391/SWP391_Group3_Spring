package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Job_post")
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobPostId;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @Column(name = "job_title", length = 255)
    private String jobTitle;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "job_requirement", columnDefinition = "TEXT")
    private String jobRequirement;

    @Column(name = "job_benefit", columnDefinition = "TEXT")
    private String jobBenefit;

    @Column(name = "job_address", length = 255)
    private String jobAddress;

    @Column(name = "job_salary", length = 100)
    private String jobSalary;

    @Column(name = "job_deadline")
    private LocalDateTime jobDeadline;

    @Column(name = "job_status", length = 50)
    private String jobStatus;

    public JobPost() {
    }
} 