package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Event_form")
public class EventForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventFormId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "form_status", length = 50)
    private String formStatus;

    public EventForm() {
    }
} 