-- Chat Tables for Real-time Messaging System
USE swp391;

-- Create chat_room table
CREATE TABLE IF NOT EXISTS chat_room (
    chat_room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    employer_id INT NOT NULL,
    job_post_id INT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_message_at DATETIME NULL,
    last_message VARCHAR(500) NULL,
    is_active BOOLEAN DEFAULT TRUE,
    student_unread_count INT DEFAULT 0,
    employer_unread_count INT DEFAULT 0,
    
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (employer_id) REFERENCES employer(employer_id) ON DELETE CASCADE,
    FOREIGN KEY (job_post_id) REFERENCES jobs_post(job_post_id) ON DELETE SET NULL,
    
    -- Unique constraint để tránh duplicate chat rooms
    UNIQUE KEY unique_chat_room (student_id, employer_id, job_post_id),
    
    INDEX idx_student_id (student_id),
    INDEX idx_employer_id (employer_id),
    INDEX idx_job_post_id (job_post_id),
    INDEX idx_last_message_at (last_message_at)
);

-- Create chat_message table
CREATE TABLE IF NOT EXISTS chat_message (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_type ENUM('STUDENT', 'EMPLOYER') NOT NULL,
    message_content TEXT NOT NULL,
    message_type ENUM('TEXT', 'FILE', 'IMAGE') DEFAULT 'TEXT',
    file_url VARCHAR(500) NULL,
    file_name VARCHAR(255) NULL,
    file_size BIGINT NULL,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    read_at DATETIME NULL,
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at DATETIME NULL,
    
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(chat_room_id) ON DELETE CASCADE,
    
    INDEX idx_chat_room_id (chat_room_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_sent_at (sent_at),
    INDEX idx_is_read (is_read),
    INDEX idx_room_sent_at (chat_room_id, sent_at)
);

-- Sample chat data để test
INSERT INTO chat_room (student_id, employer_id, job_post_id, created_at, last_message_at, last_message) VALUES
(1, 1, 1, '2025-01-10 10:00:00', '2025-01-10 10:30:00', 'Xin chào, tôi rất quan tâm đến vị trí intern này!'),
(2, 1, 1, '2025-01-10 11:00:00', '2025-01-10 11:15:00', 'Chào anh, em muốn hỏi về thời gian thực tập ạ'),
(1, 7, 12, '2025-01-10 14:00:00', '2025-01-10 14:45:00', 'Xin chào, tôi đã apply vào vị trí Full Stack Developer');

INSERT INTO chat_message (chat_room_id, sender_id, sender_type, message_content, sent_at) VALUES
-- Chat room 1 (Student 1 with ACORP)
(1, 27, 'STUDENT', 'Xin chào anh/chị! Em rất quan tâm đến vị trí Marketing Intern tại ACORP.', '2025-01-10 10:00:00'),
(1, 1, 'EMPLOYER', 'Chào em! Cảm ơn em đã quan tâm. Bên mình đang tuyển 3 vị trí Marketing Intern. Em có kinh nghiệm gì chưa?', '2025-01-10 10:05:00'),
(1, 27, 'STUDENT', 'Em đã có 3 tháng làm intern marketing ở một startup nhỏ, chủ yếu support social media content.', '2025-01-10 10:10:00'),
(1, 1, 'EMPLOYER', 'Tốt đấy! Vị trí của bên mình cũng sẽ support content và SEO. Em có thể bắt đầu từ khi nào?', '2025-01-10 10:15:00'),
(1, 27, 'STUDENT', 'Em có thể bắt đầu ngay ạ. Lịch học của em khá linh hoạt.', '2025-01-10 10:30:00'),

-- Chat room 2 (Student 2 with ACORP)  
(2, 28, 'STUDENT', 'Chào anh/chị, em muốn hỏi về vị trí HR Intern ạ.', '2025-01-10 11:00:00'),
(2, 1, 'EMPLOYER', 'Chào em! Vị trí HR Intern sẽ support recruitment và training. Em có background gì về HR không?', '2025-01-10 11:05:00'),
(2, 28, 'STUDENT', 'Em học Business Administration và đã làm part-time sales 6 tháng, có tiếp xúc với customer service.', '2025-01-10 11:10:00'),
(2, 1, 'EMPLOYER', 'Kinh nghiệm customer service rất tốt cho HR. Thời gian thực tập dự kiến 2 tháng, em có OK không?', '2025-01-10 11:15:00'),

-- Chat room 3 (Student 1 with FPT)
(3, 27, 'STUDENT', 'Xin chào! Tôi đã apply vào vị trí Full Stack Developer tại FPT Software.', '2025-01-10 14:00:00'),
(3, 7, 'EMPLOYER', 'Chào bạn! FPT đang tìm Full Stack với kinh nghiệm JavaScript và React. Bạn có project nào để demo không?', '2025-01-10 14:15:00'),
(3, 27, 'STUDENT', 'Tôi có một vài project trên GitHub sử dụng React và Node.js. Có thể schedule một cuộc gọi để discuss không ạ?', '2025-01-10 14:30:00'),
(3, 7, 'EMPLOYER', 'Tất nhiên! Mình sẽ arrange technical interview trong tuần tới. Bạn available vào thời gian nào?', '2025-01-10 14:45:00');

-- Update unread counts
UPDATE chat_room SET 
    employer_unread_count = 1,
    student_unread_count = 0 
WHERE chat_room_id = 1;

UPDATE chat_room SET 
    employer_unread_count = 1,
    student_unread_count = 0 
WHERE chat_room_id = 2;

UPDATE chat_room SET 
    employer_unread_count = 1,
    student_unread_count = 0 
WHERE chat_room_id = 3; 