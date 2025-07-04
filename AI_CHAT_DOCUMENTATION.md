# 🤖 AI Chat System - Tài liệu kỹ thuật Backend & Data Processing

## 📋 Tổng quan
AI Chat System là một hệ thống chat tích hợp AI để hỗ trợ người dùng tìm kiếm thông tin trong cơ sở dữ liệu thông qua ngôn ngữ tự nhiên. Tài liệu này tập trung vào backend processing và JavaScript data handling.

## 🏗️ Cấu trúc hệ thống

### Files chính
```
src/main/java/.../controller/QueryController.java  # REST API Endpoint
src/main/java/.../service/ai/OpenAIService.java     # AI Processing Service
src/main/resources/templates/fragments/ai-chatbox.html # Frontend JS Logic
```

### Data Flow
```
User Input → Frontend JS → REST API → OpenAI Service → Database Query → Response
```

---

## 🔄 Backend Implementation

### 1. REST API Controller (`QueryController.java`)

```java
@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = "*")
public class QueryController {
    
    @Autowired
    private OpenAIService openAIService;
    
    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);
    
    @PostMapping("/ask")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> askQuestion(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            
            // Input validation
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Question is required"));
            }
            
            // Process with AI service
            String response = openAIService.processNaturalLanguageQuery(question);
            
            // Return success response
            return ResponseEntity.ok(Map.of("response", response));
            
        } catch (Exception e) {
            logger.error("Error processing AI question: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}
```

**Giải thích từng phần**:

#### 1.1 Controller Setup
```java
@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = "*")
```
- `@RestController`: Kết hợp `@Controller` + `@ResponseBody` để trả JSON
- `@RequestMapping("/api/query")`: Base path cho tất cả endpoints
- `@CrossOrigin`: Cho phép frontend gọi API từ domain khác (CORS)

#### 1.2 Dependency Injection
```java
@Autowired
private OpenAIService openAIService;
```
- `@Autowired`: Spring tự động inject OpenAIService bean
- Service này xử lý logic AI và database queries

#### 1.3 Endpoint Definition
```java
@PostMapping("/ask")
@ResponseBody
public ResponseEntity<Map<String, Object>> askQuestion(@RequestBody Map<String, String> request)
```
- `@PostMapping("/ask")`: Endpoint `/api/query/ask` với HTTP POST
- `@ResponseBody`: Response sẽ được serialize thành JSON
- `ResponseEntity<Map<String, Object>>`: Wrapper cho HTTP response với status code
- `@RequestBody Map<String, String>`: Parse JSON request body thành Map

#### 1.4 Input Validation
```java
String question = request.get("question");
if (question == null || question.trim().isEmpty()) {
    return ResponseEntity.badRequest()
        .body(Map.of("error", "Question is required"));
}
```
- Extract `question` từ request body
- Validate không null và không empty (sau khi trim)
- Return HTTP 400 với error message nếu invalid

#### 1.5 AI Processing
```java
String response = openAIService.processNaturalLanguageQuery(question);
return ResponseEntity.ok(Map.of("response", response));
```
- Gọi service để xử lý câu hỏi với AI
- Return HTTP 200 với AI response trong JSON

#### 1.6 Error Handling
```java
} catch (Exception e) {
    logger.error("Error processing AI question: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Internal server error: " + e.getMessage()));
}
```
- Catch mọi exception không expected
- Log error với full stack trace
- Return HTTP 500 với error message

---

## ⚙️ Frontend JavaScript Data Processing

### 1. Send Message to Backend
```javascript
async function sendAIMessage() {
    const input = document.getElementById('aiChatInput');
    const question = input.value.trim();
    
    if (!question) return;
    
    // Clear input and show loading
    input.value = '';
    showLoading(true);
    
    // Add user message to chat
    addMessageToChat('user', question);
    
    try {
        const response = await fetch('/api/query/ask', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ question: question })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            if (data.response) {
                addMessageToChat('ai', data.response);
            }
        } else {
            addMessageToChat('ai', 'Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại.');
        }
    } catch (error) {
        console.error('Error:', error);
        addMessageToChat('ai', 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.');
    } finally {
        showLoading(false);
    }
}
```

