package com.example.swp391_d01_g3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🆕 Create Chat Room DTO
 * 
 * DTO để tạo chat room mới giữa student và employer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRoomDTO {
    
    private Long studentId;
    private Long employerId;
    private Long jobPostId; // Optional - context cho cuộc chat
    
    /**
     * Constructor cho chat không liên quan đến job cụ thể
     */
    public CreateChatRoomDTO(Long studentId, Long employerId) {
        this.studentId = studentId;
        this.employerId = employerId;
        this.jobPostId = null;
    }
    
    /**
     * Validate input data
     */
    public boolean isValid() {
        return studentId != null && employerId != null;
    }
    
    /**
     * Kiểm tra có context job post không
     */
    public boolean hasJobContext() {
        return jobPostId != null;
    }
} 