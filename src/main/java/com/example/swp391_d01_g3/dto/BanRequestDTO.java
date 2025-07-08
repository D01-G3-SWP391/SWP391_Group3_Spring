package com.example.swp391_d01_g3.dto;

import com.example.swp391_d01_g3.model.BanRecord;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🚫 BanRequestDTO
 * 
 * DTO để nhận dữ liệu từ form ban user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanRequestDTO {
    
    /**
     * ID của user bị ban
     */
    @NotNull(message = "User ID không được để trống")
    private Integer userId;
    
    /**
     * Lý do ban
     */
    @NotNull(message = "Lý do ban không được để trống")
    private BanRecord.BanReason banReason;
    
    /**
     * Mô tả chi tiết lý do ban (tùy chọn)
     */
    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String banDescription;
    
    /**
     * Loại thời gian ban (TEMPORARY hoặc PERMANENT)
     */
    @NotNull(message = "Loại thời gian ban không được để trống")
    private BanRecord.BanDurationType banDurationType;
    
    /**
     * Số ngày ban (chỉ có khi ban tạm thời)
     */
    @Min(value = 1, message = "Số ngày ban phải lớn hơn 0")
    @Max(value = 365, message = "Số ngày ban không được quá 365 ngày")
    private Integer banDurationDays;
    
    /**
     * Validate logic cho duration
     */
    public boolean isValid() {
        // Nếu ban permanent thì không cần duration days
        if (banDurationType == BanRecord.BanDurationType.PERMANENT) {
            return true;
        }
        
        // Nếu ban temporary thì phải có duration days và > 0
        if (banDurationType == BanRecord.BanDurationType.TEMPORARY) {
            return banDurationDays != null && banDurationDays > 0;
        }
        
        return false;
    }
    
    /**
     * Lấy text mô tả thời gian ban
     */
    public String getDurationText() {
        if (banDurationType == BanRecord.BanDurationType.PERMANENT) {
            return "Vĩnh viễn";
        } else if (banDurationDays != null) {
            return banDurationDays + " ngày";
        }
        return "Không xác định";
    }
} 