# 🔄 Luồng Chạy Chi Tiết - File Upload & Display System

## 📋 Tổng Quan Luồng

Hệ thống xử lý file upload và hiển thị gồm **2 luồng chính**:
1. **📤 Upload Flow**: User upload CV → Validation → Save file → Save DB
2. **👁️ Display Flow**: User xem CV → Load từ disk → Stream về browser

---

## 📤 **LUỒNG 1: UPLOAD FILE CV**

### 🎯 **Bước 1: User Tương Tác với Form**

**📍 Location**: `src/main/resources/templates/homePage/applyForm.html`

```html
<form th:action="@{/Student/addJobApplication}" method="post" enctype="multipart/form-data">
    <input type="file" id="cv" name="cv" 
           accept=".pdf,.png,.jpg,.jpeg,.gif" 
           onchange="validateCVFile(this)"/>
    <button type="submit">Gửi</button>
</form>
```

**🔍 Điều gì xảy ra:**
1. **User click** vào `<input type="file">`
2. **Browser mở** file dialog
3. **User chọn file** từ máy tính  
4. **Event trigger**: `onchange="validateCVFile(this)"` được gọi
5. **File object** được tạo trong browser memory

**📊 File Object chứa:**
```javascript
file.name = "my-resume.pdf"           // Tên file gốc
file.size = 1048576                   // Kích thước (bytes)  
file.type = "application/pdf"         // MIME type
file.lastModified = 1640995200000     // Timestamp
```

---

### ✅ **Bước 2: Frontend Validation**

**📍 Location**: `src/main/resources/templates/homePage/applyForm.html` (JavaScript section)

```javascript
function validateCVFile(input) {
    // Bước 2.1: Lấy file từ input
    const file = input.files[0];
    if (!file) return true; // Không có file thì OK
    
    // Bước 2.2: Định nghĩa rules
    const allowedTypes = ['application/pdf', 'image/png', 'image/jpg', 'image/jpeg', 'image/gif'];
    const maxSize = 2048 * 1024; // 2MB in bytes
    
    // Bước 2.3: Validate file type
    if (!allowedTypes.includes(file.type)) {
        showFileError("Chỉ chấp nhận file PDF và ảnh (PNG, JPG, JPEG, GIF)");
        input.value = ''; // Clear input
        return false;
    }
    
    // Bước 2.4: Validate file size  
    if (file.size > maxSize) {
        showFileError("Kích thước file không được vượt quá 2048KB");
        input.value = ''; // Clear input
        return false;
    }
    
    // Bước 2.5: All good - hide errors
    hideFileError();
    return true;
}
```

**🔍 Chi tiết xử lý:**

**2.1 - Truy cập File Object:**
```javascript
const file = input.files[0]; // FileList[0] → File object
console.log("File info:", {
    name: file.name,
    size: file.size,
    type: file.type
});
```

**2.2 - Type Validation:**
```javascript
// Browser tự động detect MIME type dựa trên file header
// Ví dụ:
// PDF file → "application/pdf"
// JPG file → "image/jpeg"  
// PNG file → "image/png"
// TXT file → "text/plain" ← Sẽ bị reject
```

**2.3 - Size Validation:**
```javascript
const sizeInMB = file.size / (1024 * 1024);
console.log(`File size: ${sizeInMB.toFixed(2)} MB`);
if (sizeInMB > 2) {
    // File quá lớn
}
```

**2.4 - Error Display:**
```javascript
function showFileError(message) {
    const errorDiv = document.getElementById('cv-error');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block'; // Show red error box
    input.setCustomValidity(message); // Browser validation API
}
```

---

### 📨 **Bước 3: Form Submission**

**🔍 Điều gì xảy ra khi user click "Gửi":**

