-- Add chat room for current student (34) and employer  
USE swp391;

-- Kiểm tra student_id 34 tồn tại không
SELECT student_id, account_id FROM student WHERE student_id = 34;

-- Kiểm tra employer hiện tại (từ log: user 33)  
SELECT employer_id, account_id FROM employer WHERE account_id = 33;

-- Thêm chat room mới
INSERT INTO chat_room (student_id, employer_id, job_post_id, created_at, last_message_at, last_message) 
VALUES (34, 1, 31, NOW(), NOW(), 'Chat room được tạo mới');

-- Thêm một vài tin nhắn mẫu
INSERT INTO chat_message (chat_room_id, sender_id, sender_type, message_content, sent_at) VALUES
((SELECT LAST_INSERT_ID()), 34, 'STUDENT', 'Xin chào! Tôi rất quan tâm đến vị trí này.', NOW()),
((SELECT chat_room_id FROM chat_room WHERE student_id = 34 AND employer_id = 1 LIMIT 1), 33, 'EMPLOYER', 'Chào bạn! Cảm ơn bạn đã apply. Chúng ta có thể trao đổi thêm.', NOW()); 