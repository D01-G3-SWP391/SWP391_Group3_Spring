-- ===============================================================
-- SAMPLE DATA FOR EVENT TABLE - UPDATED FOR CORRECT DATABASE
-- Dựa trên dữ liệu Account, Employer, Student đã có sẵn
-- Account ID: Admin = 26, Employer = 1-25, Student = 27-31  
-- Employer ID: 1-25, Student ID: 1-5
-- ===============================================================

-- Insert Event data (15 records)
INSERT INTO Event (
    event_title, 
    event_description, 
    event_date, 
    event_location, 
    registration_deadline, 
    max_participants, 
    current_participants, 
    contact_email, 
    event_status, 
    approval_status, 
    employer_id, 
    approved_by, 
    approved_at
) VALUES 

-- 1. VNG Corporation - Game Development Workshop
(
    'Workshop Game Development với Unity', 
    'Khóa học thực hành về phát triển game mobile sử dụng Unity Engine. Học viên sẽ được hướng dẫn từ cơ bản đến nâng cao, tạo ra một game demo hoàn chỉnh. Phù hợp cho sinh viên CNTT muốn tìm hiểu ngành game.', 
    '2025-06-15 09:00:00', 
    'VNG Campus, 222 Pasteur, Quận 3, TP.HCM', 
    '2025-06-10 23:59:59', 
    30, 
    4, 
    'vng@email.com', 
    'ACTIVE', 
    'APPROVED', 
    6, -- VNG Corporation employer_id
    26, -- Admin account_id  
    '2025-05-20 14:30:00'
),

-- 2. FPT Software - Career Fair
(
    'FPT Software Career Fair 2025', 
    'Ngày hội tuyển dụng quy mô lớn của FPT Software với hơn 100 vị trí đang tuyển dụng từ Fresher đến Senior. Cơ hội phỏng vấn trực tiếp với HR và kỹ thuật viên senior, tặng quà lưu niệm và voucher khóa học.', 
    '2025-06-20 08:00:00', 
    'FPT Complex, Cầu Giấy, Hà Nội', 
    '2025-06-18 17:00:00', 
    200, 
    8, 
    'fpt@email.com', 
    'ACTIVE', 
    'APPROVED', 
    7, -- FPT Software employer_id
    26, -- Admin account_id
    '2025-05-18 09:15:00'
),

-- 3. Techcombank - Fintech Innovation Seminar
(
    'Hội thảo Đổi mới Công nghệ Tài chính', 
    'Hội thảo chuyên sâu về xu hướng Fintech, Digital Banking và cơ hội nghề nghiệp trong lĩnh vực tài chính ngân hàng. Diễn giả là các chuyên gia hàng đầu từ Techcombank và đối tác quốc tế.', 
    '2025-07-05 14:00:00', 
    'Techcombank Tower, 191 Bà Triệu, Hà Nội', 
    '2025-07-02 12:00:00', 
    100, 
    6, 
    'techcombank@email.com', 
    'ACTIVE', 
    'APPROVED', 
    9, -- Techcombank employer_id
    26, -- Admin account_id
    '2025-05-25 11:20:00'
),

-- 4. Shopee - E-commerce Marketing Workshop
(
    'Workshop Marketing Thương mại Điện tử', 
    'Khóa học 2 ngày về Marketing trong lĩnh vực E-commerce, bao gồm: SEO, SEM, Social Media Marketing, Data Analytics. Có chứng chỉ hoàn thành và cơ hội thực tập tại Shopee Vietnam.', 
    '2025-06-25 09:00:00', 
    'Shopee Office, Lầu 25-26 Toà nhà Viettel, 285 Cách Mạng Tháng 8, TP.HCM', 
    '2025-06-22 23:59:59', 
    50, 
    3, 
    'shopee@email.com', 
    'ACTIVE', 
    'APPROVED', 
    10, -- Shopee employer_id
    26, -- Admin account_id
    '2025-05-22 16:45:00'
),

