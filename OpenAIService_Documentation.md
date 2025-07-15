# OpenAIService Documentation

## Tổng Quan
`OpenAIService` là lớp service chính xử lý tích hợp AI (Google Gemini) cho website tuyển dụng SWP391. Lớp này cung cấp các chức năng:
- Xử lý natural language queries
- Chuyển đổi câu hỏi thành SQL
- Tìm kiếm ứng viên thông minh
- Hỗ trợ đa ngôn ngữ (Việt/Anh)
- Rate limiting và token management

---

## 1. Constants và Configuration

### 1.1 Endpoint và Client
```java
private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
private final String apiKey;
private final OkHttpClient client;
```
- **ENDPOINT**: URL của Google Gemini API
- **apiKey**: API key để authenticate với Gemini
- **client**: HTTP client cho API calls

### 1.2 Rate Limiting Constants
```java
private static final int DEFAULT_MAX_REQUESTS_PER_MINUTE = 60;
private static final int DEFAULT_MAX_REQUESTS_PER_DAY = 500;
private static final int DEFAULT_TARGET_MAX_TOKENS = 20000;
private static final int CHARS_PER_TOKEN = 4;
```
- **Rate limits**: Giới hạn requests để tránh vượt quota API
- **Token estimation**: Ước tính token count từ character count

### 1.3 Processing Constants
```java
private static final int MIN_QUESTION_LENGTH_FOR_ANALYSIS = 15;
private static final int MAX_SAMPLE_RESULTS = 5;
private static final double RATE_LIMIT_WARNING_THRESHOLD = 0.8;
```
- **MIN_QUESTION_LENGTH**: Độ dài tối thiểu của câu hỏi để xử lý AI
- **MAX_SAMPLE_RESULTS**: Số lượng mẫu kết quả hiển thị trong prompt
- **RATE_LIMIT_WARNING_THRESHOLD**: Ngưỡng cảnh báo rate limit

### 1.4 Database Tables
```java
private static final Set<String> CORE_TABLES = Set.of(
    "account", "student", "employer", "jobs_post", "job_application", 
    "job_fields", "interview", "event", "event_form", "blog_post", 
    "blog_image", "resource", "notifications", "forgot_password"
);
```
**Mục đích**: Danh sách các bảng cơ sở dữ liệu chính để AI tham chiếu

### 1.5 Language Mapping
```java
private static final Map<String, String> VIETNAMESE_MAPPING = Map.ofEntries(...);
private static final Map<String, String> ENGLISH_MAPPING = Map.ofEntries(...);
```
**Mục đích**: Map từ khóa tiếng Việt/Anh sang tên field database

---

## 2. Constructor và Initialization

### 2.1 Constructor
```java
public OpenAIService(@Value("${openai.api.key}") String apiKey)
```
**Chức năng**:
- Inject API key từ configuration
- Khởi tạo HTTP client với timeout settings
- Validate API key format
- Setup rate limiting counters

**Error Handling**: Throw exception nếu API key không hợp lệ

### 2.2 API Key Validation
```java
private boolean isValidApiKeyFormat(String apiKey)
```
**Chức năng**: Kiểm tra format của API key (length, không null/empty)
**Return**: `true` nếu valid, `false` nếu không

---

## 3. Rate Limiting Methods

### 3.1 Update Rate Limits
```java
private synchronized void updateRateLimits()
```
**Chức năng**:
- Reset counters khi qua phút/ngày mới
- Thread-safe với synchronized
- Sử dụng AtomicReference cho thread safety

### 3.2 Token Estimation
```java
private int estimateTokenCount(String text)
```
**Chức năng**: Ước tính số token từ text length
**Algorithm**: `text.length() / CHARS_PER_TOKEN`
**Use case**: Kiểm tra trước khi gửi request tới API

---

## 4. Schema Optimization Methods

### 4.1 General Schema Optimization
```java
private String optimizeSchema(String schema, String userQuestion)
```
**Chức năng**:
- Phân tích user question để xác định tables liên quan
- Lọc schema chỉ giữ tables cần thiết
- Giảm token count cho API calls
- Cải thiện accuracy bằng cách loại bỏ noise

