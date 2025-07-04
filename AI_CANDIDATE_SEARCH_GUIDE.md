# 🤖 AI Candidate Search - Complete Guide

## 📖 Overview

AI Candidate Search là tính năng thông minh cho phép nhà tuyển dụng tìm kiếm ứng viên bằng **natural language** (ngôn ngữ tự nhiên) thay vì điền form truyền thống.

### ✨ Key Features
- 🗣️ **Natural Language Search** - Tìm kiếm bằng tiếng Việt/Anh
- 🧠 **AI-Powered** - Sử dụng Google Gemini AI  
- 🔍 **Smart SQL Generation** - Tự động tạo query database
- 🌐 **Language Detection** - Tự động detect ngôn ngữ
- 💬 **Intelligent Responses** - Phản hồi thông minh
- 🎨 **Modern UI** - Giao diện card-based đẹp mắt

---

## 🏗️ System Architecture

```
User Input (Natural Language)
         ↓
Language Detection & Translation
         ↓
AI Processing (Gemini API)
         ↓
SQL Query Generation
         ↓
Database Execution
         ↓
Results + AI Insights
         ↓
UI Rendering (Cards)
```

---

## 🔧 Backend Implementation

### 1. Controller (`QueryController.java`)

**Endpoint:** `POST /api/query/search-candidates`

```java
@PostMapping("/search-candidates")
public ResponseEntity<?> searchCandidates(@RequestBody Map<String, String> request) {
    String searchQuery = request.get("query");
    
    // 1. Validate input
    // 2. Get database schema
    String schema = schemaService.getAllDatabaseSchema();
    
    // 3. Use AI to convert to SQL
    String sql = openAIService.searchCandidatesWithAI(searchQuery, schema);
    
    // 4. Execute query
    List<Object[]> results = queryService.executeQuery(sql);
    
    // 5. Generate response
    String responseMessage = openAIService.generateCandidateSearchResponse(searchQuery, results);
    
    return ResponseEntity.ok(Map.of(
        "success", true,
        "results", results,
        "totalResults", results.size(),
        "message", responseMessage
    ));
}
```

### 2. AI Service (`OpenAIService.java`)

#### Core Methods:

**a) Language Detection:**
```java
private boolean isVietnameseQuery(String query) {
    // 1. Check Vietnamese diacritics (àáạảã...)
    if (query.matches(".*[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ].*")) {
        return true;
    }
    
    // 2. Check Vietnamese keywords
    String[] vietnameseWords = {"tôi", "tìm", "muốn", "có", "kinh nghiệm", "về", "ở", "năm", "tháng"};
    
    // 3. Character pattern analysis
    // 4. English pattern detection
    // 5. Decision logic
}
```

**b) Keyword Translation:**
```java
private String translateCandidateKeywords(String userQuestion) {
    Map<String, String> translations = Map.of(
        "tháng", "months",
        "năm", "years", 
        "kinh nghiệm", "experience",
        "marketing", "marketing",
        "hà nội", "hanoi",
        "hồ chí minh", "ho chi minh"
        // ... 100+ mappings
    );
    
    // Apply translations with word boundary matching
}
```

**c) SQL Generation:**
```java
public String searchCandidatesWithAI(String userQuestion, String schema) {
    // 1. Translate Vietnamese keywords
    String translatedQuestion = translateCandidateKeywords(userQuestion);
    
    // 2. Optimize schema for candidates
    String optimizedSchema = optimizeSchemaForCandidates(schema, translatedQuestion);
    
    // 3. Create AI prompt
    String prompt = "Bạn là chuyên gia SQL cho hệ thống tuyển dụng..." +
                   "Database schema:\n" + optimizedSchema +
                   "\n\nTạo SQL query để tìm ứng viên dựa trên yêu cầu:\n" + translatedQuestion +
                   "\n\nYêu cầu QUAN TRỌNG:" +
                   "\n- LUÔN JOIN: FROM student s JOIN account a ON s.user_id = a.user_id" +
                   "\n- SELECT: s.student_id, a.full_name, a.email, a.phone, s.address, s.university, s.experience, s.profile_description, jf.job_field_name" +
                   "\n- CHỈ trả về SQL query, KHÔNG giải thích";
    
    // 4. Call Gemini API
    // 5. Return generated SQL
}
```

