-- ===================================================================
-- 💬 SAMPLE CHAT DATA 
-- Chỉ insert dữ liệu mẫu sau khi Hibernate đã tạo bảng
-- ===================================================================

USE swp391;

-- ===================================================================
-- INSERT SAMPLE CHAT ROOMS
-- ===================================================================

-- Kiểm tra xem bảng chat_room đã tồn tại chưa
-- Nếu chưa có thì Hibernate sẽ tạo khi start application

INSERT IGNORE INTO chat_room 
(student_id, employer_id, job_post_id, created_at, last_message, last_message_at, student_unread_count, employer_unread_count, is_active) 
VALUES
-- Student 1 chat với các Employers
(1, 1, 1, '2024-01-15 10:00:00', 'Chào anh, em muốn hỏi về vị trí Marketing Intern', '2024-01-15 10:00:00', 0, 1, TRUE),
(1, 2, 2, '2024-01-16 14:30:00', 'Em có thể sắp xếp phỏng vấn vào tuần sau được không?', '2024-01-16 14:30:00', 1, 0, TRUE),

-- Student 2 chat với các Employers  
(2, 3, 3, '2024-01-17 09:15:00', 'Khi nào anh có thể phỏng vấn được ạ?', '2024-01-17 09:15:00', 0, 1, TRUE),
(2, 4, 4, '2024-01-18 16:45:00', 'Em có kinh nghiệm về Java Spring Boot rồi ạ', '2024-01-18 16:45:00', 1, 0, TRUE),

-- Student 3 chat với Employer
(3, 5, 5, '2024-01-19 11:20:00', 'Lương khởi điểm cho vị trí này là bao nhiêu ạ?', '2024-01-19 11:20:00', 0, 1, TRUE);

-- ===================================================================
-- INSERT SAMPLE MESSAGES
-- ===================================================================

INSERT IGNORE INTO chat_message 
(chat_room_id, sender_id, sender_type, message_type, message_content, sent_at, is_read) 
VALUES
-- Chat room 1: Student 1 & Employer 1
(1, 1, 'STUDENT', 'TEXT', 'Chào anh, em muốn hỏi về vị trí Marketing Intern tại công ty', '2024-01-15 10:00:00', TRUE),
(1, 1, 'EMPLOYER', 'TEXT', 'Chào em! Cảm ơn em đã quan tâm đến vị trí này. Em có câu hỏi gì không?', '2024-01-15 10:05:00', TRUE),
(1, 1, 'STUDENT', 'TEXT', 'Em muốn biết thêm về công việc cụ thể và thời gian thử việc ạ', '2024-01-15 10:10:00', TRUE),
(1, 1, 'EMPLOYER', 'TEXT', 'Vị trí này sẽ support team marketing trong việc tạo content và phân tích campaign. Thời gian thử việc là 2 tháng.', '2024-01-15 10:15:00', FALSE),

-- Chat room 2: Student 1 & Employer 2
(2, 2, 'EMPLOYER', 'TEXT', 'Chào em, chúng tôi đã xem CV của em và rất ấn tượng', '2024-01-16 14:30:00', TRUE),
(2, 1, 'STUDENT', 'TEXT', 'Cảm ơn anh ạ! Em rất mong được làm việc tại công ty', '2024-01-16 14:35:00', TRUE),
(2, 2, 'EMPLOYER', 'TEXT', 'Em có thể sắp xếp phỏng vấn vào tuần sau được không?', '2024-01-16 14:40:00', FALSE),

-- Chat room 3: Student 2 & Employer 3
(3, 2, 'STUDENT', 'TEXT', 'Chào anh, em đã nộp đơn ứng tuyển vị trí English Teaching Assistant', '2024-01-17 09:15:00', TRUE),
(3, 3, 'EMPLOYER', 'TEXT', 'Chào em! Em có bằng IELTS hoặc TOEIC không?', '2024-01-17 09:20:00', TRUE),
(3, 2, 'STUDENT', 'TEXT', 'Em có IELTS 7.0 ạ, và đã từng dạy kèm tại nhà', '2024-01-17 09:25:00', FALSE),

-- Chat room 4: Student 2 & Employer 4
(4, 4, 'EMPLOYER', 'TEXT', 'Chào em, tôi thấy em có background về IT, em có kinh nghiệm về Java Spring Boot không?', '2024-01-18 16:45:00', TRUE),
(4, 2, 'STUDENT', 'TEXT', 'Chào anh, em có học qua Spring Boot và đã làm 1 số project nhỏ ạ', '2024-01-18 16:50:00', FALSE),

-- Chat room 5: Student 3 & Employer 5
(5, 3, 'STUDENT', 'TEXT', 'Chào anh, em muốn hỏi về mức lương cho vị trí General Laborer', '2024-01-19 11:20:00', TRUE),
(5, 5, 'EMPLOYER', 'TEXT', 'Chào em, mức lương là 7-8 triệu/tháng tùy theo kinh nghiệm', '2024-01-19 11:25:00', FALSE);

-- ===================================================================
-- HIỂN THỊ KẾT QUẢ
-- ===================================================================

SELECT 'Chat rooms created:' AS status;
SELECT COUNT(*) AS total_chat_rooms FROM chat_room;

SELECT 'Chat messages created:' AS status;  
SELECT COUNT(*) AS total_chat_messages FROM chat_message;

SELECT 'Sample data insertion completed!' AS result; 