-- 5. Grab - Tech Talk on AI and Machine Learning
(
    'Tech Talk: AI & Machine Learning trong Transportation', 
    'Buổi chia sẻ kỹ thuật về ứng dụng AI và ML trong ngành vận tải thông minh. Tìm hiểu về thuật toán định tuyến, dự đoán nhu cầu và tối ưu hóa trải nghiệm người dùng tại Grab.', 
    '2025-07-10 19:00:00', 
    'Grab Vietnam Office, Lầu 1-5 toà nhà Flemington, 182 Lê Đại Hành, TP.HCM', 
    '2025-07-08 18:00:00', 
    80, 
    2, 
    'grab@email.com', 
    'ACTIVE', 
    'APPROVED', 
    14, -- Grab employer_id
    26, -- Admin account_id
    '2025-05-28 13:10:00'
),

-- 6. ACORP - Investment and Business Strategy Seminar
(
    'Hội thảo Chiến lược Đầu tư và Kinh doanh', 
    'Buổi seminar về xu hướng đầu tư BĐS, phân tích thị trường và cơ hội nghề nghiệp trong lĩnh vực đầu tư. Diễn giả là các chuyên gia đầu tư hàng đầu từ ACORP.', 
    '2025-09-01 14:30:00', 
    'ACORP Office, 35 Thái Phiên, Phước Ninh, Hải Châu, Đà Nẵng', 
    '2025-08-29 17:00:00', 
    60, 
    1, 
    'acorp@email.com', 
    'ACTIVE', 
    'APPROVED', 
    1, -- ACORP employer_id
    26, -- Admin account_id
    '2025-06-15 10:20:00'
),

-- 7. Viettel - 5G Technology Summit
(
    'Hội nghị Công nghệ 5G và Tương lai', 
    'Sự kiện công nghệ lớn về mạng 5G, IoT và chuyển đổi số. Cơ hội networking với các chuyên gia công nghệ hàng đầu và tìm hiểu về cơ hội việc làm trong lĩnh vực viễn thông.', 
    '2025-07-15 08:30:00', 
    'Viettel Complex, 285 Cách Mạng Tháng 8, Quận 10, TP.HCM', 
    '2025-07-12 17:00:00', 
    150, 
    12, 
    'viettel@email.com', 
    'ACTIVE', 
    'APPROVED', 
    13, -- Viettel employer_id
    26, -- Admin account_id
    '2025-05-30 10:25:00'
),

-- 8. VinHomes - Real Estate Investment Workshop
(
    'Workshop Đầu tư Bất động sản 2025', 
    'Khóa học về xu hướng thị trường BĐS, phân tích đầu tư và cơ hội nghề nghiệp trong lĩnh vực bất động sản. Đặc biệt có phần thực hành phân tích dự án thực tế của VinHomes.', 
    '2025-08-01 09:00:00', 
    'VinHomes Gallery, 198 Pasteur, Quận 3, TP.HCM', 
    '2025-07-29 23:59:59', 
    40, 
    0, 
    'vinhomes@email.com', 
    'ACTIVE', 
    'APPROVED', 
    8, -- VinHomes employer_id
    26, -- Admin account_id
    '2025-06-01 14:40:00'
),

-- 9. English Center - Teaching Skills Training
(
    'Khóa đào tạo Kỹ năng Giảng dạy Tiếng Anh', 
    'Chương trình đào tạo dành cho sinh viên sắp tốt nghiệp ngành Anh văn. Nội dung bao gồm: phương pháp giảng dạy hiện đại, quản lý lớp học, thiết kế bài giảng và đánh giá học sinh.', 
    '2025-08-05 14:00:00', 
    'English Center, 123 Nguyễn Huệ, Quận 1, TP.HCM', 
    '2025-08-03 12:00:00', 
    25, 
    2, 
    'englishcenter@email.com', 
    'ACTIVE', 
    'APPROVED', 
    20, -- English Center employer_id
    26, -- Admin account_id
    '2025-06-05 09:30:00'
),

