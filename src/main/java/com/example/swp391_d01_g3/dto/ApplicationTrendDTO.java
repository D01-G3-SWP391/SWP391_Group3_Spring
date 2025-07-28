package com.example.swp391_d01_g3.dto;

import java.time.LocalDate;

public class ApplicationTrendDTO {
    private LocalDate date;
    private Long applicationCount;
    
    public ApplicationTrendDTO() {}
    
    public ApplicationTrendDTO(LocalDate date, Long applicationCount) {
        this.date = date;
        this.applicationCount = applicationCount;
    }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public Long getApplicationCount() { return applicationCount; }
    public void setApplicationCount(Long applicationCount) { this.applicationCount = applicationCount; }
} 