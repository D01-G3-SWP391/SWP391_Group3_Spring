package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer blogId;

    @Column(name = "blog_title", length = 255)
    private String blogTitle;

    @Column(name = "blog_content", columnDefinition = "TEXT")
    private String blogContent;

    @Column(name = "blog_date")
    private LocalDateTime blogDate;

    @Column(name = "blog_status", length = 50)
    private String blogStatus;

    public Blog() {
    }
} 