**Giải thích chi tiết từng bước**:

#### 1.1 Input Validation & Preparation
```javascript
const input = document.getElementById('aiChatInput');
const question = input.value.trim();
if (!question) return;
```
- **Lấy input element**: Tìm DOM element bằng ID
- **Extract & clean data**: Lấy value và trim whitespace đầu/cuối
- **Early return**: Nếu empty string thì dừng execution

#### 1.2 UI State Management
```javascript
input.value = '';
showLoading(true);
addMessageToChat('user', question);
```
- **Clear input**: Reset input field ngay lập tức để UX tốt
- **Show loading**: Hiện loading indicator cho user biết đang process
- **Add user message**: Hiển thị tin nhắn user trong chat UI

#### 1.3 HTTP Request Construction
```javascript
const response = await fetch('/api/query/ask', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ question: question })
});
```
- **Endpoint URL**: `/api/query/ask` - khớp với backend `@PostMapping("/ask")`
- **HTTP Method**: POST - gửi data lên server
- **Headers**: 
  - `Content-Type: application/json` - báo server expect JSON
- **Request Body**:
  - `JSON.stringify({ question: question })` - convert object thành JSON string
  - Tạo JSON object: `{"question": "user input here"}`

#### 1.4 Response Processing
```javascript
const data = await response.json();
if (response.ok) {
    if (data.response) {
        addMessageToChat('ai', data.response);
    }
}
```
- **Parse JSON**: `response.json()` parse response body thành JavaScript object
- **Check HTTP Status**: `response.ok` kiểm tra status 200-299
- **Extract data**: Lấy `data.response` từ JSON object
- **Update UI**: Thêm AI response vào chat

#### 1.5 Error Response Handling  
```javascript
} else {
    addMessageToChat('ai', 'Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại.');
}
```
- **HTTP Error**: Khi server return 4xx/5xx status
- **User-friendly message**: Không show technical error cho user

#### 1.6 Network Error Handling
```javascript
} catch (error) {
    console.error('Error:', error);
    addMessageToChat('ai', 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.');
} finally {
    showLoading(false);
}
```
- **Network errors**: Connection timeout, DNS failure, etc.
- **Console logging**: Log technical error cho developers
- **User message**: Show user-friendly error message
- **Cleanup**: Luôn luôn tắt loading animation trong `finally`

---

### 2. Add Message to Chat UI
```javascript
function addMessageToChat(sender, message) {
    const messagesContainer = document.getElementById('aiChatMessages');
    const messageDiv = document.createElement('div');
    
    messageDiv.className = `ai-message ${sender === 'user' ? 'user-message' : ''}`;
    
    const avatarClass = sender === 'user' ? 'user-avatar' : '';
    const contentClass = sender === 'user' ? 'user-content' : '';
    
    messageDiv.innerHTML = `
        <div class="ai-message-avatar ${avatarClass}">
            <i class="fas fa-${sender === 'user' ? 'user' : 'robot'}"></i>
        </div>
        <div class="ai-message-content ${contentClass}">
            <p>${escapeHtml(message)}</p>
        </div>
    `;
    
    messagesContainer.appendChild(messageDiv);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}
```

**Giải thích từng bước**:

#### 2.1 DOM Element Creation
```javascript
const messagesContainer = document.getElementById('aiChatMessages');
const messageDiv = document.createElement('div');
```
- **Get container**: Lấy container chứa tất cả messages
- **Create element**: Tạo div mới cho message này

