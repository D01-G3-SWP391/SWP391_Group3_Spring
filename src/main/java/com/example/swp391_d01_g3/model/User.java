package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 100)
    private String fullName;

    @Column(length = 255, unique = true)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Role role;

    @Column(length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status;

    public User() {

    }

    public enum Role {
        student, employer, admin
    }

    public enum Status {
        active, inactive
    }
}

