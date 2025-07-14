-- Student Favorite Jobs Table - New name to avoid conflict
USE swp391;

-- Create student_favorite_jobs table (tên mới để tránh conflict)
CREATE TABLE IF NOT EXISTS student_favorite_jobs (
    favorite_job_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    job_post_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (student_id) REFERENCES account(user_id) ON DELETE CASCADE,
    FOREIGN KEY (job_post_id) REFERENCES jobs_post(job_post_id) ON DELETE CASCADE,
    
    -- Unique constraint để tránh duplicate favorite jobs
    UNIQUE KEY unique_student_favorite_job (student_id, job_post_id),
    
    -- Indexes để tăng performance
    INDEX idx_student_id (student_id),
    INDEX idx_job_post_id (job_post_id),
    INDEX idx_created_at (created_at)
);

-- Comment để giải thích bảng
ALTER TABLE student_favorite_jobs COMMENT = 'Bảng lưu trữ thông tin công việc yêu thích của sinh viên'; 