**3.1 - Browser tạo HTTP Request:**
```http
POST /Student/addJobApplication HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="fullName"

Nguyễn Văn A
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="email"

nguyenvana@email.com
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="cv"; filename="my-resume.pdf"
Content-Type: application/pdf

%PDF-1.4
1 0 obj
<< /Type /Catalog /Pages 2 0 R >>
[BINARY PDF DATA...]
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

**3.2 - Multipart Encoding:**
- **Text fields**: Encoded as form-data parts
- **File field**: Binary data với original filename
- **Boundary**: Phân tách các parts

---

### ⚙️ **Bước 4: Spring Boot Controller Xử Lý**

**📍 Location**: `src/main/java/com/example/swp391_d01_g3/controller/student/AddJobApplication.java`

```java
@PostMapping("/addJobApplication")
public String addJobApplication(
    @ModelAttribute("jobApplicationDTO") JobApplicationDTO jobApplicationDTO,
    @RequestParam("cv") MultipartFile cvFile, // ← File được map vào đây
    Model model, RedirectAttributes redirectAttributes) {
```

**🔍 Chi tiết xử lý:**

**4.1 - Spring MVC Parsing:**
```java
// Spring tự động parse multipart request
// Text data → JobApplicationDTO (via @ModelAttribute)
// File data → MultipartFile (via @RequestParam)

System.out.println("Received file:");
System.out.println("- Name: " + cvFile.getOriginalFilename());
System.out.println("- Size: " + cvFile.getSize());
System.out.println("- Type: " + cvFile.getContentType());
System.out.println("- Empty: " + cvFile.isEmpty());
```

**4.2 - MultipartFile API:**
```java
public interface MultipartFile {
    String getOriginalFilename();  // "my-resume.pdf"
    long getSize();               // 1048576
    String getContentType();      // "application/pdf"
    boolean isEmpty();            // false
    InputStream getInputStream(); // Để đọc binary data
    void transferTo(File dest);   // Helper để save file
}
```

---

### 💾 **Bước 5: File Processing & Storage**

**📍 Location**: Trong `addJobApplication` method

```java
// Bước 5.1: Kiểm tra file có tồn tại
if (cvFile != null && !cvFile.isEmpty()) {
    try {
        // Bước 5.2: Tạo upload directory
        String uploadDir = "uploads/cv/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ Created directory: " + uploadPath.toAbsolutePath());
        }
        
        // Bước 5.3: Generate unique filename
        String originalName = cvFile.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + originalName;
        
        System.out.println("📝 Generated filename: " + fileName);
        
        // Bước 5.4: Create full file path
        Path filePath = uploadPath.resolve(fileName);
        System.out.println("📁 Full path: " + filePath.toAbsolutePath());
        
        // Bước 5.5: Save file to disk
        Files.copy(cvFile.getInputStream(), filePath);
        System.out.println("💾 File saved successfully");
        
        // Bước 5.6: Store relative path for database
        cvUrl = uploadDir + fileName;
        System.out.println("🔗 CV URL for DB: " + cvUrl);
        
    } catch (IOException e) {
        System.err.println("❌ File save error: " + e.getMessage());
        // Handle error...
    }
}
```

**🔍 Chi tiết từng bước:**

**5.2 - Directory Creation:**
```java
Path uploadPath = Paths.get("uploads/cv/");
// Kết quả: "uploads/cv/" (relative to working directory)

Files.createDirectories(uploadPath);
// Tạo cả folder uploads/ và cv/ nếu chưa có
```

**5.3 - Unique Filename Generation:**
```java
String uuid = UUID.randomUUID().toString();
// Kết quả: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

String originalName = cvFile.getOriginalFilename();
// Kết quả: "my-resume.pdf"

String fileName = uuid + "_" + originalName;
// Kết quả: "a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf"
```

**5.5 - File Writing:**
```java
Files.copy(cvFile.getInputStream(), filePath);
// Đọc binary stream từ memory → Ghi ra file trên disk
// Equivalent to:
try (InputStream input = cvFile.getInputStream();
     FileOutputStream output = new FileOutputStream(filePath.toFile())) {
    input.transferTo(output);
}
```

**📁 File System sau khi save:**
```
C:\Users\ADMIN\Desktop\SWP391_D01_G3\
└── uploads/
    └── cv/
        └── a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf
```

---

### 🗄️ **Bước 6: Database Storage**

```java
// Bước 6.1: Create JobApplication entity
JobApplication jobApplication = new JobApplication();

// Bước 6.2: Map form data
jobApplication.setFullName(jobApplicationDTO.getFullname());
jobApplication.setEmail(jobApplicationDTO.getEmail());
jobApplication.setPhone(jobApplicationDTO.getPhoneNumber());
jobApplication.setDescription(jobApplicationDTO.getDescription());

// Bước 6.3: Store file path (NOT binary data)
jobApplication.setCvUrl(cvUrl); // "uploads/cv/a1b2c3d4-..._my-resume.pdf"

