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
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final String apiKey;
    private final OkHttpClient client;

    // Counters for rate limiting - Thread-safe implementation
    private final AtomicInteger requestsPerMinute = new AtomicInteger(0);
    private final AtomicInteger requestsPerDay = new AtomicInteger(0);
    private final AtomicReference<LocalDateTime> lastMinuteReset = new AtomicReference<>(LocalDateTime.now());
    private final AtomicReference<LocalDateTime> lastDayReset = new AtomicReference<>(LocalDateTime.now());

    // Configuration constants - should be externalized
    @Value("${ai.rate-limit.per-minute:60}")
    private int maxRequestsPerMinute;
    
    @Value("${ai.rate-limit.per-day:500}")
    private int maxRequestsPerDay;
    
    @Value("${ai.max-tokens:20000}")
    private int targetMaxTokens;

    // Constants for rate limits (fallback values)
    private static final int DEFAULT_MAX_REQUESTS_PER_MINUTE = 60;
    private static final int DEFAULT_MAX_REQUESTS_PER_DAY = 500;
    private static final int DEFAULT_TARGET_MAX_TOKENS = 20000;
    
    // Approximate token counting
    private static final int CHARS_PER_TOKEN = 4;
    
    // Magic numbers as constants
    private static final int MIN_QUESTION_LENGTH_FOR_ANALYSIS = 15;
    private static final int MAX_SAMPLE_RESULTS = 5;
    private static final double RATE_LIMIT_WARNING_THRESHOLD = 0.8;
    
    // Common tables in SWP391
    private static final Set<String> CORE_TABLES = Set.of(
        "account", "blog_image", "blog_post", "employer", "event", "event_form",
        "forgot_password", "interview", "job_application", "job_fields", "jobs_post",
        "notifications", "resource", "student"
    );

    // Vietnamese to English/SQL field mapping
    private static final Map<String, String> VIETNAMESE_MAPPING = Map.ofEntries(
            // account
            Map.entry("tài khoản", "account"),
            Map.entry("người dùng", "account"),
            Map.entry("user", "account"),
            Map.entry("thông tin người dùng", "account"),
            Map.entry("đăng nhập", "account"),
            Map.entry("đăng ký", "account"),
            Map.entry("username", "account"),
            Map.entry("hồ sơ người dùng", "account"),
            Map.entry("tên đăng nhập", "account"),

            // blog_post
            Map.entry("bài viết", "blog_post"),
            Map.entry("tin tức", "blog_post"),
            Map.entry("bài blog", "blog_post"),
            Map.entry("blog", "blog_post"),
            Map.entry("bài đăng", "blog_post"),
            Map.entry("bài chia sẻ", "blog_post"),
            Map.entry("nội dung blog", "blog_post"),
            Map.entry("bài báo", "blog_post"),
            Map.entry("tin blog", "blog_post"),
            Map.entry("nội dung chia sẻ", "blog_post"),

            // blog_image
            Map.entry("hình ảnh blog", "blog_image"),
            Map.entry("ảnh blog", "blog_image"),
            Map.entry("ảnh bài viết", "blog_image"),
            Map.entry("hình minh họa", "blog_image"),
            Map.entry("ảnh đính kèm", "blog_image"),

            // employer
            Map.entry("nhà tuyển dụng", "employer"),
            Map.entry("công ty", "employer"),
            Map.entry("doanh nghiệp", "employer"),
            Map.entry("đơn vị tuyển dụng", "employer"),
            Map.entry("tổ chức", "employer"),
            Map.entry("người tuyển dụng", "employer"),
            Map.entry("bên tuyển dụng", "employer"),
            Map.entry("nơi tuyển dụng", "employer"),
            Map.entry("bên đăng việc", "employer"),
            Map.entry("tên công ty", "company_name"),
            Map.entry("địa chỉ công ty", "company_address"),
            Map.entry("mô tả công ty", "company_description"),
            Map.entry("lĩnh vực công ty", "jobs_field_id"),

            // event
            Map.entry("sự kiện", "event"),
            Map.entry("chương trình", "event"),
            Map.entry("hoạt động", "event"),
            Map.entry("hội thảo", "event"),
            Map.entry("buổi gặp mặt", "event"),
            Map.entry("buổi giao lưu", "event"),
            Map.entry("event", "event"),

            // event_form
            Map.entry("đăng ký sự kiện", "event_form"),
            Map.entry("biểu mẫu sự kiện", "event_form"),
            Map.entry("form sự kiện", "event_form"),
            Map.entry("form đăng ký", "event_form"),
            Map.entry("mẫu tham gia sự kiện", "event_form"),

            // forgot_password
            Map.entry("quên mật khẩu", "forgot_password"),
            Map.entry("lấy lại mật khẩu", "forgot_password"),
            Map.entry("khôi phục mật khẩu", "forgot_password"),
            Map.entry("reset mật khẩu", "forgot_password"),
            Map.entry("yêu cầu đổi mật khẩu", "forgot_password"),

            // interview
            Map.entry("phỏng vấn", "interview"),
            Map.entry("lịch phỏng vấn", "interview"),
            Map.entry("buổi phỏng vấn", "interview"),
            Map.entry("mời phỏng vấn", "interview"),
            Map.entry("hẹn phỏng vấn", "interview"),

            // job_application
            Map.entry("ứng tuyển", "job_application"),
            Map.entry("đơn ứng tuyển", "job_application"),
            Map.entry("hồ sơ ứng tuyển", "job_application"),
            Map.entry("nộp hồ sơ", "job_application"),
            Map.entry("đơn xin việc", "job_application"),
            Map.entry("ứng viên ứng tuyển", "student_id"),
            Map.entry("công việc ứng tuyển", "job_post_id"),
            Map.entry("trạng thái ứng tuyển", "status"),

            // job_fields
            Map.entry("lĩnh vực", "job_fields"),
            Map.entry("ngành nghề", "job_fields"),
            Map.entry("lĩnh vực công việc", "job_fields"),
            Map.entry("ngành", "job_fields"),
            Map.entry("ngành nghề tuyển dụng", "job_fields"),
            Map.entry("danh mục ngành", "job_fields"),
            Map.entry("danh sách ngành", "job_fields"),
            Map.entry("job field", "job_fields"),
            Map.entry("ngành nghề công việc", "job_field_id"),
            Map.entry("lĩnh vực việc làm", "job_fields"),
            Map.entry("tên lĩnh vực", "job_field_name"),

            // jobs_post
            Map.entry("việc làm", "jobs_post"),
            Map.entry("công việc", "jobs_post"),
            Map.entry("tin tuyển dụng", "jobs_post"),
            Map.entry("job", "jobs_post"),
            Map.entry("bài đăng tuyển dụng", "jobs_post"),
            Map.entry("việc cần tuyển", "jobs_post"),
            Map.entry("tuyển người", "jobs_post"),
            Map.entry("job post", "jobs_post"),
            Map.entry("việc làm mới", "jobs_post"),
            Map.entry("đăng việc", "jobs_post"),
            Map.entry("vị trí tuyển dụng", "jobs_post"),
            Map.entry("tuyển dụng gấp", "jobs_post"),
            Map.entry("job tuyển", "jobs_post"),

            // job types and conditions
            Map.entry("full time", "job_type"),
            Map.entry("toàn thời gian", "job_type"),
            Map.entry("part time", "job_type"),
            Map.entry("bán thời gian", "job_type"),
            Map.entry("loại hình", "job_type"),
            Map.entry("công việc toàn thời gian", "job_type"),
            Map.entry("làm cả ngày", "job_type"),
            Map.entry("ca sáng", "job_type"),
            Map.entry("ca tối", "job_type"),
            Map.entry("theo giờ", "job_type"),
            Map.entry("việc làm thêm", "job_type"),

            // salary related
            Map.entry("lương", "job_salary"),
            Map.entry("mức lương", "job_salary"),
            Map.entry("thu nhập", "job_salary"),
            Map.entry("mức thu nhập", "job_salary"),
            Map.entry("tiền lương", "job_salary"),
            Map.entry("thu nhập hàng tháng", "job_salary"),

            // location related
            Map.entry("địa điểm", "job_location"),
            Map.entry("nơi làm việc", "job_location"),
            Map.entry("làm ở đâu", "job_location"),
            Map.entry("chỗ làm", "job_location"),
            Map.entry("địa chỉ làm việc", "job_location"),
            Map.entry("vị trí làm việc", "job_location"),

            // requirements
            Map.entry("yêu cầu công việc", "job_requirements"),
            Map.entry("yêu cầu", "job_requirements"),
            Map.entry("điều kiện ứng tuyển", "job_requirements"),
            Map.entry("kỹ năng cần thiết", "job_requirements"),
            Map.entry("đòi hỏi công việc", "job_requirements"),

            // notifications
            Map.entry("thông báo", "notifications"),
            Map.entry("tin nhắn hệ thống", "notifications"),
            Map.entry("cảnh báo", "notifications"),
            Map.entry("nhắc nhở", "notifications"),
            Map.entry("notification", "notifications"),

            // resource
            Map.entry("tài liệu", "resource"),
            Map.entry("tài nguyên", "resource"),
            Map.entry("file", "resource"),
            Map.entry("tệp đính kèm", "resource"),
            Map.entry("tài liệu tham khảo", "resource"),
            Map.entry("tài liệu học tập", "resource"),
            Map.entry("hướng dẫn", "resource"),
            Map.entry("file tài nguyên", "resource"),
            Map.entry("tên tài nguyên", "resource_title"),
            Map.entry("loại tài nguyên", "resource_type"),
            Map.entry("nội dung tài nguyên", "resource_content"),

            // student
            Map.entry("sinh viên", "student"),
            Map.entry("ứng viên", "student"),
            Map.entry("người học", "student"),
            Map.entry("người xin việc", "student"),
            Map.entry("người ứng tuyển", "student"),
            Map.entry("hồ sơ sinh viên", "student"),
            Map.entry("profile sinh viên", "student"),
            Map.entry("hồ sơ cá nhân", "student"),
            Map.entry("thông tin sinh viên", "student"),
            Map.entry("học sinh", "student"),
            Map.entry("profile ứng viên", "student"),
            Map.entry("địa chỉ sinh viên", "address"),
            Map.entry("trường đại học", "university"),
            Map.entry("mô tả bản thân", "profile_description"),

            // experience
            Map.entry("kinh nghiệm", "experience"),
            Map.entry("kinh nghiệm làm việc", "experience"),
            Map.entry("thâm niên", "experience"),
            Map.entry("số năm kinh nghiệm", "experience"),

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

            // Other business fields
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
            Map.entry("khách sạn", "hotel")
    );
    private static final Map<String, String> ENGLISH_MAPPING = Map.ofEntries(
            // account
            Map.entry("account", "account"),
            Map.entry("user", "account"),
            Map.entry("user information", "account"),
            Map.entry("login", "account"),
            Map.entry("register", "account"),
            Map.entry("username", "account"),
            Map.entry("user profile", "account"),

            // blog_post
            Map.entry("post", "blog_post"),
            Map.entry("news", "blog_post"),
            Map.entry("blog", "blog_post"),
            Map.entry("blog post", "blog_post"),
            Map.entry("article", "blog_post"),
            Map.entry("shared post", "blog_post"),
            Map.entry("blog content", "blog_post"),

            // blog_image
            Map.entry("blog image", "blog_image"),
            Map.entry("post image", "blog_image"),
            Map.entry("illustration", "blog_image"),
            Map.entry("attached image", "blog_image"),

            // employer
            Map.entry("employer", "employer"),
            Map.entry("company", "employer"),
            Map.entry("business", "employer"),
            Map.entry("organization", "employer"),
            Map.entry("recruiter", "employer"),
            Map.entry("recruiting unit", "employer"),

            // event
            Map.entry("event", "event"),
            Map.entry("program", "event"),
            Map.entry("activity", "event"),
            Map.entry("seminar", "event"),
            Map.entry("meeting", "event"),
            Map.entry("workshop", "event"),

            // event_form
            Map.entry("event registration", "event_form"),
            Map.entry("event form", "event_form"),
            Map.entry("registration form", "event_form"),
            Map.entry("event application", "event_form"),

            // forgot_password
            Map.entry("forgot password", "forgot_password"),
            Map.entry("reset password", "forgot_password"),
            Map.entry("recover password", "forgot_password"),
            Map.entry("change password", "forgot_password"),

            // interview
            Map.entry("interview", "interview"),
            Map.entry("interview schedule", "interview"),
            Map.entry("interview invitation", "interview"),
            Map.entry("interview meeting", "interview"),

            // job_application
            Map.entry("apply", "job_application"),
            Map.entry("application", "job_application"),
            Map.entry("job application", "job_application"),
            Map.entry("resume", "job_application"),
            Map.entry("cover letter", "job_application"),
            Map.entry("applied job", "job_post_id"),
            Map.entry("application status", "status"),

            // job_fields
            Map.entry("field", "job_fields"),
            Map.entry("industry", "job_fields"),
            Map.entry("job field", "job_fields"),
            Map.entry("field category", "job_fields"),
            Map.entry("field name", "job_field_name"),

            // jobs_post
            Map.entry("job", "jobs_post"),
            Map.entry("job post", "jobs_post"),
            Map.entry("job listing", "jobs_post"),
            Map.entry("vacancy", "jobs_post"),
            Map.entry("opening", "jobs_post"),
            Map.entry("full time", "job_type"),
            Map.entry("part time", "job_type"),
            Map.entry("salary", "job_salary"),
            Map.entry("location", "job_location"),
            Map.entry("job requirement", "job_requirements"),
            Map.entry("type", "job_type"),
            Map.entry("career field", "job_field_id"),

            // notifications
            Map.entry("notification", "notifications"),
            Map.entry("alert", "notifications"),
            Map.entry("reminder", "notifications"),
            Map.entry("system message", "notifications"),

            // resource
            Map.entry("document", "resource"),
            Map.entry("resource", "resource"),
            Map.entry("file", "resource"),
            Map.entry("attachment", "resource"),
            Map.entry("manual", "resource"),
            Map.entry("guide", "resource"),
            Map.entry("reference material", "resource"),
            Map.entry("resource name", "resource_title"),
            Map.entry("resource type", "resource_type"),
            Map.entry("resource content", "resource_content"),

            // student
            Map.entry("student", "student"),
            Map.entry("applicant", "student"),
            Map.entry("learner", "student"),
            Map.entry("candidate", "student"),
            Map.entry("student profile", "student"),
            Map.entry("personal profile", "student"),
            Map.entry("student info", "student"),
            Map.entry("student address", "address"),
            Map.entry("university", "university"),
            Map.entry("experience", "experience"),
            Map.entry("self description", "profile_description"),
            Map.entry("major", "job_field_id")
    );


    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty() || "${API_KEY}".equals(apiKey)) {
            throw new IllegalStateException("Google API key is not configured. Please set the openai.api.key property.");
        }
        
        // Validate API key format (basic validation)
        if (!isValidApiKeyFormat(apiKey)) {
            throw new IllegalStateException("Invalid API key format provided.");
        }
        
        this.apiKey = apiKey;
        
        // Configure OkHttpClient with proper timeouts to prevent resource leaks
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
            
        // Initialize rate limits from properties or use defaults
        if (maxRequestsPerMinute <= 0) maxRequestsPerMinute = DEFAULT_MAX_REQUESTS_PER_MINUTE;
        if (maxRequestsPerDay <= 0) maxRequestsPerDay = DEFAULT_MAX_REQUESTS_PER_DAY;
        if (targetMaxTokens <= 0) targetMaxTokens = DEFAULT_TARGET_MAX_TOKENS;
    }
    
    /**
     * Basic validation for API key format
     */
    private boolean isValidApiKeyFormat(String apiKey) {
        // Add basic format validation here
        return apiKey.length() > 10 && !apiKey.contains(" ");
    }

    private synchronized void updateRateLimits() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentMinuteReset = lastMinuteReset.get();
        LocalDateTime currentDayReset = lastDayReset.get();

        if (now.minusMinutes(1).isAfter(currentMinuteReset)) {
            logger.info("Resetting per-minute request counter. Previous count: {}", requestsPerMinute.get());
            requestsPerMinute.set(0);
            lastMinuteReset.set(now);
        }

        if (now.minusDays(1).isAfter(currentDayReset)) {
            logger.info("Resetting daily request counter. Previous count: {}", requestsPerDay.get());
            requestsPerDay.set(0);
            lastDayReset.set(now);
        }

        int currentMinute = requestsPerMinute.incrementAndGet();
        int currentDay = requestsPerDay.incrementAndGet();

        logger.info("Current rate limits - Requests this minute: {}/{}, Requests today: {}/{}",
                   currentMinute, maxRequestsPerMinute,
                   currentDay, maxRequestsPerDay);

        if (currentMinute > maxRequestsPerMinute * RATE_LIMIT_WARNING_THRESHOLD) {
            logger.warn("Approaching per-minute rate limit: {}/{}", currentMinute, maxRequestsPerMinute);
        }
        if (currentDay > maxRequestsPerDay * RATE_LIMIT_WARNING_THRESHOLD) {
            logger.warn("Approaching daily rate limit: {}/{}", currentDay, maxRequestsPerDay);
        }
        
        // Check if rate limits are exceeded
        if (currentMinute > maxRequestsPerMinute) {
            throw new RuntimeException("Per-minute rate limit exceeded: " + currentMinute + "/" + maxRequestsPerMinute);
        }
        if (currentDay > maxRequestsPerDay) {
            throw new RuntimeException("Daily rate limit exceeded: " + currentDay + "/" + maxRequestsPerDay);
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

    /**
     * Translates English keywords in user question to SQL field names
     * to help AI better understand database structure
     */
    private String translateEnglishKeywords(String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return userQuestion;
        }

        String translatedQuestion = userQuestion;
        Map<String, String> replacements = new HashMap<>();

        // Sort by length descending to match longer phrases first
        List<String> sortedKeys = new ArrayList<>(ENGLISH_MAPPING.keySet());
        sortedKeys.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String englishKey : sortedKeys) {
            String sqlValue = ENGLISH_MAPPING.get(englishKey);
            
            // Use word boundaries to avoid partial matches
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(englishKey) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(translatedQuestion);
            
            if (matcher.find()) {
                replacements.put(englishKey, sqlValue);
                translatedQuestion = matcher.replaceAll(sqlValue);
            }
        }

        if (!replacements.isEmpty()) {
            logger.info("Translated English keywords: {}", replacements);
            logger.info("Original question: {}", userQuestion);
            logger.info("Translated question: {}", translatedQuestion);
        }

        return translatedQuestion;
    }

    /**
     * Universal keyword translation based on detected language
     */
    private String translateKeywords(String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return userQuestion;
        }

        boolean isVietnamese = isVietnameseQuery(userQuestion);
        
        if (isVietnamese) {
            logger.debug("Detected Vietnamese query, applying Vietnamese keyword translation");
            return translateVietnameseKeywords(userQuestion);
        } else {
            logger.debug("Detected English query, applying English keyword translation");
            return translateEnglishKeywords(userQuestion);
        }
    }

    public String convertToSQL(String userQuestion, String schema) throws Exception {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            throw new IllegalArgumentException("User question cannot be null or empty");
        }

        updateRateLimits();

        // Dịch các từ khóa tiếng Việt trước khi xử lý
        String translatedQuestion = translateKeywords(userQuestion);

        // Tối ưu schema trước khi sử dụng
        String optimizedSchema = optimizeSchema(schema, translatedQuestion);

        String prompt = "Dựa vào schema database sau:\n" + optimizedSchema +
                       "\n\nHãy chuyển câu hỏi sau thành câu lệnh SQL (chỉ trả về câu lệnh SQL, không giải thích):\n" +
                       translatedQuestion;

        // Kiểm tra số token và cắt bớt nếu cần
        int estimatedTokens = estimateTokenCount(prompt);
        if (estimatedTokens > targetMaxTokens) {
            logger.warn("Token count exceeds target maximum. Truncating schema...");
            int excessTokens = estimatedTokens - targetMaxTokens;
            int charsToRemove = excessTokens * CHARS_PER_TOKEN;
            
            // Prevent IndexOutOfBoundsException
            int newLength = Math.max(0, optimizedSchema.length() - charsToRemove);
            if (newLength < optimizedSchema.length()) {
                optimizedSchema = optimizedSchema.substring(0, newLength);
                prompt = "Dựa vào schema database sau:\n" + optimizedSchema +
                        "\n\nHãy chuyển câu hỏi sau thành câu lệnh SQL (chỉ trả về câu lệnh SQL, không giải thích):\n" +
                        translatedQuestion;
                estimatedTokens = estimateTokenCount(prompt);
            }
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
            // Detect language first
            boolean isVietnamese = isVietnameseQuery(userQuestion);
            
            StringBuilder prompt = new StringBuilder();
            
            // Language-aware prompt generation
            if (isVietnamese) {
                prompt.append("Bạn là một AI assistant thân thiện đang giúp người dùng tìm hiểu thông tin về việc làm và ứng viên.\n");
                prompt.append("Người dùng đã hỏi: \"").append(userQuestion).append("\"\n");
                
                if (results != null && !results.isEmpty()) {
                    prompt.append("Tôi đã tìm được ").append(results.size()).append(" kết quả phù hợp.\n");
                    
                    // Hiển thị thông tin chi tiết từ kết quả để AI có thể phân tích
                    int sampleSize = Math.min(MAX_SAMPLE_RESULTS, results.size());
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
            } else {
                prompt.append("You are a friendly AI assistant helping users find information about jobs and candidates.\n");
                prompt.append("User asked: \"").append(userQuestion).append("\"\n");
                
                if (results != null && !results.isEmpty()) {
                    prompt.append("I found ").append(results.size()).append(" matching results.\n");
                    
                    // Show detailed information from results for AI analysis
                    int sampleSize = Math.min(MAX_SAMPLE_RESULTS, results.size());
                    prompt.append("Detailed information:\n");
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
                        prompt.append("... and ").append(results.size() - sampleSize).append(" other results\n");
                    }
                } else {
                    prompt.append("No data found matching the request.\n");
                }
                
                prompt.append("\nPlease write a response in English (2-4 sentences) as a job consulting expert:\n");
                prompt.append("1. Answer the user's question directly\n");
                prompt.append("2. Provide specific and useful information from the data\n");
                prompt.append("3. Give suggestions or advice if appropriate\n");
                prompt.append("4. Use a friendly, professional tone\n");
                prompt.append("Don't mention SQL or database.");
            }

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
        if (lowerQuestion.length() < MIN_QUESTION_LENGTH_FOR_ANALYSIS && !containsJobRelatedKeywords(lowerQuestion)) {
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
        boolean isVietnamese = isVietnameseQuery(question);
        
        // Greeting responses
        if (lowerQuestion.contains("hello") || lowerQuestion.contains("hi") || 
            lowerQuestion.contains("chào") || lowerQuestion.contains("xin chào")) {
            if (isVietnamese) {
                return "Xin chào! Tôi là AI Assistant của website tìm việc làm. Tôi có thể giúp bạn:\n" +
                       "• Tìm kiếm công việc phù hợp\n" +
                       "• Thông tin về mức lương và yêu cầu công việc\n" +
                       "• Thống kê về thị trường việc làm\n" +
                       "• Tư vấn nghề nghiệp\n\n" +
                       "Bạn có thể hỏi tôi bất cứ điều gì về việc làm!";
            } else {
                return "Hello! I'm the AI Assistant for the job search website. I can help you with:\n" +
                       "• Finding suitable jobs\n" +
                       "• Information about salaries and job requirements\n" +
                       "• Job market statistics\n" +
                       "• Career consulting\n\n" +
                       "Feel free to ask me anything about jobs!";
            }
        }
        
        // Website information
        if (lowerQuestion.contains("website") || lowerQuestion.contains("web") || 
            lowerQuestion.contains("trang web") || lowerQuestion.contains("đây là")) {
            if (isVietnamese) {
                return "Đây là website tìm việc làm SWP391 - một nền tảng kết nối ứng viên và nhà tuyển dụng. " +
                       "Website cung cấp:\n" +
                       "• Đăng tin tuyển dụng cho nhà tuyển dụng\n" +
                       "• Tìm kiếm và ứng tuyển việc làm cho ứng viên\n" +
                       "• Quản lý hồ sơ cá nhân\n" +
                       "• Tư vấn nghề nghiệp với AI\n" +
                       "• Thông tin về sự kiện tuyển dụng\n\n" +
                       "Bạn muốn tìm hiểu về lĩnh vực nào cụ thể?";
            } else {
                return "This is SWP391 job search website - a platform connecting candidates and employers. " +
                       "The website provides:\n" +
                       "• Job posting for employers\n" +
                       "• Job search and application for candidates\n" +
                       "• Personal profile management\n" +
                       "• AI career consulting\n" +
                       "• Recruitment event information\n\n" +
                       "What specific area would you like to know more about?";
            }
        }
        
        // About AI
        if (lowerQuestion.contains("bạn là ai") || lowerQuestion.contains("bạn là gì") || 
            lowerQuestion.contains("ai là bạn") || lowerQuestion.contains("you are") ||
            lowerQuestion.contains("who are you") || lowerQuestion.contains("what are you")) {
            if (isVietnamese) {
                return "Tôi là AI Assistant của website việc làm SWP391. Tôi được thiết kế để:\n" +
                       "• Hỗ trợ tìm kiếm thông tin về việc làm\n" +
                       "• Phân tích dữ liệu thị trường lao động\n" +
                       "• Tư vấn về cơ hội nghề nghiệp\n" +
                       "• Trả lời các câu hỏi về tuyển dụng\n\n" +
                       "Tôi có thể giúp bạn tìm hiểu về mức lương, yêu cầu công việc, và xu hướng tuyển dụng!";
            } else {
                return "I'm the AI Assistant for SWP391 job website. I'm designed to:\n" +
                       "• Help search for job information\n" +
                       "• Analyze job market data\n" +
                       "• Provide career opportunity advice\n" +
                       "• Answer recruitment questions\n\n" +
                       "I can help you learn about salaries, job requirements, and recruitment trends!";
            }
        }
        
        // Help and guidance
        if (lowerQuestion.contains("giúp") || lowerQuestion.contains("help") || 
            lowerQuestion.contains("hướng dẫn") || lowerQuestion.contains("làm sao")) {
            if (isVietnamese) {
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
            } else {
                return "I can help you with lots of job information:\n\n" +
                       "📊 **Job Statistics:**\n" +
                       "• \"How many IT jobs are there?\"\n" +
                       "• \"Which jobs have the highest salary?\"\n" +
                       "• \"What's the average programmer salary?\"\n\n" +
                       "🔍 **Specific Search:**\n" +
                       "• \"Find jobs in Hanoi\"\n" +
                       "• \"Part-time jobs for students\"\n" +
                       "• \"Which companies are hiring AI engineers?\"\n\n" +
                       "Feel free to ask me any question!";
            }
        }
        
        // Thanks and goodbye
        if (lowerQuestion.contains("cảm ơn") || lowerQuestion.contains("thank")) {
            if (isVietnamese) {
                return "Rất vui được giúp đỡ bạn! Nếu có thêm câu hỏi về việc làm, đừng ngần ngại hỏi tôi nhé. " +
                       "Chúc bạn tìm được công việc ưng ý! 😊";
            } else {
                return "Happy to help you! If you have more questions about jobs, don't hesitate to ask me. " +
                       "Good luck finding your ideal job! 😊";
            }
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
        if (estimatedTokens > targetMaxTokens) {
            logger.warn("Candidate search token count exceeds limit. Truncating...");
            int excessTokens = estimatedTokens - targetMaxTokens;
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
        logger.info("Language detection for query: '{}'", query);
        
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
            " the ", " and ", " with ", " for ", " from ", " that ", " this ", " are ", " is ", " in ",
            "experience", "developer", "engineer", "manager", "years", "months",
            "job", "jobs", "work", "working", "position", "positions", "career", "careers",
            "highest", "best", "top", "good", "great", "excellent", "professional",
            "paying", "salary", "income", "compensation", "benefits", "company", "companies",
            "skills", "requirements", "qualifications", "candidates", "applicants",
            "software", "technology", "business", "marketing", "sales", "finance"
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
        
        // Decision logic - Enhanced with better English detection
        // Strong English indicators
        if (englishScore >= 2 && commonVietnameseCount == 0) {
            return false; // Strong English signals
        }
        
        // Clear English with minimal Vietnamese patterns
        if (englishScore > 0 && commonVietnameseCount == 0 && vietnameseCharScore < 2) {
            return false; // Likely English
        }
        
        // Strong Vietnamese indicators  
        if (commonVietnameseCount > 0 || vietnameseCharScore >= 3) {
            return true; // Likely Vietnamese
        }
        
        // 6. Additional English checks for job-related queries
        String[] jobEnglishKeywords = {
            "find", "search", "looking", "need", "want", "seeking", "hiring", "recruitment",
            "apply", "application", "resume", "cv", "interview", "qualification"
        };
        
        int jobEnglishScore = 0;
        for (String keyword : jobEnglishKeywords) {
            if (lowerQuery.contains(keyword)) {
                jobEnglishScore++;
            }
        }
        
        if (jobEnglishScore > 0 && commonVietnameseCount == 0) {
            return false; // Job-related English query
        }
        
        // 7. Word structure analysis
        String[] words = lowerQuery.split("\\s+");
        int shortWordsCount = 0;
        int longWordsCount = 0;
        
        for (String word : words) {
            if (word.length() <= 4 && !word.matches("\\d+")) {
                shortWordsCount++;
            } else if (word.length() > 6) {
                longWordsCount++;
            }
        }
        
        // If many long words and few short words, likely English
        if (words.length > 0 && longWordsCount > shortWordsCount) {
            return false;
        }
        
        // If most words are short, lean towards Vietnamese
        if (words.length > 0 && (double) shortWordsCount / words.length > 0.6) {
            return true;
        }
        
        // Final fallback: if no strong indicators either way, check for pure ASCII
        boolean hasPureEnglishChars = lowerQuery.matches("[a-z0-9\\s.,!?-]+");
        if (hasPureEnglishChars && englishScore == 0 && commonVietnameseCount == 0 && vietnameseCharScore <= 1) {
            return false; // Pure English characters with no Vietnamese indicators
        }
        
        // Default to Vietnamese for ambiguous cases
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

    /**
     * Enhanced keyword translation specifically for candidate search
     * Supports both Vietnamese and English with domain-specific patterns
     */
    private String translateCandidateKeywords(String userQuestion) {
        // First apply universal translation
        String translatedQuestion = translateKeywords(userQuestion);
        
        boolean isVietnamese = isVietnameseQuery(userQuestion);
        
        if (isVietnamese) {
            // Enhanced Vietnamese patterns for candidate search
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
                
                // Education patterns
                .replaceAll("(?i)\\bhọc\\s*tại\\s*(.+?)\\s*(,|$|\\s+và)", "studied at $1")
                .replaceAll("(?i)\\btrường\\s*(.+?)\\s*(,|$|\\s+và)", "university $1");
        } else {
            // Enhanced English patterns for candidate search
            translatedQuestion = translatedQuestion
                // Experience patterns
                .replaceAll("(?i)\\b(\\d+)\\s*years?\\s*experience\\b", "$1 years experience")
                .replaceAll("(?i)\\b(\\d+)\\s*months?\\s*experience\\b", "$1 months experience")
                .replaceAll("(?i)\\bexperience\\s*of\\s*(\\d+)\\s*years?\\b", "$1 years experience")
                .replaceAll("(?i)\\bexperience\\s*of\\s*(\\d+)\\s*months?\\b", "$1 months experience")
                .replaceAll("(?i)\\bwith\\s*(\\d+)\\s*years?\\s*experience\\b", "$1 years experience")
                .replaceAll("(?i)\\bwith\\s*(\\d+)\\s*months?\\s*experience\\b", "$1 months experience")
                
                // Experience in specific field
                .replaceAll("(?i)\\bexperience\\s*in\\s*(\\w+)", "experience in $1")
                .replaceAll("(?i)\\bexperienced\\s*in\\s*(\\w+)", "experience in $1")
                .replaceAll("(?i)\\bskilled\\s*in\\s*(\\w+)", "experience in $1")
                
                // Location patterns - normalize city names
                .replaceAll("(?i)\\bin\\s*ho\\s*chi\\s*minh\\b", "in Ho Chi Minh")
                .replaceAll("(?i)\\bin\\s*hcm\\b", "in Ho Chi Minh")
                .replaceAll("(?i)\\bin\\s*saigon\\b", "in Ho Chi Minh")
                .replaceAll("(?i)\\bin\\s*hanoi\\b", "in Hanoi")
                .replaceAll("(?i)\\bin\\s*ha\\s*noi\\b", "in Hanoi")
                .replaceAll("(?i)\\bin\\s*da\\s*nang\\b", "in Da Nang")
                .replaceAll("(?i)\\bin\\s*danang\\b", "in Da Nang")
                .replaceAll("(?i)\\bfrom\\s*(ho\\s*chi\\s*minh|hcm|saigon)\\b", "in Ho Chi Minh")
                .replaceAll("(?i)\\bfrom\\s*(hanoi|ha\\s*noi)\\b", "in Hanoi")
                .replaceAll("(?i)\\bfrom\\s*(da\\s*nang|danang)\\b", "in Da Nang")
                
                // Search patterns
                .replaceAll("(?i)\\bfind\\s*(candidates?|people)\\b", "find candidates")
                .replaceAll("(?i)\\blooking\\s*for\\s*(candidates?|people)\\b", "find candidates")
                .replaceAll("(?i)\\bsearch\\s*(for\\s*)?(candidates?|people)\\b", "find candidates")
                .replaceAll("(?i)\\bneed\\s*(candidates?|people)\\b", "find candidates")
                
                // Level patterns
                .replaceAll("(?i)\\bfresh\\s*grad(uate)?s?\\b", "fresh graduate")
                .replaceAll("(?i)\\bjunior\\s*(developer|engineer|programmer)s?\\b", "junior $1")
                .replaceAll("(?i)\\bsenior\\s*(developer|engineer|programmer)s?\\b", "senior $1")
                .replaceAll("(?i)\\bintern(ship)?s?\\b", "internship")
                
                // Education patterns
                .replaceAll("(?i)\\bgraduated?\\s*from\\s*(.+?)\\s*(,|$|\\s+and)", "studied at $1")
                .replaceAll("(?i)\\bstudied?\\s*at\\s*(.+?)\\s*(,|$|\\s+and)", "studied at $1")
                .replaceAll("(?i)\\buniversity\\s*of\\s*(.+?)\\s*(,|$|\\s+and)", "university $1");
        }
         
        return translatedQuestion;
    }
}