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
 * Enhanced với validation logic chuyển từ frontend về backend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanRequestDTO {
    
    /**
     * ID của user bị ban
     */
    @NotNull(message = "User ID không được để trống")
    @Positive(message = "User ID phải là số dương")
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
     * Note: Validation được xử lý trong custom logic, không dùng annotations
     */
    private Integer banDurationDays;
    
    /**
     * Enhanced validation logic chuyển từ frontend về backend
     * ✅ FIXED: Loại bỏ xung đột với Jakarta annotations
     */
    public boolean isValid() {
        // Nếu ban permanent thì không cần duration days
        if (banDurationType == BanRecord.BanDurationType.PERMANENT) {
            return banDurationDays == null || banDurationDays == 0; // ✅ Cho phép null hoặc 0
        }
        
        // Nếu ban temporary thì phải có duration days và > 0
        if (banDurationType == BanRecord.BanDurationType.TEMPORARY) {
            return banDurationDays != null && banDurationDays > 0 && banDurationDays <= 365;
        }
        
        return false;
    }
    
    /**
     * Validation thông tin user (chuyển từ frontend về backend)
     */
    public boolean isUserIdValid() {
        return userId != null && userId > 0;
    }
    
    /**
     * Validation mô tả ban (chuyển từ frontend về backend)
     */
    public boolean isDescriptionValid() {
        return banDescription == null || banDescription.length() <= 1000;
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
    
    /**
     * Lấy thông báo validation lỗi (chuyển từ frontend về backend)
     */
    public String getValidationErrorMessage() {
        if (!isUserIdValid()) {
            return "User ID không hợp lệ";
        }
        if (banReason == null) {
            return "Vui lòng chọn lý do ban";
        }
        if (banDurationType == null) {
            return "Vui lòng chọn loại thời gian ban";
        }
        if (!isDescriptionValid()) {
            return "Mô tả quá dài (tối đa 1000 ký tự)";
        }
        if (!isValid()) {
            if (banDurationType == BanRecord.BanDurationType.TEMPORARY) {
                return "Ban tạm thời phải có số ngày từ 1-365";
            } else {
                return "Ban vĩnh viễn phải có số ngày = 0 hoặc để trống";
            }
        }
        return null;
    }
} 