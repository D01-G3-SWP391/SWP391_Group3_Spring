package com.example.swp391_d01_g3.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/uploads")
public class FileController {

    @GetMapping("/test")
    @ResponseBody
    public String testFileAccess() {
        try {
            String workingDir = System.getProperty("user.dir");
            Path cvDir = Paths.get(workingDir, "uploads", "cv");
            File dir = cvDir.toFile();
            
            StringBuilder result = new StringBuilder();
            result.append("Working Directory: ").append(workingDir).append("<br>");
            result.append("CV Directory: ").append(cvDir.toAbsolutePath()).append("<br>");
            result.append("Directory exists: ").append(dir.exists()).append("<br>");
            result.append("Directory readable: ").append(dir.canRead()).append("<br>");
            
            if (dir.exists() && dir.isDirectory()) {
                result.append("Files in directory:<br>");
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        result.append("- ").append(file.getName())
                              .append(" (").append(file.length()).append(" bytes)")
                              .append(" readable: ").append(file.canRead())
                              .append("<br>");
                    }
                } else {
                    result.append("No files found<br>");
                }
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/cv/{filename:.+}")
    public ResponseEntity<Resource> downloadCVFile(@PathVariable String filename) {
        try {
            System.out.println("🔍 Attempting to serve file: " + filename);
            
            // Sử dụng absolute path từ working directory
            String workingDir = System.getProperty("user.dir");
            Path filePath = Paths.get(workingDir, "uploads", "cv", filename).normalize();
            System.out.println("📁 Full file path: " + filePath.toAbsolutePath());
            System.out.println("📁 Working directory: " + workingDir);
            
            File file = filePath.toFile();
            System.out.println("📊 File exists: " + file.exists());
            System.out.println("📖 File can read: " + file.canRead());
            System.out.println("📏 File size: " + file.length());
            
            if (!file.exists()) {
                System.out.println("❌ File not found");
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            System.out.println("🔗 Resource URI: " + resource.getURI());

            if (resource.exists() && resource.isReadable()) {
                // Xác định content type dựa trên file extension
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    String lowerFilename = filename.toLowerCase();
                    if (lowerFilename.endsWith(".pdf")) {
                        contentType = "application/pdf";
                    } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                        contentType = "image/jpeg";
                    } else if (lowerFilename.endsWith(".png")) {
                        contentType = "image/png";
                    } else if (lowerFilename.endsWith(".gif")) {
                        contentType = "image/gif";
                    } else {
                        contentType = "application/octet-stream";
                    }
                }
                
                System.out.println("🎯 Content type: " + contentType);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                System.out.println("❌ Resource not readable");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("💥 Error serving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
} 