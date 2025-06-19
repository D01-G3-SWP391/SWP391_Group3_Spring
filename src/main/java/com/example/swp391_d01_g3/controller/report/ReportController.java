package com.example.swp391_d01_g3.controller.report;

import com.example.swp391_d01_g3.dto.ReportDTO;
import com.example.swp391_d01_g3.model.Report;
import com.example.swp391_d01_g3.service.report.IReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Report")
public class ReportController {
    @Autowired
    private IReportService reportService;

    // Hiển thị form tạo báo cáo
    @GetMapping("/CreateReport")
    public String showCreateReportForm(Model model, Authentication authentication) {
        model.addAttribute("reportDTO", new ReportDTO());
        model.addAttribute("reportTypes", Report.ReportType.values());
        model.addAttribute("entityTypes", Report.ReportedEntityType.values());
        String userRole = "STUDENT"; // Mặc định
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_employer"))) {
            userRole = "EMPLOYER";
        }
        model.addAttribute("userRole", userRole);
        return "report/createReport";
    }

    // Xử lý tạo báo cáo
    @PostMapping("/CreateReport")
    public String createReport(@Valid @ModelAttribute("reportDTO") ReportDTO reportDTO,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // ✅ QUAN TRỌNG: Luôn add các attributes cần thiết vào model
        model.addAttribute("reportTypes", Report.ReportType.values());
        model.addAttribute("entityTypes", Report.ReportedEntityType.values());
        String userRole = "STUDENT"; // Mặc định
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_employer"))) {
            userRole = "EMPLOYER";
        }
        model.addAttribute("userRole", userRole);

        // ✅ Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            // reportDTO đã được bind tự động, chỉ cần return view
            return "report/createReport";
        }

        try {
            String userEmail = authentication.getName();

            // Tạo Entity từ DTO
            Report report = new Report();
            report.setReportType(reportDTO.getReportType());
            report.setReportedEntityType(reportDTO.getReportedEntityType());
            report.setReportedEntityId(reportDTO.getReportedEntityId());
            report.setTitle(reportDTO.getTitle());
            report.setDescription(reportDTO.getDescription());
            report.setReporterEmail(userEmail);

            // Xác định loại người báo cáo
            if (userRole.equals("EMPLOYER")) {
                report.setReporterType(Report.ReporterType.EMPLOYER);
            } else {
                report.setReporterType(Report.ReporterType.STUDENT);
            }

            reportService.createReport(report);
            redirectAttributes.addFlashAttribute("successMessage", "Báo cáo đã được gửi thành công!");

            return "redirect:/Report/ShowReport";

        } catch (Exception e) {
            // ✅ Khi có exception, đảm bảo model có đầy đủ attributes
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi báo cáo: " + e.getMessage());
            return "report/createReport";
        }
    }


    // Xem báo cáo của người dùng
    @GetMapping("/ShowReport")
    public String viewMyReports(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model,
                                Authentication authentication) {

        String userEmail = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<Report> reports = reportService.getReportsByReporter(userEmail, pageable);
        String userRole = "STUDENT"; // Mặc định
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_employer"))) {
            userRole = "EMPLOYER";
        }

        model.addAttribute("reports", reports);
        model.addAttribute("statuses", Report.ReportStatus.values());
        model.addAttribute("userRole", userRole);

        if ("EMPLOYER".equals(userRole)) {
            return "report/myReportEmployer"; // view riêng cho employer
        } else {
            return "report/myReportStudent"; // view riêng cho student
        }
    }



    // Xem chi tiết báo cáo
    @GetMapping("/Detail/{reportId}")
    public String viewReportDetail(@PathVariable Integer reportId,
                                   Model model,
                                   Authentication authentication) {

        Report report = reportService.getReportById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Kiểm tra quyền truy cập
        String userEmail = authentication.getName();
        if (!report.getReporterEmail().equals(userEmail)) {
            return "redirect:/Report/ShowReport";
        }
        String userRole = "STUDENT"; // Mặc định
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_employer"))) {
            userRole = "EMPLOYER";
        }

        model.addAttribute("report", report);
        model.addAttribute("userRole", userRole);
        return "report/reportDetail";
    }
    @PostMapping("/Delete/{reportId}")
    public String deleteMyReport(@PathVariable Integer reportId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            String userEmail = authentication.getName();

            // Kiểm tra báo cáo có tồn tại và thuộc về user không
            Report report = reportService.getReportById(reportId)
                    .orElseThrow(() -> new RuntimeException("Báo cáo không tồn tại"));

            if (!report.getReporterEmail().equals(userEmail)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xóa báo cáo này!");
                return "redirect:/Report/ShowReport";
            }

            // Chỉ cho phép xóa báo cáo chưa được xử lý
            if (report.getStatus() != Report.ReportStatus.PENDING) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa báo cáo đã được xử lý!");
                return "redirect:/Report/ShowReport";
            }

            reportService.deleteReportById(reportId, userEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Báo cáo đã được xóa thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa báo cáo: " + e.getMessage());
        }

        return "redirect:/Report/ShowReport";
    }
}
