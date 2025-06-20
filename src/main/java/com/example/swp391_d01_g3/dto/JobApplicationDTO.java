package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.*;

public class JobApplicationDTO {
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2-100 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ và tên chỉ được chứa chữ cái và khoảng trắng")
    private String fullname;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 255, message = "Email không được quá 255 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng")
    @Size(max = 15, message = "Số điện thoại không được quá 15 ký tự")
    private String phoneNumber;

    @Size(max = 200, message = "Tên trường đại học không được quá 200 ký tự")
    private String university;

    @NotBlank(message = "Nội dung ứng tuyển không được để trống")
    @Size(min = 10, max = 1000, message = "Nội dung phải từ 10-1000 ký tự")
    private String description;

    private String cv_url;

    @NotNull(message = "Student ID không được để trống")
    @Min(value = 1, message = "Student ID phải là số dương")
    private Long studentId;

    @NotNull(message = "Job Post ID không được để trống")
    @Min(value = 1, message = "Job Post ID phải là số dương")
    private Integer jobPostId;

    public JobApplicationDTO() {
    }

    public JobApplicationDTO(String fullname, String email, String phoneNumber, String university, String description, String cv_url, Long studentId, Integer jobPostId) {
        this.fullname = fullname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.university = university;
        this.description = description;
        this.cv_url = cv_url;
        this.studentId = studentId;
        this.jobPostId = jobPostId;
    }

    // Getters and Setters
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCv_url() { return cv_url; }
    public void setCv_url(String cv_url) { this.cv_url = cv_url; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Integer getJobPostId() { return jobPostId; }
    public void setJobPostId(Integer jobPostId) { this.jobPostId = jobPostId; }
}
