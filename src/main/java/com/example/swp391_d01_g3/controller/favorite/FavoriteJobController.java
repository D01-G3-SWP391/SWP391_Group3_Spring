package com.example.swp391_d01_g3.controller.favorite;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.FavoriteJob;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.favorite.IFavoriteJobService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import com.example.swp391_d01_g3.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/favorites")
public class FavoriteJobController {

    @Autowired
    private IFavoriteJobService favoriteJobService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IStudentService studentService;

    /**
     * Lấy student ID từ account
     */
    private Integer getStudentIdFromAuthentication() {
        String email = AuthenticationHelper.getCurrentUserEmail();
        
        if (email == null) {
            return null;
        }
        
        Account account = accountService.findByEmail(email);
        if (account == null || account.getRole() != Account.Role.student) {
            return null;
        }
        
        // Lấy Student record từ Account.userId
        Student student = studentService.findByAccountUserId(account.getUserId());
        if (student == null) {
            return null;
        }
        
        return student.getStudentId();
    }

    /**
     * Toggle favorite status của một job post
     * POST /favorites/toggle
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@RequestParam Integer jobPostId) {
        
        Map<String, Object> response = new HashMap<>();
        Integer studentId = getStudentIdFromAuthentication();
        try {
            // Lấy student ID từ account

            
            if (studentId == null) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập với tài khoản sinh viên để sử dụng tính năng này");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Toggle favorite
            boolean isFavorited = favoriteJobService.toggleFavorite(studentId, jobPostId);
            
            response.put("success", true);
            response.put("isFavorited", isFavorited);
            response.put("message", isFavorited ? "Đã thêm vào danh sách yêu thích" : "Đã xóa khỏi danh sách yêu thích");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Xử lý lỗi duplicate key gracefully
            if (e.getMessage() != null && (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("duplicate key"))) {
                // Kiểm tra trạng thái hiện tại
                boolean currentlyFavorited = favoriteJobService.isFavorited(studentId, jobPostId);
                response.put("success", true);
                response.put("isFavorited", currentlyFavorited);
                response.put("message", currentlyFavorited ? "Đã có trong danh sách yêu thích" : "Đã xóa khỏi danh sách yêu thích");
                return ResponseEntity.ok(response);
            }
            
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    /**
     * Handle GET requests to /favorites/toggle-form (Method Not Allowed prevention)
     * GET /favorites/toggle-form
     */
//    @GetMapping("/toggle-form")
//    public String handleGetToggleFavorite(@RequestParam(required = false) Integer jobPostId,
//                                        @RequestParam(required = false) String redirectUrl,
//                                        RedirectAttributes redirectAttributes) {
//
//
//
//        String email = AuthenticationHelper.getCurrentUserEmail();
//
//        if (email == null) {
//            // Guest user
//            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để sử dụng tính năng yêu thích");
//            return "redirect:/Login";
//        }
//
//        Account account = accountService.findByEmail(email);
//        if (account == null) {
//            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để sử dụng tính năng yêu thích");
//            return "redirect:/Login";
//        }
//
//        // Check role
//        if (account.getRole() == Account.Role.admin || account.getRole() == Account.Role.employer) {
//            redirectAttributes.addFlashAttribute("error", "Tính năng yêu thích chỉ dành cho sinh viên");
//            return "redirect:" + (redirectUrl != null ? redirectUrl : "/");
//        }
//
//        if (account.getRole() != Account.Role.student) {
//            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản sinh viên để sử dụng tính năng này");
//            return "redirect:" + (redirectUrl != null ? redirectUrl : "/");
//        }
//
//        // If student but somehow accessed via GET, redirect back
//        redirectAttributes.addFlashAttribute("info", "Vui lòng sử dụng nút yêu thích để thêm/xóa công việc");
//        return "redirect:" + (redirectUrl != null ? redirectUrl : "/");
//    }

    /**
     * Toggle favorite job via form submission (redirect approach)
     * POST /favorites/toggle-form
     */
    @PostMapping("/toggle-form")
    public String toggleFavoriteForm(@RequestParam Integer jobPostId, 
                                   @RequestParam(required = false) String redirectUrl,
                                   RedirectAttributes redirectAttributes) {
        

        
        String email = AuthenticationHelper.getCurrentUserEmail();
        
        if (email == null) {
            // Guest user
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để sử dụng tính năng yêu thích");
            return "redirect:/Login";
        }
        
        Account account = accountService.findByEmail(email);
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để sử dụng tính năng yêu thích");
            return "redirect:/Login";
        }
        
        // Check role
        if (account.getRole() == Account.Role.admin || account.getRole() == Account.Role.employer) {
            redirectAttributes.addFlashAttribute("error", "Tính năng yêu thích chỉ dành cho sinh viên");
            return "redirect:" + (redirectUrl != null ? redirectUrl : "/");
        }
        
        if (account.getRole() != Account.Role.student) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với tài khoản sinh viên để sử dụng tính năng này");
            return "redirect:" + (redirectUrl != null ? redirectUrl : "/");
        }
        
        // Get student info
        Student student = studentService.findByAccountUserId(account.getUserId());
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin sinh viên");
            return "redirect:" + (redirectUrl != null ? redirectUrl : "/");
        }
        
        Integer studentId = student.getStudentId();
        
        try {
            // Toggle favorite
            boolean isFavorited = favoriteJobService.toggleFavorite(studentId, jobPostId);
            
            String message = isFavorited ? "❤️ Đã thêm việc làm vào danh sách yêu thích!" : "💔 Đã xóa việc làm khỏi danh sách yêu thích!";
            redirectAttributes.addFlashAttribute("success", message);
            
        } catch (Exception e) {
            // Xử lý lỗi duplicate key gracefully
            if (e.getMessage() != null && (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("duplicate key"))) {
                boolean currentlyFavorited = favoriteJobService.isFavorited(studentId, jobPostId);
                String message = currentlyFavorited ? "❤️ Việc làm đã có trong danh sách yêu thích!" : "💔 Đã xóa việc làm khỏi danh sách yêu thích!";
                redirectAttributes.addFlashAttribute("success", message);
            } else {
                redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            }
        }
        
        // Redirect back to the original page
        String redirectPath = "redirect:" + (redirectUrl != null ? redirectUrl : "/");

        return redirectPath;
    }

} 