**d) Response Generation:**
```java
public String generateCandidateSearchResponse(String originalQuery, List<Object[]> results) {
    boolean isVietnamese = isVietnameseQuery(originalQuery);
    
    if (results.isEmpty()) {
        return isVietnamese ? 
            "Không tìm thấy ứng viên phù hợp. Thử lại với từ khóa khác." :
            "No candidates found. Try different search terms.";
    }
    
    // Generate insights about candidates
    String insights = generateCandidateInsights(results, isVietnamese);
    
    return isVietnamese ?
        "Chào bạn, tôi thấy " + results.size() + " ứng viên phù hợp. " + insights :
        "Hello, I found " + results.size() + " matching candidates. " + insights;
}
```

---

## 🎨 Frontend Implementation

### 1. HTML Structure

```html
<!-- Search Mode Toggle -->
<div class="btn-group">
    <button id="traditional-search-btn" class="btn btn-outline-primary active">
        <i class="bi bi-filter"></i> Traditional Search
    </button>
    <button id="ai-search-btn" class="btn btn-outline-primary">
        <i class="bi bi-robot"></i> AI Search
    </button>
</div>

<!-- AI Search Form -->
<div id="ai-search-form" class="search-form" style="display: none;">
    <div class="row">
        <div class="col-md-10">
            <label for="ai-search-input" class="form-label">
                <i class="bi bi-robot"></i> AI Search - Describe what kind of candidates you're looking for
            </label>
            <input type="text" id="ai-search-input" class="form-control" 
                   placeholder="Example: 'Find Java developers with 3+ years experience in Hanoi'"
                   style="height: 50px;">
        </div>
        <div class="col-md-2 d-flex align-items-center" style="padding-top: 10px;">
            <button type="button" id="ai-search-button" class="btn search-btn w-100" 
                    onclick="performAISearch()" style="height: 50px;">
                <i class="bi bi-search"></i>
                <span class="spinner-border spinner-border-sm d-none" id="ai-search-spinner"></span>
            </button>
        </div>
    </div>
</div>

<!-- AI Results Container -->
<div id="ai-results-container" class="d-none">
    <!-- Results Header -->
    <div class="ai-results-header">
        <h2 class="ai-results-title">
            <i class="bi bi-robot"></i> AI Search Results
        </h2>
        <div class="results-count-badge" id="results-count-badge">
            <i class="bi bi-person-check"></i>
            <span id="candidates-count">0 candidates found</span>
        </div>
    </div>
    
    <!-- AI Insight Message -->
    <div class="ai-insight-message" id="ai-insight-message">
        <div class="message-content" id="ai-insight-text">
            AI is analyzing your search...
        </div>
    </div>
    
    <!-- Candidate Cards -->
    <div class="candidate-cards-container" id="candidate-cards-container">
        <!-- Cards will be populated here -->
    </div>
</div>
```

### 2. JavaScript Logic

