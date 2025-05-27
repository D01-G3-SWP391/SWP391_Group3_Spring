package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Event_form", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "student_id"}))
public class EventForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventFormId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "registration_date", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "form_status", length = 20)
    private RegistrationStatus formStatus = RegistrationStatus.REGISTERED;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Ghi chú của student khi đăng ký
    
    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;
    
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    
    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;
    
    @Column(name = "checked_out_at")
    private LocalDateTime checkedOutAt;
    
    // Feedback sau event
    @Column(name = "rating")
    private Integer rating; // 1-5 stars
    
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "feedback_date")
    private LocalDateTime feedbackDate;

    // Enum for registration status
    public enum RegistrationStatus {
        REGISTERED,     // Đã đăng ký
        CANCELLED,      // Đã hủy
        ATTENDED,       // Đã tham gia
        NO_SHOW,        // Không đến
        WAITLISTED      // Trong danh sách chờ
    }
    
    // Constructors
    public EventForm() {
    }
    
    public EventForm(Event event, Student student) {
        this.event = event;
        this.student = student;
        this.registrationDate = LocalDateTime.now();
        this.formStatus = RegistrationStatus.REGISTERED;
    }
    
    public EventForm(Event event, Student student, String notes) {
        this(event, student);
        this.notes = notes;
    }
    
    // Getters and Setters
    public Integer getEventFormId() {
        return eventFormId;
    }
    
    public void setEventFormId(Integer eventFormId) {
        this.eventFormId = eventFormId;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public RegistrationStatus getFormStatus() {
        return formStatus;
    }
    
    public void setFormStatus(RegistrationStatus formStatus) {
        this.formStatus = formStatus;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCancelledDate() {
        return cancelledDate;
    }
    
    public void setCancelledDate(LocalDateTime cancelledDate) {
        this.cancelledDate = cancelledDate;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }
    
    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }
    
    public LocalDateTime getCheckedOutAt() {
        return checkedOutAt;
    }
    
    public void setCheckedOutAt(LocalDateTime checkedOutAt) {
        this.checkedOutAt = checkedOutAt;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }
    
    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
    
    // Business methods
    public void cancel(String reason) {
        this.formStatus = RegistrationStatus.CANCELLED;
        this.cancelledDate = LocalDateTime.now();
        this.cancellationReason = reason;
        
        // Giảm số lượng participants trong Event
        if (this.event != null) {
            this.event.decrementParticipants();
        }
    }
    
    public void checkIn() {
        this.checkedInAt = LocalDateTime.now();
        if (this.formStatus == RegistrationStatus.REGISTERED) {
            this.formStatus = RegistrationStatus.ATTENDED;
        }
    }
    
    public void checkOut() {
        this.checkedOutAt = LocalDateTime.now();
    }
    
    public void submitFeedback(Integer rating, String feedback) {
        this.rating = rating;
        this.feedback = feedback;
        this.feedbackDate = LocalDateTime.now();
    }
    
    public boolean canCancel() {
        return formStatus == RegistrationStatus.REGISTERED 
               && (event.getRegistrationDeadline() == null 
                   || LocalDateTime.now().isBefore(event.getRegistrationDeadline()));
    }
    
    public boolean isAttended() {
        return formStatus == RegistrationStatus.ATTENDED;
    }
    
    public boolean canProvideFeedback() {
        return isAttended() && feedback == null;
    }
} 