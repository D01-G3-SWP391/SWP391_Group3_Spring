-- ==========================================
-- 💖 FAVORITE JOBS TABLE CREATION SCRIPT
-- ==========================================

-- Tạo bảng favorite_jobs để lưu trữ thông tin favorite jobs của student
CREATE TABLE IF NOT EXISTS favorite_jobs (
    favorite_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    job_post_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint để tránh duplicate
    UNIQUE KEY unique_student_job (student_id, job_post_id),
    
    -- Foreign key constraints (optional - có thể bỏ comment nếu muốn enforce)
    -- FOREIGN KEY (student_id) REFERENCES Account(userId) ON DELETE CASCADE,
    -- FOREIGN KEY (job_post_id) REFERENCES Jobs_post(job_post_id) ON DELETE CASCADE,
    
    -- Indexes để tối ưu performance
    INDEX idx_student_id (student_id),
    INDEX idx_job_post_id (job_post_id),
    INDEX idx_created_at (created_at)
);

-- ==========================================
-- SAMPLE DATA (for testing)
-- ==========================================

-- Thêm một số data mẫu để test (có thể comment lại nếu không cần)
-- INSERT INTO favorite_jobs (student_id, job_post_id) VALUES
-- (1, 1),  -- Student 1 favorite job 1
-- (1, 2),  -- Student 1 favorite job 2  
-- (2, 1),  -- Student 2 favorite job 1
-- (2, 3);  -- Student 2 favorite job 3

-- ==========================================
-- VERIFICATION QUERIES
-- ==========================================

-- Kiểm tra table đã tạo thành công
SHOW CREATE TABLE favorite_jobs;

-- Kiểm tra indexes
SHOW INDEXES FROM favorite_jobs;

-- Đếm số record (should be 0 initially)
SELECT COUNT(*) as total_favorites FROM favorite_jobs; 