-- BƯỚC 1: Kiểm tra student_id và employer_id thực tế
USE swp391;

-- Xem student có ID = 6
SELECT * FROM student WHERE student_id = 6;

-- Xem employer có account_id = 33 (user đang login)
SELECT * FROM employer WHERE account_id = 33;

-- BƯỚC 2: Tạo chat room từng bước
-- Đầu tiên xóa chat room cũ nếu có
DELETE FROM chat_room WHERE student_id = 6 AND employer_id = 1;

-- Tạo chat room mới với IDs đúng
INSERT INTO chat_room (student_id, employer_id, job_post_id, created_at, last_message_at, last_message, student_unread_count, employer_unread_count) 
VALUES (6, 1, 31, NOW(), NOW(), 'Chat room được tạo mới', 0, 0);

-- BƯỚC 3: Lấy chat_room_id vừa tạo
SELECT chat_room_id FROM chat_room WHERE student_id = 6 AND employer_id = 1 LIMIT 1;

-- BƯỚC 4: Thêm tin nhắn với chat_room_id cụ thể (thay X bằng ID từ bước 3)
-- INSERT INTO chat_message (chat_room_id, sender_id, sender_type, message_content, sent_at) VALUES
-- (X, 33, 'STUDENT', 'Xin chào! Tôi rất quan tâm đến vị trí này.', NOW()),
-- (X, 33, 'EMPLOYER', 'Chào bạn! Cảm ơn bạn đã apply. Chúng ta có thể trao đổi thêm.', NOW()); 