-- 10. Tiki - Product Management Workshop
(
    'Workshop Quản lý Sản phẩm trong E-commerce', 
    'Khóa học thực hành về Product Management: từ ý tưởng đến thực thi, phân tích user research, thiết kế UX/UI và đo lường hiệu quả sản phẩm. Có certificate từ Tiki.', 
    '2025-09-05 09:30:00', 
    'Tiki Office, Tầng 8 Toà nhà Rivera Park, 7/28 Thành Thái, TP.HCM', 
    '2025-09-02 23:59:59', 
    45, 
    1, 
    'tiki@email.com', 
    'ACTIVE', 
    'APPROVED', 
    11, -- Tiki employer_id
    26, -- Admin account_id
    '2025-06-20 16:30:00'
),

-- Events đang chờ duyệt (PENDING)
-- 11. Lazada - Digital Transformation Conference
(
    'Hội nghị Chuyển đổi Số trong Thương mại', 
    'Sự kiện lớn về chuyển đổi số, e-commerce và logistics thông minh. Cơ hội học hỏi từ các chuyên gia hàng đầu và network với các startup, doanh nghiệp công nghệ.', 
    '2025-08-20 09:00:00', 
    'Lazada Office, Tầng 17-20 Toà nhà Vincom Center, 72 Lê Thánh Tôn, TP.HCM', 
    '2025-08-18 23:59:59', 
    120, 
    0, 
    'lazada@email.com', 
    'ACTIVE', 
    'PENDING', 
    12, -- Lazada employer_id
    NULL, 
    NULL
),

-- 12. Interior Corp - Design Thinking Workshop
(
    'Workshop Tư duy Thiết kế Nội thất', 
    'Khóa học về Design Thinking trong thiết kế nội thất, sử dụng các phần mềm 3D hiện đại và xu hướng thiết kế 2025. Có thực hành với project thực tế.', 
    '2025-09-10 08:00:00', 
    'Interior Corp Showroom, 456 Nguyễn Trãi, Hà Nội', 
    '2025-09-07 18:00:00', 
    30, 
    0, 
    'interiorcorp@email.com', 
    'ACTIVE', 
    'PENDING', 
    23, -- Interior Corp employer_id
    NULL, 
    NULL
),

-- 13. PharmaCorp - Healthcare Innovation Summit
(
    'Hội nghị Đổi mới trong Ngành Dược phẩm', 
    'Sự kiện về những xu hướng mới trong ngành dược: digital health, personalized medicine và cơ hội nghề nghiệp. Có phần networking với các chuyên gia y tế.', 
    '2025-09-15 13:00:00', 
    'Khách sạn Rex, 141 Nguyễn Huệ, Quận 1, TP.HCM', 
    '2025-09-12 12:00:00', 
    80, 
    0, 
    'pharmacorp@email.com', 
    'ACTIVE', 
    'PENDING', 
    18, -- PharmaCorp employer_id
    NULL, 
    NULL
),

-- Events đã bị từ chối (REJECTED)
-- 14. DatVietVAC - Media Streaming Workshop (Rejected - Location not confirmed)
(
    'Workshop Streaming Media và OTT Platform', 
    'Khóa học về công nghệ streaming, phát triển OTT platform và cơ hội trong ngành media-tech. Tìm hiểu về CDN, video encoding và user experience.', 
    '2025-10-15 10:00:00', 
    'TBA - To be announced', 
    '2025-10-12 23:59:59', 
    40, 
    0, 
    'datvietvac@email.com', 
    'ACTIVE', 
    'REJECTED', 
    3, -- DatVietVAC employer_id
    26, -- Admin account_id
    '2025-07-05 14:20:00'
),

-- 15. The Fan Representative - Restaurant Management Course (Rejected - Content not relevant)
(
    'Khóa học Quản lý Nhà hàng Chuyên nghiệp', 
    'Đào tạo về quản lý F&B, customer service, menu planning và cost control trong ngành nhà hàng. Phù hợp cho sinh viên ngành Du lịch - Khách sạn.', 
    '2025-10-20 13:30:00', 
    'The Fan Steak House, 123 Nguyễn Văn Cừ, TP.HCM', 
    '2025-10-17 20:00:00', 
    25, 
    0, 
    'thefan@email.com', 
    'ACTIVE', 
    'REJECTED', 
    2, -- The Fan employer_id
    26, -- Admin account_id
    '2025-07-08 10:45:00'
);

