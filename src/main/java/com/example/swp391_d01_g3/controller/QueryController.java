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
            
            // Lấy schema
            String schema = schemaService.getAllDatabaseSchema();
            
            // Chuyển đổi câu hỏi thành SQL
            String sql = openAIService.convertToSQL(question, schema);
            // Loại bỏ markdown nếu có
            sql = sql.replaceAll("(?s)```sql|```", "").trim();
            
            // Thực thi SQL
            List<Object[]> results = queryService.executeQuery(sql);
            
            return ResponseEntity.ok(Map.of(
                "sql", sql,
                "results", results
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
} 