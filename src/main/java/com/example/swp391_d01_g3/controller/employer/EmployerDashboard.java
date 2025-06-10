package com.example.swp391_d01_g3.controller.employer;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/Employer")
public class EmployerDashboard {

    @Autowired
    private IEmployerService employerService;

    @Autowired
    private IAccountService IAccountService;

    @GetMapping("")
    public String showEmployeeDashboard() {
        return "employee/dashboardEmployee";
    }
    @GetMapping("/Profile")
    public String showProfile(Model model, Principal principal) {
        if (principal != null) {
            String currentUserEmail = principal.getName();
            Account currentAccount = IAccountService.findByEmail(currentUserEmail);

            if (currentAccount != null) {
                Employer employer = employerService.findByUserId(currentAccount.getUserId());
                model.addAttribute("currentAccount", currentAccount);
                model.addAttribute("employer", employer);
            }
        }
        return "employee/profileEmployer";
    }
//        @GetMapping("/Profile")
//        public String showProfile(Model model, Principal principal) {
//            if (principal != null) {
//                // Lấy email từ principal
//                String currentUserEmail = principal.getName();
//
//                // Tìm Employer thông qua email của người dùng
//                Employer employer = employerService.findByEmail(currentUserEmail);
//
//                if (employer != null) {
//                    model.addAttribute("employer", employer);
//                    model.addAttribute("currentAccount", employer.getAccount());  // Giả sử Employer có 1 trường Account
//                }
//            }
//            return "employee/profileEmployer";
//        }

//    @GetMapping("/ShowJobsPost")
//    public String showJobsPost(Model model){
//
//    }
}