```javascript
// Main search function
async function performAISearch() {
    const searchInput = document.getElementById('ai-search-input');
    const query = searchInput.value.trim();
    
    if (!query) {
        showMessage('warning', 'Vui lòng nhập từ khóa tìm kiếm.');
        return;
    }

    // Show loading state
    showLoadingState();
    
    try {
        const response = await fetch('/api/query/search-candidates', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ query: query })
        });

        const result = await response.json();

        if (result.success) {
            displayAIResults(result.results, result.message);
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('Không thể kết nối đến hệ thống AI. Vui lòng thử lại sau.');
    } finally {
        hideLoadingState();
    }
}

// Display results as cards
function displayAIResults(results, message) {
    const candidatesContainer = document.getElementById('candidate-cards-container');
    const candidatesCountSpan = document.getElementById('candidates-count');
    const aiInsightText = document.getElementById('ai-insight-text');

    // Update header
    candidatesCountSpan.textContent = `${results.length} candidate${results.length > 1 ? 's' : ''} found`;
    aiInsightText.textContent = message;

    // Clear previous results
    candidatesContainer.innerHTML = '';

    // Create candidate cards
    results.forEach((row) => {
        const [studentId, fullName, email, phone, address, university, experience, profileDescription, jobFieldName] = row;
        
        const candidateCard = document.createElement('div');
        candidateCard.className = 'candidate-card';
        candidateCard.innerHTML = `
            <div class="candidate-name">${fullName || 'N/A'}</div>
            <div class="candidate-email">${email || 'Email not provided'}</div>
            
            <div class="candidate-details">
                <div class="candidate-detail-item">
                    <i class="bi bi-geo-alt"></i>
                    <div class="detail-content">
                        <span class="detail-label">Address:</span> ${address || 'Not updated'}
                    </div>
                </div>
                
                <div class="candidate-detail-item">
                    <i class="bi bi-mortarboard"></i>
                    <div class="detail-content">
                        <span class="detail-label">University:</span> ${university || 'Not updated'}
                    </div>
                </div>
                
                <div class="candidate-detail-item">
                    <i class="bi bi-briefcase"></i>
                    <div class="detail-content">
                        <span class="detail-label">Experience:</span> ${experience || 'Not updated'}
                    </div>
                </div>
            </div>
            
            <a href="/Employer/CandidateDetail/${studentId}" class="btn-info">
                <i class="bi bi-eye"></i>
                View Details
            </a>
        `;
        
        candidatesContainer.appendChild(candidateCard);
    });

    // Show results container
    showResults();
}
```

### 3. CSS Styling

```css
/* AI Search Results Styles */
.ai-results-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.ai-results-title {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-size: 1.5rem;
    font-weight: 600;
    color: #374151;
}

.results-count-badge {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 20px;
    font-weight: 600;
}

.ai-insight-message {
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
    border: 1px solid rgba(99, 102, 241, 0.2);
    border-left: 4px solid #667eea;
    border-radius: 12px;
    padding: 1.5rem;
    margin-bottom: 2rem;
}

.candidate-cards-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 1.5rem;
}

.candidate-card {
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 16px;
    padding: 2rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
}

.candidate-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.candidate-name {
    font-size: 1.4rem;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 0.5rem;
}

.candidate-detail-item {
    display: flex;
    align-items: flex-start;
    gap: 0.75rem;
    margin-bottom: 1rem;
}

.candidate-detail-item i {
    color: #667eea;
    width: 18px;
    flex-shrink: 0;
}
```

---

## 💾 Database Integration

### Tables Structure:
```sql
-- Core tables for candidate search
Student (
    student_id INT PRIMARY KEY,
    user_id INT, -- FK to Account
    address VARCHAR(255),
    job_field_id INT, -- FK to JobField  
    university VARCHAR(100),
    preferred_job_address VARCHAR(255),
    profile_description TEXT,
    experience TEXT
)

Account (
    user_id INT PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(50),
    phone VARCHAR(15),
    role ENUM('student', 'employer', 'admin'),
    status ENUM('active', 'inactive')
)

JobField (
    job_field_id INT PRIMARY KEY,
    job_field_name VARCHAR(100)
)
```

### Generated SQL Pattern:
```sql
SELECT 
    s.student_id,
    a.full_name,
    a.email,
    a.phone,
    s.address,
    s.university,
    s.experience,
    s.profile_description,
    jf.job_field_name
FROM student s 
JOIN account a ON s.user_id = a.user_id 
LEFT JOIN job_fields jf ON s.job_field_id = jf.job_field_id 
WHERE [AI-generated conditions based on natural language]
```

---

## 🌐 API Documentation

### Request Format:
```json
POST /api/query/search-candidates
Content-Type: application/json

{
    "query": "tôi muốn tìm ứng viên có 6 tháng kinh nghiệm marketing"
}
```

