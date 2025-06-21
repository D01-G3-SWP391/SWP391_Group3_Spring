package com.example.swp391_d01_g3.service.report;

import com.example.swp391_d01_g3.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IReportService {
    Report createReport(Report report);
    Page<Report> getReportsByReporter(String reporterEmail, Pageable pageable);
    Page<Report> getAllReports(Pageable pageable);
    Page<Report> getReportsByStatus(Report.ReportStatus status, Pageable pageable);
    Page<Report> searchReports(String keyword, Pageable pageable);
    Optional<Report> getReportById(Integer reportId);
    Report updateReportStatus(Integer reportId, Report.ReportStatus status, String adminResponse);
    Map<Report.ReportStatus, Long> getReportStatistics();
    Page<Report> getDeletableReports(Pageable pageable);

    boolean deleteReportById(Integer reportId, String adminEmail);
    int deleteBulkReports(List<Integer> reportIds, String adminEmail);
    int deleteOldResolvedReports(int daysOld, String adminEmail);
    boolean canDeleteReport(Integer reportId);
    long countDeletableReports();

}
