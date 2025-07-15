-- =======================================================
-- FIX FAVORITE JOBS TABLE - Remove account_id field
-- =======================================================

-- Kiểm tra cấu trúc bảng hiện tại
DESCRIBE favorite_jobs;

-- Xóa field account_id nếu tồn tại
ALTER TABLE favorite_jobs DROP COLUMN IF EXISTS account_id;

-- Kiểm tra lại cấu trúc sau khi fix
DESCRIBE favorite_jobs;

-- Đảm bảo structure đúng như mong muốn
-- Bảng chỉ nên có: favorite_id, student_id, job_post_id, created_at 