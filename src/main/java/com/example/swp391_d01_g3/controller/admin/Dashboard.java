package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.admin.IAdminEmployerService;
import com.example.swp391_d01_g3.service.admin.IAdminStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Admin")
public class Dashboard {

    @Autowired
    private IAdminStudentService adminStudentService;
    @Autowired
    private IAdminEmployerService adminEmployerService;
    private static final Logger logger = LoggerFactory.getLogger(Dashboard.class);

    @GetMapping("")
    public String showDashboard() {
        return "admin/dashboardPage";
    }

    @GetMapping("/ListStudent")
    public String showListStudent(Model model) {
        try {
            Iterable<Account> studentList = adminStudentService.getStudents();
            model.addAttribute("studentList", studentList);
            return "admin/viewListStudent";
        } catch (Exception e) {
            logger.error("Error loading student list: ", e);
            model.addAttribute("error", "Error loading student list: " + e.getMessage());
            return "admin/error";
        }
    }

    @GetMapping("/ListEmployer")
    public String showListEmployer(Model model) {
        try {
            Iterable<Account> employerList = adminEmployerService.getEmployers();
            model.addAttribute("employerList", employerList);
            return "admin/viewListEmployer";
        } catch (Exception e) {
            logger.error("Error loading employer list: ", e);
            model.addAttribute("error", "Error loading employer list: " + e.getMessage());
            return "admin/error";
        }
    }

    // Student Ban/Unban Methods
    @PostMapping("/banStudent/{id}")
    public String banStudent(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to ban student with ID: {}", userId);
            adminStudentService.banStudent(userId);
            redirectAttributes.addFlashAttribute("success", "Student banned successfully");
            return "redirect:/Admin/ListStudent";
        } catch (Exception e) {
            logger.error("Error banning student: ", e);
            redirectAttributes.addFlashAttribute("error", "Error banning student: " + e.getMessage());
            return "redirect:/Admin/ListStudent";
        }
    }

    @PostMapping("/unbanStudent/{id}")
    public String unbanStudent(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to unban student with ID: {}", userId);
            adminStudentService.unbanStudent(userId);
            redirectAttributes.addFlashAttribute("success", "Student unbanned successfully");
            return "redirect:/Admin/ListStudent";
        } catch (Exception e) {
            logger.error("Error unbanning student: ", e);
            redirectAttributes.addFlashAttribute("error", "Error unbanning student: " + e.getMessage());
            return "redirect:/Admin/ListStudent";
        }
    }

    // Employer Ban/Unban Methods
    @PostMapping("/banEmployer/{id}")
    public String banEmployer(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to ban employer with ID: {}", userId);
            adminEmployerService.banEmployer(userId);
            redirectAttributes.addFlashAttribute("success", "Employer banned successfully");
            return "redirect:/Admin/ListEmployer";
        } catch (Exception e) {
            logger.error("Error banning employer: ", e);
            redirectAttributes.addFlashAttribute("error", "Error banning employer: " + e.getMessage());
            return "redirect:/Admin/ListEmployer";
        }
    }

    @PostMapping("/unbanEmployer/{id}")
    public String unbanEmployer(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to unban employer with ID: {}", userId);
            adminEmployerService.unbanEmployer(userId);
            redirectAttributes.addFlashAttribute("success", "Employer unbanned successfully");
            return "redirect:/Admin/ListEmployer";
        } catch (Exception e) {
            logger.error("Error unbanning employer: ", e);
            redirectAttributes.addFlashAttribute("error", "Error unbanning employer: " + e.getMessage());
            return "redirect:/Admin/ListEmployer";
        }
    }
    @GetMapping("/employer/{id}")
    public String viewEmployerDetails(@PathVariable("id") Integer userId, Model model) {
        try {
            logger.info("Viewing employer details for ID: {}", userId);

            // SỬA: Sử dụng getEmployerDetailsById() để lấy object Employer
            Employer employer = adminEmployerService.getEmployerDetailsById(userId);
            if (employer != null) {
                model.addAttribute("employer", employer);
                return "admin/viewEmployerDetails";
            } else {
                logger.error("Employer details not found for user ID: {}", userId);
                model.addAttribute("error", "Employer details not found. This user may not have completed employer profile.");
                return "redirect:/Admin/ListEmployer";
            }
        } catch (Exception e) {
            logger.error("Error loading employer details for ID: {}", userId, e);
            model.addAttribute("error", "Error loading employer details: " + e.getMessage());
            return "redirect:/Admin/ListEmployer";
        }
    }


    // View Student Details
    @GetMapping("/student/{id}")
    public String viewStudentDetails(@PathVariable("id") Integer userId, Model model) {
        try {
            logger.info("Viewing student details for ID: {}", userId);

            // Lấy thông tin Student với relationship Account
            Student student = adminStudentService.getStudentDetailsById(userId);
            if (student != null) {
                model.addAttribute("student", student);
                return "admin/viewStudentDetails";
            } else {
                logger.error("Student details not found for user ID: {}", userId);
                model.addAttribute("error", "Student details not found. This user may not have completed student profile.");
                return "redirect:/Admin/ListStudent";
            }
        } catch (Exception e) {
            logger.error("Error loading student details: ", e);
            model.addAttribute("error", "Error loading student details: " + e.getMessage());
            return "redirect:/Admin/ListStudent";
        }
    }

}
