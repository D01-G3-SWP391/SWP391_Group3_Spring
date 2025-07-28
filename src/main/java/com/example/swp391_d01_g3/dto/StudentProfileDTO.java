package com.example.swp391_d01_g3.dto;

// Không cần import JobField ở đây nếu chúng ta chỉ lưu trữ ID
// import com.example.swp391_d01_g3.model.JobField;

public class StudentProfileDTO {

    // Fields from Account
    private String fullName;
    private String phone;

    // Fields from Student
    private String address;
    private String university;
    private String preferredJobAddress;
    private String profileDescription;
    private String experience;
    private String avatarUrl;
    private Integer jobFieldId;

    // Constructors
    public StudentProfileDTO() {
    }

    public StudentProfileDTO(String fullName, String phone,  String address, String university, String preferredJobAddress, String profileDescription, String experience, String avatarUrl, Integer jobFieldId) {
        this.fullName = fullName;
        this.phone = phone;

        this.address = address;
        this.university = university;
        this.preferredJobAddress = preferredJobAddress;
        this.profileDescription = profileDescription;
        this.experience = experience;
        this.avatarUrl = avatarUrl;
        this.jobFieldId = jobFieldId;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getJobFieldId() {
        return jobFieldId;
    }

    public void setJobFieldId(Integer jobFieldId) {
        this.jobFieldId = jobFieldId;
    }
} 