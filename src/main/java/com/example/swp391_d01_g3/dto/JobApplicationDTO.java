package com.example.swp391_d01_g3.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import org.springframework.validation.BindingResult;

import java.util.Set;

public class JobApplicationDTO implements Validator {
    private String fullname;
    private String email;
    private String phoneNumber;
    private String university;
    private String description;
    private String cv_url;
    private Long studentId;
    private Long jobPostId;


    public JobApplicationDTO() {
    }

    public JobApplicationDTO(String fullname, String email, String phoneNumber, String university, String description, String cv_url, Long studentId, Long jobPostId) {
        this.fullname = fullname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.university = university;
        this.description = description;
        this.cv_url = cv_url;
        this.studentId = studentId;
        this.jobPostId = jobPostId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCv_url() {
        return cv_url;
    }

    public void setCv_url(String cv_url) {
        this.cv_url = cv_url;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
        return Set.of();
    }

    public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes) {
        return Set.of();
    }

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> aClass, String s, Object o, Class<?>... classes) {
        return Set.of();
    }

    public BeanDescriptor getConstraintsForClass(Class<?> aClass) {
        return null;
    }

    public <T> T unwrap(Class<T> aClass) {
        return null;
    }

    public ExecutableValidator forExecutables() {
        return null;
    }

    public void validate(@Valid JobApplicationDTO jobApplicationDTO, BindingResult bindingResult) {
    }
}
