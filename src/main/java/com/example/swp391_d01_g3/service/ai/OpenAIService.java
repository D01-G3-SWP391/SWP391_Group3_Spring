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
}