// Bước 6.4: Set relationships
Optional<Student> student = studentService.findById(jobApplicationDTO.getStudentId());
Optional<JobPost> jobPost = iJobpostService.findByJobPostId(jobApplicationDTO.getJobPostId());
jobApplication.setStudent(student.get());
jobApplication.setJobPost(jobPost.get());

// Bước 6.5: Save to database
jobApplicationService.save(jobApplication);
```

**🔍 SQL được execute:**
```sql
INSERT INTO job_application (
    student_id, job_post_id, full_name, email, phone, 
    description, cv_url, applied_at, status
) VALUES (
    123,                                    -- student_id
    456,                                    -- job_post_id  
    'Nguyễn Văn A',                        -- full_name
    'nguyenvana@email.com',                -- email
    '0123456789',                          -- phone
    'Tôi muốn ứng tuyển vị trí này',       -- description
    'uploads/cv/a1b2c3d4-..._my-resume.pdf', -- cv_url (chỉ lưu path!)
    '2025-06-11 20:30:00',                 -- applied_at
    'SUBMITTED'                            -- status
);
```

**📊 Lưu ý quan trọng:**
- Database **KHÔNG lưu** binary file data
- Database **CHỈ lưu** đường dẫn (string path)
- File binary được lưu riêng trên file system

---

## 👁️ **LUỒNG 2: XEM FILE CV**

### 📄 **Bước 7: Load Danh Sách Applications**

**📍 Location**: `src/main/java/com/example/swp391_d01_g3/controller/student/StudentDashboard.java`

```java
@GetMapping("/applications")
public String viewApplications(Model model, Principal principal) {
    // Bước 7.1: Get current user from Spring Security
    String email = principal.getName(); // Từ session/JWT
    System.out.println("👤 Current user: " + email);
    
    // Bước 7.2: Find Account by email
    Account account = IAccountService.findByEmail(email);
    System.out.println("🆔 Account ID: " + account.getUserId());
    
    // Bước 7.3: Find Student linked to Account
    Student student = studentService.findByAccountUserId(account.getUserId());
    System.out.println("🎓 Student ID: " + student.getStudentId());
    
    // Bước 7.4: Load all applications of this student
    List<JobApplication> applications = iJobApplicationService.findByStudentId(student.getStudentId());
    System.out.println("📋 Found applications: " + applications.size());
    
    // Bước 7.5: Pass data to template
    model.addAttribute("applications", applications);
    return "student/my-applications";
}
```

**🔍 SQL Queries được execute:**

**7.2 - Find Account:**
```sql
SELECT * FROM account WHERE email = 'nguyenvana@email.com';
```

**7.3 - Find Student:**
```sql
SELECT * FROM student WHERE account_user_id = 123;
```

**7.4 - Load Applications với JOIN:**
```sql
SELECT 
    ja.*,           -- JobApplication fields
    jp.*,           -- JobPost fields  
    e.company_name  -- Employer company name
FROM job_application ja
LEFT JOIN job_post jp ON ja.job_post_id = jp.job_post_id
LEFT JOIN employer e ON jp.employer_id = e.employer_id
WHERE ja.student_id = 456;
```

**📊 Data được load:**
```java
List<JobApplication> applications = [
    JobApplication {
        applicationId: 1,
        fullName: "Nguyễn Văn A",
        email: "nguyenvana@email.com", 
        cvUrl: "uploads/cv/a1b2c3d4-..._my-resume.pdf", // ← Đường dẫn file
        jobPost: JobPost {
            jobTitle: "Java Developer",
            employer: Employer {
                companyName: "ABC Company"
            }
        }
    },
    // ... more applications
]
```

---

### 🖼️ **Bước 8: Template Rendering**

**📍 Location**: `src/main/resources/templates/student/my-applications.html`

```html
<div th:each="jobApp : ${applications}" class="application-card">
    <!-- Display job info -->
    <h2 th:text="${jobApp.jobPost?.jobTitle}">Job Title</h2>
    <span th:text="${jobApp.jobPost?.employer?.companyName}">Company</span>
    
    <!-- CV Link Generation -->
    <a th:href="@{/{cvPath}(cvPath=${jobApp.cvUrl})}"
       target="_blank" class="btn btn-success"
       th:if="${jobApp.cvUrl != null and !#strings.isEmpty(jobApp.cvUrl)}">
        <i class="bi bi-file-earmark-pdf-fill"></i>
        <span>Xem CV</span>
    </a>