#### 2.2 Conditional Styling Classes
```javascript
messageDiv.className = `ai-message ${sender === 'user' ? 'user-message' : ''}`;
const avatarClass = sender === 'user' ? 'user-avatar' : '';
const contentClass = sender === 'user' ? 'user-content' : '';
```
- **Base class**: `ai-message` cho tất cả messages
- **User-specific classes**: Thêm special classes cho user messages
- **Conditional logic**: Ternary operator để switch giữa user/AI styling

#### 2.3 Dynamic HTML Generation
```javascript
messageDiv.innerHTML = `
    <div class="ai-message-avatar ${avatarClass}">
        <i class="fas fa-${sender === 'user' ? 'user' : 'robot'}"></i>
    </div>
    <div class="ai-message-content ${contentClass}">
        <p>${escapeHtml(message)}</p>
    </div>
`;
```
- **Template literals**: Sử dụng backticks để multi-line string
- **Dynamic icons**: `fa-user` cho user, `fa-robot` cho AI
- **Security**: `escapeHtml(message)` để prevent XSS attacks
- **Structured HTML**: Avatar + content layout

#### 2.4 DOM Update & UX
```javascript
messagesContainer.appendChild(messageDiv);
messagesContainer.scrollTop = messagesContainer.scrollHeight;
```
- **Append to DOM**: Thêm message vào container
- **Auto-scroll**: Scroll xuống message mới nhất để user thấy

---

### 3. Security Function - XSS Prevention
```javascript
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}
```

**Giải thích**:
- **XSS Prevention**: Ngăn chặn Cross-Site Scripting attacks
- **Character mapping**: Map dangerous HTML characters thành safe entities
- **Regex replacement**: Replace tất cả dangerous chars trong một pass

---

## 🔄 Complete Data Flow Analysis

### Request Flow: Frontend → Backend
```
1. User Input: "Hiển thị tất cả công việc mới nhất"
   ↓
2. JavaScript: Validate & clean input
   ↓  
3. JSON Request: {"question": "Hiển thị tất cả công việc mới nhất"}
   ↓
4. HTTP POST: /api/query/ask
   ↓
5. Spring Controller: @PostMapping("/ask")
   ↓
6. Input Validation: Check not null/empty
   ↓
7. Service Call: openAIService.processNaturalLanguageQuery(question)
   ↓
8. AI Processing: OpenAI API + Database query
   ↓
9. Response Generation: Tạo câu trả lời từ data
```

### Response Flow: Backend → Frontend
```
10. JSON Response: {"response": "Đây là danh sách 10 công việc mới nhất..."}
    ↓
11. HTTP 200 OK: Return về frontend
    ↓
12. JavaScript: await response.json()
    ↓
13. Data Extraction: data.response
    ↓
14. Security: escapeHtml(data.response)
    ↓
15. DOM Update: addMessageToChat('ai', response)
    ↓
16. UI Update: Hiển thị message trong chat
```

---

## 💾 Backend Service Implementation

### OpenAI Service (Simplified)
```java
@Service
public class OpenAIService {
    
    public String processNaturalLanguageQuery(String question) {
        try {
            // 1. Send question to OpenAI API
            String aiResponse = callOpenAI(question);
            
            // 2. Parse AI response to extract SQL or action
            QueryResult queryResult = parseAIResponse(aiResponse);
            
            // 3. Execute database query if needed
            if (queryResult.hasQuery()) {
                List<Object> results = executeQuery(queryResult.getSql());
                return formatResults(results);
            }
            
            // 4. Return direct AI response
            return aiResponse;
            
        } catch (Exception e) {
            logger.error("Error in AI processing", e);
            throw new RuntimeException("AI processing failed", e);
        }
    }
    
    private String callOpenAI(String question) {
        // OpenAI API call implementation
        // Return structured response with SQL query or direct answer
    }
    
    private List<Object> executeQuery(String sql) {
        // Database query execution
        // Return results as List of Objects
    }
    
    private String formatResults(List<Object> results) {
        // Format database results into human-readable text
        // Return formatted string for display
    }
}
```

**Giải thích Service Layer**:

