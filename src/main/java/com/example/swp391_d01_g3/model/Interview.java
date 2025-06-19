package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Interview")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewId;

    @ManyToOne
    @JoinColumn(name = "job_application_id")
    private JobApplication jobApplication;

    @Column(name = "interview_date")
    private LocalDateTime interviewDate;

    @Column(name = "interview_location", length = 255)
    private String interviewLocation;

    @Column(name = "interview_status", length = 50)
    private String interviewStatus;

    @Column(name = "interview_feedback", columnDefinition = "TEXT")
    private String interviewFeedback;
    @Column(name = "interview_type", length = 50)
    private String interviewType;

    @Column(name = "meeting_link", length = 255)
    private String meetingLink;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public Interview() {
    }

    public Integer getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }

    public LocalDateTime getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDateTime interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public String getInterviewFeedback() {
        return interviewFeedback;
    }

    public void setInterviewFeedback(String interviewFeedback) {
        this.interviewFeedback = interviewFeedback;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}