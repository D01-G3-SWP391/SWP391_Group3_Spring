package com.example.swp391_d01_g3.service.report;

import com.example.swp391_d01_g3.model.Report;
import com.example.swp391_d01_g3.repository.IReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class ReportServiceImpl implements IReportService{
    @Autowired
    private IReportRepository reportRepository;

    @Override
    public Report createReport(Report report) {
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    @Override
    public Page<Report> getReportsByReporter(String reporterEmail, Pageable pageable) {
        return reportRepository.findByReporterEmailOrderByCreatedAtDesc(reporterEmail, pageable);
    }

    @Override
    public Page<Report> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    @Override
    public Page<Report> getReportsByStatus(Report.ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    public Page<Report> searchReports(String keyword, Pageable pageable) {
        return reportRepository.searchReports(keyword, pageable);
    }

    @Override
    public Optional<Report> getReportById(Integer reportId) {
        return reportRepository.findById(reportId);
    }

    @Override
    public Report updateReportStatus(Integer reportId, Report.ReportStatus status, String adminResponse) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(status);
        report.setAdminResponse(adminResponse);
        report.setUpdatedAt(LocalDateTime.now());

        if (status == Report.ReportStatus.RESOLVED || status == Report.ReportStatus.CLOSED) {
            report.setResolvedAt(LocalDateTime.now());
        }

        return reportRepository.save(report);
    }


    @Override
    public Map<Report.ReportStatus, Long> getReportStatistics() {
        Map<Report.ReportStatus, Long> stats = new HashMap<>();
        for (Report.ReportStatus status : Report.ReportStatus.values()) {
            stats.put(status, reportRepository.countByStatus(status));
        }
        return stats;
    }

    @Override
    public Page<Report> getDeletableReports(Pageable pageable) {
        return reportRepository.findDeletableReports(pageable);
    }

    @Override
    @Transactional
    public boolean deleteReportById(Integer reportId, String adminEmail) {
        try {
            // Kiểm tra báo cáo có tồn tại và có thể xóa không
            if (!canDeleteReport(reportId)) {
                System.out.println("Admin {} attempted to delete non-deletable report {}"+ adminEmail+ reportId);
                return false;
            }

            int deletedCount = reportRepository.deleteResolvedReportById(reportId);

            if (deletedCount > 0) {
                System.out.println("Admin {} successfully deleted report {}"+ adminEmail+ reportId);
                return true;
            }

            return false;
        } catch (Exception e) {
            System.out.println("Error deleting report {} by admin {}: {}"+ reportId+ adminEmail+ e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public int deleteBulkReports(List<Integer> reportIds, String adminEmail) {
        try {
            // Lọc chỉ những báo cáo có thể xóa
            List<Report> deletableReports = reportRepository.findReportsByIds(reportIds)
                    .stream()
                    .filter(report -> report.getStatus() == Report.ReportStatus.RESOLVED ||
                            report.getStatus() == Report.ReportStatus.CLOSED)
                    .toList();

            List<Integer> deletableIds = deletableReports.stream()
                    .map(Report::getReportId)
                    .toList();

            if (deletableIds.isEmpty()) {
                System.out.println("Admin {} attempted bulk delete but no deletable reports found"+ adminEmail);
                return 0;
            }

            int deletedCount = reportRepository.deleteBulkResolvedReports(deletableIds);
            System.out.println("Admin {} successfully deleted {} reports in bulk"+ adminEmail+ deletedCount);

            return deletedCount;
        } catch (Exception e) {
            System.out.println("Error in bulk delete by admin {}: {}"+ adminEmail+ e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional
    public int deleteOldResolvedReports(int daysOld, String adminEmail) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            Page<Report> oldReports = reportRepository.findOldResolvedReports(cutoffDate, Pageable.unpaged());

            List<Integer> oldReportIds = oldReports.getContent()
                    .stream()
                    .map(Report::getReportId)
                    .toList();

            if (oldReportIds.isEmpty()) {
                return 0;
            }

            int deletedCount = reportRepository.deleteBulkResolvedReports(oldReportIds);
            System.out.println("Admin {} deleted {} old resolved reports (older than {} days)"+
                    adminEmail+ deletedCount+ daysOld);

            return deletedCount;
        } catch (Exception e) {
            System.out.println("Error deleting old reports by admin {}: {}"+ adminEmail+ e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean canDeleteReport(Integer reportId) {
        return reportRepository.findById(reportId)
                .map(report -> report.getStatus() == Report.ReportStatus.RESOLVED ||
                        report.getStatus() == Report.ReportStatus.CLOSED)
                .orElse(false);
    }

    @Override
    public long countDeletableReports() {
        return reportRepository.countDeletableReports();
    }
}