#### Step 1: OpenAI API Call
- Gửi user question đến OpenAI
- Nhận structured response (có thể chứa SQL query)

#### Step 2: Response Parsing  
- Parse AI response để extract actionable information
- Determine nếu cần query database

#### Step 3: Database Execution
- Execute SQL query nếu cần
- Get results từ database

#### Step 4: Response Formatting
- Format kết quả thành human-readable text
- Return về controller

---

## 🔒 Error Handling Strategy

### Frontend Error Types
```javascript
// 1. Network Errors (fetch failures)
catch (error) {
    if (error instanceof TypeError) {
        // Network connection issues
        showErrorMessage("Không thể kết nối đến server");
    }
}

// 2. HTTP Errors (4xx, 5xx status codes)
if (!response.ok) {
    if (response.status === 400) {
        // Bad request - validation error
        showErrorMessage("Dữ liệu không hợp lệ");
    } else if (response.status === 500) {
        // Server error
        showErrorMessage("Lỗi server, vui lòng thử lại");
    }
}

// 3. JSON Parsing Errors
try {
    const data = await response.json();
} catch (parseError) {
    showErrorMessage("Dữ liệu trả về không hợp lệ");
}
```

### Backend Error Types
```java
// 1. Input Validation Errors
if (question == null || question.trim().isEmpty()) {
    return ResponseEntity.badRequest()
        .body(Map.of("error", "Question is required"));
}

// 2. Service Layer Errors  
try {
    String response = openAIService.processNaturalLanguageQuery(question);
} catch (OpenAIException e) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(Map.of("error", "AI service unavailable"));
} catch (DatabaseException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Database query failed"));
}

// 3. Generic Exception Handling
} catch (Exception e) {
    logger.error("Unexpected error", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Internal server error"));
}
```

---

## 📊 JSON Request/Response Examples

### Request Example
```javascript
// Frontend sends:
{
    "question": "Hiển thị 5 công việc có mức lương cao nhất"
}
```

### Success Response Example
```javascript
// Backend returns:
{
    "response": "Đây là 5 công việc có mức lương cao nhất:\n\n1. Senior Software Engineer - ABC Corp: 50,000,000 VNĐ/tháng\n2. Technical Lead - XYZ Ltd: 45,000,000 VNĐ/tháng\n3. Product Manager - DEF Inc: 40,000,000 VNĐ/tháng\n4. DevOps Engineer - GHI Co: 38,000,000 VNĐ/tháng\n5. Data Scientist - JKL Corp: 35,000,000 VNĐ/tháng"
}
```

### Error Response Example
```javascript
// Backend returns (400 Bad Request):
{
    "error": "Question is required"
}

// Backend returns (500 Internal Server Error):
{
    "error": "Internal server error: Database connection failed"
}
```

---

## 🎯 Performance & Security Best Practices

### Frontend Best Practices
1. **Async/Await**: Non-blocking API calls
2. **Input Validation**: Client-side validation trước khi gửi request  
3. **Error Boundaries**: Comprehensive error handling
4. **XSS Prevention**: Escape HTML trong user content
5. **Loading States**: Visual feedback cho user

### Backend Best Practices  
1. **Input Validation**: Server-side validation là mandatory
2. **Exception Handling**: Structured error responses
3. **Logging**: Chi tiết logs cho debugging
4. **CORS Configuration**: Proper cross-origin settings
5. **Rate Limiting**: Prevent API abuse

---

## 🎉 Kết luận

AI Chat System data processing bao gồm:
- ✅ **Robust Frontend JS**: Async API calls với proper error handling
- ✅ **Clean Backend API**: RESTful endpoints với validation
- ✅ **Secure Data Flow**: XSS prevention và input sanitization  
- ✅ **Comprehensive Error Handling**: User-friendly error messages
- ✅ **JSON Data Format**: Structured request/response format
- ✅ **Service Layer Architecture**: Separation of concerns

System đã được optimize cho reliability và maintainability trong production environment.
