# 🤖 AI Candidate Search - Hướng dẫn chi tiết

## 📋 Mục lục
1. [Tổng quan](#tổng-quan)
2. [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
3. [Backend Implementation](#backend-implementation)
4. [Frontend Implementation](#frontend-implementation)
5. [Database Schema](#database-schema)
6. [Language Detection](#language-detection)
7. [SQL Generation](#sql-generation)
8. [API Documentation](#api-documentation)
9. [Examples](#examples)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Tổng quan

### Tính năng AI Candidate Search
Hệ thống tìm kiếm ứng viên thông minh sử dụng **Google Gemini AI** để chuyển đổi **natural language queries** thành **SQL queries**, giúp nhà tuyển dụng tìm kiếm ứng viên bằng ngôn ngữ tự nhiên.

### Điểm nổi bật
- ✅ **Natural Language Processing** - Tìm kiếm bằng tiếng Việt/tiếng Anh
- ✅ **Smart Language Detection** - Tự động detect ngôn ngữ query
- ✅ **Intelligent SQL Generation** - AI tạo SQL query tối ưu
- ✅ **Localized Responses** - Phản hồi phù hợp với ngôn ngữ user
- ✅ **Modern UI** - Card-based layout đẹp mắt
- ✅ **Real-time Search** - Kết quả ngay lập tức

---

## 🏗️ Kiến trúc hệ thống

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend UI   │───▶│   Controller    │───▶│   AI Service    │
│   (Thymeleaf)   │    │ QueryController │    │  OpenAIService  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         ▲                        │                        │
         │                        ▼                        ▼
         │              ┌─────────────────┐    ┌─────────────────┐
         │              │     Database    │    │   Gemini API    │
         └──────────────│   QueryService  │    │ (Google Cloud)  │
                        └─────────────────┘    └─────────────────┘
```

### Flow tổng quát:
1. **User input** → Natural language query
2. **Language detection** → Detect Vietnamese/English
3. **AI processing** → Convert to SQL via Gemini API
4. **Database execution** → Run SQL query
5. **Response generation** → Create localized response
6. **UI rendering** → Display candidate cards

---

## 🔧 Backend Implementation

### 1. Controller Layer (`QueryController.java`)

```java
@PostMapping("/search-candidates")
public ResponseEntity<?> searchCandidates(@RequestBody Map<String, String> request) {
    // 1. Validate input
    // 2. Get database schema
    // 3. Use AI to convert natural language to SQL
    // 4. Execute SQL query
    // 5. Generate appropriate response message
    // 6. Return results in JSON format
}
```

### 2. Service Layer (`OpenAIService.java`)

#### Main methods:
- `searchCandidatesWithAI()` - Core AI search logic
- `translateCandidateKeywords()` - Vietnamese keyword translation
- `optimizeSchemaForCandidates()` - Schema optimization
- `isVietnameseQuery()` - Language detection
- `generateCandidateSearchResponse()` - Response generation

#### Key features:
```java
// Enhanced Vietnamese keyword mapping
Map<String, String> VIETNAMESE_MAPPING = Map.ofEntries(
    Map.entry("tháng", "months"),
    Map.entry("năm", "years"),
    Map.entry("kinh nghiệm", "experience"),
    Map.entry("marketing", "marketing"),
    // ... 100+ mappings
);
```

### 3. Database Layer (`DynamicQueryService.java`)
- Executes dynamically generated SQL queries
- Returns `List<Object[]>` for flexible result handling
- Supports complex JOINs across multiple tables

---

## 🎨 Frontend Implementation

### 1. HTML Structure (`potentialCandidates.html`)

```html
<!-- Search Mode Toggle -->
<div class="btn-group">
    <button id="traditional-search-btn">Traditional Search</button>
    <button id="ai-search-btn">AI Search</button>
</div>

<!-- AI Search Form -->
<div id="ai-search-form">
    <input id="ai-search-input" placeholder="Describe candidates...">
    <button onclick="performAISearch()">Search</button>
</div>

<!-- AI Results Display -->
<div id="ai-results-container">
    <!-- Results Header -->
    <div class="ai-results-header">
        <h2>AI Search Results</h2>
        <div class="results-count-badge">X candidates found</div>
    </div>
    
    <!-- AI Insight Message -->
    <div class="ai-insight-message">
        <div id="ai-insight-text">AI analysis...</div>
    </div>
    
    <!-- Candidate Cards -->
    <div class="candidate-cards-container">
        <!-- Dynamically generated cards -->
    </div>
</div>
```

### 2. JavaScript Logic

```javascript
async function performAISearch() {
    // 1. Validate input
    // 2. Show loading state
    // 3. Call API endpoint
    // 4. Process response
    // 5. Display results or error
}

function displayAIResults(results, message) {
    // 1. Create candidate cards
    // 2. Populate with data
    // 3. Show results container
    // 4. Hide loading state
}
```

### 3. CSS Styling
- **Card-based layout** cho candidate results
- **Responsive design** cho mobile/desktop
- **Loading animations** và hover effects
- **Consistent styling** với traditional search

---

## 💾 Database Schema

### Tables involved:
```sql
-- Main tables
Student (student_id, user_id, address, job_field_id, university, experience, profile_description)
Account (user_id, full_name, email, phone, role, status)
JobField (job_field_id, job_field_name)

-- Join relationship
Student.user_id = Account.user_id
Student.job_field_id = JobField.job_field_id
```

### Sample query structure:
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
WHERE [AI-generated conditions]
```

---

## 🌐 Language Detection

### Algorithm:
```java
private boolean isVietnameseQuery(String query) {
    // 1. Check for Vietnamese diacritics (àáạảã...)
    // 2. Check for high-confidence Vietnamese words
    // 3. Analyze character patterns (ng, ch, th, ph...)
    // 4. Check for English patterns
    // 5. Word structure analysis
    // 6. Default to Vietnamese for ambiguous cases
}
```

### Decision logic:
- **Vietnamese diacritics** → Definitely Vietnamese
- **Strong Vietnamese words** → Vietnamese  
- **English patterns** + no Vietnamese → English
- **Ambiguous cases** → Default Vietnamese

---

## 🤖 SQL Generation

### AI Prompt Engineering:
```java
String prompt = "Bạn là chuyên gia SQL cho hệ thống tuyển dụng. " +
               "Database schema:\n" + optimizedSchema +
               "\n\nTạo SQL query để tìm ứng viên/sinh viên dựa trên yêu cầu sau:\n" +
               translatedQuestion + 
               "\n\nYêu cầu QUAN TRỌNG:" +
               "\n- LUÔN JOIN: FROM student s JOIN account a ON s.user_id = a.user_id" +
               "\n- SELECT: s.student_id, a.full_name, a.email, a.phone, s.address, s.university, s.experience, s.profile_description, jf.job_field_name" +
               "\n- Tìm kiếm trong experience: Dùng LIKE để tìm cả tiếng Việt và tiếng Anh" +
               "\n- CHỈ trả về SQL query, KHÔNG giải thích";
```

### Schema optimization:
- Focus on **student**, **account**, **job_fields** tables
- Remove irrelevant tables to reduce token usage
- Preserve important relationships and constraints

---

## 📡 API Documentation

### Endpoint: `/api/query/search-candidates`

#### Request:
```json
{
    "query": "tôi muốn tìm ứng viên có kinh nghiệm 6 tháng về marketing"
}
```

#### Response (Success):
```json
{
    "success": true,
    "totalResults": 1,
    "message": "Chào bạn, tôi thấy ứng viên Nguyen Van An phù hợp với yêu cầu của bạn. Có 1 ứng viên có kinh nghiệm thực tế. Phần lớn từ Ho Chi Minh City University of Technology. Bạn có thể xem chi tiết để đánh giá kỹ hơn!",
    "results": [
        [2, "Nguyen Van An", "nguyenvanan@gmail.com", "0905123456", "123 Nguyen Hue, District 1, Ho Chi Minh City", "Ho Chi Minh City University of Technology", "Marketing intern at a startup company (3 months), supported social media content creation.", "Third-year Marketing student, passionate about content creation and market analysis. Seeking internship opportunities in digital marketing to gain practical experience.", "Marketing"]
    ]
}
```

#### Response (Error):
```json
{
    "success": false,
    "error": "API error message",
    "message": "Không thể tìm kiếm ứng viên. Vui lòng thử lại với từ khóa khác."
}
```

---

## 💡 Examples

### 1. Vietnamese Queries:
```
✅ "tôi muốn tìm ứng viên có kinh nghiệm 6 tháng về marketing"
✅ "sinh viên IT mới ra trường ở Hà Nội" 
✅ "người có kinh nghiệm sales ở TP.HCM"
✅ "ứng viên từ trường Bách Khoa"
```

### 2. English Queries:
```
✅ "marketing students with 6 months experience"
✅ "fresh IT graduates from Hanoi"
✅ "sales experience in Ho Chi Minh"
✅ "candidates from technology universities"
```

### 3. Mixed Queries:
```
✅ "marketing intern ở Hà Nội"
✅ "Java developer có 2 năm kinh nghiệm"
✅ "fresh graduate marketing"
```

---

## 🛠️ Troubleshooting

### Common Issues:

#### 1. "Không thể kết nối đến hệ thống AI"
**Nguyên nhân:**
- API key không hợp lệ hoặc hết hạn
- Network connectivity issues  
- Gemini API quotas exceeded

**Khắc phục:**
```bash
# Check API key in .env file
API_KEY=your-gemini-api-key-here

# Verify API key at: https://aistudio.google.com/app/apikey
# Check quotas and billing
```

#### 2. "No candidates found" (nhưng có data)
**Nguyên nhân:**
- SQL query được generate sai
- Database không có data phù hợp
- Keyword translation không chính xác

**Debug:**
```java
// Enable debug logging in controller
System.out.println("🤖 Generated SQL: " + sql);
System.out.println("📋 Query results count: " + results.size());
```

#### 3. JavaScript errors
**Nguyên nhân:**
- Template syntax trong JavaScript
- Missing DOM elements
- Async/await errors

**Khắc phục:**
- Check browser console (F12)
- Verify all element IDs exist
- Use proper JavaScript syntax (không dùng Thymeleaf trong JS)

### Debug Mode:
```java
// Add these logs in QueryController for debugging:
System.out.println("🔍 AI Search Request: " + searchQuery);
System.out.println("📊 Schema length: " + schema.length() + " characters");
System.out.println("🤖 Generated SQL: " + sql);
System.out.println("✨ Cleaned SQL: " + sql);
System.out.println("📋 Query results count: " + results.size());
System.out.println("💬 Response message: " + responseMessage);
```

---

## ⚙️ Configuration

### Environment Variables:
```env
# .env file
API_KEY=your-gemini-api-key-here
```

### Application Properties:
```properties
# application.properties
openai.api.key=${API_KEY}
```

### Rate Limiting:
```java
// In OpenAIService.java
private static final int MAX_REQUESTS_PER_MINUTE = 60;
private static final int MAX_REQUESTS_PER_DAY = 500;
```

---

## 🚀 Performance Optimization

### 1. Schema Optimization
- Only include relevant tables for candidate search
- Reduce token count by removing unnecessary fields
- Focus on core relationships

### 2. Keyword Translation  
- Pre-translate common Vietnamese terms
- Cache translation results
- Optimize regex patterns

### 3. Response Caching
- Cache similar queries for short periods
- Implement query normalization
- Use database query optimization

### 4. Frontend Optimization
- Debounce search input
- Implement loading states
- Optimize DOM manipulation

---

## 📚 Technical Stack

- **Backend:** Spring Boot, Java 17
- **Frontend:** Thymeleaf, JavaScript ES6, Bootstrap 5
- **Database:** MySQL with JPA/Hibernate
- **AI Service:** Google Gemini API
- **HTTP Client:** OkHttp
- **JSON Processing:** org.json
- **CSS Framework:** Bootstrap 5 + Custom CSS

---

## 🔮 Future Enhancements

### Planned Features:
1. **Search History** - Lưu lại search queries của user
2. **Advanced Filters** - Kết hợp AI search với traditional filters
3. **Saved Searches** - Bookmark các query thường dùng
4. **Batch Operations** - Bulk actions trên search results
5. **Analytics** - Track search patterns và performance
6. **Voice Search** - Tích hợp speech-to-text
7. **Smart Suggestions** - Auto-suggest based on typing
8. **Export Results** - Export candidate list to Excel/PDF

### Technical Improvements:
1. **Caching Layer** - Redis cho search results
2. **Queue System** - Background processing cho large queries  
3. **Real-time Updates** - WebSocket cho live results
4. **API Versioning** - Support multiple API versions
5. **Monitoring** - Logging và metrics cho performance
6. **Testing** - Unit tests và integration tests
7. **Documentation** - API docs với Swagger

---

## 📞 Support

Nếu gặp vấn đề hoặc cần hỗ trợ:

1. **Check server logs** trong console
2. **Verify API key** trong .env file  
3. **Test với simple queries** trước
4. **Check browser console** cho JavaScript errors
5. **Verify database connection** và sample data

---

**🎯 Happy Searching! Chúc bạn tìm được những ứng viên tuyệt vời!** ✨ 