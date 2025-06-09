# Job4You - Function Details

## 3.2.1 Homepage (index.html)

### Function Details:

| #                              | Component              | Comp. Type | Editable | Mandatory | Default Value                                       | Description                                            |
| ------------------------------ | ---------------------- | ---------- | -------- | --------- | --------------------------------------------------- | ------------------------------------------------------ |
| **Header Section**             |
| 1                              | Logo                   | Image      | No       | Yes       | JOB4YOU Logo                                        | Display system logo, clickable to redirect to homepage |
| 1                              | Navigation Menu        | Menu       | No       | Yes       | Fixed Menu                                          | Horizontal navigation bar with main sections           |
| 1                              | Job Link               | Link       | No       | Yes       | "Việc làm"                                          | Redirects to job search page (UC-07)                   |
| 1                              | CV Link                | Link       | No       | Yes       | "Tạo CV"                                            | Redirects to CV builder page (UC-20)                   |
| 1                              | Tools Link             | Link       | No       | Yes       | "Công cụ"                                           | Redirects to career tools page                         |
| 1                              | Guide Link             | Link       | No       | Yes       | "Cẩm nang nghề nghiệp"                              | Redirects to career handbook page (UC-48)              |
| 1                              | Blog Link              | Link       | No       | Yes       | "Blogs"                                             | Redirects to blog page (UC-48)                         |
| 1                              | Events Link            | Link       | No       | Yes       | "Events"                                            | Redirects to events page (UC-49)                       |
| 1                              | Register Button        | Button     | Yes      | Yes       | "Đăng ký"                                           | Opens registration form (UC-02)                        |
| 1                              | Login Button           | Button     | Yes      | Yes       | "Đăng nhập"                                         | Opens login form (UC-03, UC-04)                        |
| **Hero Section**               |
| 2                              | Hero Title             | Heading    | No       | Yes       | "Find The Perfect Job That You Deserved"            | Main page title with English text                      |
| 2                              | Hero Subtitle          | Text       | No       | Yes       | "Tìm việc làm mơ ước của bạn"                       | Vietnamese subtitle translation                        |
| 2                              | Hero Background        | Image      | No       | Yes       | Gradient Background                                 | Purple gradient background                             |
| **Featured Jobs Section**      |
| 3                              | Section Title          | Heading    | No       | Yes       | "Việc làm nổi bật"                                  | Section header for featured jobs                       |
| 3                              | Section Description    | Text       | No       | Yes       | "Những cơ hội việc làm tốt nhất dành cho bạn"       | Section description                                    |
| 3                              | Job Cards Grid         | Container  | No       | Yes       | 2x2 Grid Layout                                     | Grid container for job cards                           |
| 3                              | Job Card               | Card       | Yes      | Yes       | Job Card Template                                   | Individual job listing card                            |
| 3                              | Company Name           | Text       | Yes      | Yes       | Company Name                                        | Display company name (UC-07)                           |
| 3                              | Job Title              | Text       | Yes      | Yes       | Job Title                                           | Display job position title (UC-07)                     |
| 3                              | Job Location           | Text       | Yes      | Yes       | Location Icon + City                                | Display job location with icon                         |
| 3                              | Job Salary             | Text       | Yes      | Yes       | Salary Range                                        | Display salary information                             |
| 3                              | Posted Date            | Text       | Yes      | Yes       | Date Format                                         | Display job posting date                               |
| 3                              | Job Type Tag           | Badge      | Yes      | Yes       | Job Type                                            | Display employment type (Full-time, Part-time, etc.)   |
| 3                              | View More Jobs Link    | Link       | Yes      | Yes       | "Xem thêm việc làm"                                 | Link to full job listings page                         |
| **Why Choose Job4You Section** |
| 4                              | Section Title          | Heading    | No       | Yes       | "Tại sao chọn Job4You?"                             | Section header                                         |
| 4                              | Feature Cards Grid     | Container  | No       | Yes       | 3-column Grid                                       | Grid for feature cards                                 |
| 4                              | Jobs Feature Card      | Card       | No       | Yes       | Feature Card                                        | "Hàng nghìn việc làm" feature                          |
| 4                              | Employers Feature Card | Card       | No       | Yes       | Feature Card                                        | "Nhà tuyển dụng uy tín" feature                        |
| 4                              | Career Feature Card    | Card       | No       | Yes       | Feature Card                                        | "Phát triển sự nghiệp" feature                         |
| 4                              | Feature Icon           | Icon       | No       | Yes       | Emoji Icon                                          | Visual icon for each feature                           |
| 4                              | Feature Title          | Text       | No       | Yes       | Feature Title                                       | Title for each feature                                 |
| 4                              | Feature Description    | Text       | No       | Yes       | Feature Description                                 | Description of each feature benefit                    |
| **Quick Links Section**        |
| 5                              | Section Title          | Heading    | No       | Yes       | "Bạn muốn làm việc ở đâu?"                          | Section header for location links                      |
| 5                              | Location Cards Grid    | Container  | No       | Yes       | 4-column Grid                                       | Grid for location cards                                |
| 5                              | Location Card          | Card       | Yes      | Yes       | City Card                                           | Individual city/location card                          |
| 5                              | City Name              | Text       | Yes      | Yes       | City Name                                           | Display city name                                      |
| 5                              | Job Count              | Text       | Yes      | Yes       | "X việc làm"                                        | Display number of jobs in city                         |
| **Footer Section**             |
| 6                              | Footer Container       | Container  | No       | Yes       | Footer Layout                                       | Main footer container                                  |
| 6                              | Company Info           | Text       | No       | Yes       | "Job4You - Nền tảng tìm việc làm hàng đầu Việt Nam" | Company description                                    |
| 6                              | Social Links           | Link Group | Yes      | Yes       | Social Media Links                                  | Facebook, LinkedIn, Twitter links                      |
| 6                              | Candidate Links        | Link Group | No       | Yes       | Candidate Menu                                      | Links for job seekers                                  |
| 6                              | Employer Links         | Link Group | No       | Yes       | Employer Menu                                       | Links for employers                                    |
| 6                              | Support Links          | Link Group | No       | Yes       | Support Menu                                        | Help and support links                                 |
| 6                              | Copyright              | Text       | No       | Yes       | "© 2025 Job4You. All rights reserved."              | Copyright notice                                       |

