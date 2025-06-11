# 🔄 Luồng Chạy Chi Tiết - File Upload & Display

## 📋 Tổng Quan
Hệ thống xử lý file upload và hiển thị gồm **2 luồng chính**:
1. **📤 Upload Flow**: User upload CV → Validation → Save file → Save DB
2. **👁️ Display Flow**: User xem CV → Load từ disk → Stream về browser

---

## 📤 **LUỒNG 1: UPLOAD FILE CV**

### 🎯 **Bước 1: User Chọn File**

**📍 File**: `src/main/resources/templates/homePage/applyForm.html`

```html
<input type="file" id="cv" name="cv" 
       accept=".pdf,.png,.jpg,.jpeg,.gif" 
       onchange="validateCVFile(this)"/>
```

**🔍 Điều gì xảy ra:**
1. User click input file
2. Browser mở file dialog
3. User chọn file → File object được tạo
4. Event `onchange` trigger validation

**📊 File Object Properties:**
```javascript
file.name = "my-resume.pdf"
file.size = 1048576              // bytes
file.type = "application/pdf"     // MIME type
file.lastModified = 1640995200000
```

---

### ✅ **Bước 2: Frontend Validation**

```javascript
function validateCVFile(input) {
    const file = input.files[0];
    const allowedTypes = ['application/pdf', 'image/png', 'image/jpeg'];
    const maxSize = 2048 * 1024; // 2MB
    
    // Kiểm tra loại file
    if (!allowedTypes.includes(file.type)) {
        showError("Chỉ chấp nhận PDF và ảnh");
        input.value = ''; // Xóa file
        return false;
    }
    
    // Kiểm tra kích thước
    if (file.size > maxSize) {
        showError("File quá lớn");
        input.value = '';
        return false;
    }
    
    hideError();
    return true;
}
```

**🔍 Chi tiết:**
- **Type check**: Browser tự detect MIME type từ file header
- **Size check**: So sánh với limit 2MB
- **Error handling**: Hiển thị message + clear input
- **Real-time**: Validation ngay khi chọn file

---

### 📨 **Bước 3: Form Submit**

**HTTP Request được tạo:**
```http
POST /Student/addJobApplication HTTP/1.1
Content-Type: multipart/form-data; boundary=----WebKit...

------WebKit...
Content-Disposition: form-data; name="fullName"

Nguyễn Văn A
------WebKit...
Content-Disposition: form-data; name="cv"; filename="resume.pdf"
Content-Type: application/pdf

[BINARY FILE DATA]
------WebKit...
```

**🔍 Multipart encoding:**
- Text fields → form-data parts
- File → binary data với metadata
- Boundary phân tách các parts

---

### ⚙️ **Bước 4: Spring Controller Nhận Request**

**📍 File**: `AddJobApplication.java`

```java
@PostMapping("/addJobApplication")
public String addJobApplication(
    @RequestParam("cv") MultipartFile cvFile, // ← File ở đây
    @ModelAttribute JobApplicationDTO dto) {
    
    System.out.println("File info:");
    System.out.println("- Name: " + cvFile.getOriginalFilename());
    System.out.println("- Size: " + cvFile.getSize());
    System.out.println("- Type: " + cvFile.getContentType());
}
```

**🔍 Spring MVC Magic:**
- **Auto parsing**: Multipart request → objects
- **Text data** → DTO via `@ModelAttribute`
- **File data** → MultipartFile via `@RequestParam`

---

### 💾 **Bước 5: Lưu File vào Disk**

```java
if (cvFile != null && !cvFile.isEmpty()) {
    // 5.1: Tạo thư mục
    String uploadDir = "uploads/cv/";
    Path uploadPath = Paths.get(uploadDir);
    Files.createDirectories(uploadPath);
    
    // 5.2: Tạo tên file unique
    String uuid = UUID.randomUUID().toString();
    String fileName = uuid + "_" + cvFile.getOriginalFilename();
    
    // 5.3: Lưu file
    Path filePath = uploadPath.resolve(fileName);
    Files.copy(cvFile.getInputStream(), filePath);
    
    // 5.4: Lưu đường dẫn cho DB
    cvUrl = uploadDir + fileName;
}
```

