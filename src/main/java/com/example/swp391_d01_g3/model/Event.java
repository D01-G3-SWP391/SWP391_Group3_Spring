package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "Event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    @Column(name = "event_title", length = 255, nullable = false)
    private String eventTitle;

    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;
    
    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventDate;
    
    @Column(name = "event_location", length = 255)
    private String eventLocation;
    
    @Column(name = "registration_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime registrationDeadline;
    
    @Column(name = "max_participants")
    private Integer maxParticipants;
    
    @Column(name = "current_participants", columnDefinition = "INT DEFAULT 0")
    private Integer currentParticipants = 0;
    
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", length = 20)
    private EventStatus eventStatus = EventStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Account approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Enums - chỉ giữ lại những cái cần thiết
    public enum EventStatus {
        ACTIVE,     // Đang mở đăng ký
        FULL,       // Đã đủ người
        CANCELLED   // Đã hủy
    }
    
    public enum ApprovalStatus {
        PENDING,    // Chờ duyệt
        APPROVED,   // Đã duyệt
        REJECTED    // Bị từ chối
    }
    
    // Constructors
    public Event() {
    }
    
    public Event(String eventTitle, String eventDescription, LocalDateTime eventDate, 
                String eventLocation, Employer employer) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.employer = employer;
    }
    
    // Getters and Setters
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
    public String getEventTitle() {
        return eventTitle;
    }
    
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    
    public String getEventDescription() {
        return eventDescription;
    }
    
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
    
    public LocalDateTime getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
    
    public String getEventLocation() {
        return eventLocation;
    }
    
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
    
    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }
    
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }
    
    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    
    public Integer getCurrentParticipants() {
        return currentParticipants;
    }
    
    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public EventStatus getEventStatus() {
        return eventStatus;
    }
    
    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
    
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public Employer getEmployer() {
        return employer;
    }
    
    public void setEmployer(Employer employer) {
        this.employer = employer;
    }
    
    public Account getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(Account approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    // Business methods
    public boolean canRegister() {
        return approvalStatus == ApprovalStatus.APPROVED 
               && eventStatus == EventStatus.ACTIVE
               && (maxParticipants == null || currentParticipants < maxParticipants)
               && (registrationDeadline == null || LocalDateTime.now().isBefore(registrationDeadline));
    }
    
    public boolean isFull() {
        return maxParticipants != null && currentParticipants >= maxParticipants;
    }
    
    public void incrementParticipants() {
        this.currentParticipants++;
        if (isFull()) {
            this.eventStatus = EventStatus.FULL;
        }
    }
    
    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
            if (this.eventStatus == EventStatus.FULL) {
                this.eventStatus = EventStatus.ACTIVE;
            }
        }
    }
} 