---

## 3.2.2 Job Search Page (viec-lam.html)

### Function Details:

| #                  | Component         | Comp. Type | Editable | Mandatory | Default Value                                         | Description                                 |
| ------------------ | ----------------- | ---------- | -------- | --------- | ----------------------------------------------------- | ------------------------------------------- |
| **Header Section** |
| 1                  | Header Navigation | Container  | No       | Yes       | Static Header                                         | Same as homepage with "Việc làm" active     |
| **Page Header**    |
| 2                  | Page Title        | Heading    | No       | Yes       | "Tìm việc làm"                                        | Main page title with gradient background    |
| 2                  | Page Subtitle     | Text       | No       | Yes       | "Khám phá hàng nghìn cơ hội việc làm phù hợp với bạn" | Page description                            |
| **Search Section** |
| 3                  | Search Form       | Form       | Yes      | Yes       | Search Form                                           | Main job search form                        |
| 3                  | Job Search Input  | Input      | Yes      | Yes       | "Tìm theo tên công việc, tên công ty..."              | Primary search field (UC-07)                |
| 3                  | Location Input    | Input      | Yes      | Yes       | "Thành phố"                                           | Location filter input (UC-07)               |
| 3                  | Search Button     | Button     | Yes      | Yes       | "Tìm kiếm"                                            | Submit search form (UC-07)                  |
| **Filter Section** |
| 4                  | Filter Container  | Container  | No       | Yes       | Filter Section                                        | Container for all filters                   |
| 4                  | Industry Filter   | Select     | Yes      | Yes       | "Tất cả"                                              | Industry/field filter dropdown (UC-07)      |
| 4                  | Level Filter      | Select     | Yes      | Yes       | "Tất cả"                                              | Job level filter (Intern, Staff, etc.)      |
| 4                  | Experience Filter | Select     | Yes      | Yes       | "Tất cả"                                              | Experience requirement filter               |
| 4                  | Salary Filter     | Select     | Yes      | Yes       | "Tất cả"                                              | Salary range filter                         |
| 4                  | Job Type Filter   | Select     | Yes      | Yes       | "Tất cả"                                              | Employment type filter                      |
| **Job Listings**   |
| 5                  | Results Count     | Text       | Yes      | Yes       | "Kết quả tìm kiếm (156 việc làm)"                     | Display search results count                |
| 5                  | Job Cards Grid    | Container  | Yes      | Yes       | 2-column Grid                                         | Grid for job listing cards                  |
| 5                  | Job Listing Card  | Card       | Yes      | Yes       | Job Card                                              | Individual job listing with details (UC-07) |
| 5                  | Company Name      | Text       | Yes      | Yes       | Company Name                                          | Display hiring company                      |
| 5                  | Job Title         | Heading    | Yes      | Yes       | Job Position                                          | Clickable job title (UC-08)                 |
| 5                  | Job Details       | Text Group | Yes      | Yes       | Location, Salary, Date                                | Job meta information                        |
| 5                  | Job Type Badge    | Badge      | Yes      | Yes       | Employment Type                                       | Full-time, Part-time, Internship tags       |
| 5                  | Job Description   | Text       | Yes      | Yes       | Job Preview                                           | Brief job description                       |
| 5                  | Pagination        | Navigation | Yes      | Yes       | Page Controls                                         | Previous, numbered pages, Next navigation   |
| **Footer Section** |
| 6                  | Footer Container  | Container  | No       | Yes       | Standard Footer                                       | Same footer as homepage                     |

