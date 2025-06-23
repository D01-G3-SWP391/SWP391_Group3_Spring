package com.example.swp391_d01_g3.util;

import org.springframework.stereotype.Component;

@Component
public class ThymeleafUtils {
    
    /**
     * Format lương cho hiển thị trong Thymeleaf templates
     */
    public String formatSalary(Double salary) {
        return SalaryFormatter.formatSalary(salary);
    }
    
    /**
     * Format lương ngắn gọn cho hiển thị trong Thymeleaf templates
     */
    public String formatSalaryShort(Double salary) {
        return SalaryFormatter.formatSalaryShort(salary);
    }
    
    /**
     * Format lương theo currency Việt Nam
     */
    public String formatSalaryCurrency(Double salary) {
        return SalaryFormatter.formatSalaryCurrency(salary);
    }
    
    /**
     * Format lương range
     */
    public String formatSalaryRange(Double minSalary, Double maxSalary) {
        return SalaryFormatter.formatSalaryRange(minSalary, maxSalary);
    }
} 