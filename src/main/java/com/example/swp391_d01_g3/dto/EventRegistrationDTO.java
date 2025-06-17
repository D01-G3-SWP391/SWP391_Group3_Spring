package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EventRegistrationDTO {
    
    @NotBlank(message = "Vui lòng nhập họ và tên")
    @Size(min = 5, max = 100, message = "Họ và tên phải từ 5-100 ký tự")
    private String fullName;
    
    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Size(min = 10, max = 11, message = "Số điện thoại không hợp lệ")
    private String phone;
    
    @NotBlank(message = "Vui lòng nhập tên trường")
    private String organization; // Sẽ được lưu vào university trong Student
    
    private String notes; // Ghi chú cho event registration
    
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
    
    public String getOrganization() {
        return organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "EventRegistrationDTO{" +
                "fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", organization='" + organization + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
} 