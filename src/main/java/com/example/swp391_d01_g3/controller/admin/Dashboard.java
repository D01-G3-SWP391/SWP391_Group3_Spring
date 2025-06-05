package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.service.admin.IAdminEmployerService;
import com.example.swp391_d01_g3.service.admin.IAdminStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Admin")
public class Dashboard {

    @Autowired
    private IAdminStudentService adminStudentService;
    @Autowired
    private IAdminEmployerService adminEmployerService;

    @GetMapping("")
    public String showDashboard() {
        return "admin/dashboardPage";
    }

    @GetMapping("/ListStudent")
    public String showListStudent(Model model) {
        Iterable<Account> studentList = adminStudentService.getStudents();
        model.addAttribute("studentList", studentList);
        return "admin/viewListStudent";
    }

    @GetMapping("/ListEmployer")
    public String showListEmployer(Model model) {
        Iterable<Account> employerList = adminEmployerService.getEmployers();
        model.addAttribute("employerList", employerList);
        return "admin/viewListEmployer";
    }
}
