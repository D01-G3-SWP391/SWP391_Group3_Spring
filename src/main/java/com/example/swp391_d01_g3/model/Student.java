package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Account account;

    @Column(length = 255)
    private String address;

    private Integer jobs;

    @Column(length = 100)
    private String university;

    @Column(name = "preferred_job_address", length = 255)
    private String preferredJobAddress;

    @Column(name = "profile_description", columnDefinition = "TEXT")
    private String profileDescription;

    @Column(columnDefinition = "TEXT")
    private String experience;

    public Student() {
    }
}

