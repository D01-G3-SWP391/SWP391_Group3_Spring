package com.example.swp391_d01_g3.controller.student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Student")
public class StudentDashboard {

    @GetMapping("")
    public String showStudentDashboard(){
        return "student/dashboardStudent";
    }
}
