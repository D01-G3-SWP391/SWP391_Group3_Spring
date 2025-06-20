package com.example.swp391_d01_g3.controller.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.swp391_d01_g3.service.chat.IChatService;
import com.example.swp391_d01_g3.service.security.IAccountService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Employer")
public class EmployerChatController {

    @Autowired
    private IChatService chatService;
    
    @Autowired
    private IAccountService accountService;

    @GetMapping("/chat")
    public String chatPage(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long jobPostId,
            HttpSession session,
            Model model) {
        
        // Get current employer from session
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/login";
        }

        // Add data to model for the chat page
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("selectedStudentId", studentId);
        model.addAttribute("selectedJobPostId", jobPostId);
        
        // You can add more data like chat rooms, etc.
        // List<ChatRoom> chatRooms = chatService.findChatRoomsByEmployerId(currentUserId);
        // model.addAttribute("chatRooms", chatRooms);

        return "employee/chatPage";
    }
} 