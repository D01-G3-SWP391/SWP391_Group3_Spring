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
            @RequestParam(required = false) String currentPage,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
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
            
            // Add current state parameters
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("keyword", keyword);
            model.addAttribute("status", status);
            
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
            @RequestParam(required = false) String currentPage,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 1. Create BanRequestDTO (server-side data binding)
            BanRequestDTO banRequest = new BanRequestDTO();
            banRequest.setUserId(Integer.parseInt(userId));
            banRequest.setBanReason(BanRecord.BanReason.valueOf(banReason));
            banRequest.setBanDescription(banDescription);
            banRequest.setBanDurationType(BanRecord.BanDurationType.valueOf(banDurationType));
            
            // ✅ FIXED: Logic xử lý banDurationDays cho permanent ban
            System.out.println("🔍 DEBUG: Raw form data - banDurationType: " + banDurationType + ", banDurationDays: " + banDurationDays);
            
            if (banDurationType.equals("PERMANENT")) {
                banRequest.setBanDurationDays(null); // Permanent ban luôn set null
                System.out.println("🔍 DEBUG: Permanent ban detected - setting banDurationDays to null");
            } else if (banDurationDays != null && !banDurationDays.isEmpty() && !banDurationDays.equals("0")) {
                banRequest.setBanDurationDays(Integer.parseInt(banDurationDays));
                System.out.println("🔍 DEBUG: Temporary ban - setting banDurationDays to " + banDurationDays);
            } else {
                System.out.println("🔍 DEBUG: No valid duration days provided");
            }
            
            // 2. Server-side validation (không cần client validation)
            System.out.println("🔍 DEBUG: Ban request validation - userId: " + banRequest.getUserId() + 
                             ", durationType: " + banRequest.getBanDurationType() + 
                             ", durationDays: " + banRequest.getBanDurationDays() + 
                             ", isValid: " + banRequest.isValid());
            
            String validationError = banRequest.getValidationErrorMessage();
            if (validationError != null) {
                System.out.println("❌ Validation failed: " + validationError);
                redirectAttributes.addFlashAttribute("error", validationError);
                return "redirect:/Admin"; 
            }
            
            System.out.println("✅ Validation passed");
            
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
                String durationText = banRequest.getBanDurationType() == BanRecord.BanDurationType.PERMANENT ? 
                    "vĩnh viễn" : banRequest.getBanDurationDays() + " ngày";
                redirectAttributes.addFlashAttribute("success", 
                    "✅ Đã ban student thành công với lý do: " + banRequest.getBanReason().getDescription() + 
                    " (Thời gian: " + durationText + "). Email thông báo đã được gửi.");
            } else if ("employer".equalsIgnoreCase(userType)) {
                adminEmployerService.banEmployerWithReason(banRequest, adminId);
                String durationText = banRequest.getBanDurationType() == BanRecord.BanDurationType.PERMANENT ? 
                    "vĩnh viễn" : banRequest.getBanDurationDays() + " ngày";
                redirectAttributes.addFlashAttribute("success", 
                    "✅ Đã ban employer thành công với lý do: " + banRequest.getBanReason().getDescription() + 
                    " (Thời gian: " + durationText + "). Email thông báo đã được gửi.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Loại user không hợp lệ");
            }
            
            // 6. Redirect with flash message and preserve current state
            StringBuilder redirectUrl = new StringBuilder();
            if ("student".equalsIgnoreCase(userType)) {
                redirectUrl.append("/Admin/ListStudent");
            } else {
                redirectUrl.append("/Admin/ListEmployer");
            }
            
            // Add query parameters to preserve current state
            boolean hasParams = false;
            if (currentPage != null && !currentPage.isEmpty()) {
                redirectUrl.append(hasParams ? "&" : "?").append("page=").append(currentPage);
                hasParams = true;
            }
            if (keyword != null && !keyword.isEmpty()) {
                redirectUrl.append(hasParams ? "&" : "?").append("keyword=").append(keyword);
                hasParams = true;
            }
            if (status != null && !status.isEmpty()) {
                redirectUrl.append(hasParams ? "&" : "?").append("status=").append(status);
                hasParams = true;
            }
            
            return "redirect:" + redirectUrl.toString();
            
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
            @RequestParam(required = false) String currentPage,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
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
            
            // 4. Redirect with flash message and preserve current state
            StringBuilder redirectUrl = new StringBuilder();
            if ("student".equalsIgnoreCase(userType)) {
                redirectUrl.append("/Admin/ListStudent");
            } else {
                redirectUrl.append("/Admin/ListEmployer");
            }
            
            // Add query parameters to preserve current state
            boolean hasParams = false;
            if (currentPage != null && !currentPage.isEmpty()) {
                redirectUrl.append(hasParams ? "&" : "?").append("page=").append(currentPage);
                hasParams = true;
            }
            if (keyword != null && !keyword.isEmpty()) {
                redirectUrl.append(hasParams ? "&" : "?").append("keyword=").append(keyword);
                hasParams = true;
            }
            if (status != null && !status.isEmpty()) {
                redirectUrl.append(hasParams ? "&" : "?").append("status=").append(status);
                hasParams = true;
            }
            
            return "redirect:" + redirectUrl.toString();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/Admin";
        }
    }
} 