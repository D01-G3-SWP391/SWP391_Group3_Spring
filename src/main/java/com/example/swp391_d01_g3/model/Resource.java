package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by", nullable = false)
//    private Account createdBy;
//
//
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;


    public enum ResourceType {
        interview_guide, application_tips, quotes
    }

    public Resource() {
    }

    public Resource(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Resource(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

//    public Account getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(Account createdBy) {
//        this.createdBy = createdBy;
//    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

}
