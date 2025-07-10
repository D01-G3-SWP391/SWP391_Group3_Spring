package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.dto.BanRequestDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.BanRecord;
import com.example.swp391_d01_g3.service.admin.IAdminStudentService;
import com.example.swp391_d01_g3.service.admin.IAdminEmployerService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 🚫 Admin Ban Controller - 100% Server-side rendering
 * 
 * ✅ KHÔNG SỬ DỤNG API - Chỉ form submission và redirect
 * ✅ KHÔNG CẦN AJAX - Chỉ HTML form thuần
 * ✅ KHÔNG CẦN JSON - Chỉ RedirectAttributes cho flash messages
 * ✅ KHÔNG CẦN JavaScript phức tạp - Chỉ 20 dòng JS đơn giản
 */
@Controller
@RequestMapping("/Admin")
@RequiredArgsConstructor
public class AdminBanController {

    private final IAdminStudentService adminStudentService;
    private final IAdminEmployerService adminEmployerService;
    private final IAccountService accountService;

    /**
     * ✅ Show Ban Form - 100% Server-side rendering
     * 
     * Flow: Link click → Display ban form with user info
     */
    @GetMapping("/banUser")
    public String showBanForm(
            @RequestParam String userId,
            @RequestParam String userType, 
            @RequestParam String userName,
            @RequestParam String userEmail,
            Model model) {
        
        try {
            // Security check
            if (!AuthenticationHelper.isCurrentUserAdmin()) {
                model.addAttribute("error", "Bạn cần đăng nhập với quyền admin");
                return "redirect:/Admin";
            }
            
            // Add user info to model
            model.addAttribute("userId", userId);
            model.addAttribute("userType", userType);
            model.addAttribute("userName", userName);
            model.addAttribute("userEmail", userEmail);
            
            return "admin/banForm";
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/Admin";
        }
    }

    /**
     * ✅ Process Ban Form - 100% Server-side (NO API needed)
     * 
     * Flow: Form submit → Server validation → Business logic → Redirect with message
     */
    @PostMapping("/processBan")
    public String processBan(
            @RequestParam String userId,
            @RequestParam String userType,
            @RequestParam String banReason,
            @RequestParam(required = false) String banDescription,
            @RequestParam String banDurationType,
            @RequestParam(required = false) String banDurationDays,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 1. Create BanRequestDTO (server-side data binding)
            BanRequestDTO banRequest = new BanRequestDTO();
            banRequest.setUserId(Integer.parseInt(userId));
            banRequest.setBanReason(BanRecord.BanReason.valueOf(banReason));
            banRequest.setBanDescription(banDescription);
            banRequest.setBanDurationType(BanRecord.BanDurationType.valueOf(banDurationType));
            
            if (banDurationDays != null && !banDurationDays.isEmpty()) {
                banRequest.setBanDurationDays(Integer.parseInt(banDurationDays));
            }
            
            // 2. Server-side validation (không cần client validation)
            String validationError = banRequest.getValidationErrorMessage();
            if (validationError != null) {
                redirectAttributes.addFlashAttribute("error", validationError);
                return "redirect:/Admin"; 
            }
            
            // 3. Security check (server-side only)
            if (!AuthenticationHelper.isCurrentUserAdmin()) {
                redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập với quyền admin");
                return "redirect:/Admin";
            }
            
            String adminEmail = AuthenticationHelper.getCurrentUserEmail();
            Account adminAccount = accountService.findByEmail(adminEmail);
            Integer adminId = adminAccount != null ? adminAccount.getUserId() : null;
            
            if (adminId == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin admin");
                return "redirect:/Admin";
            }
            
            // 4. Prevent self-ban
            if (banRequest.getUserId().equals(adminId)) {
                redirectAttributes.addFlashAttribute("error", "Không thể ban chính mình");
                return "redirect:/Admin";
            }
            
            // 5. Execute ban (business logic)
            if ("student".equalsIgnoreCase(userType)) {
                adminStudentService.banStudentWithReason(banRequest, adminId);
                redirectAttributes.addFlashAttribute("success", "Đã ban student thành công. Email thông báo đã được gửi.");
            } else if ("employer".equalsIgnoreCase(userType)) {
                adminEmployerService.banEmployerWithReason(banRequest, adminId);
                redirectAttributes.addFlashAttribute("success", "Đã ban employer thành công. Email thông báo đã được gửi.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Loại user không hợp lệ");
            }
            
            // 6. Redirect with flash message (no JSON response)
            if ("student".equalsIgnoreCase(userType)) {
                return "redirect:/Admin/ListStudent";
            } else {
                return "redirect:/Admin/ListEmployer";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/Admin";
        }
    }

    /**
     * ✅ Process Unban Form - 100% Server-side (NO API needed)
     * 
     * Flow: Form submit → Server validation → Business logic → Redirect with message
     */
    @PostMapping("/processUnban")
    public String processUnban(
            @RequestParam String userId,
            @RequestParam String userType,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 1. Server-side validation
            Integer userIdInt = Integer.parseInt(userId);
            if (userIdInt <= 0) {
                redirectAttributes.addFlashAttribute("error", "User ID không hợp lệ");
                return "redirect:/Admin";
            }
            
            // 2. Security check
            if (!AuthenticationHelper.isCurrentUserAdmin()) {
                redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập với quyền admin");
                return "redirect:/Admin";
            }
            
            String adminEmail = AuthenticationHelper.getCurrentUserEmail();
            Account adminAccount = accountService.findByEmail(adminEmail);
            Integer adminId = adminAccount != null ? adminAccount.getUserId() : null;
            
            if (adminId == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin admin");
                return "redirect:/Admin";
            }
            
            // 3. Execute unban
            if ("student".equalsIgnoreCase(userType)) {
                adminStudentService.unbanStudentWithNotification(userIdInt, adminId);
                redirectAttributes.addFlashAttribute("success", "Đã unban student thành công. Email thông báo đã được gửi.");
            } else if ("employer".equalsIgnoreCase(userType)) {
                adminEmployerService.unbanEmployerWithNotification(userIdInt, adminId);
                redirectAttributes.addFlashAttribute("success", "Đã unban employer thành công. Email thông báo đã được gửi.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Loại user không hợp lệ");
            }
            
            // 4. Redirect with flash message
            if ("student".equalsIgnoreCase(userType)) {
                return "redirect:/Admin/ListStudent";
            } else {
                return "redirect:/Admin/ListEmployer";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/Admin";
        }
    }
} 