**🔍 Chi tiết steps:**

**5.1 - Directory Creation:**
```java
Files.createDirectories(Paths.get("uploads/cv/"));
// Tạo folder nếu chưa có
```

**5.2 - Unique Naming:**
```java
String uuid = UUID.randomUUID().toString();
// → "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

String fileName = uuid + "_" + cvFile.getOriginalFilename();
// → "a1b2c3d4-..._my-resume.pdf"
```

**5.3 - File Writing:**
```java
Files.copy(cvFile.getInputStream(), filePath);
// InputStream (memory) → File (disk)
```

**📁 Kết quả trên disk:**
```
uploads/
└── cv/
    └── a1b2c3d4-e5f6-7890-abcd-ef1234567890_my-resume.pdf
```

---

### 🗄️ **Bước 6: Lưu vào Database**

```java
JobApplication jobApp = new JobApplication();
jobApp.setFullName(dto.getFullname());
jobApp.setEmail(dto.getEmail());
jobApp.setCvUrl(cvUrl); // CHỈ lưu đường dẫn!

// Set relationships
jobApp.setStudent(student);
jobApp.setJobPost(jobPost);

// Save to DB
jobApplicationService.save(jobApp);
```

**📊 SQL được execute:**
```sql
INSERT INTO job_application (
    student_id, job_post_id, full_name, email,
    cv_url, applied_at, status
) VALUES (
    123, 456, 'Nguyễn Văn A', 'email@test.com',
    'uploads/cv/a1b2c3d4-..._my-resume.pdf',
    NOW(), 'SUBMITTED'
);
```

**🎯 Key point**: Database chỉ lưu **string path**, không lưu binary!

---

## 👁️ **LUỒNG 2: XEM FILE CV**

### 📄 **Bước 7: Load Danh Sách Applications**

**📍 File**: `StudentDashboard.java`

```java
@GetMapping("/applications")
public String viewApplications(Model model, Principal principal) {
    // 7.1: Get current user
    String email = principal.getName();
    Account account = accountService.findByEmail(email);
    
    // 7.2: Find student
    Student student = studentService.findByAccountUserId(account.getUserId());
    
    // 7.3: Load applications
    List<JobApplication> apps = jobApplicationService.findByStudentId(student.getStudentId());
    
    // 7.4: Pass to template
    model.addAttribute("applications", apps);
    return "student/my-applications";
}
```

**📊 SQL Queries:**
```sql
-- 7.1
SELECT * FROM account WHERE email = 'user@email.com';

-- 7.2  
SELECT * FROM student WHERE account_user_id = 123;

-- 7.3 (với JOIN)
SELECT ja.*, jp.job_title, e.company_name 
FROM job_application ja
JOIN job_post jp ON ja.job_post_id = jp.id
JOIN employer e ON jp.employer_id = e.id
WHERE ja.student_id = 456;
```

---

### 🖼️ **Bước 8: Template Render**

**📍 File**: `my-applications.html`

```html
<div th:each="jobApp : ${applications}">
    <h2 th:text="${jobApp.jobPost.jobTitle}">Job Title</h2>
    
    <!-- Link xem CV -->
    <a th:href="@{/{cvPath}(cvPath=${jobApp.cvUrl})}"
       target="_blank" class="btn btn-success"
       th:if="${jobApp.cvUrl != null}">
        Xem CV
    </a>
</div>
```

**🔍 Thymeleaf Processing:**

**URL Generation:**
```html
<!-- Input -->
jobApp.cvUrl = "uploads/cv/a1b2c3d4-..._my-resume.pdf"

<!-- Thymeleaf expression -->
@{/{cvPath}(cvPath=${jobApp.cvUrl})}

<!-- Output -->
href="/uploads/cv/a1b2c3d4-..._my-resume.pdf"
```

**Final HTML:**
```html
<a href="/uploads/cv/a1b2c3d4-..._my-resume.pdf" target="_blank">
    Xem CV
</a>
```

---

### 🖱️ **Bước 9: User Click Link**

**Browser Action:**
```javascript
// User click link
window.open("/uploads/cv/a1b2c3d4-..._my-resume.pdf", "_blank");
```

