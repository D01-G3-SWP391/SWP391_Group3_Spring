package com.example.swp391_d01_g3.dto;

import com.example.swp391_d01_g3.model.Report;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportDTO {
    @NotNull(message = "Loại báo cáo không được để trống")
    private Report.ReportType reportType;

    private Report.ReportedEntityType reportedEntityType;

//    @Min(value = 1, message = "ID đối tượng phải lớn hơn 0")
    private Integer reportedEntityId;

    @NotBlank(message = "Tiêu đề báo cáo không được để trống")
    @Size(min = 10, max = 255, message = "Tiêu đề phải từ 10 đến 255 ký tự")
    private String title;

    @NotBlank(message = "Mô tả chi tiết không được để trống")
    @Size(min = 20, max = 2000, message = "Mô tả phải từ 20 đến 2000 ký tự")
    private String description;

    // ✅ Constructor mặc định
    public ReportDTO() {}

    // ✅ Constructor từ Entity (giống cách bạn đang làm)
    public ReportDTO(Report report) {
        if (report != null) {
            this.reportType = report.getReportType();
            this.reportedEntityType = report.getReportedEntityType();
            this.reportedEntityId = report.getReportedEntityId();
            this.title = report.getTitle();
            this.description = report.getDescription();
        }
    }


    public Report.ReportType getReportType() {
        return reportType;
    }

    public void setReportType(Report.ReportType reportType) {
        this.reportType = reportType;
    }

    public Report.ReportedEntityType getReportedEntityType() {
        return reportedEntityType;
    }

    public void setReportedEntityType(Report.ReportedEntityType reportedEntityType) {
        this.reportedEntityType = reportedEntityType;
    }

    public Integer getReportedEntityId() {
        return reportedEntityId;
    }

    public void setReportedEntityId(Integer reportedEntityId) {
        this.reportedEntityId = reportedEntityId;
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
    @Override
    public String toString() {
        return "ReportDTO{" +
                "reportType=" + reportType +
                ", reportedEntityType=" + reportedEntityType +
                ", reportedEntityId=" + reportedEntityId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
