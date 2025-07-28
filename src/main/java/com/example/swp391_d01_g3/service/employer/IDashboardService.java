package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.dto.EmployerDashboardDTO;

public interface IDashboardService {
    EmployerDashboardDTO getEmployerDashboard(Integer employerId);
} 