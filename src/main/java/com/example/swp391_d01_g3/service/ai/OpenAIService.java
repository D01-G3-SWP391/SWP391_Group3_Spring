package com.example.swp391_d01_g3.service.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final String apiKey;
    private final OkHttpClient client;

    // Counters for rate limiting
    private final AtomicInteger requestsPerMinute = new AtomicInteger(0);
    private final AtomicInteger requestsPerDay = new AtomicInteger(0);
    private LocalDateTime lastMinuteReset = LocalDateTime.now();
    private LocalDateTime lastDayReset = LocalDateTime.now();

    // Constants for rate limits
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_REQUESTS_PER_DAY = 500;
    private static final int TARGET_MAX_TOKENS = 20000;

    // Approximate token counting
    private static final int CHARS_PER_TOKEN = 4;

    // Common tables in SWP391
    private static final Set<String> CORE_TABLES = Set.of(
        "account", "blog_image", "blog_post", "employer", "event", "event_form",
        "forgot_password", "interview", "job_application", "job_fields", "jobs_post",
        "notifications", "resource", "student"
    );

    // Vietnamese to English/SQL field mapping
    private static final Map<String, String> VIETNAMESE_MAPPING = Map.ofEntries(
        // jobs_post
        Map.entry("công việc", "jobs_post"),
        Map.entry("việc làm", "jobs_post"),
        Map.entry("tin tuyển dụng", "jobs_post"),
        Map.entry("job", "jobs_post"),
        Map.entry("bài đăng tuyển dụng", "jobs_post"),
        Map.entry("vị trí tuyển dụng", "jobs_post"),
        Map.entry("full time", "job_type"),
        Map.entry("toàn thời gian", "job_type"),
        Map.entry("part time", "job_type"),
        Map.entry("bán thời gian", "job_type"),
        Map.entry("lương", "job_salary"),
        Map.entry("mức lương", "job_salary"),
        Map.entry("địa điểm", "job_location"),
        Map.entry("nơi làm việc", "job_location"),
        Map.entry("yêu cầu công việc", "job_requirements"),
        Map.entry("loại hình", "job_type"),
        Map.entry("ngành nghề công việc", "job_field_id"),
        Map.entry("lĩnh vực công việc", "job_field_id"),

        // IT and Technology fields
        Map.entry("it", "Information Technology"),
        Map.entry("công nghệ thông tin", "Information Technology"),
        Map.entry("cntt", "Information Technology"),
        Map.entry("lập trình", "Programming"),
        Map.entry("phần mềm", "Software"),
        Map.entry("web", "Web Development"),
        Map.entry("app", "Application Development"),
        Map.entry("mobile", "Mobile Development"),
        Map.entry("data", "Data"),
        Map.entry("ai", "Artificial Intelligence"),
        Map.entry("machine learning", "Machine Learning"),
        Map.entry("trí tuệ nhân tạo", "Artificial Intelligence"),
        Map.entry("cơ sở dữ liệu", "Database"),
        Map.entry("database", "Database"),
        Map.entry("devops", "DevOps"),
        Map.entry("cloud", "Cloud Computing"),
        Map.entry("điện toán đám mây", "Cloud Computing"),
        Map.entry("cybersecurity", "Cybersecurity"),
        Map.entry("an ninh mạng", "Cybersecurity"),
        Map.entry("mạng", "Network"),
        Map.entry("network", "Network"),

        // employer
        Map.entry("nhà tuyển dụng", "employer"),
        Map.entry("công ty", "employer"),
        Map.entry("doanh nghiệp", "employer"),
        Map.entry("tổ chức", "employer"),
        Map.entry("tên công ty", "company_name"),
        Map.entry("địa chỉ công ty", "company_address"),
        Map.entry("mô tả công ty", "company_description"),
        Map.entry("lĩnh vực công ty", "jobs_field_id"),

        // student
        Map.entry("sinh viên", "student"),
        Map.entry("người học", "student"),
        Map.entry("ứng viên", "student"),
        Map.entry("hồ sơ sinh viên", "student"),
        Map.entry("profile sinh viên", "student"),
        Map.entry("địa chỉ sinh viên", "address"),
        Map.entry("trường đại học", "university"),
        Map.entry("kinh nghiệm", "experience"),
        Map.entry("mô tả bản thân", "profile_description"),
        Map.entry("ngành học", "job_field_id"),

        // candidate search specific terms for better matching
        Map.entry("marketing", "marketing"),
        Map.entry("kinh doanh", "business"),
        Map.entry("bán hàng", "sales"),
        Map.entry("kế toán", "accounting"),
        Map.entry("nhân sự", "human resources"),
        Map.entry("quản lý", "management"),
        Map.entry("thiết kế", "design"),
        Map.entry("kỹ thuật", "engineering"),
        Map.entry("y tế", "healthcare"),
        Map.entry("giáo dục", "education"),
        Map.entry("tài chính", "finance"),
        Map.entry("logistics", "logistics"),
        Map.entry("du lịch", "tourism"),
        Map.entry("nhà hàng", "restaurant"),
        Map.entry("khách sạn", "hotel"),

        // job_application
        Map.entry("đơn ứng tuyển", "job_application"),
        Map.entry("ứng tuyển", "job_application"),
        Map.entry("hồ sơ ứng tuyển", "job_application"),
        Map.entry("đơn xin việc", "job_application"),
        Map.entry("ứng viên ứng tuyển", "student_id"),
        Map.entry("công việc ứng tuyển", "job_post_id"),
        Map.entry("trạng thái ứng tuyển", "status"),

        // job_fields
        Map.entry("lĩnh vực", "job_fields"),
        Map.entry("danh mục ngành nghề", "job_fields"),
        Map.entry("ngành", "job_fields"),
        Map.entry("lĩnh vực việc làm", "job_fields"),
        Map.entry("tên lĩnh vực", "job_field_name"),

        // blog_post
        Map.entry("bài viết", "blog_post"),
        Map.entry("blog", "blog_post"),
        Map.entry("tin tức", "blog_post"),
        Map.entry("bài blog", "blog_post"),
        Map.entry("bài đăng", "blog_post"),
        Map.entry("tiêu đề bài viết", "title"),
        Map.entry("nội dung bài viết", "content"),
        Map.entry("tóm tắt bài viết", "summary"),
        Map.entry("hình ảnh nổi bật", "featured_image_url"),
        Map.entry("trạng thái bài viết", "status"),

        // event
        Map.entry("sự kiện", "event"),
        Map.entry("chương trình", "event"),
        Map.entry("hoạt động", "event"),
        Map.entry("hội thảo", "event"),
        Map.entry("tên sự kiện", "event_title"),
        Map.entry("ngày sự kiện", "event_date"),
        Map.entry("địa điểm sự kiện", "event_location"),
        Map.entry("mô tả sự kiện", "event_description"),
        Map.entry("trạng thái sự kiện", "event_status"),
        Map.entry("người tổ chức", "employer_id"),

        // account
        Map.entry("tài khoản", "account"),
        Map.entry("người dùng", "account"),
        Map.entry("user", "account"),
        Map.entry("email", "email"),
        Map.entry("họ tên", "full_name"),
        Map.entry("số điện thoại", "phone"),
        Map.entry("trạng thái tài khoản", "status"),

        // resource
        Map.entry("tài nguyên", "resource"),
        Map.entry("tài liệu", "resource"),
        Map.entry("file", "resource"),
        Map.entry("tên tài nguyên", "resource_title"),
        Map.entry("loại tài nguyên", "resource_type"),
        Map.entry("nội dung tài nguyên", "resource_content")
    );

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty() || "${API_KEY}".equals(apiKey)) {
            throw new IllegalStateException("Google API key is not configured. Please set the openai.api.key property.");
        }
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }

    private void updateRateLimits() {
        LocalDateTime now = LocalDateTime.now();

        if (now.minusMinutes(1).isAfter(lastMinuteReset)) {
            logger.info("Resetting per-minute request counter. Previous count: {}", requestsPerMinute.get());
            requestsPerMinute.set(0);
            lastMinuteReset = now;
        }

        if (now.minusDays(1).isAfter(lastDayReset)) {
            logger.info("Resetting daily request counter. Previous count: {}", requestsPerDay.get());
            requestsPerDay.set(0);
            lastDayReset = now;
        }

        int currentMinute = requestsPerMinute.incrementAndGet();
        int currentDay = requestsPerDay.incrementAndGet();

        logger.info("Current rate limits - Requests this minute: {}/{}, Requests today: {}/{}",
                   currentMinute, MAX_REQUESTS_PER_MINUTE,
                   currentDay, MAX_REQUESTS_PER_DAY);

        if (currentMinute > MAX_REQUESTS_PER_MINUTE * 0.8) {
            logger.warn("Approaching per-minute rate limit: {}/{}", currentMinute, MAX_REQUESTS_PER_MINUTE);
        }
        if (currentDay > MAX_REQUESTS_PER_DAY * 0.8) {
            logger.warn("Approaching daily rate limit: {}/{}", currentDay, MAX_REQUESTS_PER_DAY);
        }
    }

    private int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) return 0;
        return text.length() / CHARS_PER_TOKEN;
    }

    private String optimizeSchema(String schema, String userQuestion) {
        if (schema == null || schema.isEmpty()) {
            logger.error("Schema is null or empty");
            throw new IllegalArgumentException("Database schema cannot be null or empty");
        }

        // Tách schema thành các phần CREATE TABLE riêng biệt
        Pattern createTablePattern = Pattern.compile("CREATE TABLE ([^(]+)\\([^;]+;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = createTablePattern.matcher(schema);
        Map<String, String> tables = new HashMap<>();

        while (matcher.find()) {
            String fullDefinition = matcher.group(0);
            String tableName = matcher.group(1).trim().toLowerCase();
            tables.put(tableName, fullDefinition);
        }

        if (tables.isEmpty()) {
            logger.error("No valid table definitions found in schema");
            throw new IllegalArgumentException("Invalid database schema format");
        }

        // Tìm các từ khóa trong câu hỏi
        Set<String> questionKeywords = new HashSet<>(Arrays.asList(userQuestion.toLowerCase().split("\\s+")));

        // Lọc các bảng có liên quan
        Set<String> relevantTableNames = new HashSet<>();
        
        // Thêm các bảng core luôn được ưu tiên nếu được đề cập
        for (String coreTable : CORE_TABLES) {
            if (tables.containsKey(coreTable) && 
                questionKeywords.stream().anyMatch(keyword -> 
                    coreTable.contains(keyword) || keyword.contains(coreTable))) {
                relevantTableNames.add(coreTable);
            }
        }

        // Tìm các bảng được đề cập trong câu hỏi
        for (String tableName : tables.keySet()) {
            if (relevantTableNames.contains(tableName)) continue;

            boolean isRelevant = questionKeywords.stream()
                .anyMatch(keyword -> tableName.contains(keyword) || 
                         tables.get(tableName).toLowerCase().contains(keyword));

            if (isRelevant) {
                relevantTableNames.add(tableName);
            }
        }

        // Thêm các bảng có khóa ngoại liên quan
        Set<String> additionalTables = new HashSet<>();
        for (String tableName : relevantTableNames) {
            String tableDefinition = tables.get(tableName);
            Pattern fkPattern = Pattern.compile("REFERENCES ([^\\s(]+)", Pattern.CASE_INSENSITIVE);
            Matcher fkMatcher = fkPattern.matcher(tableDefinition);
            
            while (fkMatcher.find()) {
                String referencedTable = fkMatcher.group(1).toLowerCase();
                if (tables.containsKey(referencedTable)) {
                    additionalTables.add(referencedTable);
                }
            }
        }
        relevantTableNames.addAll(additionalTables);

        // Nếu không tìm thấy bảng nào liên quan, lấy các bảng core
        if (relevantTableNames.isEmpty()) {
            relevantTableNames.addAll(CORE_TABLES.stream()
                .filter(tables::containsKey)
                .limit(5)
                .toList());
        }

        // Ghép các bảng đã lọc lại thành schema
        StringBuilder optimizedSchema = new StringBuilder();
        relevantTableNames.stream()
            .map(tables::get)
            .forEach(tableDef -> optimizedSchema.append(tableDef).append("\n"));

        // Log thông tin về việc tối ưu
        logger.info("Schema optimization:");
        logger.info("- Original tables count: {}", tables.size());
        logger.info("- Optimized tables count: {}", relevantTableNames.size());
        logger.info("- Original schema size: {} characters", schema.length());
        logger.info("- Optimized schema size: {} characters", optimizedSchema.length());
        logger.info("- Selected tables: {}", String.join(", ", relevantTableNames));

        return optimizedSchema.toString();
    }

    /**
     * Translates Vietnamese keywords in user question to English/SQL field names
     * to help AI better understand the context
     */
    private String translateVietnameseKeywords(String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return userQuestion;
        }

        String translatedQuestion = userQuestion;
        Map<String, String> replacements = new HashMap<>();

        // Sort by length descending to match longer phrases first
        List<String> sortedKeys = new ArrayList<>(VIETNAMESE_MAPPING.keySet());
        sortedKeys.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String vietnameseKey : sortedKeys) {
            String englishValue = VIETNAMESE_MAPPING.get(vietnameseKey);
            
            // Use word boundaries to avoid partial matches
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(vietnameseKey) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(translatedQuestion);
            
            if (matcher.find()) {
                replacements.put(vietnameseKey, englishValue);
                translatedQuestion = matcher.replaceAll(englishValue);
            }
        }

        if (!replacements.isEmpty()) {
            logger.info("Translated Vietnamese keywords: {}", replacements);
            logger.info("Original question: {}", userQuestion);
            logger.info("Translated question: {}", translatedQuestion);
        }

        return translatedQuestion;
    }

    public String convertToSQL(String userQuestion, String schema) throws Exception {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            throw new IllegalArgumentException("User question cannot be null or empty");
        }

        updateRateLimits();

        // Dịch các từ khóa tiếng Việt trước khi xử lý
        String translatedQuestion = translateVietnameseKeywords(userQuestion);

        // Tối ưu schema trước khi sử dụng
        String optimizedSchema = optimizeSchema(schema, translatedQuestion);

        String prompt = "Dựa vào schema database sau:\n" + optimizedSchema +
                       "\n\nHãy chuyển câu hỏi sau thành câu lệnh SQL (chỉ trả về câu lệnh SQL, không giải thích):\n" +
                       translatedQuestion;

        // Kiểm tra số token và cắt bớt nếu cần
        int estimatedTokens = estimateTokenCount(prompt);
        if (estimatedTokens > TARGET_MAX_TOKENS) {
            logger.warn("Token count exceeds target maximum. Truncating schema...");
            int excessTokens = estimatedTokens - TARGET_MAX_TOKENS;
            int charsToRemove = excessTokens * CHARS_PER_TOKEN;
            optimizedSchema = optimizedSchema.substring(0, optimizedSchema.length() - charsToRemove);
            prompt = "Dựa vào schema database sau:\n" + optimizedSchema +
                    "\n\nHãy chuyển câu hỏi sau thành câu lệnh SQL (chỉ trả về câu lệnh SQL, không giải thích):\n" +
                    translatedQuestion;
            estimatedTokens = estimateTokenCount(prompt);
        }

        // Log input details
        logger.info("Request details:");
        logger.info("- Input length: {} characters", prompt.length());
        logger.info("- Estimated input tokens: {}", estimatedTokens);
        logger.info("- Schema length: {} characters", optimizedSchema.length());
        logger.info("- Original question length: {} characters", userQuestion.length());
        logger.info("- Translated question length: {} characters", translatedQuestion.length());

        JSONObject contents = new JSONObject()
            .put("role", "user")
            .put("parts", new JSONArray()
                .put(new JSONObject()
                    .put("text", prompt)
                )
            );

        JSONObject body = new JSONObject()
            .put("contents", new JSONArray().put(contents))
            .put("generationConfig", new JSONObject()
                .put("temperature", 0.3)
                .put("maxOutputTokens", 500)
            );

        HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINT).newBuilder();
        urlBuilder.addQueryParameter("key", apiKey);

        Request request = new Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            // Log response details
            int estimatedOutputTokens = estimateTokenCount(responseBody);
            logger.info("Response details:");
            logger.info("- Response length: {} characters", responseBody.length());
            logger.info("- Estimated output tokens: {}", estimatedOutputTokens);
            logger.info("- Total estimated tokens for this request: {}", estimatedTokens + estimatedOutputTokens);

            if (!response.isSuccessful()) {
                logger.error("Gemini API error: {} - {}", response.code(), responseBody);
                throw new RuntimeException("Gemini API error: " + response.code() + " - " + responseBody);
            }

            try {
                JSONObject json = new JSONObject(responseBody);
                if (!json.has("candidates") || json.getJSONArray("candidates").length() == 0) {
                    throw new RuntimeException("Invalid response from Gemini API: No candidates found");
                }
                String result = json.getJSONArray("candidates")
                          .getJSONObject(0)
                          .getJSONObject("content")
                          .getJSONArray("parts")
                          .getJSONObject(0)
                          .getString("text")
                          .trim();

                // Log final result details
                logger.info("Final SQL query length: {} characters", result.length());
                return result;
            } catch (JSONException e) {
                logger.error("Failed to parse Gemini response: {}", responseBody, e);
                throw new RuntimeException("Failed to parse Gemini response", e);
            }
        } catch (Exception e) {
            logger.error("Error calling Gemini API", e);
            throw new RuntimeException("Error calling Gemini API: " + e.getMessage(), e);
        }
    }

    /**
     * Analyzes query results and provides a natural language response to user
     */
    public String generateNaturalResponse(String userQuestion, String sql, List<Object[]> results) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Bạn là một AI assistant thân thiện đang giúp người dùng tìm hiểu thông tin về việc làm và ứng viên.\n");
            prompt.append("Người dùng đã hỏi: \"").append(userQuestion).append("\"\n");
            
            if (results != null && !results.isEmpty()) {
                prompt.append("Tôi đã tìm được ").append(results.size()).append(" kết quả phù hợp.\n");
                
                // Hiển thị thông tin chi tiết từ kết quả để AI có thể phân tích
                int sampleSize = Math.min(5, results.size());
                prompt.append("Thông tin chi tiết:\n");
                for (int i = 0; i < sampleSize; i++) {
                    Object[] row = results.get(i);
                    prompt.append("- ");
                    for (int j = 0; j < row.length; j++) {
                        if (j > 0) prompt.append(" | ");
                        prompt.append(row[j] != null ? row[j].toString() : "");
                    }
                    prompt.append("\n");
                }
                if (results.size() > sampleSize) {
                    prompt.append("... và ").append(results.size() - sampleSize).append(" kết quả khác\n");
                }
            } else {
                prompt.append("Không tìm thấy dữ liệu phù hợp với yêu cầu.\n");
            }
            
            prompt.append("\nHãy viết một phản hồi bằng tiếng Việt (2-4 câu) như một chuyên gia tư vấn việc làm:\n");
            prompt.append("1. Trả lời trực tiếp câu hỏi của người dùng\n");
            prompt.append("2. Cung cấp thông tin cụ thể và hữu ích từ dữ liệu\n");
            prompt.append("3. Đưa ra gợi ý hoặc lời khuyên nếu phù hợp\n");
            prompt.append("4. Giọng điệu thân thiện, chuyên nghiệp\n");
            prompt.append("Không nhắc đến SQL hay cơ sở dữ liệu.");

            // Gọi AI để tạo response
            String aiPrompt = prompt.toString();
            
            JSONObject contents = new JSONObject()
                .put("role", "user")
                .put("parts", new JSONArray()
                    .put(new JSONObject()
                        .put("text", aiPrompt)
                    )
                );

            JSONObject body = new JSONObject()
                .put("contents", new JSONArray().put(contents))
                .put("generationConfig", new JSONObject()
                    .put("temperature", 0.7)
                    .put("maxOutputTokens", 300)
                );

            HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINT).newBuilder();
            urlBuilder.addQueryParameter("key", apiKey);

            Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    logger.error("Gemini API error in natural response: {} - {}", response.code(), responseBody);
                    return "Tôi đã tìm được " + (results != null ? results.size() : 0) + " kết quả cho câu hỏi của bạn.";
                }

                try {
                    JSONObject json = new JSONObject(responseBody);
                    if (!json.has("candidates") || json.getJSONArray("candidates").length() == 0) {
                        return "Tôi đã tìm được " + (results != null ? results.size() : 0) + " kết quả cho câu hỏi của bạn.";
                    }
                    
                    String naturalResponse = json.getJSONArray("candidates")
                              .getJSONObject(0)
                              .getJSONObject("content")
                              .getJSONArray("parts")
                              .getJSONObject(0)
                              .getString("text")
                              .trim();

                    logger.info("Generated natural response: {}", naturalResponse);
                    return naturalResponse;
                    
                } catch (JSONException e) {
                    logger.error("Failed to parse natural response: {}", responseBody, e);
                    return "Tôi đã tìm được " + (results != null ? results.size() : 0) + " kết quả cho câu hỏi của bạn.";
                }
            }
            
        } catch (Exception e) {
            logger.error("Error generating natural response", e);
            return "Tôi đã tìm được " + (results != null ? results.size() : 0) + " kết quả cho câu hỏi của bạn.";
        }
    }

    /**
     * Main method to process natural language queries
     * Handles both general questions and database-specific queries
     */
    public String processNaturalLanguageQuery(String userQuestion) {
        try {
            if (userQuestion == null || userQuestion.trim().isEmpty()) {
                return "Vui lòng nhập câu hỏi của bạn.";
            }
            
            // Check if this is a general conversation question
            if (isGeneralQuestion(userQuestion)) {
                return handleGeneralQuestion(userQuestion);
            }
            
            // For database-related questions, provide better guidance
            logger.info("Processing database query: {}", userQuestion);
            
            // Check if question contains job-related keywords
            if (containsJobRelatedKeywords(userQuestion)) {
                return "Đây là một câu hỏi về cơ sở dữ liệu. Vui lòng sử dụng hệ thống query chính.";
            }
            
            // If not clearly job-related but not general, provide helpful guidance
            return "Tôi hiểu bạn đang muốn tìm hiểu thông tin. Để tôi có thể giúp bạn tốt nhất, " +
                   "hãy hỏi về các chủ đề sau:\n\n" +
                   "🔍 **Tìm kiếm việc làm:**\n" +
                   "• \"Hiển thị công việc mới nhất\"\n" +
                   "• \"Tìm việc làm lương cao\"\n" +
                   "• \"Công việc IT ở TP.HCM\"\n\n" +
                   "📊 **Thống kê và phân tích:**\n" +
                   "• \"Có bao nhiêu công việc về IT?\"\n" +
                   "• \"Mức lương trung bình của developer\"\n" +
                   "• \"Top 5 công ty tuyển nhiều nhất\"\n\n" +
                   "Hoặc bạn có thể hỏi về website và cách sử dụng!";
            
        } catch (Exception e) {
            logger.error("Error processing natural language query", e);
            return "Xin lỗi, tôi không thể xử lý câu hỏi của bạn lúc này. Vui lòng thử lại sau.";
        }
    }

    /**
     * Check if the question is a general conversation question
     */
    private boolean isGeneralQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            return true;
        }
        
        String lowerQuestion = question.toLowerCase().trim();
        
        // General greetings and conversation patterns
        String[] generalPatterns = {
            "hello", "hi", "chào", "xin chào", "hey",
            "bạn là ai", "bạn là gì", "ai là bạn", "you are",
            "website này là gì", "web này", "trang web này",
            "đây là web gì", "đây là website gì", "đây là trang web gì",
            "giới thiệu", "thông tin", "hướng dẫn sử dụng",
            "làm sao để", "cách sử dụng", "hướng dẫn",
            "tôi cần", "giúp tôi", "help me",
            "cảm ơn", "thank you", "thanks", "bye", "tạm biệt",
            "kết quả là gì", "kết quả", "result"
        };
        
        for (String pattern : generalPatterns) {
            if (lowerQuestion.contains(pattern)) {
                return true;
            }
        }
        
        // Check if question is very short (likely greeting)
        if (lowerQuestion.length() < 15 && !containsJobRelatedKeywords(lowerQuestion)) {
            return true;
        }
        
        return false;
    }

    /**
     * Check if question contains job-related keywords
     */
    private boolean containsJobRelatedKeywords(String question) {
        String[] jobKeywords = {
            "công việc", "việc làm", "job", "lương", "salary", "company", "công ty",
            "ứng tuyển", "tuyển dụng", "tuyển", "hire", "career", "nghề nghiệp",
            "it", "cntt", "lập trình", "developer", "engineer", "kỹ sư"
        };
        
        String lowerQuestion = question.toLowerCase();
        for (String keyword : jobKeywords) {
            if (lowerQuestion.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle general conversation questions
     */
    private String handleGeneralQuestion(String question) {
        String lowerQuestion = question.toLowerCase().trim();
        
        // Greeting responses
        if (lowerQuestion.contains("hello") || lowerQuestion.contains("hi") || 
            lowerQuestion.contains("chào") || lowerQuestion.contains("xin chào")) {
            return "Xin chào! Tôi là AI Assistant của website tìm việc làm. Tôi có thể giúp bạn:\n" +
                   "• Tìm kiếm công việc phù hợp\n" +
                   "• Thông tin về mức lương và yêu cầu công việc\n" +
                   "• Thống kê về thị trường việc làm\n" +
                   "• Tư vấn nghề nghiệp\n\n" +
                   "Bạn có thể hỏi tôi bất cứ điều gì về việc làm!";
        }
        
        // Website information
        if (lowerQuestion.contains("website") || lowerQuestion.contains("web") || 
            lowerQuestion.contains("trang web") || lowerQuestion.contains("đây là")) {
            return "Đây là website tìm việc làm SWP391 - một nền tảng kết nối ứng viên và nhà tuyển dụng. " +
                   "Website cung cấp:\n" +
                   "• Đăng tin tuyển dụng cho nhà tuyển dụng\n" +
                   "• Tìm kiếm và ứng tuyển việc làm cho ứng viên\n" +
                   "• Quản lý hồ sơ cá nhân\n" +
                   "• Tư vấn nghề nghiệp với AI\n" +
                   "• Thông tin về sự kiện tuyển dụng\n\n" +
                   "Bạn muốn tìm hiểu về lĩnh vực nào cụ thể?";
        }
        
        // About AI
        if (lowerQuestion.contains("bạn là ai") || lowerQuestion.contains("bạn là gì") || 
            lowerQuestion.contains("ai là bạn") || lowerQuestion.contains("you are")) {
            return "Tôi là AI Assistant của website việc làm SWP391. Tôi được thiết kế để:\n" +
                   "• Hỗ trợ tìm kiếm thông tin về việc làm\n" +
                   "• Phân tích dữ liệu thị trường lao động\n" +
                   "• Tư vấn về cơ hội nghề nghiệp\n" +
                   "• Trả lời các câu hỏi về tuyển dụng\n\n" +
                   "Tôi có thể giúp bạn tìm hiểu về mức lương, yêu cầu công việc, và xu hướng tuyển dụng!";
        }
        
        // Help and guidance
        if (lowerQuestion.contains("giúp") || lowerQuestion.contains("help") || 
            lowerQuestion.contains("hướng dẫn") || lowerQuestion.contains("làm sao")) {
            return "Tôi có thể giúp bạn với nhiều thông tin về việc làm:\n\n" +
                   "📊 **Thống kê việc làm:**\n" +
                   "• \"Có bao nhiêu công việc IT?\"\n" +
                   "• \"Công việc nào có lương cao nhất?\"\n" +
                   "• \"Mức lương trung bình của lập trình viên?\"\n\n" +
                   "🔍 **Tìm kiếm cụ thể:**\n" +
                   "• \"Tìm công việc ở Hà Nội\"\n" +
                   "• \"Việc làm part-time cho sinh viên\"\n" +
                   "• \"Công ty nào đang tuyển AI engineer?\"\n\n" +
                   "Hãy thử hỏi tôi bất kỳ câu hỏi nào!";
        }
        
        // Thanks and goodbye
        if (lowerQuestion.contains("cảm ơn") || lowerQuestion.contains("thank")) {
            return "Rất vui được giúp đỡ bạn! Nếu có thêm câu hỏi về việc làm, đừng ngần ngại hỏi tôi nhé. " +
                   "Chúc bạn tìm được công việc ưng ý! 😊";
        }
        
        if (lowerQuestion.contains("bye") || lowerQuestion.contains("tạm biệt")) {
            return "Tạm biệt! Hy vọng thông tin tôi cung cấp hữu ích cho bạn. " +
                   "Chúc bạn thành công trong việc tìm kiếm cơ hội nghề nghiệp! 👋";
        }
        
        // Results question
        if (lowerQuestion.contains("kết quả") || lowerQuestion.contains("result")) {
            return "Để xem kết quả chi tiết, bạn cần đặt một câu hỏi cụ thể về việc làm. Ví dụ:\n" +
                   "• \"Hiển thị 10 công việc mới nhất\"\n" +
                   "• \"Công việc nào có lương cao nhất?\"\n" +
                   "• \"Có bao nhiêu việc làm IT?\"\n\n" +
                   "Tôi sẽ phân tích dữ liệu và cung cấp thông tin chi tiết cho bạn!";
        }
        
        // Default general response
        return "Tôi hiểu bạn muốn trò chuyện! Tôi là AI chuyên về tư vấn việc làm. " +
               "Hãy hỏi tôi về:\n" +
               "• Thông tin công việc và mức lương\n" +
               "• Thống kê thị trường lao động\n" +
               "• Xu hướng tuyển dụng\n" +
               "• Tư vấn nghề nghiệp\n\n" +
               "Ví dụ: \"Tìm công việc lương cao\" hoặc \"Có bao nhiêu việc làm IT?\"";
    }

    /**
     * 🎯 AI-powered candidate search - separate from general chat AI
     * Converts natural language queries to SQL for finding students/candidates
     */
    public String searchCandidatesWithAI(String userQuestion, String schema) throws Exception {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }

        updateRateLimits();

        // Detect language of the query for appropriate response
        boolean isVietnamese = isVietnameseQuery(userQuestion);

        // Enhanced translation for candidate-specific queries
        String translatedQuestion = translateCandidateKeywords(userQuestion);

        // Optimize schema for candidate search (focus on student + account tables)
        String optimizedSchema = optimizeSchemaForCandidates(schema, translatedQuestion);

        // Enhanced prompt specifically for candidate search
        String prompt = "Bạn là chuyên gia SQL cho hệ thống tuyển dụng. " +
                       "Database schema:\n" + optimizedSchema +
                       "\n\nTạo SQL query để tìm ứng viên/sinh viên dựa trên yêu cầu sau:\n" +
                       translatedQuestion + 
                       "\n\nYêu cầu QUAN TRỌNG:" +
                       "\n- LUÔN JOIN: FROM student s JOIN account a ON s.user_id = a.user_id LEFT JOIN job_fields jf ON s.job_field_id = jf.job_field_id" +
                       "\n- SELECT: s.student_id, a.full_name, a.email, a.phone, s.address, s.university, s.experience, s.profile_description, jf.job_field_name" +
                       "\n- Tìm kiếm trong experience: Dùng LIKE để tìm cả tiếng Việt và tiếng Anh (VD: experience LIKE '%3 months%' OR experience LIKE '%3 tháng%')" +
                       "\n- Tìm kiếm trong address: Dùng LIKE không phân biệt hoa thường (VD: LOWER(s.address) LIKE LOWER('%ho chi minh%'))" +
                       "\n- Tìm kiếm skill/field: Kiểm tra cả experience, profile_description và job_field_name" +
                       "\n- Kết hợp điều kiện với AND khi có nhiều yêu cầu" +
                       "\n- CHỈ trả về SQL query, KHÔNG giải thích";

        // Token management for candidate search
        int estimatedTokens = estimateTokenCount(prompt);
        if (estimatedTokens > TARGET_MAX_TOKENS) {
            logger.warn("Candidate search token count exceeds limit. Truncating...");
            int excessTokens = estimatedTokens - TARGET_MAX_TOKENS;
            int charsToRemove = excessTokens * CHARS_PER_TOKEN;
            optimizedSchema = optimizedSchema.substring(0, Math.max(0, optimizedSchema.length() - charsToRemove));
            prompt = "Database schema:\n" + optimizedSchema +
                    "\n\nTìm ứng viên: " + translatedQuestion +
                    "\nSELECT s.student_id, a.full_name, a.email, s.address, s.university, s.experience " +
                    "FROM student s JOIN account a ON s.user_id = a.user_id WHERE ";
        }

        logger.info("Processing candidate search query: {}", userQuestion);

        // Call Gemini API for candidate search
        JSONObject contents = new JSONObject()
            .put("role", "user")
            .put("parts", new JSONArray()
                .put(new JSONObject()
                    .put("text", prompt)
                )
            );

        JSONObject body = new JSONObject()
            .put("contents", new JSONArray().put(contents))
            .put("generationConfig", new JSONObject()
                .put("temperature", 0.1)  // Very low temperature for precise SQL
                .put("maxOutputTokens", 300)
            );

        HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINT).newBuilder();
        urlBuilder.addQueryParameter("key", apiKey);

        Request request = new Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                logger.error("Gemini API error in candidate search: {} - {}", response.code(), responseBody);
                throw new RuntimeException("Gemini API error: " + response.code());
            }

            try {
                JSONObject json = new JSONObject(responseBody);
                if (!json.has("candidates") || json.getJSONArray("candidates").length() == 0) {
                    throw new RuntimeException("No SQL generated for candidate search");
                }
                
                String sql = json.getJSONArray("candidates")
                          .getJSONObject(0)
                          .getJSONObject("content")
                          .getJSONArray("parts")
                          .getJSONObject(0)
                          .getString("text")
                          .trim();

                // Clean SQL
                sql = sql.replaceAll("(?s)```sql|```", "").trim();
                
                return sql;
                
            } catch (JSONException e) {
                logger.error("Failed to parse candidate search response: {}", responseBody, e);
                throw new RuntimeException("Failed to parse SQL response", e);
            }
        }
    }
    
    /**
     * Enhanced keyword translation specifically for candidate search
     */
    private String translateCandidateKeywords(String userQuestion) {
        String translatedQuestion = translateVietnameseKeywords(userQuestion);
        
        // Enhanced time unit translations
        translatedQuestion = translatedQuestion
            // Time units with numbers
            .replaceAll("(?i)\\b(\\d+)\\s*tháng\\b", "$1 months")
            .replaceAll("(?i)\\b(\\d+)\\s*năm\\b", "$1 years")
            .replaceAll("(?i)\\b(\\d+)\\s*tuần\\b", "$1 weeks")
            
            // Experience patterns
            .replaceAll("(?i)\\bkinh\\s*nghiệm\\s*(\\d+)\\s*tháng\\b", "experience with $1 months")
            .replaceAll("(?i)\\bkinh\\s*nghiệm\\s*(\\d+)\\s*năm\\b", "experience with $1 years")
            .replaceAll("(?i)\\b(\\d+)\\s*tháng\\s*kinh\\s*nghiệm\\b", "$1 months experience")
            .replaceAll("(?i)\\b(\\d+)\\s*năm\\s*kinh\\s*nghiệm\\b", "$1 years experience")
            
            // Vietnamese experience patterns
            .replaceAll("(?i)\\bcó\\s*kinh\\s*nghiệm\\s*(\\d+)\\s*tháng\\b", "have $1 months experience")
            .replaceAll("(?i)\\bcó\\s*kinh\\s*nghiệm\\s*(\\d+)\\s*năm\\b", "have $1 years experience")
            
            // "về" (about/in) patterns for skills/fields
            .replaceAll("(?i)\\bvề\\s*(\\w+)", "in $1")
            .replaceAll("(?i)\\bkinh\\s*nghiệm\\s*về\\s*(\\w+)", "experience in $1")
            
            // Location patterns
            .replaceAll("(?i)\\bở\\s+hồ\\s*chí\\s*minh\\b", "in Ho Chi Minh")
            .replaceAll("(?i)\\bở\\s+hà\\s*nội\\b", "in Hanoi")
            .replaceAll("(?i)\\bở\\s+đà\\s*nẵng\\b", "in Da Nang")
            .replaceAll("(?i)\\btại\\s+hồ\\s*chí\\s*minh\\b", "in Ho Chi Minh")
            .replaceAll("(?i)\\btại\\s+hà\\s*nội\\b", "in Hanoi")
            .replaceAll("(?i)\\btại\\s+đà\\s*nẵng\\b", "in Da Nang")
            
            // General location patterns
            .replaceAll("(?i)\\bở\\s+(.+?)\\s*(,|$|\\s+và|\\s+with)", "in $1")
            .replaceAll("(?i)\\btại\\s+(.+?)\\s*(,|$|\\s+và|\\s+with)", "in $1")
            
            // Remove search-specific words
            .replaceAll("(?i)\\btìm\\s*(ứng\\s*viên|người)\\b", "find candidates")
            .replaceAll("(?i)\\bmuốn\\s*tìm\\b", "looking for")
            .replaceAll("(?i)\\btôi\\s*muốn\\b", "I want")
            
            // Intern/internship patterns
            .replaceAll("(?i)\\bthực\\s*tập\\s*sinh\\b", "intern")
            .replaceAll("(?i)\\bthực\\s*tập\\b", "internship")
            
            // Fresh graduate patterns
            .replaceAll("(?i)\\bmới\\s*ra\\s*trường\\b", "fresh graduate")
            .replaceAll("(?i)\\bfresh\\s*graduate\\b", "fresh graduate")
            
            // Education patterns
            .replaceAll("(?i)\\bhọc\\s*tại\\s*(.+?)\\s*(,|$|\\s+và)", "studied at $1")
            .replaceAll("(?i)\\btrường\\s*(.+?)\\s*(,|$|\\s+và)", "university $1");
             
        return translatedQuestion;
    }
    
    /**
     * Optimize schema specifically for candidate search
     */
    private String optimizeSchemaForCandidates(String schema, String userQuestion) {
        // Always include these tables for candidate search
        Set<String> candidateTables = Set.of("student", "account", "job_fields");
        
        Pattern createTablePattern = Pattern.compile("CREATE TABLE ([^(]+)\\([^;]+;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = createTablePattern.matcher(schema);
        Map<String, String> tables = new HashMap<>();

        while (matcher.find()) {
            String fullDefinition = matcher.group(0);
            String tableName = matcher.group(1).trim().toLowerCase();
            tables.put(tableName, fullDefinition);
        }

        StringBuilder optimizedSchema = new StringBuilder();
        
        // Add candidate-relevant tables
        for (String tableName : candidateTables) {
            if (tables.containsKey(tableName)) {
                optimizedSchema.append(tables.get(tableName)).append("\n");
            }
        }
        
        return optimizedSchema.toString();
    }

    /**
     * Detect if query is in Vietnamese to generate appropriate response language
     */
    private boolean isVietnameseQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return true; // Default to Vietnamese
        }
        
        String lowerQuery = query.toLowerCase();
        
        // 1. Check for Vietnamese diacritics - most reliable indicator
        if (lowerQuery.matches(".*[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ].*")) {
            return true;
        }
        
        // 2. Check for distinctly Vietnamese words (high confidence)
        String[] highConfidenceVietnamese = {
            "tôi", "tìm", "muốn", "cần", "của", "là", "được", "cho", "này", "đó",
            "ứng viên", "sinh viên", "kinh nghiệm", "thực tập", "hà nội", "hồ chí minh", "đà nẵng"
        };
        
        for (String word : highConfidenceVietnamese) {
            if (lowerQuery.contains(word)) {
                return true;
            }
        }
        
        // 3. Character-based analysis for Vietnamese patterns
        int vietnameseCharScore = 0;
        
        // Common Vietnamese character combinations
        String[] vietnamesePatterns = {
            "ng", "ch", "th", "ph", "gh", "kh", "nh", "tr", "qu", "gi"
        };
        
        for (String pattern : vietnamesePatterns) {
            if (lowerQuery.contains(pattern)) {
                vietnameseCharScore++;
            }
        }
        
        // 4. English-specific patterns (if found, likely English)
        String[] englishPatterns = {
            " the ", " and ", " with ", " for ", " from ", " that ", " this ",
            "experience", "developer", "engineer", "manager", "years", "months"
        };
        
        int englishScore = 0;
        for (String pattern : englishPatterns) {
            if (lowerQuery.contains(pattern)) {
                englishScore++;
            }
        }
        
        // 5. Vietnamese common words (lower confidence but still indicators)
        String[] commonVietnamese = {
            "người", "có", "về", "ở", "tại", "làm", "việc", "công ty", 
            "năm", "tháng", "ngày", "và", "với", "trong", "nào", "gì", "như"
        };
        
        int commonVietnameseCount = 0;
        for (String word : commonVietnamese) {
            if (lowerQuery.contains(word)) {
                commonVietnameseCount++;
            }
        }
        
        // Decision logic
        if (englishScore > 0 && commonVietnameseCount == 0 && vietnameseCharScore < 2) {
            return false; // Likely English
        }
        
        if (commonVietnameseCount > 0 || vietnameseCharScore >= 2) {
            return true; // Likely Vietnamese
        }
        
        // 6. As a fallback, check word structure
        // Vietnamese words tend to be shorter and have certain patterns
        String[] words = lowerQuery.split("\\s+");
        int shortWordsCount = 0;
        for (String word : words) {
            if (word.length() <= 4 && !word.matches("\\d+")) { // Short words, not numbers
                shortWordsCount++;
            }
        }
        
        // If most words are short, lean towards Vietnamese
        if (words.length > 0 && (double) shortWordsCount / words.length > 0.6) {
            return true;
        }
        
        // Default to Vietnamese for ambiguous cases (since this is primarily a Vietnamese system)
        return true;
    }

    /**
     * Generate natural language response for candidate search results
     */
    public String generateCandidateSearchResponse(String originalQuery, List<Object[]> results) {
        boolean isVietnamese = isVietnameseQuery(originalQuery);
        int resultCount = results != null ? results.size() : 0;

        if (resultCount == 0) {
            if (isVietnamese) {
                return "Không tìm thấy ứng viên phù hợp với yêu cầu của bạn. Hãy thử lại với từ khóa khác hoặc mở rộng tiêu chí tìm kiếm.";
            } else {
                return "No candidates found matching your criteria. Try adjusting your search terms or broadening your requirements.";
            }
        } else {
            // Analyze results for insights
            String insights = generateCandidateInsights(results, isVietnamese);
            
            if (resultCount == 1) {
                if (isVietnamese) {
                    return "Chào bạn, tôi thấy ứng viên " + getCandidateName(results.get(0)) + 
                           " phù hợp với yêu cầu tìm kiếm của bạn. " + insights;
                } else {
                    return "Hello, I found candidate " + getCandidateName(results.get(0)) + 
                           " matching your search criteria. " + insights;
                }
            } else {
                if (isVietnamese) {
                    return "Chào bạn, tôi thấy " + resultCount + " ứng viên phù hợp với yêu cầu của bạn. " + insights;
                } else {
                    return "Hello, I found " + resultCount + " candidates matching your requirements. " + insights;
                }
            }
        }
    }

    /**
     * Generate insights about the found candidates
     */
    private String generateCandidateInsights(List<Object[]> results, boolean isVietnamese) {
        if (results == null || results.isEmpty()) {
            return "";
        }

        try {
            // Analyze experience levels, locations, universities
            Map<String, Integer> universities = new HashMap<>();
            Map<String, Integer> locations = new HashMap<>();
            int withExperience = 0;

            for (Object[] result : results) {
                // Assuming: student_id, full_name, email, phone, address, university, experience, profile_description, job_field_name
                String university = result.length > 5 && result[5] != null ? result[5].toString() : "";
                String address = result.length > 4 && result[4] != null ? result[4].toString() : "";
                String experience = result.length > 6 && result[6] != null ? result[6].toString() : "";

                if (!university.isEmpty()) {
                    universities.put(university, universities.getOrDefault(university, 0) + 1);
                }
                
                if (!address.isEmpty()) {
                    // Extract city from address
                    String city = extractCity(address);
                    if (!city.isEmpty()) {
                        locations.put(city, locations.getOrDefault(city, 0) + 1);
                    }
                }
                
                if (!experience.isEmpty() && !experience.toLowerCase().contains("not updated")) {
                    withExperience++;
                }
            }

            // Generate insights
            StringBuilder insights = new StringBuilder();
            
            if (isVietnamese) {
                if (withExperience > 0) {
                    insights.append("Có ").append(withExperience).append(" ứng viên có kinh nghiệm thực tế. ");
                }
                
                if (!universities.isEmpty()) {
                    String topUniversity = universities.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("");
                    if (!topUniversity.isEmpty()) {
                        insights.append("Phần lớn từ ").append(topUniversity).append(". ");
                    }
                }
                
                if (!locations.isEmpty() && locations.size() > 1) {
                    insights.append("Ứng viên đến từ nhiều địa phương khác nhau. ");
                }
                
                insights.append("Bạn có thể xem chi tiết để đánh giá kỹ hơn!");
            } else {
                if (withExperience > 0) {
                    insights.append(withExperience).append(" candidate(s) have practical experience. ");
                }
                
                if (!universities.isEmpty()) {
                    String topUniversity = universities.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("");
                    if (!topUniversity.isEmpty()) {
                        insights.append("Most are from ").append(topUniversity).append(". ");
                    }
                }
                
                if (!locations.isEmpty() && locations.size() > 1) {
                    insights.append("Candidates are from various locations. ");
                }
                
                insights.append("You can view details for further evaluation!");
            }

            return insights.toString();
            
        } catch (Exception e) {
            // If analysis fails, return simple message
            if (isVietnamese) {
                return "Hãy xem chi tiết từng ứng viên để đánh giá phù hợp nhất.";
            } else {
                return "Please review each candidate's details for the best match.";
            }
        }
    }

    /**
     * Extract candidate name from result row
     */
    private String getCandidateName(Object[] result) {
        if (result != null && result.length > 1 && result[1] != null) {
            return result[1].toString();
        }
        return "N/A";
    }

    /**
     * Extract city name from address
     */
    private String extractCity(String address) {
        if (address == null || address.isEmpty()) {
            return "";
        }
        
        String lowerAddress = address.toLowerCase();
        
        if (lowerAddress.contains("ho chi minh") || lowerAddress.contains("hcm") || lowerAddress.contains("saigon")) {
            return "Ho Chi Minh";
        } else if (lowerAddress.contains("hanoi") || lowerAddress.contains("hà nội")) {
            return "Hà Nội";
        } else if (lowerAddress.contains("da nang") || lowerAddress.contains("đà nẵng")) {
            return "Đà Nẵng";
        }
        
        // Try to extract last part (usually city)
        String[] parts = address.split(",");
        if (parts.length > 0) {
            return parts[parts.length - 1].trim();
        }
        
        return "";
    }

    /**
     * Generate error message for candidate search based on query language
     */
    public String generateCandidateSearchErrorMessage(String originalQuery) {
        boolean isVietnamese = isVietnameseQuery(originalQuery);
        
        if (isVietnamese) {
            return "Không thể tìm kiếm ứng viên. Vui lòng thử lại với từ khóa khác.\n" +
                   "Ví dụ: \"Tìm ứng viên Java có 3 năm kinh nghiệm\" hoặc \"Sinh viên IT ở Hà Nội\"";
        } else {
            return "Unable to search for candidates. Please try again with different keywords.\n" +
                   "Examples: \"Find Java developers with 3 years experience\" or \"Fresh IT graduates in Hanoi\"";
        }
    }
}