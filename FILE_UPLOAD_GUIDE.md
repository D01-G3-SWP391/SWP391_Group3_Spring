# 📁 Hướng Dẫn Xử Lý File Upload & Hiển Thị Ảnh

## 🎯 Tổng Quan

Dự án này hỗ trợ upload và hiển thị file CV (PDF, hình ảnh) với validation frontend và backend hoàn chỉnh.

## 📋 Mục Lục

1. [Cấu Trúc Thư Mục](#cấu-trúc-thư-mục)
2. [🔄 Luồng Chạy Chi Tiết](#luồng-chạy-chi-tiết)
3. [Frontend Validation](#frontend-validation)
4. [Backend Processing](#backend-processing)
5. [File Serving](#file-serving)
6. [Security Configuration](#security-configuration)
7. [Troubleshooting](#troubleshooting)

---

## 🔄 Luồng Chạy Chi Tiết

### 📤 **PHẦN 1: QUY TRÌNH UPLOAD FILE**

#### Bước 1: User Tương Tác với Form
```html
<!-- User nhìn thấy form này -->
<form th:action="@{/Student/addJobApplication}" method="post" enctype="multipart/form-data">
    <input type="file" id="cv" name="cv" accept=".pdf,.png,.jpg,.jpeg,.gif" onchange="validateCVFile(this)"/>
</form>
```

**🔍 Điều gì xảy ra:**
- User click vào input file
- Browser mở dialog chọn file
- User chọn file từ máy tính
- Event `onchange="validateCVFile(this)"` được trigger

---

#### Bước 2: Frontend Validation Ngay Lập Tức
```javascript
function validateCVFile(input) {
    const file = input.files[0]; // Lấy file đầu tiên user chọn
    
    // Bước 2a: Kiểm tra loại file
    const allowedTypes = ['application/pdf', 'image/png', 'image/jpg', 'image/jpeg', 'image/gif'];
    if (!allowedTypes.includes(file.type)) {
        // ❌ File không hợp lệ
        showError("Chỉ chấp nhận PDF và ảnh");
        input.value = ''; // Xóa file khỏi input
        return false;
    }
    
    // Bước 2b: Kiểm tra kích thước
    const maxSize = 2048 * 1024; // 2MB
    if (file.size > maxSize) {
        // ❌ File quá lớn
        showError("File không được vượt quá 2MB");
        input.value = ''; // Xóa file khỏi input
        return false;
    }
    
    // ✅ File hợp lệ - ẩn error messages
    hideError();
    return true;
}
```

**🔍 Điều gì xảy ra:**
- JavaScript đọc thông tin file (type, size, name)
- Kiểm tra `file.type` với danh sách cho phép
- Kiểm tra `file.size` với giới hạn 2MB
- Nếu fail: hiển thị error đỏ + xóa file
- Nếu pass: cho phép user tiếp tục

---

#### Bước 3: User Submit Form
```html
<!-- User click nút "Gửi" -->
<button type="submit" class="submit-btn">Gửi</button>
```

**🔍 Điều gì xảy ra:**
1. Browser tạo HTTP POST request
2. `enctype="multipart/form-data"` cho phép gửi file
3. Form data bao gồm:
   - Text fields: fullName, email, phone, description
   - File field: cv (binary data)
   - Hidden fields: studentId, jobPostId

**📊 Request Structure:**
```http
POST /Student/addJobApplication HTTP/1.1
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary...

------WebKitFormBoundary...
Content-Disposition: form-data; name="fullName"

Nguyễn Văn A
------WebKitFormBoundary...
Content-Disposition: form-data; name="cv"; filename="my-cv.pdf"
Content-Type: application/pdf

[BINARY FILE DATA]
------WebKitFormBoundary...
```

---

#### Bước 4: Spring Boot Controller Nhận Request
```java
@PostMapping("/addJobApplication")
public String addJobApplication(
    @ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
    @RequestParam("cv") MultipartFile cvFile, // ← File được map vào đây
    Model model, RedirectAttributes redirectAttributes) {
```

**🔍 Điều gì xảy ra:**
1. **Spring MVC** parse multipart request
2. **Text data** → `JobApplicationDTO` object
3. **File data** → `MultipartFile cvFile` object
4. Controller method được execute

**📊 MultipartFile Object chứa:**
```java
cvFile.getOriginalFilename() // "my-cv.pdf"
cvFile.getSize()            // 1024000 (bytes)
cvFile.getContentType()     // "application/pdf"
cvFile.getInputStream()     // Binary stream để đọc file
```

---

#### Bước 5: Xử Lý File Upload trong Controller
```java
// Bước 5a: Kiểm tra file có tồn tại không
if (cvFile != null && !cvFile.isEmpty()) {
    
    // Bước 5b: Tạo thư mục nếu chưa có
    String uploadDir = "uploads/cv/";
    Path uploadPath = Paths.get(uploadDir);
    if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath); // Tạo thư mục uploads/cv/
    }
    
    // Bước 5c: Tạo tên file unique
    String fileName = UUID.randomUUID().toString() + "_" + cvFile.getOriginalFilename();
    // Kết quả: "a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-cv.pdf"
    
    // Bước 5d: Tạo đường dẫn file đầy đủ
    Path filePath = uploadPath.resolve(fileName);
    // Kết quả: "uploads/cv/a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-cv.pdf"
    
    // Bước 5e: Lưu file vào disk
    Files.copy(cvFile.getInputStream(), filePath);
    // File binary data được ghi vào đường dẫn trên
    
    // Bước 5f: Lưu đường dẫn để insert database
    cvUrl = uploadDir + fileName; // "uploads/cv/a1b2c3d4-..."
}
```

**🔍 Điều gì xảy ra:**
1. **Check file**: Đảm bảo user đã chọn file
2. **Create directory**: Tạo folder `uploads/cv/` nếu chưa có
3. **Generate unique name**: UUID + tên gốc tránh trùng lặp
4. **Save to disk**: Ghi binary data từ memory ra file system
5. **Store path**: Lưu đường dẫn string để insert database

**📁 File System sau khi save:**
```
uploads/
└── cv/
    └── a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-cv.pdf  ← File vừa lưu
```

---

#### Bước 6: Lưu thông tin vào Database
```java
// Bước 6a: Tạo JobApplication object
JobApplication jobApplication = new JobApplication();
jobApplication.setFullName(jobApplicationDTO.getFullname());
jobApplication.setEmail(jobApplicationDTO.getEmail());
jobApplication.setPhone(jobApplicationDTO.getPhoneNumber());
jobApplication.setCvUrl(cvUrl); // ← Đường dẫn file
jobApplication.setDescription(jobApplicationDTO.getDescription());

// Bước 6b: Set relationships
jobApplication.setStudent(student.get());
jobApplication.setJobPost(jobPost.get());

// Bước 6c: Save vào database
jobApplicationService.save(jobApplication);
```

**🔍 Điều gì xảy ra:**
1. **Create entity**: Tạo object JobApplication
2. **Map data**: Copy dữ liệu từ DTO sang entity
3. **Set cv_url**: Lưu đường dẫn file (không phải file binary)
4. **Set references**: Link với Student và JobPost
5. **Insert database**: Hibernate execute INSERT SQL

**📊 Database record được tạo:**
```sql
INSERT INTO Job_application (
    student_id, job_post_id, full_name, email, phone, 
    description, cv_url, applied_at, status
) VALUES (
    123, 456, 'Nguyễn Văn A', 'email@test.com', '0123456789',
    'Tôi muốn ứng tuyển...', 'uploads/cv/a1b2c3d4-..._my-cv.pdf',
    '2025-06-11 20:30:00', 'SUBMITTED'
);
```

---

### 👁️ **PHẦN 2: QUY TRÌNH XEM FILE**

#### Bước 7: User Xem Danh Sách Ứng Tuyển
```java
@GetMapping("/applications")
public String viewApplications(Model model, Principal principal) {
    // Bước 7a: Lấy thông tin user hiện tại
    String email = principal.getName();
    Account account = IAccountService.findByEmail(email);
    
    // Bước 7b: Tìm student từ account
    Student student = studentService.findByAccountUserId(account.getUserId());
    
    // Bước 7c: Lấy danh sách ứng tuyển của student
    List<JobApplication> applications = iJobApplicationService.findByStudentId(student.getStudentId());
    
    // Bước 7d: Truyền data cho template
    model.addAttribute("applications", applications);
    return "student/my-applications";
}
```

**🔍 Điều gì xảy ra:**
1. **Get current user**: Từ Spring Security session
2. **Find student**: Query database bằng account ID
3. **Load applications**: Query tất cả applications của student
4. **Pass to view**: Thymeleaf template nhận list applications

**📊 SQL Queries được execute:**
```sql
-- Bước 7a
SELECT * FROM account WHERE email = 'user@email.com';

-- Bước 7b  
SELECT * FROM student WHERE account_user_id = 123;

-- Bước 7c
SELECT ja.*, jp.*, e.* FROM job_application ja 
JOIN job_post jp ON ja.job_post_id = jp.id
JOIN employer e ON jp.employer_id = e.id  
WHERE ja.student_id = 456;
```

---

#### Bước 8: Template Render Links "Xem CV"
```html
<!-- Thymeleaf loop qua từng application -->
<div th:each="jobApp : ${applications}" class="application-card">
    
    <!-- Các thông tin khác... -->
    
    <!-- Link xem CV -->
    <a th:href="@{/{cvPath}(cvPath=${jobApp.cvUrl})}"
       target="_blank" class="btn btn-success"
       th:if="${jobApp.cvUrl != null and !#strings.isEmpty(jobApp.cvUrl)}">
        <i class="bi bi-file-earmark-pdf-fill"></i>
        <span>Xem CV</span>
    </a>
</div>
```

**🔍 Điều gì xảy ra:**
1. **Thymeleaf loop**: Iterate qua từng JobApplication
2. **Check cv_url**: Chỉ hiện link nếu có file CV
3. **Generate URL**: `@{/{cvPath}(cvPath=${jobApp.cvUrl})}` 
4. **Result**: `href="/uploads/cv/a1b2c3d4-..._my-cv.pdf"`

**📊 HTML được render:**
```html
<a href="/uploads/cv/a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-cv.pdf" 
   target="_blank" class="btn btn-success">
    <i class="bi bi-file-earmark-pdf-fill"></i>
    <span>Xem CV</span>
</a>
```

---

#### Bước 9: User Click "Xem CV"
**🔍 Điều gì xảy ra:**
1. **Browser**: Tạo GET request đến URL
2. **New tab**: `target="_blank"` mở tab mới
3. **Request**: `GET /uploads/cv/a1b2c3d4-..._my-cv.pdf`

**📊 HTTP Request:**
```http
GET /uploads/cv/a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-cv.pdf HTTP/1.1
Host: localhost:8080
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
```

---

#### Bước 10: Spring Boot Route Request
```java
// Spring tìm controller mapping với pattern "/uploads/cv/{filename:.+}"
@Controller
@RequestMapping("/uploads")
public class FileController {
    
    @GetMapping("/cv/{filename:.+}")  // ← Pattern match!
    public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
        // filename = "a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-cv.pdf"
```

**🔍 Điều gì xảy ra:**
1. **Spring Router**: Tìm controller có pattern phù hợp
2. **Extract filename**: PathVariable từ URL
3. **Call method**: Execute downloadCVFile()

---

#### Bước 11: FileController Xử Lý Request
```java
@GetMapping("/cv/{filename:.+}")
public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
    // Bước 11a: Xây dựng đường dẫn file đầy đủ
    String workingDir = System.getProperty("user.dir");
    // workingDir = "C:\Users\ADMIN\Desktop\SWP391_D01_G3"
    
    Path filePath = Paths.get(workingDir, "uploads", "cv", filename).normalize();
    // filePath = "C:\Users\ADMIN\Desktop\SWP391_D01_G3\uploads\cv\a1b2c3d4-..._my-cv.pdf"
    
    // Bước 11b: Kiểm tra file có tồn tại không
    File file = filePath.toFile();
    if (!file.exists()) {
        return ResponseEntity.notFound().build(); // HTTP 404
    }
    
    // Bước 11c: Tạo Resource object
    Resource resource = new UrlResource(filePath.toUri());
    
    // Bước 11d: Xác định Content-Type
    String contentType = Files.probeContentType(filePath);
    if (contentType == null) {
        if (filename.toLowerCase().endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (filename.toLowerCase().endsWith(".jpg")) {
            contentType = "image/jpeg";
        }
        // ... other types
    }
    
    // Bước 11e: Trả về ResponseEntity với file stream
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
            .body(resource);
}
```

**🔍 Điều gì xảy ra:**
1. **Build file path**: Từ working directory + uploads/cv/ + filename
2. **Check existence**: File có tồn tại trên disk không
3. **Create Resource**: Spring Resource object để stream file
4. **Detect MIME type**: application/pdf, image/jpeg, etc.
5. **Return response**: HTTP 200 với file content

---

#### Bước 12: Browser Nhận Response và Hiển Thị
**📊 HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/pdf
Content-Disposition: inline; filename="a1b2c3d4-..._my-cv.pdf"
Content-Length: 1024000

[BINARY FILE DATA]
```

**🔍 Điều gì xảy ra:**
1. **Browser nhận headers**:
   - `Content-Type: application/pdf` → Browser biết đây là PDF
   - `Content-Disposition: inline` → Hiển thị trong browser (không download)
2. **Browser đọc binary data**: Stream file content
3. **Render file**:
   - PDF: Mở PDF viewer built-in
   - Image: Hiển thị ảnh trực tiếp
   - Other: Download hoặc hiển thị tùy type

---

## 📊 **TÓM TẮT LUỒNG DỮ LIỆU**

### Upload Flow:
```
User chọn file → Frontend validation → Form submit → 
Controller nhận MultipartFile → Save file to disk → 
Save path to database → Success response
```

### View Flow:
```
User click "Xem CV" → GET request với filename → 
FileController tìm file trên disk → Stream file binary → 
Browser hiển thị file
```

### Data Storage:
```
Database: chỉ lưu STRING path "uploads/cv/filename.pdf"
File System: lưu BINARY file content tại đường dẫn đó
```

### Security & Validation:
```
Frontend: File type + size validation với JavaScript
Backend: Path normalization + existence check
Spring Security: Public access cho /uploads/**
```

---

## 📂 Cấu Trúc Thư Mục

```
SWP391_D01_G3/
├── uploads/
│   └── cv/
│       ├── [UUID]_[filename].pdf
│       ├── [UUID]_[filename].jpg
│       ├── [UUID]_[filename].png
│       └── ...
├── src/main/java/
│   └── com/example/swp391_d01_g3/
│       ├── controller/
│       │   ├── FileController.java
│       │   └── student/AddJobApplication.java
│       └── configuration/
│           ├── SecurityConfig.java
│           └── ConfigI18N.java
└── src/main/resources/
    ├── templates/
    │   ├── homePage/
    │   │   ├── applyForm.html
    │   │   └── descriptionJob.html
    │   └── student/
    │       └── my-applications.html
    └── static/css/
        └── apply-form.css
```

---

## ✅ Frontend Validation

### 🎨 HTML Input với Validation

```html
<!-- File Upload Input -->
<div class="form-group">
    <label for="cv">Hồ sơ ứng viên (CV)</label>
    <input type="file" id="cv" name="cv" 
           accept=".pdf,.png,.jpg,.jpeg,.gif" 
           onchange="validateCVFile(this)"/>
    <p class="file-info">Hỗ trợ định dạng PDF và ảnh (png, jpg, jpeg, gif) nhỏ hơn 2048KB</p>
    <div id="cv-error" class="file-error" style="display: none;"></div>
</div>
```

### 🔍 JavaScript Validation

```javascript
function validateCVFile(input) {
    const errorDiv = document.getElementById('cv-error');
    const allowedTypes = ['application/pdf', 'image/png', 'image/jpg', 'image/jpeg', 'image/gif'];
    const maxSize = 2048 * 1024; // 2048KB
    
    // Clear previous errors
    errorDiv.style.display = 'none';
    errorDiv.textContent = '';
    input.setCustomValidity('');
    
    if (input.files.length === 0) {
        return true; // No file selected is okay
    }
    
    const file = input.files[0];
    
    // Check file type
    if (!allowedTypes.includes(file.type)) {
        const errorMessage = 'Chỉ chấp nhận file PDF và ảnh (PNG, JPG, JPEG, GIF)';
        showFileError(errorDiv, errorMessage);
        input.setCustomValidity(errorMessage);
        input.value = ''; // Clear the input
        return false;
    }
    
    // Check file size
    if (file.size > maxSize) {
        const errorMessage = 'Kích thước file không được vượt quá 2048KB';
        showFileError(errorDiv, errorMessage);
        input.setCustomValidity(errorMessage);
        input.value = ''; // Clear the input
        return false;
    }
    
    return true;
}

function showFileError(errorDiv, message) {
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}
```

### 🎨 CSS Styling cho Error Messages

```css
.file-error {
    font-size: 12px;
    color: #e74c3c;
    margin-top: 8px;
    padding: 8px 12px;
    background-color: #fdf2f2;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    font-weight: 500;
}
```

---

## ⚙️ Backend Processing

### 📤 Upload Controller

```java
@Controller
@RequestMapping("/Student")
public class AddJobApplication {
    
    @PostMapping("/addJobApplication")
    public String addJobApplication(
        @ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
        @RequestParam("cv") MultipartFile cvFile,
        Model model, RedirectAttributes redirectAttributes) {
        
        // Xử lý file upload
        String cvUrl = null;
        if (cvFile != null && !cvFile.isEmpty()) {
            try {
                // Tạo thư mục uploads nếu chưa có
                String uploadDir = "uploads/cv/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Tạo tên file unique để tránh trùng lặp
                String fileName = UUID.randomUUID().toString() + "_" + cvFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                
                // Lưu file vào thư mục
                Files.copy(cvFile.getInputStream(), filePath);
                cvUrl = uploadDir + fileName; // Lưu path vào database
                
            } catch (IOException e) {
                model.addAttribute("error", "Lỗi khi tải lên file CV: " + e.getMessage());
                return "homePage/applyForm";
            }
        }
        
        // Lưu đường dẫn file vào database
        jobApplication.setCvUrl(cvUrl);
        
        // Save to database...
        return "redirect:/";
    }
}
```

### 🗄️ Database Schema

```sql
CREATE TABLE Job_application (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    job_post_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    description TEXT,
    cv_url VARCHAR(255),  -- Lưu đường dẫn file: "uploads/cv/[UUID]_[filename]"
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SUBMITTED', 'INTERVIEW', 'ACCEPTED', 'REJECTED') DEFAULT 'SUBMITTED'
);
```

---

## 🌐 File Serving

### 📁 FileController cho Static Files

```java
@Controller
@RequestMapping("/uploads")
public class FileController {

    @GetMapping("/test")
    @ResponseBody
    public String testFileAccess() {
        // Debug endpoint để kiểm tra file system
        String workingDir = System.getProperty("user.dir");
        Path cvDir = Paths.get(workingDir, "uploads", "cv");
        // ... debug logic
    }

    @GetMapping("/cv/{filename:.+}")
    public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
        try {
            // Sử dụng absolute path từ working directory
            String workingDir = System.getProperty("user.dir");
            Path filePath = Paths.get(workingDir, "uploads", "cv", filename).normalize();
            
            File file = filePath.toFile();
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            // Xác định content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                String lowerFilename = filename.toLowerCase();
                if (lowerFilename.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (lowerFilename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (lowerFilename.endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "application/octet-stream";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
```

### 🔗 Resource Handler Configuration

```java
@Configuration
public class ConfigI18N implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để serve files từ thư mục uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600); // Cache trong 1 tiếng
    }
}
```

### 🖼️ Hiển Thị File trong Template

```html
<!-- Link xem CV trong danh sách ứng tuyển -->
<a th:href="@{/{cvPath}(cvPath=${jobApp.cvUrl})}"
   target="_blank" class="btn btn-success"
   th:if="${jobApp.cvUrl != null and !#strings.isEmpty(jobApp.cvUrl)}">
    <i class="bi bi-file-earmark-pdf-fill"></i>
    <span>Xem CV</span>
</a>
```

---

## 🔒 Security Configuration

### 🛡️ Spring Security Setup

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/css/**", "/js/**", "/images/**", 
                           "/uploads/**", "/favicon.ico", 
                           "/Login", "/*.css", "/*.js", 
                           "/HomePage/**", "/Register/**", 
                           "/ForgotPassword/**", "/Blog/**").permitAll()
            .requestMatchers("/Admin/**").hasRole("admin")
            .requestMatchers("/Employee/**").hasRole("employer") 
            .requestMatchers("/Student/**", "/JobDescription/**").hasRole("student")
            .anyRequest().authenticated()
        );
        
        return http.build();
    }
}
```

### ⚙️ Application Properties

```properties
# File upload configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true
```

---

## 🛠️ Troubleshooting

### ❌ Common Issues & Solutions

#### 1. **404 Error - File Not Found**
```bash
# Kiểm tra file tồn tại
ls -la uploads/cv/

# Kiểm tra quyền đọc
chmod 755 uploads/
chmod 644 uploads/cv/*
```

#### 2. **500 Error - Server Internal Error**
```java
// Thêm logging trong FileController
System.out.println("📁 Working directory: " + System.getProperty("user.dir"));
System.out.println("📊 File exists: " + file.exists());
```

#### 3. **Security Access Denied**
```java
// Đảm bảo pattern trong SecurityConfig
.requestMatchers("/uploads/**").permitAll()
```

#### 4. **Content Type Issues**
```java
// Force content type nếu auto-detection thất bại
if (filename.toLowerCase().endsWith(".jpg")) {
    contentType = "image/jpeg";
}
```

### 🔍 Debug Endpoints

```bash
# Test file system access
GET http://localhost:8080/uploads/test

# Test specific file
GET http://localhost:8080/uploads/cv/[filename]

# Check application logs
tail -f logs/application.log
```

### 📊 File Size Limits

| File Type | Max Size | Frontend | Backend |
|-----------|----------|----------|---------|
| PDF | 2048KB | ✅ | ✅ |
| JPG/JPEG | 2048KB | ✅ | ✅ |
| PNG | 2048KB | ✅ | ✅ |
| GIF | 2048KB | ✅ | ✅ |

---

## 🚀 Best Practices

### 1. **File Naming**
- ✅ Sử dụng UUID để tránh trùng lặp
- ✅ Giữ nguyên extension gốc
- ✅ Format: `[UUID]_[original_filename]`

### 2. **Security**
- ✅ Validate file type ở frontend & backend
- ✅ Limit file size
- ✅ Sanitize filename
- ✅ Store outside web root nếu có thể

### 3. **Performance**
- ✅ Set appropriate cache headers
- ✅ Use Content-Type detection
- ✅ Stream large files

### 4. **Error Handling**
- ✅ Graceful fallback for missing files
- ✅ User-friendly error messages
- ✅ Detailed server-side logging

---

## 📝 API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `POST` | `/Student/addJobApplication` | Upload CV file | Student |
| `GET` | `/uploads/cv/{filename}` | Download/View CV | Public |
| `GET` | `/uploads/test` | Debug file system | Public |
| `GET` | `/Student/applications` | View applications | Student |

---

## 🎯 Key Features

- ✅ **Frontend Validation**: JavaScript validation với real-time feedback
- ✅ **File Type Support**: PDF, JPG, PNG, GIF
- ✅ **Size Limitation**: Max 2048KB per file
- ✅ **Unique Naming**: UUID-based collision prevention
- ✅ **Security**: Spring Security integration
- ✅ **Debug Support**: Test endpoints và detailed logging
- ✅ **Responsive UI**: Error messages và progress indicators

---

*📅 Last Updated: June 2025*
*🔧 Version: 1.0.0* 