</div>
```

**🔍 Thymeleaf Processing:**

**8.1 - Loop qua applications:**
```java
// Thymeleaf iterate qua List<JobApplication>
for (JobApplication jobApp : applications) {
    // Render từng application card
}
```

**8.2 - URL Generation:**
```html
<!-- Thymeleaf expression -->
th:href="@{/{cvPath}(cvPath=${jobApp.cvUrl})}"

<!-- Input data -->
jobApp.cvUrl = "uploads/cv/a1b2c3d4-..._my-resume.pdf"

<!-- Generated URL -->
href="/uploads/cv/a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf"
```

**8.3 - Conditional Rendering:**
```html
th:if="${jobApp.cvUrl != null and !#strings.isEmpty(jobApp.cvUrl)}"
<!-- Chỉ hiện link nếu có CV -->
```

**📊 Final HTML Output:**
```html
<div class="application-card">
    <h2>Java Developer</h2>
    <span>ABC Company</span>
    
    <a href="/uploads/cv/a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf"
       target="_blank" class="btn btn-success">
        <i class="bi bi-file-earmark-pdf-fill"></i>
        <span>Xem CV</span>
    </a>
</div>
```

---

### 🖱️ **Bước 9: User Click "Xem CV"**

**🔍 Browser Actions:**

**9.1 - Click Event:**
```javascript
// User click vào link
<a href="/uploads/cv/a1b2c3d4-..._my-resume.pdf" target="_blank">

// Browser tạo new tab và navigate
window.open("/uploads/cv/a1b2c3d4-..._my-resume.pdf", "_blank");
```

**9.2 - HTTP Request:**
```http
GET /uploads/cv/a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf HTTP/1.1
Host: localhost:8080
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Encoding: gzip, deflate
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
```

---

### 🎯 **Bước 10: Spring Route Matching**

**📍 Location**: Spring Boot routing system

```java
// Spring tìm controller mapping
@Controller
@RequestMapping("/uploads")  // Base path: /uploads
public class FileController {
    
    @GetMapping("/cv/{filename:.+}")  // Pattern: /cv/{anything}
    public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
        // filename = "a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf"
```

**🔍 Pattern Matching Process:**

**10.1 - URL Decomposition:**
```
Request URL: /uploads/cv/a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf

Base path: /uploads        ← @RequestMapping("/uploads")
Sub path:  /cv/{filename}  ← @GetMapping("/cv/{filename:.+}")
Filename:  a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf
```

**10.2 - PathVariable Extraction:**
```java
@PathVariable String filename
// Spring tự động extract từ URL
// filename = "a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf"
```

**10.3 - Regex Pattern `{filename:.+}`:**
```
.+ = match 1 hoặc nhiều ký tự bất kỳ (including dots)
Điều này cho phép filename có dấu chấm: "file.pdf", "image.jpg"
```

---

### 📁 **Bước 11: File Loading & Streaming**

**📍 Location**: `src/main/java/com/example/swp391_d01_g3/controller/FileController.java`

```java
@GetMapping("/cv/{filename:.+}")
public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
    try {
        // Bước 11.1: Build absolute file path
        String workingDir = System.getProperty("user.dir");
        Path filePath = Paths.get(workingDir, "uploads", "cv", filename).normalize();
        
        System.out.println("🔍 Looking for file: " + filePath.toAbsolutePath());
        
        // Bước 11.2: Check file existence
        File file = filePath.toFile();
        if (!file.exists()) {
            System.out.println("❌ File not found");
            return ResponseEntity.notFound().build(); // HTTP 404
        }
        
        System.out.println("✅ File found, size: " + file.length() + " bytes");
        
        // Bước 11.3: Create Spring Resource
        Resource resource = new UrlResource(filePath.toUri());
        
        // Bước 11.4: Detect content type
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            // Fallback to manual detection
            String lowerFilename = filename.toLowerCase();
            if (lowerFilename.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerFilename.endsWith(".png")) {
                contentType = "image/png";
            } else {
                contentType = "application/octet-stream";
            }
        }
        
        System.out.println("📄 Content type: " + contentType);
        
