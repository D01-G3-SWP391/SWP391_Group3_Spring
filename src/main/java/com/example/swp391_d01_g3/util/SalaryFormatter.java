package com.example.swp391_d01_g3.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SalaryFormatter {
    
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("#,###");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    /**
     * Format lương theo định dạng VND với dấu phẩy ngăn cách hàng nghìn
     * Ví dụ: 15000000 -> "15,000,000 VND"
     */
    public static String formatSalary(Double salary) {
        if (salary == null) {
            return "Thỏa thuận";
        }
        
        try {
            // Format số với dấu phẩy ngăn cách hàng nghìn
            String formattedNumber = VND_FORMAT.format(salary);
            return formattedNumber + " VND";
        } catch (Exception e) {
            return "Thỏa thuận";
        }
    }
    
    /**
     * Format lương theo định dạng currency của Việt Nam
     * Ví dụ: 15000000 -> "15.000.000 ₫"
     */
    public static String formatSalaryCurrency(Double salary) {
        if (salary == null) {
            return "Thỏa thuận";
        }
        
        try {
            return CURRENCY_FORMAT.format(salary);
        } catch (Exception e) {
            return "Thỏa thuận";
        }
    }
    
    /**
     * Format lương theo định dạng ngắn gọn
     * Ví dụ: 15000000 -> "15 triệu VND"
     */
    public static String formatSalaryShort(Double salary) {
        if (salary == null) {
            return "Thỏa thuận";
        }
        
        try {
            if (salary >= 1000000000) {
                double billions = salary / 1000000000;
                return String.format("%.1f tỷ VND", billions);
            } else if (salary >= 1000000) {
                double millions = salary / 1000000;
                return String.format("%.0f triệu VND", millions);
            } else if (salary >= 1000) {
                double thousands = salary / 1000;
                return String.format("%.0f nghìn VND", thousands);
            } else {
                return VND_FORMAT.format(salary) + " VND";
            }
        } catch (Exception e) {
            return "Thỏa thuận";
        }
    }
    
    /**
     * Format lương theo định dạng range (min - max)
     * Ví dụ: 10000000, 15000000 -> "10 - 15 triệu VND"
     */
    public static String formatSalaryRange(Double minSalary, Double maxSalary) {
        if (minSalary == null && maxSalary == null) {
            return "Thỏa thuận";
        }
        
        if (minSalary == null) {
            return formatSalaryShort(maxSalary);
        }
        
        if (maxSalary == null) {
            return "Từ " + formatSalaryShort(minSalary);
        }
        
        if (minSalary.equals(maxSalary)) {
            return formatSalaryShort(minSalary);
        }
        
        return formatSalaryShort(minSalary) + " - " + formatSalaryShort(maxSalary);
    }
    
    /**
     * Parse lương từ string sang Double
     * Hỗ trợ các định dạng: "15,000,000", "15 triệu", "15.000.000"
     */
    public static Double parseSalary(String salaryStr) {
        if (salaryStr == null || salaryStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Loại bỏ các ký tự không phải số và dấu chấm, phẩy
            String cleaned = salaryStr.replaceAll("[^\\d.,]", "");
            
            // Thay thế dấu phẩy bằng dấu chấm
            cleaned = cleaned.replace(",", ".");
            
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Kiểm tra xem string có phải là định dạng lương hợp lệ không
     */
    public static boolean isValidSalaryFormat(String salaryStr) {
        if (salaryStr == null || salaryStr.trim().isEmpty()) {
            return true; // Cho phép null/empty
        }
        
        // Kiểm tra các pattern phổ biến
        String pattern = ".*\\d+.*";
        return salaryStr.matches(pattern);
    }
} 