---

## 3.2.3 CV Builder Page (tao-cv.html)

### Function Details:

| #                      | Component                    | Comp. Type | Editable | Mandatory | Default Value                                        | Description                                |
| ---------------------- | ---------------------------- | ---------- | -------- | --------- | ---------------------------------------------------- | ------------------------------------------ |
| **Header Section**     |
| 1                      | Header Navigation            | Container  | No       | Yes       | Static Header                                        | Navigation with "Tạo CV" active            |
| **Page Header**        |
| 2                      | Page Title                   | Heading    | No       | Yes       | "Tạo CV chuyên nghiệp"                               | Main page title with gradient              |
| 2                      | Page Subtitle                | Text       | No       | Yes       | "Xây dựng CV ấn tượng với các mẫu thiết kế hiện đại" | Page description                           |
| **CV Builder Options** |
| 3                      | Section Title                | Heading    | No       | Yes       | "Chọn cách tạo CV"                                   | Section header for CV creation options     |
| 3                      | Create From Scratch Card     | Card       | No       | Yes       | Feature Card                                         | "Tạo CV từ đầu" option (UC-20)             |
| 3                      | Quick Create Card            | Card       | No       | Yes       | Feature Card                                         | "Tạo CV nhanh" with AI option (UC-20)      |
| 3                      | Start Button                 | Button     | Yes      | Yes       | "Bắt đầu"                                            | CTA to start CV creation (UC-20)           |
| 3                      | Quick Create Button          | Button     | Yes      | Yes       | "Tạo ngay"                                           | CTA for quick CV creation (UC-20)          |
| **CV Templates**       |
| 4                      | Templates Title              | Heading    | No       | Yes       | "Mẫu CV chuyên nghiệp"                               | Section title for templates                |
| 4                      | Templates Grid               | Container  | No       | Yes       | 3-column Grid                                        | Grid layout for template cards             |
| 4                      | Template Card                | Card       | Yes      | Yes       | Template Card                                        | Individual CV template card (UC-20)        |
| 4                      | Template Preview             | Image      | No       | Yes       | Gradient Placeholder                                 | Visual preview of template design          |
| 4                      | Template Name                | Text       | No       | Yes       | "CV Modern", "CV Classic", etc.                      | Template name and type                     |
| 4                      | Template Description         | Text       | No       | Yes       | Suitable for...                                      | Description of template usage              |
| 4                      | Use Template Button          | Button     | Yes      | Yes       | "Sử dụng"                                            | Select and customize template (UC-20)      |
| 4                      | View All Templates Link      | Link       | Yes      | No        | "Xem tất cả mẫu CV"                                  | Link to complete template gallery          |
| **CV Tips**            |
| 5                      | Tips Title                   | Heading    | No       | Yes       | "Mẹo viết CV hiệu quả"                               | Section title for CV writing tips          |
| 5                      | CV Structure Card            | Card       | No       | Yes       | Info Card                                            | "Cấu trúc CV chuẩn" with checklist (UC-48) |
| 5                      | CV Guidelines Card           | Card       | No       | Yes       | Info Card                                            | "Lưu ý quan trọng" with tips list (UC-48)  |
| **CV Services**        |
| 6                      | Services Title               | Heading    | No       | Yes       | "Dịch vụ CV chuyên nghiệp"                           | Section title for premium services         |
| 6                      | Professional Writing Service | Card       | No       | Yes       | Service Card                                         | Professional CV writing service (UC-20)    |
| 6                      | Free Review Service          | Card       | No       | Yes       | Service Card                                         | Free CV review service (UC-20)             |
| 6                      | Premium CV Service           | Card       | No       | Yes       | Service Card                                         | Premium CV with ATS optimization (UC-20)   |
| 6                      | Service Pricing              | Text       | No       | Yes       | Price Display                                        | Clear pricing in VND currency              |
| 6                      | Service CTA Button           | Button     | Yes      | Yes       | "Đặt dịch vụ", "Gửi CV"                              | Action buttons for services (UC-20)        |
| **Footer Section**     |
| 7                      | Footer Container             | Container  | No       | Yes       | Standard Footer                                      | Consistent footer across pages             |

