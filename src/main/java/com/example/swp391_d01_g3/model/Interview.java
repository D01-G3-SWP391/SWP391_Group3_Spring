package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Interview")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewId;

    @ManyToOne
    @JoinColumn(name = "job_application_id")
    private JobApplication jobApplication;

    @Column(name = "interview_date")
    private LocalDateTime interviewDate;

    @Column(name = "interview_location", length = 255)
    private String interviewLocation;

    @Column(name = "interview_status", length = 50)
    private String interviewStatus;

    @Column(name = "interview_feedback", columnDefinition = "TEXT")
    private String interviewFeedback;

    public Interview() {
    }
} 