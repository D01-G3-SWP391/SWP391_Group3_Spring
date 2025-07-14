-- =================================================================
-- SCRIPT XÓA FIELD ACCOUNT_ID KHỎI BẢNG FAVORITE_JOBS
-- =================================================================

-- Sử dụng database
USE SWP391;

-- Kiểm tra cấu trúc bảng hiện tại
SHOW COLUMNS FROM favorite_jobs;

-- Xóa field account_id nếu tồn tại
ALTER TABLE favorite_jobs DROP COLUMN IF EXISTS account_id;

-- Kiểm tra lại cấu trúc sau khi xóa
SHOW COLUMNS FROM favorite_jobs;

-- Hiển thị thông tin bảng sau khi fix
DESCRIBE favorite_jobs;

-- =================================================================
-- KẾT QUẢ MONG MUỐN: Bảng favorite_jobs chỉ nên có 4 fields:
-- - favorite_id (INT, AUTO_INCREMENT, PRIMARY KEY)
-- - student_id (INT, NOT NULL)
-- - job_post_id (INT, NOT NULL)  
-- - created_at (DATETIME, DEFAULT CURRENT_TIMESTAMP)
-- =================================================================

-- Kiểm tra dữ liệu hiện có (nếu có)
SELECT COUNT(*) as total_records FROM favorite_jobs;

-- Hiển thị vài record mẫu (nếu có dữ liệu)
SELECT * FROM favorite_jobs LIMIT 5; 