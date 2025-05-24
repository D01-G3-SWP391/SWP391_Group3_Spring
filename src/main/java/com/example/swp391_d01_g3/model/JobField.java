package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Job_fields")
public class JobField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobFieldId;

    @Column(name = "job_field_name", length = 100)
    private String jobFieldName;

    public JobField() {
    }
} 