---

## 3.2.4 Career Handbook Page (cam-nang-nghe-nghiep.html)

### Function Details:

| #                              | Component                   | Comp. Type | Editable | Mandatory | Default Value                                            | Description                                              |
| ------------------------------ | --------------------------- | ---------- | -------- | --------- | -------------------------------------------------------- | -------------------------------------------------------- |
| **Header Section**             |
| 1                              | Header Navigation           | Container  | No       | Yes       | Static Header                                            | Navigation with "Cẩm nang nghề nghiệp" active            |
| **Page Header**                |
| 2                              | Page Title                  | Heading    | No       | Yes       | "Cẩm nang nghề nghiệp"                                   | Main page title with gradient background                 |
| 2                              | Page Subtitle               | Text       | No       | Yes       | "Hướng dẫn toàn diện để phát triển sự nghiệp thành công" | Page description                                         |
| **Quick Guide Categories**     |
| 3                              | Categories Title            | Heading    | No       | Yes       | "Danh mục hướng dẫn"                                     | Section title for guide categories                       |
| 3                              | CV Writing Category         | Card       | Yes      | Yes       | Feature Card                                             | CV & Cover Letter guide with anchor link (UC-48)         |
| 3                              | Interview Category          | Card       | Yes      | Yes       | Feature Card                                             | Interview success guide with anchor link (UC-48)         |
| 3                              | Career Development Category | Card       | Yes      | Yes       | Feature Card                                             | Career development guide with anchor link (UC-48)        |
| **CV Guide Section**           |
| 4                              | CV Guide Section            | Section    | No       | Yes       | Content Section                                          | Section with ID "cv-guide" for anchor navigation         |
| 4                              | CV Structure Card           | Card       | No       | Yes       | Info Card                                                | "Cấu trúc CV hoàn hảo" with 5-step guide (UC-48)         |
| 4                              | CV Tips Card                | Card       | No       | Yes       | Info Card                                                | "Mẹo viết CV hiệu quả" with bullet points (UC-48)        |
| 4                              | CV Mistakes Card            | Card       | No       | Yes       | Info Card                                                | "Những điều tránh trong CV" with 2-column layout (UC-48) |
| **Interview Guide Section**    |
| 5                              | Interview Guide Section     | Section    | No       | Yes       | Content Section                                          | Section with ID "interview-guide"                        |
| 5                              | Interview Preparation Card  | Card       | No       | Yes       | Info Card                                                | "Chuẩn bị trước phỏng vấn" checklist (UC-48)             |
| 5                              | Common Questions Card       | Card       | No       | Yes       | Info Card                                                | "Câu hỏi phỏng vấn phổ biến" with answers (UC-48)        |
| 5                              | Interview Tips Card         | Card       | No       | Yes       | Info Card                                                | "Trong buổi phỏng vấn" behavior tips (UC-48)             |
| 5                              | Online Interview Card       | Card       | No       | Yes       | Info Card                                                | "Phỏng vấn online" with technical setup (UC-48)          |
| **Career Development Section** |
| 6                              | Career Guide Section        | Section    | No       | Yes       | Content Section                                          | Section with ID "career-guide"                           |
| 6                              | Career Orientation Card     | Card       | No       | Yes       | Info Card                                                | "Định hướng nghề nghiệp" with 2 steps (UC-48)            |
| 6                              | Career Planning Card        | Card       | No       | Yes       | Info Card                                                | "Lập kế hoạch sự nghiệp" short/long term (UC-48)         |
| **Additional Resources**       |
| 7                              | Resources Title             | Heading    | No       | Yes       | "Tài nguyên bổ sung"                                     | Section title for additional resources                   |
| 7                              | E-book Resource Card        | Card       | Yes      | Yes       | Tool Card                                                | Free e-book download with CTA button (UC-48)             |
| 7                              | Video Tutorial Card         | Card       | Yes      | Yes       | Tool Card                                                | Video tutorials with view button (UC-48)                 |
| 7                              | 1-on-1 Consultation Card    | Card       | Yes      | Yes       | Tool Card                                                | Personal consultation booking (UC-48)                    |
| 7                              | Community Card              | Card       | Yes      | Yes       | Tool Card                                                | Professional community join (UC-48)                      |
| **Footer Section**             |
| 8                              | Footer Container            | Container  | No       | Yes       | Standard Footer                                          | Consistent footer layout                                 |

