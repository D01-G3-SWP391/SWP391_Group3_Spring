-- Thêm tin nhắn vào chat room vừa tạo
USE swp391;

-- Lấy chat_room_id của room vừa tạo
SELECT chat_room_id FROM chat_room WHERE student_id = 6 AND employer_id = 1 AND job_post_id = 31;

-- Thêm tin nhắn (thay 6 bằng chat_room_id thực tế từ query trên)
INSERT INTO chat_message (chat_room_id, sender_id, sender_type, message_content, sent_at) VALUES
(6, 33, 'STUDENT', 'Xin chào! Tôi rất quan tâm đến vị trí này.', NOW()),
(6, 33, 'EMPLOYER', 'Chào bạn! Cảm ơn bạn đã apply. Chúng ta có thể trao đổi thêm.', NOW()),
(6, 33, 'STUDENT', 'Vâng ạ, em có thể bắt đầu làm việc ngay. Em có kinh nghiệm về marketing digital.', NOW()),
(6, 33, 'EMPLOYER', 'Tuyệt vời! Chúng ta sẽ schedule một cuộc phỏng vấn trong tuần tới.', NOW());

-- Cập nhật last_message cho chat room
UPDATE chat_room 
SET last_message = 'Tuyệt vời! Chúng ta sẽ schedule một cuộc phỏng vấn trong tuần tới.',
    last_message_at = NOW(),
    employer_unread_count = 0,
    student_unread_count = 2
WHERE student_id = 6 AND employer_id = 1 AND job_post_id = 31; 