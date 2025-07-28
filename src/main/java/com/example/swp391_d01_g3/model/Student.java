package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Account account;

    @Column(length = 255)
    private String address;

    @ManyToOne
    @JoinColumn(name = "job_field_id", nullable = true)
    private JobField jobField;

    @Column(length = 100)
    private String university;

    @Column(name = "preferred_job_address", length = 255)
    private String preferredJobAddress;

    @Column(name = "profile_description", columnDefinition = "TEXT")
    private String profileDescription;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<FavoriteJob> favoriteJobs = new java.util.ArrayList<>();


    public Student() {
    }

    // Getters and Setters
    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public JobField getJobField() {
        return jobField;
    }

    public void setJobField(JobField jobField) {
        this.jobField = jobField;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getPreferredJobAddress() {
        return preferredJobAddress;
    }

    public void setPreferredJobAddress(String preferredJobAddress) {
        this.preferredJobAddress = preferredJobAddress;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public java.util.List<FavoriteJob> getFavoriteJobs() {
        return favoriteJobs;
    }

    public void setFavoriteJobs(java.util.List<FavoriteJob> favoriteJobs) {
        this.favoriteJobs = favoriteJobs;
    }


}