---

## 3.2.5 Blog Page (blogs.html)

### Function Details:

| #                           | Component              | Comp. Type | Editable | Mandatory | Default Value                                                                | Description                                      |
| --------------------------- | ---------------------- | ---------- | -------- | --------- | ---------------------------------------------------------------------------- | ------------------------------------------------ |
| **Header Section**          |
| 1                           | Header Navigation      | Container  | No       | Yes       | Static Header                                                                | Navigation with "Blogs" active                   |
| **Page Header**             |
| 2                           | Page Title             | Heading    | No       | Yes       | "Blog nghề nghiệp"                                                           | Main page title with gradient background         |
| 2                           | Page Description       | Text       | No       | Yes       | "Cập nhật xu hướng thị trường lao động và chia sẻ kinh nghiệm từ chuyên gia" | Page description                                 |
| **Blog Categories Filter**  |
| 3                           | Filter Container       | Container  | No       | Yes       | Filter Section                                                               | Container for category filter buttons            |
| 3                           | All Categories Button  | Button     | Yes      | Yes       | "Tất cả" (active)                                                            | Show all articles filter (UC-48)                 |
| 3                           | Job Trends Filter      | Button     | Yes      | Yes       | "Xu hướng việc làm"                                                          | Filter for job market trends (UC-48)             |
| 3                           | Skills Filter          | Button     | Yes      | Yes       | "Kỹ năng nghề nghiệp"                                                        | Filter for professional skills articles (UC-48)  |
| 3                           | Interview Filter       | Button     | Yes      | Yes       | "Phỏng vấn"                                                                  | Filter for interview tips and guides (UC-48)     |
| 3                           | Salary Filter          | Button     | Yes      | Yes       | "Lương bổng"                                                                 | Filter for salary-related articles (UC-48)       |
| 3                           | Startup Filter         | Button     | Yes      | Yes       | "Khởi nghiệp"                                                                | Filter for startup and entrepreneurship (UC-48)  |
| **Featured Articles**       |
| 4                           | Featured Title         | Heading    | No       | Yes       | "Bài viết nổi bật"                                                           | Section title for featured articles              |
| 4                           | Featured Article Card  | Card       | Yes      | Yes       | Blog Card                                                                    | Large featured article card (UC-48)              |
| 4                           | Article Image          | Image      | Yes      | Yes       | Gradient Placeholder                                                         | Article featured image with category overlay     |
| 4                           | Article Meta           | Text Group | Yes      | Yes       | Date, Views, Comments                                                        | Article metadata information                     |
| 4                           | Article Title          | Heading    | Yes      | Yes       | Article Title                                                                | H3 heading, SEO optimized, clickable (UC-48)     |
| 4                           | Article Excerpt        | Text       | Yes      | Yes       | Article Preview                                                              | Brief article content preview                    |
| 4                           | Read More Button       | Button     | Yes      | Yes       | "Đọc tiếp"                                                                   | CTA to read full article (UC-48)                 |
| **Latest Articles**         |
| 5                           | Latest Articles Title  | Heading    | No       | Yes       | "Bài viết mới nhất"                                                          | Section title for recent articles                |
| 5                           | Articles Grid          | Container  | Yes      | Yes       | 3-column Grid                                                                | Grid layout for article cards                    |
| 5                           | Article Card           | Card       | Yes      | Yes       | Blog Card                                                                    | Individual article card with meta info (UC-48)   |
| 5                           | Category Tag           | Badge      | Yes      | Yes       | Category Name                                                                | Color-coded category tags                        |
| 5                           | Pagination             | Navigation | Yes      | Yes       | Page Controls                                                                | Article pagination navigation                    |
| **Newsletter Subscription** |
| 6                           | Newsletter Section     | Section    | No       | Yes       | CTA Section                                                                  | Newsletter signup with gradient background       |
| 6                           | Newsletter Title       | Heading    | No       | Yes       | "Đăng ký nhận bài viết mới"                                                  | Newsletter signup heading (UC-51)                |
| 6                           | Newsletter Description | Text       | No       | Yes       | Newsletter benefits text                                                     | Description of newsletter value                  |
| 6                           | Email Input            | Input      | Yes      | Yes       | Email Placeholder                                                            | Email input with validation (UC-51)              |
| 6                           | Subscribe Button       | Button     | Yes      | Yes       | "Đăng ký"                                                                    | Newsletter subscription submit (UC-51)           |
| 6                           | Privacy Notice         | Text       | No       | Yes       | Privacy text                                                                 | Anti-spam and unsubscribe notice                 |
| **Popular Tags**            |
| 7                           | Tags Title             | Heading    | No       | Yes       | "Chủ đề phổ biến"                                                            | Section title for popular tags                   |
| 7                           | Tags Container         | Container  | No       | Yes       | Flex Container                                                               | Container for tag pills                          |
| 7                           | Tag Pills              | Badge      | Yes      | Yes       | Hashtag Pills                                                                | Clickable topic tags with purple styling (UC-48) |
| **Footer Section**          |
| 8                           | Footer Container       | Container  | No       | Yes       | Standard Footer                                                              | Consistent footer layout                         |

