package com.example.swp391_d01_g3.controller.employee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Employer")
public class EmployerDashboard {

    @GetMapping("")
    public String showEmployeeDashboard(){
        return "employee/dashboardEmployee";
    }
}
