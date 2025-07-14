package com.example.swp391_d01_g3.controller;

import com.example.swp391_d01_g3.service.ai.OpenAIService;
import com.example.swp391_d01_g3.service.database.DatabaseSchemaService;
import com.example.swp391_d01_g3.service.database.DynamicQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = "*")
public class QueryController {
    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private DatabaseSchemaService schemaService;
    
    @Autowired
    private DynamicQueryService queryService;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            
            // Validate input
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Question is required",
                    "response", "Vui lòng nhập câu hỏi của bạn."
                ));
            }
            
            // First try to process as natural language (handles general questions)
            String response = openAIService.processNaturalLanguageQuery(question);
            
            // If it's not a general question, try database query
            if (response.contains("Đây là một câu hỏi về cơ sở dữ liệu")) {
                try {
                    // Lấy schema
                    String schema = schemaService.getAllDatabaseSchema();
                    
                    // Chuyển đổi câu hỏi thành SQL
                    String sql = openAIService.convertToSQL(question, schema);
                    // Loại bỏ markdown nếu có
                    sql = sql.replaceAll("(?s)```sql|```", "").trim();
                    
                    // Thực thi SQL
                    List<Object[]> results = queryService.executeQuery(sql);
                    
                    // Tạo phản hồi tự nhiên
                    String naturalResponse = openAIService.generateNaturalResponse(question, sql, results);
                    
                    return ResponseEntity.ok(Map.of(
                        "sql", sql,
                        "results", results,
                        "response", naturalResponse
                    ));
                } catch (Exception dbException) {
                    // If database query fails, provide helpful response
                    return ResponseEntity.ok(Map.of(
                        "response", "Tôi hiểu bạn đang hỏi về dữ liệu việc làm, nhưng hiện tại không thể truy vấn được. " +
                                   "Bạn có thể thử hỏi lại với cách diễn đạt khác như:\n" +
                                   "• 'Hiển thị công việc mới nhất'\n" +
                                   "• 'Tìm việc làm lương cao'\n" +
                                   "• 'Công việc IT ở Hà Nội'"
                    ));
                }
            }
            
            // Return the natural language response for general questions
            return ResponseEntity.ok(Map.of("response", response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "response", "Xin lỗi, tôi không thể hiểu câu hỏi của bạn. Hãy thử hỏi một cách khác."
            ));
        }
    }

    /**
     * 🎯 New endpoint specifically for AI-powered candidate search
     * Separate from general chat AI to avoid conflicts
     */
    @PostMapping("/search-candidates")
    public ResponseEntity<?> searchCandidates(@RequestBody Map<String, String> request) {
        String searchQuery = null;
        try {
            searchQuery = request.get("query");
            System.out.println("🔍 AI Search Request: " + searchQuery);

            // Validate input
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Search query is required",
                        "message", "Vui lòng nhập từ khóa tìm kiếm ứng viên."
                ));
            }

            // Get database schema
            String schema = schemaService.getAllDatabaseSchema();
            System.out.println("📊 Schema length: " + schema.length() + " characters");

            // Use AI to convert natural language to SQL for candidate search
            String sql = openAIService.searchCandidatesWithAI(searchQuery, schema);
            System.out.println("🤖 Generated SQL: " + sql);

            // Clean SQL
            sql = sql.replaceAll("(?s)```sql|```", "").trim();
            System.out.println("✨ Cleaned SQL: " + sql);

            // Execute SQL query
            List<Object[]> results = queryService.executeQuery(sql);
            System.out.println("📋 Query results count: " + results.size());

            // Generate appropriate response message based on query language
            String responseMessage = openAIService.generateCandidateSearchResponse(searchQuery, results);
            System.out.println("💬 Response message: " + responseMessage);

            // Return results in format expected by frontend
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "results", results,
                    "totalResults", results.size(),
                    "message", responseMessage
            ));

        } catch (Exception e) {
            System.out.println("❌ Error in candidate search: " + e.getMessage());
            e.printStackTrace();
            
            // Generate error message based on query language
            String errorMessage = openAIService.generateCandidateSearchErrorMessage(searchQuery);

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "message", errorMessage
            ));
        }
    }
} 