package com.example.swp391_d01_g3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Employer")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employer_id")
    private Integer employerId;

    @ManyToOne
    @JoinColumn(name = "jobs_field_id", nullable = false)
    private JobField jobField;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Account account;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    public Employer(Integer employerId, JobField jobField, Account account, String companyName, String companyAddress, String logoUrl, String companyDescription) {
        this.employerId = employerId;
        this.jobField = jobField;
        this.account = account;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.logoUrl = logoUrl;
        this.companyDescription = companyDescription;
    }

    public Employer() {
    }

    // Getter and Setter methods
    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }

    public JobField getJobField() {
        return jobField;
    }

    public void setJobField(JobField jobField) {
        this.jobField = jobField;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }
} 