### Success Response:
```json
{
    "success": true,
    "totalResults": 1,
    "message": "Chào bạn, tôi thấy ứng viên Nguyen Van An phù hợp với yêu cầu của bạn. Có 1 ứng viên có kinh nghiệm thực tế. Phần lớn từ Ho Chi Minh City University of Technology. Bạn có thể xem chi tiết để đánh giá kỹ hơn!",
    "results": [
        [
            2,
            "Nguyen Van An", 
            "nguyenvanan@gmail.com",
            "0905123456",
            "123 Nguyen Hue, District 1, Ho Chi Minh City",
            "Ho Chi Minh City University of Technology",
            "Marketing intern at a startup company (3 months), supported social media content creation.",
            "Third-year Marketing student, passionate about content creation and market analysis.",
            "Marketing"
        ]
    ]
}
```

### Error Response:
```json
{
    "success": false,
    "error": "Gemini API error: 400 - Invalid API key",
    "message": "Không thể tìm kiếm ứng viên. Vui lòng thử lại với từ khóa khác."
}
```

---

## 💡 Usage Examples

### Vietnamese Queries:
```
✅ "tôi muốn tìm ứng viên có kinh nghiệm 6 tháng về marketing"
✅ "sinh viên IT mới ra trường ở Hà Nội"  
✅ "người có kinh nghiệm sales ở TP.HCM"
✅ "ứng viên từ trường Bách Khoa"
✅ "marketing intern có 3 tháng kinh nghiệm"
```

### English Queries:
```
✅ "marketing students with 6 months experience"
✅ "fresh IT graduates from Hanoi"
✅ "sales experience in Ho Chi Minh City"
✅ "candidates from technology universities"
✅ "part-time retail experience"
```

### Mixed Language:
```
✅ "marketing intern ở Hà Nội"
✅ "Java developer có 2 năm kinh nghiệm"
✅ "fresh graduate marketing"
```

---

## 🛠️ Troubleshooting

### Common Issues:

#### 1. "Không thể kết nối đến hệ thống AI"
**Causes:**
- Invalid/expired API key
- Network connectivity issues
- API quota exceeded

**Solutions:**
```bash
# Check .env file
API_KEY=your-valid-gemini-api-key

# Verify at: https://aistudio.google.com/app/apikey
# Check quotas and billing
```

#### 2. "No candidates found" (but data exists)
**Causes:**
- Incorrect SQL generation
- Mismatched search criteria
- Database connection issues

**Debug:**
```java
// Enable logging in QueryController
System.out.println("🤖 Generated SQL: " + sql);
System.out.println("📋 Results count: " + results.size());
```

#### 3. JavaScript Errors
**Causes:**
- Missing DOM elements
- Template syntax in JavaScript
- API response format issues

**Solutions:**
- Check browser console (F12)
- Verify element IDs
- Test API directly

---

## ⚙️ Configuration

### Environment Setup:
```env
# .env file
API_KEY=your-gemini-api-key-here
```

### Application Properties:
```properties
# application.properties
openai.api.key=${API_KEY}
```

### API Rate Limits:
```java
// OpenAIService.java
private static final int MAX_REQUESTS_PER_MINUTE = 60;
private static final int MAX_REQUESTS_PER_DAY = 500;
```

---

## 🚀 Performance Tips

1. **Schema Optimization** - Only include relevant tables
2. **Keyword Caching** - Cache translated keywords
3. **Query Optimization** - Use proper indexing
4. **Frontend Debouncing** - Prevent too many requests
5. **Response Compression** - Minimize response size

---

## 🔮 Future Enhancements

- **Search History** - Save user queries
- **Advanced Filters** - Combine AI with traditional filters
- **Voice Search** - Speech-to-text integration
- **Export Results** - PDF/Excel export
- **Real-time Updates** - WebSocket for live results

---

**🎯 Happy AI Searching! Tìm kiếm ứng viên chưa bao giờ dễ dàng đến thế!** ✨ 