**Algorithm**:
1. Lowercase user question
2. Check keywords matches với table names
3. Giữ core tables + matched tables
4. Return optimized schema

### 4.2 Candidate-Specific Schema Optimization
```java
private String optimizeSchemaForCandidates(String schema, String userQuestion)
```
**Chức năng**: Tối ưu schema đặc biệt cho candidate search
**Focus**: Tables `student`, `account`, `job_fields`
**Enhancement**: Thêm candidate-specific keywords detection

---

## 5. Translation Methods

### 5.1 Vietnamese Keywords Translation
```java
private String translateVietnameseKeywords(String userQuestion)
```
**Chức năng**:
- Dịch từ khóa tiếng Việt sang English/SQL field names
- Sử dụng VIETNAMESE_MAPPING
- Regex pattern matching với word boundaries
- Sort by length để match longer phrases trước

**Example**: "mức lương" → "salary", "kinh nghiệm" → "experience"

### 5.2 English Keywords Translation
```java
private String translateEnglishKeywords(String userQuestion)
```
**Chức năng**: Tương tự Vietnamese translation nhưng cho English input
**Use case**: Normalize English terms to database field names

### 5.3 Universal Translation
```java
private String translateKeywords(String userQuestion)
```
**Chức năng**:
- Detect language và apply appropriate translation
- Combine Vietnamese và English translation
- Universal entry point cho keyword translation

### 5.4 Candidate-Specific Translation
```java
private String translateCandidateKeywords(String userQuestion)
```
**Chức năng**:
- Enhanced translation cho candidate search
- Handle experience patterns (VD: "3 năm" → "3 years")
- Location standardization
- Skill và education translations

---

## 6. Core AI Methods

### 6.1 SQL Conversion
```java
public String convertToSQL(String userQuestion, String schema) throws Exception
```
**Chức năng**: Chuyển đổi natural language question thành SQL query

**Process Flow**:
1. Translate keywords
2. Optimize schema
3. Build prompt cho AI
4. Estimate tokens và truncate nếu cần
5. Call Gemini API
6. Extract SQL từ response
7. Validate và clean SQL

**Error Handling**: Try-catch với logging và fallback responses

### 6.2 Natural Response Generation
```java
public String generateNaturalResponse(String userQuestion, String sql, List<Object[]> results)
```
**Chức năng**: Tạo natural language response từ SQL results

**Bilingual Support**:
- Detect language từ user question
- Generate Vietnamese hoặc English response accordingly
- Different prompts cho mỗi ngôn ngữ

**Process**:
1. Language detection
2. Build context prompt với results data
3. Call AI để generate response
4. Return formatted answer

### 6.3 Natural Language Query Processing
```java
public String processNaturalLanguageQuery(String userQuestion)
```
**Chức năng**: Main entry point cho natural language processing

**Flow**:
1. Check nếu là general question → handle directly
2. Nếu không job-related → polite decline
3. Convert to SQL
4. Execute SQL (delegated to controller)
5. Generate natural response

---

## 7. Candidate Search Methods

### 7.1 AI-Powered Candidate Search
```java
public String searchCandidatesWithAI(String userQuestion, String schema) throws Exception
```
**Chức năng**: Specialized method cho candidate search với AI

**Enhancements**:
- Candidate-specific schema optimization
- Enhanced translation patterns
- Specific SQL templates cho student search
- JOIN requirements cho comprehensive results

**SQL Template**: Luôn JOIN student + account + job_fields

### 7.2 Candidate Search Response
```java
public String generateCandidateSearchResponse(String originalQuery, List<Object[]> results)
```
**Chức năng**: Generate response specifically cho candidate search results

**Features**:
- Count-based responses (0, 1, multiple candidates)
- Include insights từ results
- Bilingual support
- Professional tone

### 7.3 Candidate Insights Generation
```java
private String generateCandidateInsights(List<Object[]> results, boolean isVietnamese)
```
**Chức năng**: Analyze candidate results để generate insights

**Analysis**:
- Common universities
- Experience levels
- Skill patterns
- Location distribution
- Recommendations based on data

---

## 8. Language Detection

### 8.1 Vietnamese Query Detection
```java
private boolean isVietnameseQuery(String query)
```
**Chức năng**: Sophisticated language detection algorithm

