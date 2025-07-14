-- =================================================================
-- SCRIPT AN TOÀN XÓA FIELD ACCOUNT_ID (không dùng IF EXISTS)
-- =================================================================

USE SWP391;

-- Bước 1: Kiểm tra cấu trúc bảng hiện tại
SHOW COLUMNS FROM favorite_jobs;

-- Bước 2: Kiểm tra xem column account_id có tồn tại không
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'SWP391' 
  AND TABLE_NAME = 'favorite_jobs' 
  AND COLUMN_NAME = 'account_id';

-- Bước 3: Nếu kết quả trên có trả về dữ liệu, thì chạy lệnh này:
-- ALTER TABLE favorite_jobs DROP COLUMN account_id;

-- Bước 4: Kiểm tra lại cấu trúc sau khi xóa
-- SHOW COLUMNS FROM favorite_jobs; 