---

## 3.2.6 Events Page (events.html)

### Function Details:

| #                    | Component              | Comp. Type | Editable | Mandatory | Default Value                                                     | Description                                         |
| -------------------- | ---------------------- | ---------- | -------- | --------- | ----------------------------------------------------------------- | --------------------------------------------------- |
| **Header Section**   |
| 1                    | Header Navigation      | Container  | No       | Yes       | Static Header                                                     | Navigation with "Events" active                     |
| **Page Header**      |
| 2                    | Page Title             | Heading    | No       | Yes       | "Sự kiện nghề nghiệp"                                             | Main page title with gradient background            |
| 2                    | Page Description       | Text       | No       | Yes       | "Tham gia các sự kiện để mở rộng mạng lưới và cơ hội nghề nghiệp" | Page description                                    |
| **Event Filters**    |
| 3                    | Filter Container       | Container  | No       | Yes       | Filter Section                                                    | Container for event type filters                    |
| 3                    | All Events Filter      | Button     | Yes      | Yes       | "Tất cả" (active)                                                 | Show all events filter (UC-49)                      |
| 3                    | Job Fair Filter        | Button     | Yes      | Yes       | "Job Fair"                                                        | Filter for job fair events (UC-49)                  |
| 3                    | Workshop Filter        | Button     | Yes      | Yes       | "Workshop"                                                        | Filter for skill workshops (UC-49)                  |
| 3                    | Webinar Filter         | Button     | Yes      | Yes       | "Webinar"                                                         | Filter for online webinars (UC-49)                  |
| 3                    | Networking Filter      | Button     | Yes      | Yes       | "Networking"                                                      | Filter for networking events (UC-49)                |
| 3                    | Online Filter          | Button     | Yes      | Yes       | "Online"                                                          | Filter for all online events (UC-49)                |
| **Featured Events**  |
| 4                    | Featured Title         | Heading    | No       | Yes       | "Sự kiện nổi bật"                                                 | Section title for featured events                   |
| 4                    | Featured Event Card    | Card       | Yes      | Yes       | Event Card                                                        | Large featured event card (UC-49)                   |
| 4                    | Event Date Badge       | Badge      | Yes      | Yes       | Date Format                                                       | Green date badge with "DD MMM" format               |
| 4                    | Event Title            | Heading    | Yes      | Yes       | Event Name                                                        | H3 heading, SEO optimized, clickable (UC-49)        |
| 4                    | Event Details          | Text Group | Yes      | Yes       | Location, Time, Participants                                      | Event metadata with icons                           |
| 4                    | Event Description      | Text       | Yes      | Yes       | Event Description                                                 | Brief event content and benefits                    |
| 4                    | Event Type Tags        | Badge      | Yes      | Yes       | Type Badges                                                       | Color-coded event type tags                         |
| 4                    | Register Button        | Button     | Yes      | Yes       | "Đăng ký tham gia"                                                | Event registration CTA (UC-49)                      |
| **Upcoming Events**  |
| 5                    | Upcoming Title         | Heading    | No       | Yes       | "Sự kiện sắp tới"                                                 | Section title for upcoming events                   |
| 5                    | Events Grid            | Container  | Yes      | Yes       | 3-column Grid                                                     | Grid layout for event cards                         |
| 5                    | Event Card             | Card       | Yes      | Yes       | Event Card                                                        | Individual event card with details (UC-49)          |
| 5                    | View All Events Link   | Link       | Yes      | No        | "Xem tất cả sự kiện"                                              | Link to complete events calendar                    |
| **Event Statistics** |
| 6                    | Statistics Title       | Heading    | No       | Yes       | "Thống kê sự kiện"                                                | Section title for stats showcase                    |
| 6                    | Stats Grid             | Container  | No       | Yes       | 4-column Grid                                                     | Grid for statistics cards                           |
| 6                    | Events Count Stat      | Card       | Yes      | Yes       | "120+ Sự kiện đã tổ chức"                                         | Total events organized statistic                    |
| 6                    | Participants Stat      | Card       | Yes      | Yes       | "15,000+ Người tham gia"                                          | Total participants statistic                        |
| 6                    | Companies Stat         | Card       | Yes      | Yes       | "500+ Công ty tham gia"                                           | Partner companies statistic                         |
| 6                    | Jobs Created Stat      | Card       | Yes      | Yes       | "3,200+ Việc làm được tạo"                                        | Jobs created through events                         |
| **Event Types**      |
| 7                    | Event Types Title      | Heading    | No       | Yes       | "Loại hình sự kiện"                                               | Section title for event type descriptions           |
| 7                    | Event Types Grid       | Container  | No       | Yes       | 2x2 Grid                                                          | Grid for event type info cards                      |
| 7                    | Job Fair Description   | Card       | No       | Yes       | Info Card                                                         | Job Fair description with features list (UC-49)     |
| 7                    | Workshop Description   | Card       | No       | Yes       | Info Card                                                         | Workshop description with skill topics (UC-49)      |
| 7                    | Webinar Description    | Card       | No       | Yes       | Info Card                                                         | Webinar description with online format info (UC-49) |
| 7                    | Networking Description | Card       | No       | Yes       | Info Card                                                         | Networking description with benefits (UC-49)        |
| **CTA Section**      |
| 8                    | CTA Section            | Section    | No       | Yes       | Newsletter CTA                                                    | Event notifications signup section                  |
| 8                    | CTA Title              | Heading    | No       | Yes       | "Đăng ký nhận thông báo sự kiện"                                  | Event notifications signup title (UC-51)            |
| 8                    | Email Signup Form      | Form       | Yes      | Yes       | Email Form                                                        | Email input and submit for event alerts (UC-51)     |
| 8                    | Social Media Links     | Link Group | Yes      | Yes       | Social Links                                                      | Facebook, LinkedIn, YouTube, Instagram links        |
| **Footer Section**   |
| 9                    | Footer Container       | Container  | No       | Yes       | Standard Footer                                                   | Consistent footer layout                            |

