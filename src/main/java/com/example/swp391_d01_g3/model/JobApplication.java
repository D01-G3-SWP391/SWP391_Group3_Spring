package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Job_application")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobApplicationId;

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    @Column(name = "application_status", length = 50)
    private String applicationStatus;

    @Column(name = "cv_url", length = 255)
    private String cvUrl;

    public JobApplication() {
    }
} 