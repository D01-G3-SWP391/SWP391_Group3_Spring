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

    public JobField(Integer jobFieldId, String jobFieldName) {
        this.jobFieldId = jobFieldId;
        this.jobFieldName = jobFieldName;
    }

    // Getters and Setters
    public Integer getJobFieldId() {
        return jobFieldId;
    }

    public void setJobFieldId(Integer jobFieldId) {
        this.jobFieldId = jobFieldId;
    }

    public String getJobFieldName() {
        return jobFieldName;
    }

    public void setJobFieldName(String jobFieldName) {
        this.jobFieldName = jobFieldName;
    }
} 