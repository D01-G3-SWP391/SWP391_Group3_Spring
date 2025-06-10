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

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public EventForm() {
    }
    
    public EventForm(Event event, Student student) {
        this.event = event;
        this.student = student;
    }
    
    public EventForm(Event event, Student student, String notes) {
        this.event = event;
        this.student = student;
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 