# BUSINESS RULES TABLE (BR-01 TO BR-150)

| ID     | Rule Definition                                                                                    |
| ------ | -------------------------------------------------------------------------------------------------- |
| BR-01  | The system shall verify user is logged in and has permission to access functionality               |
| BR-02  | The system will authenticate Google account credentials for OAuth login                            |
| BR-03  | The system will restrict access based on user roles (Student, Employer, Admin, Guest)              |
| BR-04  | The system will lock account after 5 failed login attempts                                         |
| BR-05  | The system will create user session and log login actions for auditing                             |
| BR-06  | The system will generate unique identifier upon successful registration                            |
| BR-07  | The system will require Admin permission for content management actions                            |
| BR-08  | The system will ensure only resource owners can edit/delete their content                          |
| BR-09  | The system will prevent unauthorized access to private user data                                   |
| BR-10  | The system will require re-authentication for sensitive operations                                 |
| BR-11  | The system shall retrieve data from database upon request                                          |
| BR-12  | The system shall display list of entities with basic details                                       |
| BR-13  | The system shall display detailed information of selected entity                                   |
| BR-14  | The system will store data in database with proper validation                                      |
| BR-15  | The system will update data and reflect changes in real-time                                       |
| BR-16  | The system will validate data existence before operations                                          |
| BR-17  | The system will support pagination and filtering for large datasets                                |
| BR-18  | The system will maintain data integrity during CRUD operations                                     |
| BR-19  | The system will backup critical data before deletion                                               |
| BR-20  | The system will archive deleted data for audit purposes                                            |
| BR-21  | The system will validate email format and registration status                                      |
| BR-22  | The system will validate password security criteria (8+ chars, mixed case, numbers, special chars) |
| BR-23  | The system will validate company name (3-100 chars, no special chars @#$)                          |
| BR-24  | The system will validate address format (city, country, max 200 chars)                             |
| BR-25  | The system will validate file uploads (JPEG/PNG, max 5MB)                                          |
| BR-26  | The system will validate search criteria format and values                                         |
| BR-27  | The system will validate job post required fields (title, description, salary > 0)                 |
| BR-28  | The system will validate job type and location from predefined lists                               |
| BR-29  | The system will validate event details (title 5-100 chars, description 100+ chars)                 |
| BR-30  | The system will validate report description and category selection                                 |
| BR-31  | The system will validate chat message length (max 1000 characters)                                 |
| BR-32  | The system will validate rejection reason (min 50 characters)                                      |
| BR-33  | The system will validate content requirements for blogs (title, content, tags)                     |
| BR-34  | The system will validate date/time format and future constraints                                   |
| BR-35  | The system will validate user input against SQL injection and XSS                                  |
| BR-36  | The system will send notification upon successful action completion                                |
| BR-37  | The system will notify relevant parties of status changes                                          |
| BR-38  | The system will send real-time notifications for application updates                               |
| BR-39  | The system will send email notifications for critical events                                       |
| BR-40  | The system will send reminder notifications 24 hours before events                                 |
| BR-41  | The system will notify within 1 hour for time-sensitive actions                                    |
| BR-42  | The system will allow users to customize notification preferences                                  |
| BR-43  | The system will display notifications in dedicated dashboard section                               |
| BR-44  | The system will provide option to mark notifications as read/delete                                |
| BR-45  | The system will store notification history for user reference                                      |
| BR-46  | The system will display appropriate error messages for invalid operations                          |
| BR-47  | The system will display "No results found" when search returns empty                               |
| BR-48  | The system will handle session timeout and require re-authentication                               |
| BR-49  | The system will display server error messages for system failures                                  |
| BR-50  | The system will provide user-friendly error descriptions                                           |
| BR-51  | The system will log all errors for debugging purposes                                              |
| BR-52  | The system will gracefully handle database connection failures                                     |
| BR-53  | The system will validate and handle file upload errors                                             |
| BR-54  | The system will handle network timeout errors                                                      |
| BR-55  | The system will provide error recovery suggestions to users                                        |
| BR-56  | The system will log all user actions for audit trail                                               |
| BR-57  | The system will log creation, update, deletion events with timestamp                               |
| BR-58  | The system will maintain change history for critical data                                          |
| BR-59  | The system will store admin actions with user identification                                       |
| BR-60  | The system will retain audit logs for minimum 6 months                                             |
| BR-61  | The system will log search and viewing activities                                                  |
| BR-62  | The system will track user login/logout activities                                                 |
| BR-63  | The system will monitor system performance and resource usage                                      |
| BR-64  | The system will log security events and suspicious activities                                      |
| BR-65  | The system will ensure audit log integrity and non-repudiation                                     |
| BR-66  | The system will require future dates for scheduling (24+ hours ahead)                              |
| BR-67  | The system will validate password reset link expiry (24 hours)                                     |
| BR-68  | The system will prevent actions after events have started                                          |
| BR-69  | The system will display events within 90 days from current date                                    |
| BR-70  | The system will update analytics data every 24 hours                                               |
| BR-71  | The system will show historical data up to 12 months                                               |
| BR-72  | The system will archive chat history after 90 days                                                 |
| BR-73  | The system will update content every 3-6 months for relevance                                      |
| BR-74  | The system will sort events/content by date (upcoming first)                                       |
| BR-75  | The system will enforce session timeout for security                                               |
| BR-76  | The system will ensure minimum content requirements for guides/tips                                |
| BR-77  | The system will limit guest access to preview content only                                         |
| BR-78  | The system will add watermarks to downloaded content                                               |
| BR-79  | The system will maintain minimum content pool (50+ quotes, 20+ questions)                          |
| BR-80  | The system will display content randomly without repetition                                        |
| BR-81  | The system will redirect shared content to platform homepage                                       |
| BR-82  | The system will support multimedia content in blogs                                                |
| BR-83  | The system will allow content tagging for searchability                                            |
| BR-84  | The system will enable content preview before publishing                                           |
| BR-85  | The system will categorize content for better organization                                         |
| BR-86  | The system will ensure content is SEO-friendly                                                     |
| BR-87  | The system will support content versioning                                                         |
| BR-88  | The system will allow bulk content operations                                                      |
| BR-89  | The system will maintain content publication status                                                |
| BR-90  | The system will track content performance metrics                                                  |
| BR-91  | The system will provide responsive design for all screen sizes                                     |
| BR-92  | The system will support multiple languages (English, Vietnamese)                                   |
| BR-93  | The system will save user language preferences                                                     |
| BR-94  | The system will apply language changes immediately without errors                                  |
| BR-95  | The system will translate all content based on selected language                                   |
| BR-96  | The system will provide user-friendly interface for all operations                                 |
| BR-97  | The system will allow filtering and sorting of content lists                                       |
| BR-98  | The system will enable direct application from job listings                                        |
| BR-99  | The system will display content in logical, organized manner                                       |
| BR-100 | The system will provide quick access to frequently used features                                   |
| BR-101 | The system will ensure fast page loading and response times                                        |
| BR-102 | The system will provide clear navigation and breadcrumbs                                           |
| BR-103 | The system will support keyboard navigation for accessibility                                      |
| BR-104 | The system will provide tooltips and help text where needed                                        |
| BR-105 | The system will maintain consistent design patterns across pages                                   |
| BR-106 | The system will link job applications to specific job posts                                        |
| BR-107 | The system will track application status progression                                               |
| BR-108 | The system will ensure unique event titles within employer scope                                   |
| BR-109 | The system will prevent duplicate applications for same job                                        |
| BR-110 | The system will manage favorites list per user                                                     |
| BR-111 | The system will enforce business rules for interview scheduling                                    |
| BR-112 | The system will manage candidate confirmation/rejection workflow                                   |
| BR-113 | The system will handle report status lifecycle                                                     |
| BR-114 | The system will enforce job post approval workflow                                                 |
| BR-115 | The system will manage user ban/unban status                                                       |
| BR-116 | The system will enforce rate limiting (50 messages/minute for chat)                                |
| BR-117 | The system will detect and flag offensive language                                                 |
| BR-118 | The system will implement progressive penalties for violations                                     |
| BR-119 | The system will encrypt sensitive data in database                                                 |
| BR-120 | The system will implement CSRF protection                                                          |
| BR-121 | The system will sanitize user input to prevent code injection                                      |
| BR-122 | The system will implement secure password hashing                                                  |
| BR-123 | The system will enforce HTTPS for all communications                                               |
| BR-124 | The system will implement proper CORS policies                                                     |
| BR-125 | The system will validate file types and scan for malware                                           |
| BR-126 | The system will implement API rate limiting                                                        |
| BR-127 | The system will mask sensitive data in logs                                                        |
| BR-128 | The system will implement secure session management                                                |
| BR-129 | The system will require strong authentication for admin functions                                  |
| BR-130 | The system will implement data privacy controls                                                    |
| BR-131 | The system will respond to user requests within 3 seconds                                          |
| BR-132 | The system will implement database query optimization                                              |
| BR-133 | The system will use caching for frequently accessed data                                           |
| BR-134 | The system will implement lazy loading for large datasets                                          |
| BR-135 | The system will compress images and assets for faster loading                                      |
| BR-136 | The system will implement database connection pooling                                              |
| BR-137 | The system will handle concurrent user sessions efficiently                                        |
| BR-138 | The system will implement CDN for static content delivery                                          |
| BR-139 | The system will optimize database indexes for search operations                                    |
| BR-140 | The system will implement graceful degradation for high load                                       |
| BR-141 | The system will export data in CSV/PDF format (max 10MB)                                           |
| BR-142 | The system will generate analytics reports with charts/graphs                                      |
| BR-143 | The system will allow data filtering in reports                                                    |
| BR-144 | The system will schedule automated report generation                                               |
| BR-145 | The system will track download history for exported files                                          |
| BR-146 | The system will implement report templates for consistency                                         |
| BR-147 | The system will allow custom date ranges for reports                                               |
| BR-148 | The system will provide real-time dashboard updates                                                |
| BR-149 | The system will ensure report data accuracy and integrity                                          |
| BR-150 | The system will allow report sharing with stakeholders                                             |
