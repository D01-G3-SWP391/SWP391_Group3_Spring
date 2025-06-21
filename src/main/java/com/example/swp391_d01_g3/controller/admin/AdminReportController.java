package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.model.Report;
import com.example.swp391_d01_g3.service.report.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class AdminReportController {
    @Autowired
    private IReportService reportService;

    // Xem danh sách tất cả báo cáo
    @GetMapping("/List")
    public String viewAllReports(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "15") int size,
                                 @RequestParam(required = false) Report.ReportStatus status,
                                 @RequestParam(required = false) String search,
                                 Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Report> reports;

        if (status != null) {
            reports = reportService.getReportsByStatus(status, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            reports = reportService.searchReports(search.trim(), pageable);
        } else {
            reports = reportService.getAllReports(pageable);
        }

        Map<Report.ReportStatus, Long> statistics = reportService.getReportStatistics();

        model.addAttribute("reports", reports);
        model.addAttribute("statistics", statistics);
        model.addAttribute("statuses", Report.ReportStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchKeyword", search);

        long deletableCount = reportService.countDeletableReports();
        model.addAttribute("deletableCount", deletableCount);

        return "admin/reportList";
    }

    @GetMapping("/Deletable")
    public String viewDeletableReports(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "15") int size,
                                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Report> reports = reportService.getDeletableReports(pageable);

        model.addAttribute("reports", reports);
        model.addAttribute("deletableCount", reports.getTotalElements());

        return "admin/deletableReports";
    }

    @GetMapping("/Edit/{reportId}")
    public String editReportForm(@PathVariable Integer reportId, Model model) {
        Report report = reportService.getReportById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        model.addAttribute("report", report);
        model.addAttribute("statuses", Report.ReportStatus.values());
        model.addAttribute("priorities", Report.Priority.values());
        model.addAttribute("canDelete", reportService.canDeleteReport(reportId));

        return "admin/editReport";
    }

    @PostMapping("/Edit/{reportId}")
    public String updateReportStatus(@PathVariable Integer reportId,
                                     @RequestParam Report.ReportStatus status,
                                     @RequestParam(required = false) String adminResponse,
                                     RedirectAttributes redirectAttributes) {
        try {
            reportService.updateReportStatus(reportId, status, adminResponse);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật trạng thái báo cáo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/Admin/List";
    }

    @PostMapping("/Delete/{reportId}")
    public String deleteReport(@PathVariable Integer reportId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String adminEmail = authentication != null ? authentication.getName() : "admin";
            boolean deleted = reportService.deleteReportById(reportId, adminEmail);

            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Báo cáo đã được xóa thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa báo cáo này! Chỉ có thể xóa báo cáo đã giải quyết hoặc đóng.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/Admin/List";
    }

    // ✅ THÊM MỚI: Xóa hàng loạt báo cáo
    @PostMapping("/DeleteBulk")
    public String deleteBulkReports(@RequestParam("reportIds") List<Integer> reportIds,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String adminEmail = authentication != null ? authentication.getName() : "admin";
            int deletedCount = reportService.deleteBulkReports(reportIds, adminEmail);

            if (deletedCount > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Đã xóa thành công %d báo cáo!", deletedCount));
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không có báo cáo nào được xóa!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/Admin/Deletable";
    }

    // ✅ THÊM MỚI: Dọn dẹp báo cáo cũ đã giải quyết
    @PostMapping("/Cleanup")
    public String cleanupOldReports(@RequestParam(defaultValue = "30") int daysOld,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String adminEmail = authentication != null ? authentication.getName() : "admin";
            int deletedCount = reportService.deleteOldResolvedReports(daysOld, adminEmail);

            if (deletedCount > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Đã dọn dẹp %d báo cáo cũ (hơn %d ngày)!", deletedCount, daysOld));
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "Không có báo cáo cũ nào để dọn dẹp!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/Admin/Deletable";
    }

    // API endpoint để cập nhật nhanh trạng thái
    @PostMapping("/QuickUpdate/{reportId}")
    @ResponseBody
    public Map<String, Object> quickUpdateStatus(@PathVariable Integer reportId,
                                                 @RequestParam Report.ReportStatus status) {
        try {
            reportService.updateReportStatus(reportId, status, null);
            return Map.of("success", true, "message", "Cập nhật thành công");
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    // ✅ THÊM MỚI: API endpoint để xóa báo cáo qua AJAX
    @PostMapping("/DeleteAjax/{reportId}")
    @ResponseBody
    public Map<String, Object> deleteReportAjax(@PathVariable Integer reportId,
                                                Authentication authentication) {
        try {
            String adminEmail = authentication != null ? authentication.getName() : "admin";
            boolean deleted = reportService.deleteReportById(reportId, adminEmail);

            if (deleted) {
                return Map.of("success", true, "message", "Báo cáo đã được xóa thành công!");
            } else {
                return Map.of("success", false, "message", "Không thể xóa báo cáo này!");
            }

        } catch (Exception e) {
            return Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // Xem chi tiết báo cáo (read-only)
    @GetMapping("/Detail/{reportId}")
    public String viewReportDetail(@PathVariable Integer reportId, Model model) {
        Report report = reportService.getReportById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        model.addAttribute("report", report);
        model.addAttribute("canDelete", reportService.canDeleteReport(reportId));

        return "admin/reportDetail";
    }
}
