package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "reporter_email", nullable = false)
    private String reporterEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "reporter_type", nullable = false)
    private ReporterType reporterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Column(name = "reported_entity_id")
    private Integer reportedEntityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reported_entity_type")
    private ReportedEntityType reportedEntityType;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status = ReportStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.MEDIUM;

    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public enum ReporterType {
        EMPLOYER, STUDENT
    }

    public enum ReportType {
        INAPPROPRIATE_CONTENT,
        SPAM,
        FAKE_JOB_POSTING,
        HARASSMENT,
        DISCRIMINATION,
        FRAUD,
        TECHNICAL_ISSUE,
        OTHER
    }

    public enum ReportedEntityType {
        JOB_POST,
        USER_PROFILE,
        APPLICATION,
        SYSTEM
    }

    public enum ReportStatus {
        PENDING,
        UNDER_REVIEW,
        RESOLVED,
        REJECTED,
        CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    // Constructors
    public Report() {
    }

    public Report(String reporterEmail, ReporterType reporterType, ReportType reportType,
                  String title, String description) {
        this.reporterEmail = reporterEmail;
        this.reporterType = reporterType;
        this.reportType = reportType;
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public ReporterType getReporterType() {
        return reporterType;
    }

    public void setReporterType(ReporterType reporterType) {
        this.reporterType = reporterType;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Integer getReportedEntityId() {
        return reportedEntityId;
    }

    public void setReportedEntityId(Integer reportedEntityId) {
        this.reportedEntityId = reportedEntityId;
    }

    public ReportedEntityType getReportedEntityType() {
        return reportedEntityType;
    }

    public void setReportedEntityType(ReportedEntityType reportedEntityType) {
        this.reportedEntityType = reportedEntityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
