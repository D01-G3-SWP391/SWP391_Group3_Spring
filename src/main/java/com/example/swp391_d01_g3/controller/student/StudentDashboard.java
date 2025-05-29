package com.example.swp391_d01_g3.controller.student;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import com.example.swp391_d01_g3.model.JobField;

// Import the DTO
import com.example.swp391_d01_g3.model.StudentProfileDTO;

@Controller
@RequestMapping("/Student")
public class StudentDashboard {

    @Autowired
    private IAccountService IAccountService;

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
            Account studentAccount = IAccountService.findByEmail(email);
            model.addAttribute("account", studentAccount);
            if (studentAccount != null) {
                Student studentDetails = studentService.findByAccountUserId(studentAccount.getUserId());
                model.addAttribute("studentDetails", studentDetails);
            }
            System.out.println(email);

        }
        return "student/profileStudent";
    }
    @GetMapping("/EditProfile")
    public String showEditForm(Model model, Principal principal){
        if (principal != null) {
            String email = principal.getName();
            Account studentAccount = IAccountService.findByEmail(email);
            Student studentDetails = null;
            if (studentAccount != null) {
                studentDetails = studentService.findByAccountUserId(studentAccount.getUserId());
            }
            StudentProfileDTO studentProfileDTO = new StudentProfileDTO();
            if (studentAccount != null) {
                studentProfileDTO.setFullName(studentAccount.getFullName());
                studentProfileDTO.setPhone(studentAccount.getPhone());
            }
            if (studentDetails != null) {
                studentProfileDTO.setAddress(studentDetails.getAddress());
                studentProfileDTO.setUniversity(studentDetails.getUniversity());
                studentProfileDTO.setPreferredJobAddress(studentDetails.getPreferredJobAddress());
                studentProfileDTO.setProfileDescription(studentDetails.getProfileDescription());
                studentProfileDTO.setExperience(studentDetails.getExperience());
            }
            model.addAttribute("studentProfileDTO", studentProfileDTO);
        }
        return "student/editStudentProfile";
    }

    @PostMapping("/EditProfile")
    public String saveStudentProfile(
            @Valid @ModelAttribute("studentProfileDTO") StudentProfileDTO studentProfileDTO,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "student/editStudentProfile";
        }
        if (principal == null) {
            return "redirect:/Login";
        }
        String email = principal.getName();
        Account currentAccount = IAccountService.findByEmail(email);
        Student currentStudent = studentService.findByAccountUserId(currentAccount.getUserId());
        BeanUtils.copyProperties(studentProfileDTO, currentAccount, "address", "university", "preferredJobAddress", "profileDescription", "experience");
        BeanUtils.copyProperties(studentProfileDTO, currentStudent, "fullName", "phone");
        try {
            IAccountService.save(currentAccount);
            studentService.save(currentStudent);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
        }

        return "redirect:/Student/Profile";
    }

}