**Detection Strategy**:
1. **Vietnamese diacritics**: Most reliable indicator
2. **High-confidence Vietnamese words**: Common Vietnamese terms
3. **Character patterns**: Vietnamese-specific combinations (ng, ch, th, etc.)
4. **English patterns**: English-specific words và structures
5. **Job-related English**: Industry-specific English terms
6. **Word structure analysis**: Length patterns
7. **Pure ASCII check**: Fallback cho ambiguous cases

**Return**: `true` cho Vietnamese, `false` cho English

---

## 9. Question Analysis Methods

### 9.1 General Question Detection
```java
private boolean isGeneralQuestion(String question)
```
**Chức năng**: Detect nếu question là general (greeting, help, etc.)
**Keywords**: hello, help, what, who, how, etc.

### 9.2 Job-Related Keywords Check
```java
private boolean containsJobRelatedKeywords(String question)
```
**Chức năng**: Check nếu question liên quan đến jobs/recruitment
**Keywords**: job, work, salary, company, career, etc.

### 9.3 General Question Handling
```java
private String handleGeneralQuestion(String question)
```
**Chức năng**: Handle general questions với pre-defined responses

**Categories**:
- **Greetings**: Welcome messages
- **Website info**: Về website và features
- **About AI**: AI capabilities explanation
- **Help**: Usage instructions với examples
- **Thanks**: Polite acknowledgments

**Bilingual**: Detect language và respond accordingly

---

## 10. Utility Methods

### 10.1 Candidate Name Extraction
```java
private String getCandidateName(Object[] result)
```
**Chức năng**: Extract candidate name từ SQL result array
**Fallback**: Return "Ứng viên" nếu name không available

### 10.2 City Extraction
```java
private String extractCity(String address)
```
**Chức năng**: Extract city name từ full address
**Algorithm**: Find known city names trong address string

### 10.3 Error Message Generation
```java
public String generateCandidateSearchErrorMessage(String originalQuery)
```
**Chức năng**: Generate friendly error messages cho candidate search failures
**Bilingual**: Vietnamese/English based on query language

---

## 11. Rate Limiting và Performance

### Thread Safety
- Sử dụng `AtomicInteger` và `AtomicReference` cho counters
- `synchronized` methods cho critical sections
- Thread-safe operations throughout

### Token Management
- Pre-flight token estimation
- Schema truncation khi vượt limits
- Intelligent prompt optimization

### Error Handling
- Comprehensive try-catch blocks
- Logging cho debugging
- Graceful degradation
- User-friendly error messages

---

## 12. Configuration Properties

### External Configuration
```properties
ai.rate-limit.per-minute=60
ai.rate-limit.per-day=500
ai.max-tokens=20000
openai.api.key=${GEMINI_API_KEY}
```

### Environment Variables
- `GEMINI_API_KEY`: Required for API authentication

---

## 13. Usage Examples

### Basic Job Query
```java
String response = openAIService.processNaturalLanguageQuery("Highest paying jobs");
```

### Candidate Search
```java
String response = openAIService.searchCandidatesWithAI("Find Java developers in Hanoi", schema);
```

### General Questions
```java
String response = openAIService.processNaturalLanguageQuery("Hello, what can you help me with?");
```

---

## 14. Error Handling Strategy

### API Failures
- Network timeouts → Retry logic
- Rate limit exceeded → Graceful degradation
- Invalid responses → Default fallbacks

### Data Issues
- Empty results → Helpful suggestions
- Malformed queries → Request clarification
- Database errors → Generic error messages

### Language Issues
- Unclear language → Default to Vietnamese
- Mixed languages → Detect primary language
- Unsupported languages → Polite decline

---

## 15. Performance Considerations

### Optimization Techniques
- Schema pruning để reduce token usage
- Intelligent prompt construction
- Result caching possibilities
- Async processing capabilities

### Monitoring
- Rate limit tracking
- Response time logging
- Error rate monitoring
- Token usage analytics

---

## 16. Future Improvements

### Potential Enhancements
- Caching layer cho common queries
- Machine learning cho better language detection
- Advanced query understanding
- Real-time learning từ user feedback
- Integration với more AI providers

### Scalability
- Async processing
- Queue management
- Load balancing
- Database connection pooling 