        // Bước 11.5: Build HTTP response
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
                
    } catch (Exception e) {
        System.err.println("💥 Error: " + e.getMessage());
        return ResponseEntity.internalServerError().build(); // HTTP 500
    }
}
```

**🔍 Chi tiết xử lý:**

**11.1 - Path Resolution:**
```java
String workingDir = System.getProperty("user.dir");
// workingDir = "C:\Users\ADMIN\Desktop\SWP391_D01_G3"

Path filePath = Paths.get(workingDir, "uploads", "cv", filename);
// filePath = "C:\Users\ADMIN\Desktop\SWP391_D01_G3\uploads\cv\a1b2c3d4-..._my-resume.pdf"

filePath.normalize(); // Remove any ".." or "." segments (security)
```

**11.3 - Spring Resource Creation:**
```java
Resource resource = new UrlResource(filePath.toUri());
// Resource là interface của Spring để handle file/stream
// UrlResource impl có thể đọc file từ file system
```

**11.4 - Content Type Detection:**
```java
String contentType = Files.probeContentType(filePath);
// Java NIO tự động detect dựa trên file header
// PDF: "application/pdf"
// JPG: "image/jpeg"  
// PNG: "image/png"
```

**11.5 - HTTP Response Headers:**
```java
.contentType(MediaType.parseMediaType(contentType))
// Set Content-Type header

.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
// inline = hiển thị trong browser (ko download)
// attachment = force download
```

---

### 📤 **Bước 12: HTTP Response Streaming**

**🔍 Response được gửi về browser:**

```http
HTTP/1.1 200 OK
Content-Type: application/pdf
Content-Length: 1048576
Content-Disposition: inline; filename="a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf"
Cache-Control: max-age=3600

%PDF-1.4
1 0 obj
<< /Type /Catalog
   /Pages 2 0 R
>>
endobj
[BINARY PDF DATA CONTINUES...]
```

**📊 Response Components:**

**Headers:**
- `Content-Type: application/pdf` → Browser biết cách xử lý
- `Content-Length: 1048576` → File size  
- `Content-Disposition: inline` → Hiển thị trong browser
- `Cache-Control: max-age=3600` → Cache 1 hour (từ ResourceHandler)

**Body:**
- **Binary stream** của file PDF/image
- Spring **tự động stream** từ disk → network
- **Không load** toàn bộ file vào memory

---

### 🖥️ **Bước 13: Browser Rendering**

**🔍 Browser xử lý response:**

**13.1 - Content Type Handling:**
```javascript
// Browser đọc Content-Type header
if (contentType === "application/pdf") {
    // Sử dụng built-in PDF viewer
    renderPDFViewer(responseBody);
} else if (contentType.startsWith("image/")) {
    // Hiển thị ảnh
    renderImage(responseBody);
}
```

**13.2 - PDF Display:**
```html
<!-- Browser tự động tạo PDF viewer -->
<embed src="blob:http://localhost:8080/a1b2c3d4-..." 
       type="application/pdf" 
       width="100%" height="100%">
```

**13.3 - Image Display:**
```html
<!-- Browser hiển thị ảnh trực tiếp -->
<img src="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEA..." 
     alt="CV Image">
```

---

## 📊 **TỔNG KẾT LUỒNG DỮ LIỆU**

### 🔄 **Data Flow Diagram:**

```
[User] → [Browser] → [Frontend JS] → [Spring Controller] → [File System]
   ↑                                                            ↓
   └─────────── [Browser Display] ← [HTTP Response] ← [FileController]
                        ↑                                       ↓  
                   [New Request] ← ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ [Database]
```

### 💾 **Storage Strategy:**

| Component | Stores What | Location |
|-----------|-------------|----------|
| **Database** | File path (string) | `job_application.cv_url` |
| **File System** | Binary file data | `uploads/cv/[uuid]_[filename]` |
| **Browser** | Temporary display | Memory/Cache |

### 🔐 **Security Layers:**

1. **Frontend**: File type + size validation
2. **Backend**: Path normalization + existence check  
3. **Spring Security**: URL pattern `/uploads/**` allowed
4. **File System**: Files stored outside webroot
5. **UUID Naming**: Prevents file name collision/guessing

### ⚡ **Performance Considerations:**

1. **Streaming**: Files không load hết vào memory
2. **Caching**: Browser cache + server cache headers
3. **Path Efficiency**: Direct file system access
4. **Database Optimization**: Chỉ lưu path, không lưu BLOB

---

*🎯 Đây là luồng chi tiết từng bước của hệ thống file upload & display!* 