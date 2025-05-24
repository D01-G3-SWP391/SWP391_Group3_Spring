package com.example.swp391_d01_g3.controller.register;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Register")
public class Register {

    @GetMapping("")
    public String showRegister (){
        return "register/registerPage";
    }
}
