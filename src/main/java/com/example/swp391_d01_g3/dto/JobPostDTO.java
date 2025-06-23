package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.*;

public class JobPostDTO {

    @NotEmpty(message = "Tên công việc không được để trống.")
    private String jobTitle;

    @NotEmpty(message = "Khu vực không được để trống.")
    private String jobLocation;

    // ✅ SAU (ĐÚNG)
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương phải lớn hơn 0")
    @DecimalMax(value = "999999999.0", message = "Lương không được quá 999,999,999")
    private Double jobSalary;  // Dùng @DecimalMin/@DecimalMax cho số    private Double jobSalary;

    @NotEmpty(message = "Yêu cầu công việc không được để trống.")
    private String jobRequirements;

    @NotEmpty(message = "Mô tả công việc không được để trống.")
    private String jobDescription;

    @NotNull(message = "Lĩnh vực công việc không được để trống.")
    private Integer jobFieldId;

    @NotEmpty(message = "Loại công việc không được để trống.")
    private String jobType;

    // Getters and setters


    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public Double getJobSalary() {
        return jobSalary;
    }

    public void setJobSalary(Double jobSalary) {
        this.jobSalary = jobSalary;
    }

    public String getJobRequirements() {
        return jobRequirements;
    }

    public void setJobRequirements(String jobRequirements) {
        this.jobRequirements = jobRequirements;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Integer getJobFieldId() {
        return jobFieldId;
    }

    public void setJobFieldId(Integer jobFieldId) {
        this.jobFieldId = jobFieldId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
}
