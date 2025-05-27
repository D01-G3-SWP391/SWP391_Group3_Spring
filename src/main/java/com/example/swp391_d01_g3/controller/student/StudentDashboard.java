package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.security.AccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/Student")
public class StudentDashboard {

    @Autowired
    private AccountService accountService;

    @Autowired
    private IStudentService studentService;

    @GetMapping("")
    public String showStudentDashboard(){
        return "student/dashboardStudent";
    }
    @GetMapping("/Profile")
    public String showStudentProfile(Model model, Principal principal){
        if (principal != null) {
            String email = principal.getName();
            Account studentAccount = accountService.findByEmail(email);
            model.addAttribute("account", studentAccount);
            if (studentAccount != null) {
                Student studentDetails = studentService.findByAccountUserId(studentAccount.getUserId());
                model.addAttribute("studentDetails", studentDetails);
            }
            System.out.println(email);

        }
        return "student/profileStudent";
    }
}