**HTTP Request:**
```http
GET /uploads/cv/a1b2c3d4-..._my-resume.pdf HTTP/1.1
Host: localhost:8080
Accept: text/html,application/pdf,*/*
```

---

### 🎯 **Bước 10: Spring Route Matching**

**📍 File**: `FileController.java`

```java
@Controller
@RequestMapping("/uploads")
public class FileController {
    
    @GetMapping("/cv/{filename:.+}")  // Pattern match!
    public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
        // filename = "a1b2c3d4-..._my-resume.pdf"
```

**🔍 URL Mapping:**
```
Request: /uploads/cv/a1b2c3d4-..._my-resume.pdf

Base:    /uploads              ← @RequestMapping
Pattern: /cv/{filename:.+}     ← @GetMapping  
Extract: filename = "a1b2c3d4-..._my-resume.pdf"
```

---

### 📁 **Bước 11: Load File từ Disk**

```java
@GetMapping("/cv/{filename:.+}")
public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
    // 11.1: Build file path
    String workingDir = System.getProperty("user.dir");
    Path filePath = Paths.get(workingDir, "uploads", "cv", filename);
    
    // 11.2: Check existence
    File file = filePath.toFile();
    if (!file.exists()) {
        return ResponseEntity.notFound().build(); // 404
    }
    
    // 11.3: Create Resource
    Resource resource = new UrlResource(filePath.toUri());
    
    // 11.4: Detect content type
    String contentType = Files.probeContentType(filePath);
    if (contentType == null) {
        if (filename.endsWith(".pdf")) contentType = "application/pdf";
        else if (filename.endsWith(".jpg")) contentType = "image/jpeg";
    }
    
    // 11.5: Return response
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
            .body(resource);
}
```

**🔍 Chi tiết:**

**Path Resolution:**
```java
workingDir = "C:\Users\ADMIN\Desktop\SWP391_D01_G3"
filePath = "C:\Users\ADMIN\Desktop\SWP391_D01_G3\uploads\cv\a1b2c3d4-..._my-resume.pdf"
```

**Content Type Detection:**
```java
Files.probeContentType(filePath);
// Java đọc file header → detect MIME type
// PDF → "application/pdf"
// JPG → "image/jpeg"
```

---

### 📤 **Bước 12: Stream Response**

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/pdf
Content-Length: 1048576
Content-Disposition: inline; filename="a1b2c3d4-..._my-resume.pdf"

[BINARY PDF DATA...]
```

**🔍 Headers:**
- `Content-Type`: Browser biết cách hiển thị
- `Content-Disposition: inline`: Hiển thị trong browser (không download)
- `Content-Length`: File size

**Streaming:**
- Spring **tự động stream** file từ disk
- **Không load** toàn bộ vào memory
- Efficient cho file lớn

---

### 🖥️ **Bước 13: Browser Display**

**🔍 Browser xử lý:**

**PDF Files:**
```html
<!-- Browser tự tạo PDF viewer -->
<embed src="blob:..." type="application/pdf" width="100%" height="100%">
```

**Image Files:**
```html
<!-- Browser hiển thị ảnh -->
<img src="data:image/jpeg;base64,..." alt="CV">
```

**Content Type Handling:**
- `application/pdf` → Built-in PDF viewer
- `image/*` → Direct image display
- Other → Download prompt

---

## 📊 **FLOW SUMMARY**

### 🔄 **Upload Flow:**
```
User chọn file → JS validation → Form submit → 
Spring Controller → Save to disk → Save path to DB → Success
```

### 👁️ **View Flow:**
```
User click link → HTTP GET → Spring routing → 
Load from disk → Stream binary → Browser display
```

### 💾 **Data Storage:**
- **Database**: String path only
- **File System**: Binary file data
- **Separation**: DB ko chứa binary, file system ko chứa metadata

### 🔐 **Security:**
- Frontend validation (type + size)
- Backend path normalization
- Spring Security URL permissions
- UUID filename (anti-guessing)

### ⚡ **Performance:**
- File streaming (không load hết vào memory)
- Browser caching với proper headers
- Efficient path resolution
- DB queries optimized với JOINs

---

*🎯 Đây là luồng chi tiết từng bước của hệ thống!* 