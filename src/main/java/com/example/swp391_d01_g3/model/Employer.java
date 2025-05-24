package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;


@Entity
@Table(name = "Employer")
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employerId;

    @ManyToOne
    @JoinColumn(name = "jobs_field_id")
    private JobField jobField;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Account account;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "company_address", length = 255)
    private String companyAddress;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    public Employer() {
    }
} 