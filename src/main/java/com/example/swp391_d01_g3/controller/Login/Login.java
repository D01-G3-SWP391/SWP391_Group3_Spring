package com.example.swp391_d01_g3.controller.Login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Login")
public class Login {

    @GetMapping("")
    public String login(){
        return "login/loginPage";
    }
}
