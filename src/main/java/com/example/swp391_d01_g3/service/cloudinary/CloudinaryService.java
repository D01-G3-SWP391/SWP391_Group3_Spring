package com.example.swp391_d01_g3.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload image to Cloudinary
     * @param file MultipartFile to upload
     * @param folder Folder in Cloudinary to store the image
     * @return URL of uploaded image
     * @throws IOException if upload fails
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
//        System.out.println("🔥 CloudinaryService.uploadImage() called");
//        System.out.println("📁 File: " + file.getOriginalFilename());
//        System.out.println("📊 Size: " + file.getSize() + " bytes");
//        System.out.println("📂 Folder: " + folder);
        
        // Validate file type
        String contentType = file.getContentType();
//        System.out.println("🎯 Content Type: " + contentType);
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là ảnh (JPG, PNG, GIF)");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB");
        }

        // Generate unique public ID
        String publicId = folder + "/" + UUID.randomUUID().toString();
        System.out.println("🆔 Public ID: " + publicId);

        try {
            System.out.println("☁️ Uploading to Cloudinary...");
            // Upload to Cloudinary - simplified without transformations
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "resource_type", "image"
                )
            );

            // Return secure URL
            String secureUrl = (String) uploadResult.get("secure_url");
            System.out.println("✅ Upload success! URL: " + secureUrl);
            return secureUrl;

        } catch (Exception e) {
//            System.out.println("❌ Upload failed: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Delete image from Cloudinary
     * @param publicId Public ID of image to delete
     * @throws IOException if deletion fails
     */
    public void deleteImage(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new IOException("Lỗi khi xóa ảnh từ Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     * @param cloudinaryUrl Full Cloudinary URL
     * @return Public ID
     */
    public String extractPublicId(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            return null;
        }

        try {
            // Extract public ID from URL like: https://res.cloudinary.com/cloudname/image/upload/v1234567890/folder/publicId.jpg
            String[] parts = cloudinaryUrl.split("/");
            if (parts.length >= 2) {
                String filename = parts[parts.length - 1];
                String folder = parts[parts.length - 2];
                
                // Remove file extension
                String publicIdWithoutExtension = filename.contains(".") 
                    ? filename.substring(0, filename.lastIndexOf("."))
                    : filename;
                
                return folder + "/" + publicIdWithoutExtension;
            }
        } catch (Exception e) {
            // If extraction fails, return null
        }
        
        return null;
    }

    /**
     * Upload any file (PDF, DOCX, etc.) to Cloudinary as raw resource
     * @param file MultipartFile to upload
     * @param folder Folder in Cloudinary to store the file
     * @return URL of uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 10MB");
        }
        String contentType = file.getContentType();
        String publicId = folder + "/" + UUID.randomUUID().toString();
        Map<String, Object> options = new java.util.HashMap<>();
        options.put("public_id", publicId);
        options.put("folder", folder);
        if (contentType != null && (contentType.equals("image/png") || contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/gif"))) {
            options.put("resource_type", "image");
        } else {
            options.put("resource_type", "raw");
        }
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new IOException("Lỗi khi upload file lên Cloudinary: " + e.getMessage(), e);
        }
    }
} 