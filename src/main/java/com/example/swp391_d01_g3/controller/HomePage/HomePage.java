package com.example.swp391_d01_g3.controller.HomePage;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/HomePage")
public class HomePage {

    @GetMapping("")
    public String showHomePage (){
        return "HomePage/HomePage";
    }
}
