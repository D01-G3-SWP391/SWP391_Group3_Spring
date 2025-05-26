package com.example.swp391_d01_g3.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Admin")
public class Dashboard {

    @GetMapping("")
    public String showDashboard(){
        return "admin/dashboardPage";
    }
}
