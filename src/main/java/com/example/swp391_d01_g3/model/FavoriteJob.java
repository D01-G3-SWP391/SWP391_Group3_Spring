package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_jobs")
public class FavoriteJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "job_post_id", nullable = false)
    private Integer jobPostId;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public FavoriteJob() {}

    public FavoriteJob(Integer studentId, Integer jobPostId) {
        this.studentId = studentId;
        this.jobPostId = jobPostId;
    }

    public FavoriteJob(Integer favoriteId, Integer studentId, Integer jobPostId, LocalDateTime createdAt) {
        this.favoriteId = favoriteId;
        this.studentId = studentId;
        this.jobPostId = jobPostId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Integer favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 