# Software Requirements Specification (SRS)
## Job Portal Management System - SWP391 Group 3

### Document Information
- **Project Name**: Job Portal Management System  
- **Version**: 1.0
- **Date**: December 2024
- **Team**: SWP391 Group 3
- **Document Type**: Software Requirements Specification

---

## 📑 Table of Contents

1. [Introduction](#1-introduction)
2. [Overall Description](#2-overall-description)
3. [System Features](#3-system-features)
4. [External Interface Requirements](#4-external-interface-requirements)
5. [System Features Details](#5-system-features-details)
6. [Non-Functional Requirements](#6-non-functional-requirements)
7. [Technical Requirements](#7-technical-requirements)
8. [Constraints](#8-constraints)
9. [Assumptions and Dependencies](#9-assumptions-and-dependencies)

---

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) document describes the functional and non-functional requirements for the Job Portal Management System. The system is designed to connect students with employers, facilitating job posting, application processes, and career event management.

### 1.2 Document Scope
This document covers:
- Functional requirements for all user types (Students, Employers, Administrators)
- Non-functional requirements including performance, security, and usability
- System interfaces and external integrations
- Technical constraints and assumptions

### 1.3 Intended Audience
- Development Team
- Project Stakeholders
- Quality Assurance Team
- System Administrators
- End Users (Students, Employers, Administrators)

### 1.4 Product Scope
The Job Portal Management System is a web-based platform that:
- Enables students to search and apply for jobs
- Allows employers to post jobs and manage applications
- Provides administrators with system oversight capabilities
- Facilitates communication through integrated chat system
- Supports multi-language functionality (Vietnamese, English)

---

## 2. Overall Description

### 2.1 Product Perspective
The Job Portal Management System is a standalone web application built using:
- **Backend**: Spring Boot 3.5.0 with Java 17
- **Frontend**: Thymeleaf templating with Bootstrap 5
- **Database**: MySQL
- **Security**: Spring Security with OAuth2 integration
- **Communication**: WebSocket for real-time chat

### 2.2 Product Functions
**Major Functions:**
1. **User Management**: Registration, authentication, profile management
2. **Job Management**: Job posting, searching, filtering, application tracking
3. **Event Management**: Career event creation, registration, and management
4. **Communication**: Real-time chat between students and employers
5. **Administrative Control**: User management, content moderation, reporting
6. **File Management**: CV upload, company logo management
7. **Notification System**: Email notifications and in-app alerts
8. **Internationalization**: Multi-language support

### 2.3 User Classes and Characteristics

#### 2.3.1 Students
- **Primary Users**: University students seeking employment opportunities
- **Technical Expertise**: Basic to intermediate computer skills
- **Key Tasks**: Job searching, profile management, application submission, chat communication

#### 2.3.2 Employers
- **Primary Users**: Companies and recruiters seeking candidates
- **Technical Expertise**: Intermediate computer skills
- **Key Tasks**: Job posting, application review, candidate communication, event creation

#### 2.3.3 Administrators
- **Primary Users**: System administrators and moderators
- **Technical Expertise**: Advanced computer skills
- **Key Tasks**: User management, content moderation, system oversight, report generation

### 2.4 Operating Environment
- **Server Environment**: Java 17, Spring Boot, Apache Tomcat
- **Database**: MySQL 8.0+
- **Client Environment**: Modern web browsers (Chrome, Firefox, Safari, Edge)
- **Network**: Internet connection required
- **Mobile Compatibility**: Responsive design for mobile devices

---

## 3. System Features

### 3.1 Feature Overview

| Feature Category | Features | Priority | Status |
|------------------|----------|----------|---------|
| **Authentication & Authorization** | Login, Register, OAuth2, Password Reset | High | ✅ Implemented |
| **User Profile Management** | Profile Creation, Edit, Avatar Upload | High | ✅ Implemented |
| **Job Management** | Job Posting, Search, Application, Tracking | High | ✅ Implemented |
| **Event Management** | Event Creation, Registration, Management | Medium | ✅ Implemented |
| **Communication** | Real-time Chat, Notifications | Medium | ✅ Implemented |
| **File Management** | CV Upload, File Storage, Validation | High | ✅ Implemented |
| **Administration** | User Management, Content Moderation | High | ✅ Implemented |
| **Reporting** | Application Reports, User Analytics | Medium | ✅ Implemented |
| **Internationalization** | Multi-language Support | Low | 🔄 Partial |

---

## 4. External Interface Requirements

### 4.1 User Interfaces

#### 4.1.1 Web Interface Requirements
- **Responsive Design**: Compatible with desktop, tablet, and mobile devices
- **Browser Compatibility**: Chrome 90+, Firefox 88+, Safari 14+, Edge 90+
- **Accessibility**: WCAG 2.1 Level AA compliance
- **Language Support**: Vietnamese (default), English

#### 4.1.2 Navigation Structure
```
Home Page
├── Job Listings
├── Event Listings
├── Authentication (Login/Register)
├── User Dashboard
│   ├── Student Portal
│   │   ├── Profile Management
│   │   ├── Job Applications
│   │   ├── Event Registrations
│   │   └── Chat Messages
│   ├── Employer Portal
│   │   ├── Company Profile
│   │   ├── Job Postings Management
│   │   ├── Application Reviews
│   │   ├── Event Management
│   │   └── Chat Messages
│   └── Admin Portal
│       ├── User Management
│       ├── Content Moderation
│       ├── System Reports
│       └── System Configuration
```

### 4.2 Hardware Interfaces
- **Server Requirements**: 
  - CPU: 2+ cores
  - RAM: 4GB minimum, 8GB recommended
  - Storage: 50GB minimum for application and file storage
- **Client Requirements**: 
  - Modern web browser
  - Internet connection (minimum 1 Mbps for optimal experience)

### 4.3 Software Interfaces

#### 4.3.1 Database Interface
- **MySQL Database**: Version 8.0+
- **Connection Pool**: HikariCP for connection management
- **ORM**: JPA/Hibernate for data persistence

#### 4.3.2 External Service Integrations
- **Email Service**: SMTP integration for notifications
- **Google OAuth2**: Authentication service integration
- **Cloudinary**: File storage and management service
- **AI Service**: OpenAI integration for enhanced features

### 4.4 Communication Interfaces
- **HTTP/HTTPS**: RESTful API communication
- **WebSocket**: Real-time chat functionality
- **SMTP**: Email communication
- **JSON**: Data interchange format

---

## 5. System Features Details

### 5.1 User Authentication and Authorization

#### 5.1.1 Description
Secure user authentication system supporting multiple login methods and role-based access control.

#### 5.1.2 Functional Requirements

**FR-1.1: User Registration**
- Students and employers can create accounts with email verification
- Required fields: Full name, email, phone, password, role selection
- Email verification required before account activation
- Unique email constraint enforcement

**FR-1.2: User Login**
- Email/password authentication
- Google OAuth2 integration
- "Remember me" functionality
- Account lockout after failed attempts

**FR-1.3: Password Management**
- Secure password reset via email
- OTP verification for password changes
- Password strength requirements
- Password encryption using BCrypt

**FR-1.4: Session Management**
- 30-minute session timeout
- Secure session handling
- Automatic logout on browser close

#### 5.1.3 Input Requirements
- **Email**: Valid email format, unique in system
- **Password**: Minimum 8 characters, alphanumeric with special characters
- **Phone**: Valid phone number format
- **OTP**: 6-digit numeric code

#### 5.1.4 Output Requirements
- Success/error messages for all operations
- Redirect to appropriate dashboard based on user role
- Session tokens for authenticated users

### 5.2 Job Management System

#### 5.2.1 Description
Comprehensive job posting and application management system for employers and students.

#### 5.2.2 Functional Requirements

**FR-2.1: Job Posting (Employer)**
- Create detailed job postings with all required information
- Upload company logos and additional resources
- Set application deadlines and requirements
- Job approval workflow by administrators

**FR-2.2: Job Search and Filtering (Student)**
- Search jobs by keywords, location, job field
- Filter by job type (full-time, part-time), salary range
- Pagination and sorting capabilities
- Bookmark favorite jobs

**FR-2.3: Job Application Process**
- One-click application with profile auto-fill
- CV upload with format validation (PDF, images)
- Application status tracking
- Withdrawal option before deadline

**FR-2.4: Application Management (Employer)**
- View all applications for posted jobs
- Filter applications by status, date
- Download applicant CVs
- Update application status (reviewing, accepted, rejected)

#### 5.2.3 Input Requirements
- **Job Title**: 1-255 characters
- **Job Description**: Rich text, unlimited length
- **Salary**: Numeric value, optional
- **Location**: Text field, 255 characters max
- **CV Files**: PDF, PNG, JPG, JPEG, GIF formats, max 2MB

#### 5.2.4 Output Requirements
- Formatted job listings with company information
- Application confirmation messages
- Status update notifications
- CV download links

### 5.3 Event Management System

#### 5.3.1 Description
Career event creation and management system for networking and recruitment activities.

#### 5.3.2 Functional Requirements

**FR-3.1: Event Creation (Employer)**
- Create events with detailed information
- Set participant limits and registration deadlines
- Event approval workflow
- Event status management

**FR-3.2: Event Registration (Student)**
- Browse available events
- Register for events with automatic confirmation
- View registration status and event details
- Receive event reminders

**FR-3.3: Event Management (Employer/Admin)**
- Track participant registrations
- Update event information
- Cancel events with participant notification
- Generate participant reports

#### 5.3.3 Input Requirements
- **Event Title**: 1-255 characters
- **Event Date**: Future date/time
- **Location**: Physical or virtual location details
- **Max Participants**: Positive integer
- **Registration Deadline**: Date before event date

### 5.4 Communication System

#### 5.4.1 Description
Real-time chat system enabling communication between students and employers.

#### 5.4.2 Functional Requirements

**FR-4.1: Chat Functionality**
- Real-time messaging using WebSocket
- Message history persistence
- File sharing capabilities
- Typing indicators

**FR-4.2: Chat Room Management**
- Automatic chat room creation for job applications
- Chat room access control
- Message status tracking (sent, delivered, read)

**FR-4.3: Notification Integration**
- New message notifications
- Email notifications for offline users
- Chat availability status

### 5.5 File Management System

#### 5.5.1 Description
Secure file upload and management system for CVs, company logos, and other documents.

#### 5.5.2 Functional Requirements

**FR-5.1: File Upload**
- Support multiple file formats (PDF, images)
- File size validation (max 10MB)
- Virus scanning integration
- Automatic file naming with UUID

**FR-5.2: File Storage**
- Local file system storage
- Cloudinary integration for enhanced management
- File backup and recovery
- Access control and security

**FR-5.3: File Serving**
- Secure file download with authentication
- File streaming for large files
- Content-type validation
- Download tracking

---

## 6. Non-Functional Requirements

### 6.1 Performance Requirements

#### 6.1.1 Response Time
- **Page Load Time**: Maximum 3 seconds for standard pages
- **Search Results**: Maximum 2 seconds for job/event searches
- **File Upload**: Progress indication for files > 1MB
- **Chat Messages**: Real-time delivery within 1 second

#### 6.1.2 Throughput
- **Concurrent Users**: Support 1000+ simultaneous users
- **Database Transactions**: 100+ queries per second
- **File Downloads**: 50+ concurrent downloads

#### 6.1.3 System Availability
- **Uptime**: 99.5% availability (excluding scheduled maintenance)
- **Maintenance Windows**: Maximum 4 hours monthly
- **Recovery Time**: Maximum 1 hour for system restoration

### 6.2 Security Requirements

#### 6.2.1 Authentication and Authorization
- **Password Policy**: Minimum complexity requirements
- **Session Security**: Secure session token management
- **Role-Based Access**: Strict access control by user roles
- **OAuth2 Integration**: Secure third-party authentication

#### 6.2.2 Data Protection
- **Encryption**: All sensitive data encrypted at rest and in transit
- **HTTPS**: Mandatory SSL/TLS for all communications
- **SQL Injection Protection**: Parameterized queries
- **XSS Protection**: Input sanitization and output encoding

#### 6.2.3 File Security
- **Upload Validation**: File type and size validation
- **Virus Scanning**: Malware detection for uploaded files
- **Access Control**: Authenticated file access only
- **Secure Storage**: Protected file system locations

### 6.3 Usability Requirements

#### 6.3.1 User Interface
- **Intuitive Design**: Self-explanatory navigation
- **Responsive Layout**: Mobile-friendly design
- **Accessibility**: WCAG 2.1 compliance
- **Loading Indicators**: Progress feedback for all operations

#### 6.3.2 User Experience
- **Error Handling**: Clear error messages and recovery suggestions
- **Help System**: Contextual help and documentation
- **Language Support**: Vietnamese and English localization
- **User Feedback**: Success confirmations for all actions

### 6.4 Reliability Requirements

#### 6.4.1 Error Handling
- **Graceful Degradation**: System continues operating with reduced functionality
- **Error Logging**: Comprehensive error tracking and logging
- **Recovery Procedures**: Automatic recovery from common failures
- **Data Integrity**: Transaction rollback on failures

#### 6.4.2 Backup and Recovery
- **Data Backup**: Daily automated database backups
- **File Backup**: Regular backup of uploaded files
- **Recovery Testing**: Monthly disaster recovery tests
- **Data Retention**: 7-year data retention policy

---

## 7. Technical Requirements

### 7.1 System Architecture

#### 7.1.1 Application Architecture
```
Presentation Layer (Thymeleaf + Bootstrap)
    ↓
Controller Layer (Spring MVC)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Spring Data JPA)
    ↓
Database Layer (MySQL)
```

#### 7.1.2 Technology Stack
- **Backend Framework**: Spring Boot 3.5.0
- **Java Version**: Java 17 LTS
- **Database**: MySQL 8.0+
- **ORM**: Hibernate/JPA
- **Security**: Spring Security 6
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap 5, jQuery
- **Real-time Communication**: WebSocket, STOMP
- **Build Tool**: Gradle 8+

### 7.2 Database Requirements

#### 7.2.1 Database Schema
```sql
-- Main entities
- Account (users)
- Student (student profiles)
- Employer (employer profiles)
- JobPost (job postings)
- JobApplication (applications)
- Event (career events)
- ChatRoom (chat rooms)
- ChatMessage (chat messages)
- Notification (system notifications)
- Report (user reports)
- BlogPost (blog content)
```

#### 7.2.2 Data Relationships
- One-to-One: Account ↔ Student/Employer
- One-to-Many: Employer → JobPost → JobApplication
- Many-to-Many: Student ↔ Event (through registrations)
- One-to-Many: ChatRoom → ChatMessage

### 7.3 Integration Requirements

#### 7.3.1 External Services
- **Email Service**: SMTP configuration for notifications
- **OAuth2 Providers**: Google authentication
- **File Storage**: Cloudinary for enhanced file management
- **AI Services**: OpenAI integration for advanced features

#### 7.3.2 API Requirements
- **RESTful APIs**: JSON-based communication
- **WebSocket APIs**: Real-time chat functionality
- **File Upload APIs**: Multipart form data handling
- **Authentication APIs**: JWT token management

---

## 8. Constraints

### 8.1 Technical Constraints
- **Java Version**: Must use Java 17 LTS for long-term support
- **Database**: MySQL required for compatibility with existing infrastructure
- **Browser Support**: Modern browsers only (IE not supported)
- **Mobile Compatibility**: Responsive design required

### 8.2 Business Constraints
- **Budget Limitations**: Open-source technologies preferred
- **Timeline**: Development within academic semester constraints
- **Compliance**: Must comply with data protection regulations
- **Scalability**: Must support growth to 10,000+ users

### 8.3 Regulatory Constraints
- **Data Privacy**: GDPR compliance for international users
- **Accessibility**: WCAG 2.1 Level AA compliance
- **Security Standards**: Industry-standard security practices
- **Academic Requirements**: Must fulfill SWP391 project requirements

---

## 9. Assumptions and Dependencies

### 9.1 Assumptions
- Users have access to modern web browsers and stable internet
- MySQL database server is available and properly configured
- Email SMTP server is accessible for notifications
- File storage infrastructure can handle expected load
- Users are familiar with basic web application usage

### 9.2 Dependencies
- **External Services**: Google OAuth2, email SMTP service
- **Third-party Libraries**: Spring Framework, Bootstrap, jQuery
- **Infrastructure**: Web server, database server, file storage
- **Development Tools**: Java 17, Gradle, IDE support

### 9.3 Risk Factors
- **Server Downtime**: Dependency on hosting infrastructure
- **Third-party Service Outages**: OAuth2 and email service availability
- **Security Vulnerabilities**: Regular security updates required
- **Data Loss**: Backup and recovery procedures critical
- **Performance Degradation**: System optimization required for scale

---

## 10. Appendices

### Appendix A: Glossary
- **SRS**: Software Requirements Specification
- **OAuth2**: Open Authorization protocol
- **JWT**: JSON Web Token
- **WCAG**: Web Content Accessibility Guidelines
- **GDPR**: General Data Protection Regulation
- **API**: Application Programming Interface
- **UUID**: Universally Unique Identifier

### Appendix B: References
- Spring Boot Documentation
- MySQL Documentation
- Bootstrap Framework Documentation
- WCAG 2.1 Guidelines
- Security Best Practices

---

**Document Control:**
- Created: December 2024
- Last Modified: December 2024
- Version: 1.0
- Status: Final
- Approved by: SWP391 Group 3 