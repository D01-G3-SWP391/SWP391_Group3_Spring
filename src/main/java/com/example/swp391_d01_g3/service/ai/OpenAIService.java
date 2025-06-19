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

    public String convertToSQL(String userQuestion, String schema) throws Exception {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            throw new IllegalArgumentException("User question cannot be null or empty");
        }

        updateRateLimits();

        // Tối ưu schema trước khi sử dụng
        String optimizedSchema = optimizeSchema(schema, userQuestion);

        String prompt = "Dựa vào schema database sau:\n" + optimizedSchema +
                       "\n\nHãy chuyển câu hỏi sau thành câu lệnh SQL (chỉ trả về câu lệnh SQL, không giải thích):\n" +
                       userQuestion;

        // Kiểm tra số token và cắt bớt nếu cần
        int estimatedTokens = estimateTokenCount(prompt);
        if (estimatedTokens > TARGET_MAX_TOKENS) {
            logger.warn("Token count exceeds target maximum. Truncating schema...");
            int excessTokens = estimatedTokens - TARGET_MAX_TOKENS;
            int charsToRemove = excessTokens * CHARS_PER_TOKEN;
            optimizedSchema = optimizedSchema.substring(0, optimizedSchema.length() - charsToRemove);
            prompt = "Dựa vào schema database sau:\n" + optimizedSchema +
                    "\n\nHãy chuyển câu hỏi sau thành câu lệnh SQL (chỉ trả về câu lệnh SQL, không giải thích):\n" +
                    userQuestion;
            estimatedTokens = estimateTokenCount(prompt);
        }

        // Log input details
        logger.info("Request details:");
        logger.info("- Input length: {} characters", prompt.length());
        logger.info("- Estimated input tokens: {}", estimatedTokens);
        logger.info("- Schema length: {} characters", optimizedSchema.length());
        logger.info("- Question length: {} characters", userQuestion.length());

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
}