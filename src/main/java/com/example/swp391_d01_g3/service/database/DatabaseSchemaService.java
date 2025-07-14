package com.example.swp391_d01_g3.service.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseSchemaService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaService.class);
    
    @Autowired
    private DataSource dataSource;

    @Cacheable("databaseSchema")
    public String getAllDatabaseSchema() {
        StringBuilder schema = new StringBuilder();
        List<String> tableDefinitions = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String currentDB = conn.getCatalog();
            // Accept both local (SWP391) and Railway (railway) database names
            if (currentDB == null || 
                (!currentDB.equalsIgnoreCase("SWP391") && !currentDB.equalsIgnoreCase("railway"))) {
                logger.error("Not connected to SWP391 or Railway database. Current database: {}", currentDB);
                throw new RuntimeException("Must be connected to SWP391 or Railway database");
            }
            
            logger.info("Getting schema for database: {}", currentDB);
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Kiểm tra xem có phải MySQL không
            String dbProductName = metaData.getDatabaseProductName();
            if (!dbProductName.toLowerCase().contains("mysql")) {
                logger.error("Unsupported database type: {}. Only MySQL is supported.", dbProductName);
                throw new RuntimeException("Only MySQL database is supported");
            }
            
            // Lấy danh sách bảng
            try (ResultSet tables = metaData.getTables(currentDB, null, "%", new String[] {"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    StringBuilder tableDefinition = new StringBuilder();
                    
                    tableDefinition.append("CREATE TABLE ").append(tableName).append(" (\n");
                    
                    // Lấy các cột
                    try (ResultSet columns = metaData.getColumns(currentDB, null, tableName, "%")) {
                        boolean firstColumn = true;
                        while (columns.next()) {
                            if (!firstColumn) {
                                tableDefinition.append(",\n");
                            }
                            
                            String columnName = columns.getString("COLUMN_NAME");
                            String columnType = columns.getString("TYPE_NAME");
                            int columnSize = columns.getInt("COLUMN_SIZE");
                            int decimalDigits = columns.getInt("DECIMAL_DIGITS");
                            boolean isNullable = columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                            String defaultValue = columns.getString("COLUMN_DEF");
                            
                            tableDefinition.append("    ").append(columnName).append(" ")
                                         .append(columnType);
                            
                            // Thêm độ dài và độ chính xác cho các kiểu dữ liệu
                            if (columnType.equalsIgnoreCase("VARCHAR") || 
                                columnType.equalsIgnoreCase("CHAR")) {
                                tableDefinition.append("(").append(columnSize).append(")");
                            } else if (columnType.equalsIgnoreCase("DECIMAL") || 
                                     columnType.equalsIgnoreCase("NUMERIC")) {
                                tableDefinition.append("(").append(columnSize).append(",")
                                             .append(decimalDigits).append(")");
                            }
                            
                            // Thêm các ràng buộc
                            if (!isNullable) {
                                tableDefinition.append(" NOT NULL");
                            }
                            
                            if (defaultValue != null && !defaultValue.isEmpty()) {
                                tableDefinition.append(" DEFAULT ").append(defaultValue);
                            }
                            
                            firstColumn = false;
                        }
                    }
                    
                    // Lấy khóa chính
                    try (ResultSet pks = metaData.getPrimaryKeys(currentDB, null, tableName)) {
                        StringBuilder primaryKeys = new StringBuilder();
                        boolean firstPK = true;
                        
                        while (pks.next()) {
                            if (firstPK) {
                                tableDefinition.append(",\n    PRIMARY KEY (");
                                firstPK = false;
                            } else {
                                primaryKeys.append(", ");
                            }
                            primaryKeys.append(pks.getString("COLUMN_NAME"));
                        }
                        
                        if (!firstPK) {
                            tableDefinition.append(primaryKeys).append(")");
                        }
                    }
                    
                    // Lấy khóa ngoại
                    try (ResultSet fks = metaData.getImportedKeys(currentDB, null, tableName)) {
                        while (fks.next()) {
                            tableDefinition.append(",\n    FOREIGN KEY (")
                                         .append(fks.getString("FKCOLUMN_NAME"))
                                         .append(") REFERENCES ")
                                         .append(fks.getString("PKTABLE_NAME"))
                                         .append("(")
                                         .append(fks.getString("PKCOLUMN_NAME"))
                                         .append(")");
                            
                            String updateRule = fks.getString("UPDATE_RULE");
                            String deleteRule = fks.getString("DELETE_RULE");
                            
                            if (!"RESTRICT".equals(updateRule)) {
                                tableDefinition.append(" ON UPDATE ").append(updateRule);
                            }
                            if (!"RESTRICT".equals(deleteRule)) {
                                tableDefinition.append(" ON DELETE ").append(deleteRule);
                            }
                        }
                    }
                    
                    tableDefinition.append("\n);\n\n");
                    tableDefinitions.add(tableDefinition.toString());
                }
            }
            
            // Sắp xếp các bảng để đảm bảo các bảng có khóa ngoại được tạo sau bảng chính
            sortTableDefinitions(tableDefinitions);
            
            // Ghép tất cả định nghĩa bảng
            for (String definition : tableDefinitions) {
                schema.append(definition);
            }
            
            logger.info("Successfully generated schema for {} tables", tableDefinitions.size());
            
        } catch (SQLException e) {
            logger.error("Error getting database schema", e);
            throw new RuntimeException("Error getting database schema: " + e.getMessage(), e);
        }
        
        return schema.toString();
    }
    
    private void sortTableDefinitions(List<String> tableDefinitions) {
        // Sắp xếp các bảng để đảm bảo thứ tự tạo đúng
        tableDefinitions.sort((def1, def2) -> {
            boolean def1HasForeignKey = def1.contains("FOREIGN KEY");
            boolean def2HasForeignKey = def2.contains("FOREIGN KEY");
            
            if (def1HasForeignKey && !def2HasForeignKey) {
                return 1;
            } else if (!def1HasForeignKey && def2HasForeignKey) {
                return -1;
            }
            return 0;
        });
    }
} 