---

## Technical Implementation Notes

### Responsive Design

- **Breakpoints:** 768px (mobile), 1024px (tablet), 1200px (desktop)
- **Grid Systems:** CSS Grid for card layouts, Flexbox for navigation
- **Mobile-First:** Progressive enhancement approach

### Performance Optimization

- **Lazy Loading:** Images and content below fold
- **Caching:** Static assets caching with CDN
- **Minification:** CSS/JS compression in production

### SEO Considerations

- **Meta Tags:** Optimized title, description, keywords
- **Structured Data:** Schema.org markup for jobs, events, articles
- **Semantic HTML:** Proper heading hierarchy and landmarks

### Accessibility (WCAG 2.1 AA)

- **Keyboard Navigation:** Tab order and focus management
- **Screen Readers:** ARIA labels and semantic markup
- **Color Contrast:** Minimum 4.5:1 ratio for text
- **Alternative Text:** Descriptive alt text for images

### Security Implementation

- **Input Validation:** Client and server-side validation
- **CSRF Protection:** Token-based protection for forms
- **XSS Prevention:** Content sanitization and CSP headers
- **Authentication:** OAuth2 integration for Google login

### Use Case Integration

- **UC-01:** Homepage and navigation functionality
- **UC-02:** User registration system
- **UC-03/UC-04:** Login with email and Gmail OAuth
- **UC-07/UC-08:** Job search and view functionality
- **UC-20:** CV creation and management
- **UC-48:** Career resources and blog content
- **UC-49:** Event management and registration
- **UC-51:** Notification and newsletter systems
