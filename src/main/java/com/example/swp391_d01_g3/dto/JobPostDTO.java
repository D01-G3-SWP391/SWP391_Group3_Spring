package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class JobPostDTO {

    @NotEmpty(message = "Tên công việc không được để trống.")
    private String jobTitle;

    @NotEmpty(message = "Khu vực không được để trống.")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Khu vực không được chứa số, chỉ bao gồm chữ và khoảng trắng.")
    private String jobLocation;

    @Size(max = 50, message = "Lương tối đa 50 ký tự.")
    private Double jobSalary;

    @Size(max = 1000000000, message = "Yêu cầu tối đa 1000000000 ký tự.")
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
