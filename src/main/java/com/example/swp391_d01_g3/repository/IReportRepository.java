package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Report;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportRepository extends JpaRepository<Report,Integer> {
    // Tìm báo cáo theo email người báo cáo
    Page<Report> findByReporterEmailOrderByCreatedAtDesc(String reporterEmail, Pageable pageable);

    // Tìm báo cáo theo trạng thái
    Page<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status, Pageable pageable);


    // Đếm báo cáo theo trạng thái
    long countByStatus(Report.ReportStatus status);


    // Tìm kiếm báo cáo theo từ khóa
    @Query("SELECT r FROM Report r WHERE " +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY r.createdAt DESC")
    Page<Report> searchReports(@Param("keyword") String keyword, Pageable pageable);

    // Tìm báo cáo theo entity được báo cáo
    List<Report> findByReportedEntityIdAndReportedEntityType(Integer entityId, Report.ReportedEntityType entityType);


    // Tìm báo cáo đã giải quyết hoặc đóng (có thể xóa)
    @Query("SELECT r FROM Report r WHERE r.status IN ('RESOLVED', 'CLOSED') ORDER BY r.updatedAt DESC")
    Page<Report> findDeletableReports(Pageable pageable);

    // Tìm báo cáo theo danh sách ID để xóa hàng loạt
    @Query("SELECT r FROM Report r WHERE r.reportId IN :ids")
    List<Report> findReportsByIds(@Param("ids") List<Integer> ids);

    // Xóa báo cáo theo ID với kiểm tra trạng thái
    @Modifying
    @Transactional
    @Query("DELETE FROM Report r WHERE r.reportId = :id AND r.status IN ('RESOLVED', 'CLOSED')")
    int deleteResolvedReportById(@Param("id") Integer id);

    // Xóa hàng loạt báo cáo đã giải quyết
    @Modifying
    @Transactional
    @Query("DELETE FROM Report r WHERE r.reportId IN :ids AND r.status IN ('RESOLVED', 'CLOSED')")
    int deleteBulkResolvedReports(@Param("ids") List<Integer> ids);

    // Tìm báo cáo cũ đã giải quyết (quá X ngày)
    @Query("SELECT r FROM Report r WHERE r.status IN ('RESOLVED', 'CLOSED') " +
            "AND r.updatedAt < :cutoffDate ORDER BY r.updatedAt ASC")
    Page<Report> findOldResolvedReports(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.status IN ('RESOLVED', 'CLOSED')")
    long countDeletableReports();


}