-- ===============================================================
-- SAMPLE DATA FOR EVENT_FORM TABLE (Student Registrations)
-- Student ID: 1-5 tương ứng với Account ID: 27-31
-- ===============================================================

INSERT INTO Event_form (event_id, student_id, notes) VALUES 

-- Student 1 (Nguyen Van An - Marketing student) registrations
(1, 1, 'Mình đang học năm 3 ngành Marketing, rất quan tâm đến game development với Unity để hiểu về marketing trong gaming industry.'),
(2, 1, 'Mong muốn tìm hiểu cơ hội việc làm tại FPT Software, đặc biệt là vị trí Marketing trong công ty công nghệ.'),
(4, 1, 'Quan tâm đến Workshop Marketing E-commerce của Shopee để nâng cao kỹ năng digital marketing.'),
(6, 1, 'Muốn tham gia seminar ACORP để học hỏi về chiến lược đầu tư và marketing BĐS.'),

-- Student 2 (Tran Thi Binh - Business Administration) registrations  
(2, 2, 'Mình học Business Administration, muốn tìm hiểu về career path trong công ty công nghệ như FPT.'),
(3, 2, 'Có background về business, muốn tìm hiểu về Fintech và cơ hội trong ngành tài chính.'),
(4, 2, 'Quan tâm đến Marketing trong E-commerce, hy vọng có cơ hội thực tập tại Shopee.'),

-- Student 3 (Le Hoang Nam - English Language) registrations
(9, 3, 'Sắp tốt nghiệp ngành Anh văn, muốn học kỹ năng giảng dạy chuyên nghiệp tại English Center.'),
(2, 3, 'Muốn tìm hiểu về cơ hội việc làm trong môi trường quốc tế tại FPT Software.'),

-- Student 4 (Pham Thi Cuc - Chemistry) registrations
(7, 4, 'Sinh viên ngành Hóa học quan tâm đến công nghệ 5G và IoT, muốn chuyển sang lĩnh vực công nghệ.'),
(13, 4, 'Muốn tìm hiểu về ngành dược phẩm và cơ hội ứng dụng kiến thức hóa học vào healthcare.'),

-- Student 5 (Hoang Van Dung - English Language) registrations
(9, 5, 'Sinh viên năm cuối ngành Anh văn, muốn tham gia khóa đào tạo kỹ năng giảng dạy.'),
(10, 5, 'Quan tâm đến Product Management, muốn chuyển sang làm trong tech sau khi tốt nghiệp.');

-- ===============================================================
-- QUERIES TO VERIFY DATA
-- ===============================================================

-- Display Event statistics
SELECT 
    'Event Statistics' as Info,
    COUNT(*) as Total_Events,
    SUM(CASE WHEN approval_status = 'APPROVED' THEN 1 ELSE 0 END) as Approved_Events,
    SUM(CASE WHEN approval_status = 'PENDING' THEN 1 ELSE 0 END) as Pending_Events,
    SUM(CASE WHEN approval_status = 'REJECTED' THEN 1 ELSE 0 END) as Rejected_Events,
    SUM(current_participants) as Total_Participants
FROM Event;

-- Display Registration statistics
SELECT 
    'Registration Statistics' as Info,
    COUNT(*) as Total_Registrations,
    COUNT(DISTINCT student_id) as Unique_Students,
    COUNT(DISTINCT event_id) as Events_With_Registrations
FROM Event_form;

-- Display Events by Company
SELECT 
    e.employer_id,
    emp.company_name,
    COUNT(*) as Total_Events,
    SUM(e.current_participants) as Total_Participants
FROM Event e
JOIN Employer emp ON e.employer_id = emp.employer_id
GROUP BY e.employer_id, emp.company_name
ORDER BY Total_Events DESC; 