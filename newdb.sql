use swp391;
-- Insert into Job_fields (10 records)
INSERT INTO Job_fields (job_field_name) VALUES
('IT'),
('Real Estate'),
('Manufacturing'),
('Marketing'),
('Education'),
('Banking'),
('Logistics'),
('Interior Design'),
('Pharmaceuticals'),
('Automotive');

-- Insert into User (30 records: 25 employers + 5 candidates)
INSERT INTO Account (full_name, email, password, phone, role, avatar_url, status) VALUES
('ACORP Representative', 'acorp@email.com', 'hashed_password6', '0975079095', 'employer', 'https://static.ybox.vn/2021/8/0/1628391947898-Thi%E1%BA%BFt%20k%E1%BA%BF%20kh%C3%B4ng%20t%C3%AAn%20-%202021-08-08T100539.319.png', 'active'),
('The Fan Representative', 'thefan@email.com', 'hashed_password7', '5551112222', 'employer', 'https://aleagues.com.au/wp-content/uploads/sites/17/2024/06/FRG_Header_1250x625.png?w=1200', 'active'),
('DatVietVAC Representative', 'datvietvac@email.com', 'hashed_password8', '5553334444', 'employer', 'https://agency.brvn.vn/u/datvietvaclogo_1424838404.jpg', 'active'),
('Persol Representative', 'persol@email.com', 'hashed_password9', '5555556666', 'employer', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQzqLG4q6t6Qa_KajGSUcAqJjKtoQd7vFxE9Q&s', 'active'),
('Parker Processing Representative', 'parker@email.com', 'hashed_password10', '5557778888', 'employer', 'https://cdn1.vieclam24h.vn/upload/files_cua_nguoi_dung/logo/2019/12/06/5787829_vieclam24h_1575598276.png', 'active'),
('VNG Representative', 'vng@email.com', 'hashed_password11', '5559990000', 'employer', 'https://jikeragency374b0.zapwp.com/q:i/r:0/wp:1/w:480/u:https://mondialbrand.com/wp-content/uploads/2024/02/vng_corporation-logo_brandlogos.net_ysr15.png', 'active'),
('FPT Representative', 'fpt@email.com', 'hashed_password12', '5551113333', 'employer', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/FPT_logo_2010.svg/1200px-FPT_logo_2010.svg.png', 'active'),
('VinHomes Representative', 'vinhomes@email.com', 'hashed_password13', '5552224444', 'employer', 'https://batdongsan.kiengiang.vn/wp-content/uploads/2023/03/logo-vinhomes-1024x621.png', 'active'),
('Techcombank Representative', 'techcombank@email.com', 'hashed_password14', '5553335555', 'employer', 'https://inuvdp.com/wp-content/uploads/2022/08/file-vector-logo-techcombank-02.jpg', 'active'),
('Shopee Representative', 'shopee@email.com', 'hashed_password15', '5554446666', 'employer', 'https://upload.wikimedia.org/wikipedia/commons/b/b5/Shopee-logo.jpg', 'active'),
('Tiki Representative', 'tiki@email.com', 'hashed_password16', '5556667777', 'employer', 'https://maisonoffice.vn/wp-content/uploads/2023/10/tiki-la-gi.jpg', 'active'),
('Lazada Representative', 'lazada@email.com', 'hashed_password17', '5558889999', 'employer', 'https://th.bing.com/th/id/OIP.HuysdVDIfO18ubBv2sV9qgHaCQ?w=347&h=106&c=7&r=0&o=7&cb=iwp2&dpr=1.3&pid=1.7&rm=3', 'active'),
('Viettel Representative', 'viettel@email.com', 'hashed_password18', '5550001111', 'employer', 'https://static.topcv.vn/company_logos/bwFOxNGcRWOFQaO3IJJzAQB9hVd4CJtk_1644919697____999e51f17e0268f3fcceaebc71a966c3.jpg', 'active'),
('Grab Representative', 'grab@email.com', 'hashed_password19', '5552223333', 'employer', 'https://images.glints.com/unsafe/glints-dashboard.oss-ap-southeast-1.aliyuncs.com/company-logo/02d7cd6d157f30c1236ec356b9f2e360.png', 'active'),
('Sunhouse Representative', 'sunhouse@email.com', 'hashed_password20', '5554445555', 'employer', 'https://th.bing.com/th/id/OIP.nf9NhiMOjZ0llKM8Rkm-cgHaD-?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('TechBuilding Representative', 'techbuilding@email.com', 'hashed_password21', '5556668888', 'employer', 'https://th.bing.com/th/id/OIP.jzFMQz9q2LmPdE8uqUpIuwHaDt?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('MaintenanceCorp Representative', 'maintenance@email.com', 'hashed_password22', '5557779999', 'employer', 'https://th.bing.com/th/id/OIP.OMU3xhg3e7UPHkJ2aE6tGwHaFO?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('PharmaCorp Representative', 'pharmacorp@email.com', 'hashed_password23', '5558880000', 'employer', 'https://th.bing.com/th/id/OIP.5nw7E0-k7VLfoJpxNLP7EwHaF4?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('LogisticsVN Representative', 'logisticsvn@email.com', 'hashed_password24', '5559991111', 'employer', 'https://th.bing.com/th/id/R.9f0445a043903e1738d8db980e8cb86f?rik=4NByAkZF16zMFg&pid=ImgRaw&r=0', 'active'),
('EnglishCenter Representative', 'englishcenter@email.com', 'hashed_password25', '5550002222', 'employer', 'https://th.bing.com/th/id/OIP.yvNAWakjoWrZod4iEe5QqwAAAA?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('WaterFilter Representative', 'waterfilter@email.com', 'hashed_password26', '5551114444', 'employer', 'https://th.bing.com/th/id/OIP.ZLShbtcFb2FCGMtz3AyTYAHaD0?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('FuelAdditive Representative', 'fueladditive@email.com', 'hashed_password27', '5552225555', 'employer', 'https://i.ytimg.com/vi/_nr-n4Dyv3Y/maxresdefault.jpg', 'active'),
('InteriorCorp Representative', 'interiorcorp@email.com', 'hashed_password28', '5553336666', 'employer', 'https://th.bing.com/th/id/OIP.FuvmYMVortSMSmyX3JATnwHaEK?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('RealEstateVN Representative', 'realestatevn@email.com', 'hashed_password29', '5554447777', 'employer', 'https://th.bing.com/th/id/OIP.As117lLTrcyzKDpPAxmlxAHaHa?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('BankVN Representative', 'bankvn@email.com', 'hashed_password30', '5555558888', 'employer', 'https://th.bing.com/th/id/OIP.71yIH0XXG4B-BP7eeH_IwQHaFj?cb=iwp2&rs=1&pid=ImgDetMain', 'active'),
('Admin','admin@gmail.com','admin','0905777999','admin','https://t3.ftcdn.net/jpg/00/65/75/68/360_F_65756860_GUZwzOKNMUU3HldFoIA44qss7ZIrCG8I.jpg','active'),
('Nguyen Van An', 'nguyenvanan@gmail.com', 'hashed_password1', '0905123456', 'student', 'https://example.com/avatar1.jpg', 'active'),
('Tran Thi Binh', 'tranbinh123@gmail.com', 'hashed_password2', '0916234567', 'student', 'https://example.com/avatar2.jpg', 'active'),
('Le Hoang Nam', 'lehoangnam@gmail.com', 'hashed_password3', '0927345678', 'student', 'https://example.com/avatar3.jpg', 'active'),
('Pham Thi Cuc', 'phamthicuc@gmail.com', 'hashed_password4', '0938456789', 'student', 'https://example.com/avatar4.jpg', 'active'),
('Hoang Van Dung', 'hoangvandung@gmail.com', 'hashed_password5', '0949567890', 'student', 'https://example.com/avatar5.jpg', 'active');
-- Insert into Employer (25 records)
INSERT INTO Employer (jobs_field_id, user_id, company_name, company_address, logo_url, company_description) VALUES
(2, 1, 'Công ty Cổ phần Đầu Tư ACORP', '35 Thái Phiên, Phước Ninh, Hải Châu, Đà Nẵng', 'https://static.ybox.vn/2021/8/0/1628391947898-Thi%E1%BA%BFt%20k%E1%BA%BF%20kh%C3%B4ng%20t%C3%AAn%20-%202021-08-08T100539.319.png', 'Investment company focusing on real estate and business services.'),
(4, 2, 'The Fan Representative - Steak House', 'TP.HCM', 'https://aleagues.com.au/wp-content/uploads/sites/17/2024/06/FRG_Header_1250x625.png?w=1200', 'Premium steak house offering high-quality dining experiences.'),
(1, 3, 'DatVietVAC', 'TP.HCM', 'https://agency.brvn.vn/u/datvietvaclogo_1424838404.jpg', 'Technology company specializing in media and streaming platforms.'),
(1, 4, 'Persol Career Tech Studio Vietnam', 'TP.HCM', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQzqLG4q6t6Qa_KajGSUcAqJjKtoQd7vFxE9Q&s', 'Tech studio focusing on software development and innovation.'),
(3, 5, 'Công Ty TNHH Parker Processing Việt Nam', 'Hà Nội', 'https://cdn1.vieclam24h.vn/upload/files_cua_nguoi_dung/logo/2019/12/06/5787829_vieclam24h_1575598276.png', 'Manufacturing company specializing in industrial processing.'),
(1, 6, 'VNG Corporation', 'TP.HCM', 'https://jikeragency374b0.zapwp.com/q:i/r:0/wp:1/w:480/u:https://mondialbrand.com/wp-content/uploads/2024/02/vng_corporation-logo_brandlogos.net_ysr15.png', 'Leading technology company in gaming and digital services.'),
(1, 7, 'FPT Software', 'Hà Nội', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/FPT_logo_2010.svg/1200px-FPT_logo_2010.svg.png', 'Global IT company specializing in software development.'),
(2, 8, 'VinHomes', 'TP.HCM', 'https://batdongsan.kiengiang.vn/wp-content/uploads/2023/03/logo-vinhomes-1024x621.png', 'Real estate developer focusing on residential properties.'),
(6, 9, 'Techcombank', 'Hà Nội', 'https://inuvdp.com/wp-content/uploads/2022/08/file-vector-logo-techcombank-02.jpg', 'Leading commercial bank in Vietnam.'),
(4, 10, 'Shopee Vietnam', 'TP.HCM', 'https://upload.wikimedia.org/wikipedia/commons/b/b5/Shopee-logo.jpg', 'E-commerce platform offering online shopping services.'),
(4, 11, 'Tiki Corporation', 'TP.HCM', 'https://maisonoffice.vn/wp-content/uploads/2023/10/tiki-la-gi.jpg', 'Leading e-commerce platform in Vietnam.'),
(4, 12, 'Lazada Vietnam', 'TP.HCM', 'https://th.bing.com/th/id/OIP.HuysdVDIfO18ubBv2sV9qgHaCQ?w=347&h=106&c=7&r=0&o=7&cb=iwp2&dpr=1.3&pid=1.7&rm=3', 'E-commerce company offering diverse online shopping services.'),
(1, 13, 'Viettel Group', 'Hà Nội', 'https://static.topcv.vn/company_logos/bwFOxNGcRWOFQaO3IJJzAQB9hVd4CJtk_1644919697____999e51f17e0268f3fcceaebc71a966c3.jpg', 'Telecommunications and technology conglomerate.'),
(7, 14, 'Grab Vietnam', 'TP.HCM', 'https://images.glints.com/unsafe/glints-dashboard.oss-ap-southeast-1.aliyuncs.com/company-logo/02d7cd6d157f30c1236ec356b9f2e360.png', 'Ride-hailing and delivery service provider.'),
(3, 15, 'Sunhouse Group', 'Hà Nội', 'https://th.bing.com/th/id/OIP.nf9NhiMOjZ0llKM8Rkm-cgHaD-?cb=iwp2&rs=1&pid=ImgDetMain', 'Manufacturer of household appliances and industrial equipment.'),
(3, 16, 'TechBuilding Corp', 'TP.HCM', 'https://th.bing.com/th/id/OIP.jzFMQz9q2LmPdE8uqUpIuwHaDt?cb=iwp2&rs=1&pid=ImgDetMain', 'Building maintenance and technical services provider.'),
(3, 17, 'Maintenance Corp', 'Hà Nội', 'https://th.bing.com/th/id/OIP.OMU3xhg3e7UPHkJ2aE6tGwHaFO?cb=iwp2&rs=1&pid=ImgDetMain', 'Industrial equipment maintenance services.'),
(9, 18, 'PharmaCorp', 'TP.HCM', 'https://th.bing.com/th/id/OIP.5nw7E0-k7VLfoJpxNLP7EwHaF4?cb=iwp2&rs=1&pid=ImgDetMain', 'Pharmaceutical distribution and sales company.'),
(7, 19, 'LogisticsVN', 'Hà Nội',  'https://th.bing.com/th/id/R.9f0445a043903e1738d8db980e8cb86f?rik=4NByAkZF16zMFg&pid=ImgRaw&r=0', 'Logistics and delivery service provider.'),
(5, 20, 'English Center', 'TP.HCM',  'https://th.bing.com/th/id/OIP.yvNAWakjoWrZod4iEe5QqwAAAA?cb=iwp2&rs=1&pid=ImgDetMain', 'English language training center.'),
(4, 21, 'World Water Filter', 'Hà Nội',  'https://th.bing.com/th/id/OIP.ZLShbtcFb2FCGMtz3AyTYAHaD0?cb=iwp2&rs=1&pid=ImgDetMain', 'Distributor of premium water filtration products.'),
(4, 22, 'Fuel Additive Corp', 'TP.HCM',  'https://i.ytimg.com/vi/_nr-n4Dyv3Y/maxresdefault.jpg', 'Supplier of fuel additives and chemical solutions.'),
(8, 23, 'Interior Corp', 'Hà Nội', 'https://th.bing.com/th/id/OIP.FuvmYMVortSMSmyX3JATnwHaEK?cb=iwp2&rs=1&pid=ImgDetMain', 'Interior design and furnishing company.'),
(2, 24, 'Real Estate VN', 'Hà Nội', 'https://th.bing.com/th/id/OIP.As117lLTrcyzKDpPAxmlxAHaHa?cb=iwp2&rs=1&pid=ImgDetMain', 'Real estate consultancy and sales agency.'),
(6, 25, 'Bank VN', 'TP.HCM', 'https://th.bing.com/th/id/OIP.71yIH0XXG4B-BP7eeH_IwQHaFj?cb=iwp2&rs=1&pid=ImgDetMain', 'Commercial banking services provider.');

-- Insert into Student (5 records matching user_id 26-30 and Job_application)
INSERT INTO Student (user_id, address, job_field_id, university, preferred_job_address, profile_description, experience) VALUES
(27, '123 Nguyen Hue, District 1, Ho Chi Minh City', 4, 'Ho Chi Minh City University of Technology', 'Ho Chi Minh City', 'Third-year Marketing student, passionate about content creation and market analysis. Seeking internship opportunities in digital marketing to gain practical experience.', 'Marketing intern at a startup company (3 months), supported social media content creation.'),
(28, '456 Le Loi, Ba Dinh, Hanoi', 4, 'National Economics University', 'Hanoi', 'Fourth-year Business Administration student with strong communication skills and customer service experience. Seeking internship opportunities in customer service.', 'Part-time salesperson at a retail store (6 months).'),
(29, '789 Tran Hung Dao, Hai Chau, Da Nang', 5, 'University of Foreign Languages', 'Da Nang', 'Third-year English Language student, passionate about teaching and working with children. Aspiring to become an English teaching assistant to develop pedagogical skills.', 'Private English tutor for students since the first year of university.'),
(30, '101 Pham Van Dong, Cau Giay, Hanoi', 3, 'Hanoi University of Industry', 'Hanoi', 'Fourth-year Chemistry student with sales and customer consulting skills. Seeking job opportunities in technical sales.', 'Sales intern at a chemical company (4 months).'),
(31, '202 Vo Van Tan, District 3, Ho Chi Minh City', 5, 'Ho Chi Minh City University of Foreign Languages', 'Ho Chi Minh City', 'Third-year English Language student with strong English communication skills (IELTS 7.0). Seeking part-time opportunities in education to develop teaching skills.', 'English teaching assistant at a language center (3 months).');


-- Insert into Jobs_post (30 records with corrected job_field_id)
INSERT INTO Jobs_post 
(job_field_id, employer_id, job_title, job_description, job_salary, job_requirements, 
 job_location, applied_quality, job_type, approval_status, display_status, created_at) VALUES
(4, 1, 'Intern (Marketing, Human Resources)', 'ACORP Investment Joint Stock Company is recruiting
… (nội dung mô tả giữ nguyên) …', 
2600000,  -- 2,600,000 VND/month
'Relevant academic major, proactive, open-minded, eager to learn, responsible, teamwork-oriented, owns a personal laptop',
'Đà Nẵng', 0, 'PART_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 10:00:00'),

(4, 2, 'Merchandising Manager', 'Responsible for developing strategies for fresh food categories …',
35000000,  -- avg of 30–40 million
'University graduate, negotiation skills, inventory management',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 11:00:00'),

(1, 3, 'Senior SRE Manager (DevOps/Agile/Cloud/Networking)', 'Oversee the management and strategic analysis …',
NULL,  -- Negotiable
'IT graduate, experience in DevOps, GCP, CI/CD',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 12:00:00'),

(1, 4, 'Middle/Senior Back End Developer (Java)', 'Developing and implementing features …',
NULL,  -- Negotiable
'Proficient in Java, Agile, automation testing',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 13:00:00'),

(3, 5, 'General Laborer', '- Operate robotic painting equipment …',
7500000,  -- avg 7–8 million
'No experience required, good health',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 14:00:00'),

(3, 16, 'Building Maintenance Technician (Bình Tân, Tân Phú, District 7)', 'Perform maintenance and repair tasks …',
8500000,  -- avg 8–9 million
'Technical vocational school graduate, meticulous',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 15:00:00'),

(3, 17, 'Maintenance Technician - No Experience Required', '1) Maintain and service industrial cleaning equipment …',
11000000, -- avg 7–15 million
'No experience required, basic technical skills',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 16:00:00'),

(1, 25, 'Solution Architect (Java)', 'Responsible for designing solution architectures …',
NULL,  -- Negotiable
'Proficient in Java, system architecture, IT experience',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 17:00:00'),

(4, 21, 'Product Training Specialist for Distributors', '- Train and support distributors …',
20000000, -- avg 10–30 million
'Training skills, communication, sales experience',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 18:00:00'),

(4, 22, 'Sales Specialist', 'Consult, introduce, and sell the company’s products …',
15000000, -- avg 10–20 million
'Sales skills, communication, customer service',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 19:00:00'),

(1, 6, 'Game Programming Intern', 'VNG Corporation is recruiting Game Programming Interns …',
4000000,  -- avg 3–5 million
'3rd or 4th-year IT student, basic programming knowledge, passion for games',
'Ho Chi Minh City', 0, 'PART_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 20:00:00'),

(1, 7, 'Full Stack Software Engineer', 'FPT Software is recruiting Full Stack Software Engineers …',
27500000, -- avg 20–35 million
'IT graduate, proficient in JavaScript, Node.js, React, SQL/NoSQL',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 21:00:00'),

(2, 8, 'Real Estate Sales Specialist', 'VinHomes is recruiting Real Estate Sales Specialists …',
15000000, -- base 15 million (commission riêng)
'University graduate, strong communication skills, pressure-resistant',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 22:00:00'),

(6, 9, 'Customer Relationship Specialist', 'Techcombank is recruiting Customer Relationship Specialists …',
15000000, -- avg 12–18 million
'Finance/Banking graduate, strong communication skills',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-22 23:00:00'),

(4, 10, 'Digital Marketing Specialist', 'Shopee Vietnam is recruiting Digital Marketing Specialists …',
21500000, -- avg 18–25 million
'Marketing graduate, proficient in Google Ads, Facebook Ads',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 00:00:00'),

(3, 5, 'CNC Machine Operator', 'Parker Processing Vietnam Co., Ltd. is recruiting CNC Machine Operators …',
10000000, -- avg 8–12 million
'Mechanics/Engineering graduate, meticulous, responsible',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 01:00:00'),

(9, 18, 'Pharmaceutical Sales Representative', 'Recruiting Pharmaceutical Sales Representatives …',
12500000, -- avg 10–15 million
'Vocational school graduate, communication skills, personal vehicle',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 02:00:00'),

(7, 19, 'Delivery Driver', 'Recruiting Delivery Drivers …',
10500000, -- avg 9–12 million
'B2 license, familiar with streets, good health',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 03:00:00'),

(5, 20, 'Part-time English Teacher', 'Recruiting Part-time English Teachers …',
250000,   -- avg 200 000–300 000 VND/hour
'University graduate, passionate about teaching, patient',
'Ho Chi Minh City', 0, 'PART_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 04:00:00'),

(10, 25, 'Automotive Maintenance Technician', 'Recruiting Automotive Maintenance Technicians …',
11000000, -- avg 8–14 million
'Automotive Engineering graduate, meticulous, responsible',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 05:00:00'),

(4, 11, 'Customer Service Representative', 'Tiki Corporation is recruiting Customer Service Representatives …',
10000000, -- avg 8–12 million
'Vocational school graduate, strong communication skills, patient',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 06:00:00'),

(4, 12, 'Content Marketing Intern', 'Lazada Vietnam is recruiting Content Marketing Interns …',
3250000,  -- avg 2.5–4 million
'Marketing/Communications student, creative, strong writing skills',
'Ho Chi Minh City', 0, 'PART_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 07:00:00'),

(1, 13, 'Telecommunications Engineer', 'Viettel Group is recruiting Telecommunications Engineers …',
20000000, -- avg 15–25 million
'Telecommunications/IT graduate, knowledge of 4G/5G networks',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 08:00:00'),

(7, 14, 'Grab Motorcycle Driver', 'Grab Vietnam is recruiting Motorcycle Drivers …',
12500000, -- avg 10–15 million
'A1/A2 license, own motorcycle, smartphone',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 09:00:00'),

(3, 15, 'Quality Control Staff', 'Sunhouse Group is recruiting Quality Control Staff …',
8500000,  -- avg 7–10 million
'Vocational school graduate, meticulous, detail-oriented',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 10:00:00'),

(5, 20, 'English Teaching Assistant', 'Recruiting English Teaching Assistants …',
200000,   -- avg 150 000–250 000 VND/hour
'English Language student/graduate, good communication, enjoys working with children',
'Ho Chi Minh City', 0, 'PART_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 11:00:00'),

(8, 23, 'Interior Design Staff', 'Recruiting Interior Design Staff …',
15000000, -- avg 12–18 million
'Interior Design/Architecture graduate, proficient in AutoCAD, SketchUp',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 12:00:00'),

(9, 18, 'Pharmaceutical Market Research Staff', 'Recruiting Pharmaceutical Market Research Staff …',
13000000, -- avg 10–16 million
'Pharmacy/Marketing graduate, data analysis skills',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 13:00:00'),

(2, 24, 'Real Estate Consultant', 'Recruiting Real Estate Consultants …',
10000000, -- base 10 million (commission riêng)
'Vocational school graduate, communication skills, pressure-resistant',
'Hanoi', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 14:00:00'),

(6, 25, 'Bank Credit Officer', 'Recruiting Bank Credit Officers …',
16000000, -- avg 12–20 million
'Finance/Banking graduate, financial analysis skills',
'Ho Chi Minh City', 0, 'FULL_TIME', 'APPROVED', 'ACTIVE', '2025-05-23 15:00:00');

INSERT INTO Job_application (student_id, job_post_id, full_name, email, phone, description, cv_url, status) VALUES
(1, 1, 'Nguyen Van An', 'nguyenvanan@gmail.com', '0905123456', 'I am a third-year Marketing student, eager to intern at ACORP to gain practical experience.', 'https://drive.google.com/file/d/1psr6tFDTNx9H9iVr50YzW2Z5VD1ajmK9/view?usp=sharing', 'SUBMITTED'),
(2, 1, 'Tran Thi Binh', 'tranbinh123@gmail.com', '0916234567', 'I am passionate about human resources and want to experience a professional environment at ACORP.', 'https://drive.google.com/file/d/155TMxfmv7gBRd74ZH1c2ODaqIgqscyFx/view?usp=sharing', 'SUBMITTED'),
(3, 3, 'Le Hoang Nam', 'lehoangnam@gmail.com', '0927345678', 'I have 5 years of experience in DevOps and would like to apply for the Senior SRE Manager position.', 'https://drive.google.com/file/d/1R2dhoq3l45z0O06acot--gdsFI3dQ64k/view?usp=sharing', 'SUBMITTED'),
(4, 7, 'Pham Thi Cuc', 'phamthicuc@gmail.com', '0938456789', 'I am a software engineer with Full Stack experience, proficient in JavaScript and React, and eager to join FPT Software.', 'https://drive.google.com/file/d/1pX7voFfWJNVVkxMXIGcrWioTqvXTot3v/view?usp=sharing', 'SUBMITTED'),
(5, 11, 'Hoang Van Dung', 'hoangvandung@gmail.com', '0949567890', 'I am a final-year IT student, passionate about game programming, and want to intern at VNG.', 'https://drive.google.com/file/d/1LAQOw5iJ29jCToyUyDp83Rh8TOcXEyqQ/view?usp=sharing', 'SUBMITTED');


-- Sample data for Blog functionality
-- Đảm bảo rằng bạn đã có ít nhất 1 admin account trong bảng account

-- =======================================================================
-- STUDENTJOB BLOG DATA - COMPLETE FINAL VERSION
-- Với cấu trúc BlogPost và BlogImage chính xác theo model
-- =======================================================================

-- =======================================================================
-- 1. HƯỚNG DẪN VIẾT CV HIỆU QUẢ (RESOURCE: APPLICATION_TIPS)
-- =======================================================================

INSERT INTO resource (resource_title, resource_content, image_url, resource_type, created_by, created_at, updated_at) 
VALUES ('Hướng dẫn viết CV xin việc hiệu quả và chuyên nghiệp', 
        'Hướng dẫn chi tiết từ A-Z cách viết CV xin việc hiệu quả: cấu trúc CV, thông tin cần thiết, kỹ năng trình bày, mẫu CV chuẩn và những lỗi thường gặp cần tránh. Giúp bạn tạo được CV ấn tượng để chinh phục nhà tuyển dụng.', 
        'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=800', 
        'application_tips', 
        1, 
        NOW(), 
        NOW());

INSERT INTO blog_post (resource_id, title, summary, content, featured_image_url, status, created_at, updated_at, published_at, meta_description)
VALUES (LAST_INSERT_ID(), 
        'Hướng dẫn viết CV xin việc hiệu quả và chuyên nghiệp',
        'CV là tấm vé đầu tiên giúp bạn gây ấn tượng với nhà tuyển dụng. Học cách viết CV hiệu quả với 8 bước chi tiết, mẫu CV chuẩn và những lỗi thường gặp cần tránh để tăng cơ hội được mời phỏng vấn.',
        '<p class="lead">Viết CV hiệu quả là kỹ năng thiết yếu giúp bạn nổi bật trong mắt nhà tuyển dụng và tăng cơ hội được mời phỏng vấn.</p>

        <h2>📋 Cấu trúc CV chuẩn gồm 8 phần</h2>
        <img src="https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=600" alt="CV Structure" class="img-fluid my-3">
        
        <div class="alert alert-primary">
            <h5 class="text-primary">🎯 8 thành phần cốt lõi của CV:</h5>
            <ol>
                <li><strong>Thông tin cá nhân</strong> - Họ tên, SĐT, email, địa chỉ</li>
                <li><strong>Mục tiêu nghề nghiệp</strong> - Objective hoặc Professional Summary</li>
                <li><strong>Kinh nghiệm làm việc</strong> - Work Experience (quan trọng nhất)</li>
                <li><strong>Học vấn</strong> - Education & Qualifications</li>
                <li><strong>Kỹ năng</strong> - Technical & Soft Skills</li>
                <li><strong>Dự án</strong> - Projects (cho Fresh Graduate)</li>
                <li><strong>Chứng chỉ</strong> - Certifications</li>
                <li><strong>Sở thích</strong> - Interests (tùy chọn)</li>
            </ol>
        </div>

        <h2>👤 Phần 1: Thông tin cá nhân</h2>
        <div class="card border-success">
            <div class="card-header bg-success text-white">
                <h6 class="mb-0">✅ THÔNG TIN CẦN CÓ</h6>
            </div>
            <div class="card-body">
                <ul>
                    <li><strong>Họ và tên:</strong> Viết đầy đủ, in hoa chữ cái đầu</li>
                    <li><strong>Số điện thoại:</strong> Số chính, luôn bật máy</li>
                    <li><strong>Email:</strong> Địa chỉ chuyên nghiệp (tên.họ@gmail.com)</li>
                    <li><strong>Địa chỉ:</strong> Quận/huyện, thành phố (không cần số nhà cụ thể)</li>
                    <li><strong>LinkedIn/Portfolio:</strong> Nếu có và chuyên nghiệp</li>
                </ul>
            </div>
        </div>

        <div class="card border-danger mt-3">
            <div class="card-header bg-danger text-white">
                <h6 class="mb-0">❌ THÔNG TIN KHÔNG NÊN GHI</h6>
            </div>
            <div class="card-body">
                <ul>
                    <li>Tuổi, ngày sinh chi tiết</li>
                    <li>Tình trạng hôn nhân</li>
                    <li>Tôn giáo, chính trị</li>
                    <li>Ảnh selfie hoặc ảnh không chuyên nghiệp</li>
                    <li>Số CMND/CCCD</li>
                </ul>
            </div>
        </div>

        <h2>🎯 Phần 2: Mục tiêu nghề nghiệp</h2>
        <img src="https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=600" alt="Career Objective" class="img-fluid my-3">
        
        <p><strong>Professional Summary</strong> (cho người có kinh nghiệm):</p>
        <div class="alert alert-info">
            <p><em>"Marketing professional với 3+ năm kinh nghiệm trong Digital Marketing. Đã quản lý ngân sách quảng cáo 2 tỷ đồng và tăng conversion rate lên 150% cho các dự án e-commerce. Tìm kiếm cơ hội phát triển tại vị trí Marketing Manager trong môi trường năng động."</em></p>
        </div>

        <p><strong>Objective</strong> (cho Fresh Graduate):</p>
        <div class="alert alert-warning">
            <p><em>"Sinh viên Marketing năm cuối với GPA 3.5/4.0 và kinh nghiệm thực tập 6 tháng tại agency. Mong muốn ứng tuyển vị trí Marketing Executive để phát triển kỹ năng Digital Marketing và đóng góp vào sự phát triển của công ty."</em></p>
        </div>

        <h2>💼 Phần 3: Kinh nghiệm làm việc</h2>
        <p>Đây là phần <strong>QUAN TRỌNG NHẤT</strong> của CV. Sắp xếp theo thứ tự thời gian ngược (mới nhất lên đầu).</p>
        
        <table class="table table-bordered">
            <thead class="thead-dark">
                <tr>
                    <th>Thông tin</th>
                    <th>Cách viết</th>
                    <th>Ví dụ</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Tên công ty</strong></td>
                    <td>Tên đầy đủ + lĩnh vực</td>
                    <td>FPT Software (IT Solutions)</td>
                </tr>
                <tr>
                    <td><strong>Vị trí</strong></td>
                    <td>Chính xác, không thổi phồng</td>
                    <td>Digital Marketing Executive</td>
                </tr>
                <tr>
                    <td><strong>Thời gian</strong></td>
                    <td>MM/YYYY - MM/YYYY</td>
                    <td>06/2022 - 12/2023</td>
                </tr>
                <tr>
                    <td><strong>Mô tả công việc</strong></td>
                    <td>3-5 bullet points, bắt đầu bằng động từ</td>
                    <td>• Quản lý chiến dịch Facebook Ads với ngân sách 100 triệu/tháng</td>
                </tr>
            </tbody>
        </table>

        <h3>🏆 Công thức STAR cho mô tả thành tích:</h3>
        <div class="row">
            <div class="col-md-6">
                <div class="card border-primary">
                    <div class="card-body">
                        <h6 class="text-primary">📈 Có số liệu cụ thể:</h6>
                        <ul>
                            <li>Tăng doanh số lên 150% trong 6 tháng</li>
                            <li>Quản lý team 5 người</li>
                            <li>Giảm chi phí vận hành 20%</li>
                            <li>Hoàn thành 95% KPI hàng quý</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-success">
                    <div class="card-body">
                        <h6 class="text-success">💡 Sử dụng Action Words:</h6>
                        <ul>
                            <li><strong>Managed</strong> - Quản lý</li>
                            <li><strong>Developed</strong> - Phát triển</li>
                            <li><strong>Implemented</strong> - Triển khai</li>
                            <li><strong>Achieved</strong> - Đạt được</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <h2>🎓 Phần 4: Học vấn</h2>
        <p>Ghi thông tin học vấn cao nhất trước, bao gồm:</p>
        <ul>
            <li><strong>Tên trường:</strong> Đại học FPT</li>
            <li><strong>Chuyên ngành:</strong> Cử nhân Quản trị Kinh doanh</li>
            <li><strong>Thời gian:</strong> 2019 - 2023</li>
            <li><strong>GPA:</strong> 3.2/4.0 (nếu ≥ 3.0)</li>
            <li><strong>Thành tích:</strong> Học bổng, giải thưởng (nếu có)</li>
        </ul>

        <h2>🛠️ Phần 5: Kỹ năng</h2>
        <img src="https://images.unsplash.com/photo-1553028826-f4804a6dfd3f?w=600" alt="Skills" class="img-fluid my-3">
        
        <div class="row">
            <div class="col-md-6">
                <h6>💻 Technical Skills:</h6>
                <ul>
                    <li><strong>Programming:</strong> Java (Advanced), Python (Intermediate)</li>
                    <li><strong>Database:</strong> MySQL, PostgreSQL</li>
                    <li><strong>Tools:</strong> Git, Docker, Jira</li>
                    <li><strong>Marketing:</strong> Google Ads, Facebook Ads, Google Analytics</li>
                </ul>
            </div>
            <div class="col-md-6">
                <h6>🤝 Soft Skills:</h6>
                <ul>
                    <li>Giao tiếp và thuyết trình</li>
                    <li>Làm việc nhóm</li>
                    <li>Quản lý thời gian</li>
                    <li>Tư duy phân tích</li>
                </ul>
            </div>
        </div>

        <h2>🚀 Phần 6: Dự án nổi bật</h2>
        <p>Đặc biệt quan trọng cho Fresh Graduate:</p>
        
        <div class="card border-info">
            <div class="card-header bg-info text-white">
                <h6 class="mb-0">📱 Dự án Website E-commerce</h6>
            </div>
            <div class="card-body">
                <ul>
                    <li><strong>Mô tả:</strong> Xây dựng website bán hàng online sử dụng Spring Boot và React</li>
                    <li><strong>Vai trò:</strong> Full-stack Developer, Team Leader</li>
                    <li><strong>Công nghệ:</strong> Java, Spring Boot, MySQL, React, Bootstrap</li>
                    <li><strong>Kết quả:</strong> Hoàn thành 100% tính năng, demo thành công trước 50+ người</li>
                    <li><strong>Link:</strong> github.com/username/project (nếu có)</li>
                </ul>
            </div>
        </div>

        <h2>📜 Phần 7: Chứng chỉ</h2>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Chứng chỉ</th>
                    <th>Tổ chức cấp</th>
                    <th>Thời gian</th>
                    <th>Tình trạng</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Google Analytics Certified</td>
                    <td>Google</td>
                    <td>12/2023</td>
                    <td>Valid until 12/2025</td>
                </tr>
                <tr>
                    <td>TOEIC 750</td>
                    <td>ETS</td>
                    <td>08/2023</td>
                    <td>Còn hiệu lực</td>
                </tr>
                <tr>
                    <td>Oracle Java SE 8 Programmer</td>
                    <td>Oracle</td>
                    <td>06/2023</td>
                    <td>Lifetime</td>
                </tr>
            </tbody>
        </table>

        <h2>⚠️ 10 lỗi thường gặp cần tránh</h2>
        <div class="alert alert-danger">
            <h5 class="text-danger">❌ Top 10 lỗi CV phổ biến:</h5>
            <ol>
                <li><strong>Quá dài:</strong> CV > 2 trang cho người < 5 năm kinh nghiệm</li>
                <li><strong>Sai chính tả:</strong> Lỗi ngữ pháp, gõ thiếu dấu</li>
                <li><strong>Format không nhất quán:</strong> Font chữ, size, spacing khác nhau</li>
                <li><strong>Thiếu keywords:</strong> Không có từ khóa của Job Description</li>
                <li><strong>Quá chung chung:</strong> "Làm việc chăm chỉ, có trách nhiệm"</li>
                <li><strong>Thông tin cũ:</strong> Email không hoạt động, số điện thoại sai</li>
                <li><strong>Thiếu số liệu:</strong> Không có metrics, KPI cụ thể</li>
                <li><strong>Copy template:</strong> CV giống hệt template mặc định</li>
                <li><strong>Ảnh không phù hợp:</strong> Ảnh selfie, ảnh party</li>
                <li><strong>File không chuẩn:</strong> Gửi file .docx thay vì PDF</li>
            </ol>
        </div>

        <h2>✅ Checklist hoàn thiện CV</h2>
        <div class="card border-success">
            <div class="card-header bg-success text-white">
                <h6 class="mb-0">📋 Checklist cuối cùng trước khi gửi:</h6>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6>📝 Nội dung:</h6>
                        <ul class="list-unstyled">
                            <li>☐ Thông tin liên lạc chính xác</li>
                            <li>☐ Objective/Summary phù hợp với JD</li>
                            <li>☐ Experience có số liệu cụ thể</li>
                            <li>☐ Skills match với yêu cầu công việc</li>
                            <li>☐ Không có lỗi chính tả</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6>🎨 Định dạng:</h6>
                        <ul class="list-unstyled">
                            <li>☐ Font chữ chuyên nghiệp (Arial, Calibri)</li>
                            <li>☐ Size 10-12pt, không quá nhỏ</li>
                            <li>☐ Margins 1 inch, spacing hợp lý</li>
                            <li>☐ Xuất file PDF chất lượng cao</li>
                            <li>☐ Tên file: HoTen_CV_ViTri.pdf</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <h2>💡 Tips cuối cùng</h2>
        <div class="card bg-primary text-white">
            <div class="card-body">
                <h5 class="card-title">🏆 Bí quyết CV thành công</h5>
                <ul class="card-text">
                    <li><strong>Customize cho từng vị trí:</strong> Đọc kỹ JD và điều chỉnh CV phù hợp</li>
                    <li><strong>Quantify achievements:</strong> Luôn có số liệu, tỷ lệ phần trăm</li>
                    <li><strong>Keep it simple:</strong> Ưu tiên nội dung hơn design phức tạp</li>
                    <li><strong>Update thường xuyên:</strong> Bổ sung kỹ năng, dự án mới</li>
                    <li><strong>Get feedback:</strong> Nhờ mentor, bạn bè review CV</li>
                </ul>
            </div>
        </div>',
        'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=800',
        'PUBLISHED',
        NOW(),
        NOW(),
        NOW(),
        'Hướng dẫn chi tiết cách viết CV xin việc hiệu quả với 8 bước cụ thể từ thông tin cá nhân đến kỹ năng và kinh nghiệm. Mẫu CV chuẩn 2024.');

-- Blog images cho bài CV
INSERT INTO blog_image (blog_post_id, image_url, alt_text, caption, display_order, image_type)
VALUES 
-- Ảnh featured
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=800', 'Hướng dẫn viết CV', 'CV là chìa khóa mở cửa cơ hội nghề nghiệp', 0, 'FEATURED'),
-- Ảnh inline 1
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=600', 'CV Structure', 'Cấu trúc CV chuẩn gồm 8 phần chính', 1, 'INLINE'),
-- Ảnh inline 2
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=600', 'Career Objective', 'Mục tiêu nghề nghiệp rõ ràng và cụ thể', 2, 'INLINE'),
-- Ảnh inline 3
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1553028826-f4804a6dfd3f?w=600', 'Skills', 'Kỹ năng chuyên môn và kỹ năng mềm', 3, 'INLINE');

-- =======================================================================
-- 2. TOP 20 CÂU HỎI PHỎNG VẤN THƯỜNG GẶP (RESOURCE: INTERVIEW_GUIDE)
-- =======================================================================

INSERT INTO resource (resource_title, resource_content, image_url, resource_type, created_by, created_at, updated_at) 
VALUES ('TOP 20 câu hỏi phỏng vấn thường gặp và cách trả lời ấn tượng', 
        'Tổng hợp 20 câu hỏi phỏng vấn phổ biến nhất mà nhà tuyển dụng thường đặt, kèm theo hướng dẫn trả lời chi tiết theo phương pháp STAR và những lưu ý quan trọng để gây ấn tượng tốt.', 
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800', 
        'interview_guide', 
        1, 
        NOW(), 
        NOW());

INSERT INTO blog_post (resource_id, title, summary, content, featured_image_url, status, created_at, updated_at, published_at, meta_description)
VALUES (LAST_INSERT_ID(), 
        'TOP 20 câu hỏi phỏng vấn thường gặp và cách trả lời ấn tượng',
        'Nắm vững 20 câu hỏi phỏng vấn phổ biến nhất và cách trả lời theo phương pháp STAR để tự tin chinh phục nhà tuyển dụng. Bao gồm cả câu hỏi khó và mẹo tránh những cạm bẫy thường gặp.',
        '<p class="lead">Chuẩn bị tốt cho phỏng vấn là chìa khóa thành công. 20 câu hỏi này chiếm 80% nội dung phỏng vấn tại các công ty.</p>

        <h2>🎯 Phương pháp STAR trả lời phỏng vấn</h2>
        <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600" alt="Interview preparation" class="img-fluid my-3">
        
        <div class="alert alert-primary">
            <h5 class="text-primary">⭐ Công thức STAR:</h5>
            <ul>
                <li><strong>S - Situation:</strong> Mô tả tình huống cụ thể</li>
                <li><strong>T - Task:</strong> Nhiệm vụ bạn cần hoàn thành</li>
                <li><strong>A - Action:</strong> Hành động bạn đã thực hiện</li>
                <li><strong>R - Result:</strong> Kết quả đạt được (có số liệu)</li>
            </ul>
        </div>

        <h2>📋 NHÓM 1: Câu hỏi về bản thân (5 câu)</h2>
        
        <h3>❓ Câu 1: "Hãy giới thiệu về bản thân"</h3>
        <div class="card border-success">
            <div class="card-header bg-success text-white">
                <h6 class="mb-0">✅ Cách trả lời hay:</h6>
            </div>
            <div class="card-body">
                <p><em>"Chào anh/chị, em tên là [Tên]. Em là sinh viên năm cuối ngành Marketing tại ĐH FPT với GPA 3.5/4.0. Trong 2 năm qua, em đã tích lũy kinh nghiệm qua 2 kỳ thực tập tại agency và startup, nơi em đã tham gia quản lý fanpage với 50K followers và tăng engagement rate lên 25%. Em đặc biệt quan tâm đến Digital Marketing và mong muốn phát triển sự nghiệp tại một môi trường năng động như [Tên công ty]."</em></p>
            </div>
        </div>

        <h3>❓ Câu 2: "Điểm mạnh và điểm yếu của bạn?"</h3>
        <div class="row">
            <div class="col-md-6">
                <div class="card border-primary">
                    <div class="card-header bg-primary text-white">
                        <h6 class="mb-0">💪 Điểm mạnh</h6>
                    </div>
                    <div class="card-body">
                        <p><strong>Công thức:</strong> Điểm mạnh + Ví dụ cụ thể + Liên kết với công việc</p>
                        <p><em>"Điểm mạnh của em là khả năng phân tích dữ liệu. Trong dự án cuối kỳ, em đã phân tích behavior của 1000+ users và đưa ra 5 insights giúp tăng conversion rate lên 15%. Kỹ năng này sẽ giúp em hiệu quả trong việc optimize campaigns."</em></p>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-warning">
                    <div class="card-header bg-warning text-dark">
                        <h6 class="mb-0">⚠️ Điểm yếu</h6>
                    </div>
                    <div class="card-body">
                        <p><strong>Công thức:</strong> Điểm yếu thật + Cách khắc phục + Tiến bộ đã đạt</p>
                        <p><em>"Trước đây em hay lo lắng khi thuyết trình trước đông người. Em đã khắc phục bằng cách tham gia CLB thuyết trình và thực hành thường xuyên. Giờ đây em đã tự tin present trước 50+ người."</em></p>
                    </div>
                </div>
            </div>
        </div>

        <h3>❓ Câu 3: "Tại sao bạn chọn công ty chúng tôi?"</h3>
        <div class="alert alert-info">
            <p><strong>Cấu trúc trả lời:</strong></p>
            <ul>
                <li><strong>Research về công ty:</strong> Sản phẩm, văn hóa, giá trị</li>
                <li><strong>Kết nối với mục tiêu cá nhân:</strong> Tại sao phù hợp?</li>
                <li><strong>Đóng góp cụ thể:</strong> Bạn mang lại gì cho công ty?</li>
            </ul>
            <p><em>"Em rất ấn tượng với cách [Công ty] đổi mới trong lĩnh vực fintech và văn hóa work-life balance. Đặc biệt, sau khi tìm hiểu về dự án [Tên dự án cụ thể], em thấy kinh nghiệm UX research của mình có thể đóng góp vào việc cải thiện user experience."</em></p>
        </div>

        <h3>❓ Câu 4: "Mục tiêu nghề nghiệp của bạn?"</h3>
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Thời gian</th>
                    <th>Mục tiêu</th>
                    <th>Cách đạt được</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>1 năm</strong></td>
                    <td>Thành thạo công việc Marketing Executive</td>
                    <td>Học hỏi từ mentor, hoàn thành training program</td>
                </tr>
                <tr>
                    <td><strong>3 năm</strong></td>
                    <td>Thăng tiến lên Marketing Manager</td>
                    <td>Lấy thêm chứng chỉ, quản lý team nhỏ</td>
                </tr>
                <tr>
                    <td><strong>5 năm</strong></td>
                    <td>Chuyên gia Digital Marketing</td>
                    <td>MBA hoặc chuyên sâu về AI Marketing</td>
                </tr>
            </tbody>
        </table>

        <h3>❓ Câu 5: "Lương mong muốn?"</h3>
        <div class="card border-info">
            <div class="card-body">
                <h6 class="text-info">💰 3 cách trả lời thông minh:</h6>
                <ol>
                    <li><strong>Hỏi ngược:</strong> "Anh/chị có thể chia sẻ mức lương cho vị trí này không ạ?"</li>
                    <li><strong>Đưa range:</strong> "Theo research, mức lương cho vị trí này là 12-15 triệu. Em mong muốn trong khoảng này."</li>
                    <li><strong>Linh hoạt:</strong> "Em ưu tiên cơ hội học hỏi. Em có thể linh hoạt về mức lương nếu có package benefits tốt."</li>
                </ol>
            </div>
        </div>

        <h2>🎯 NHÓM 2: Câu hỏi về kinh nghiệm (5 câu)</h2>
        <img src="https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=600" alt="Work experience questions" class="img-fluid my-3">

        <h3>❓ Câu 6: "Kể về một dự án thành công"</h3>
        <div class="alert alert-success">
            <p><strong>Template STAR:</strong></p>
            <p><em><strong>Situation:</strong> "Trong thực tập tại công ty X, em được giao nhiệm vụ tăng engagement cho fanpage có 10K followers."</em></p>
            <p><em><strong>Task:</strong> "Mục tiêu là tăng engagement rate từ 2% lên 5% trong 3 tháng."</em></p>
            <p><em><strong>Action:</strong> "Em đã research audience, tạo content calendar, post 2 bài/ngày với format video ngắn, và chạy contest mini."</em></p>
            <p><em><strong>Result:</strong> "Kết quả engagement tăng lên 7.2%, followers tăng thêm 3K, và boss praise em trước toàn team."</em></p>
        </div>

        <h3>❓ Câu 7: "Thử thách lớn nhất bạn đã vượt qua?"</h3>
        <div class="card border-warning">
            <div class="card-body">
                <h6>🚧 Ví dụ thử thách học tập:</h6>
                <p><em>"Kỳ học trước em phải handle 6 môn khó đồng thời với thực tập part-time. Em đã tạo time-blocking schedule, ưu tiên theo ma trận Eisenhower và nhờ sự support của study group. Kết quả em pass tất cả môn với GPA 3.4 và complete tốt internship."</em></p>
            </div>
        </div>

        <h2>🎯 NHÓM 3: Câu hỏi tình huống (5 câu)</h2>

        <h3>❓ Câu 8: "Nếu có conflict với colleague?"</h3>
        <div class="alert alert-primary">
            <h6>🤝 4 bước giải quyết xung đột:</h6>
            <ol>
                <li><strong>Listen:</strong> Lắng nghe quan điểm của người khác</li>
                <li><strong>Understand:</strong> Hiểu root cause của vấn đề</li>
                <li><strong>Communicate:</strong> Thảo luận để tìm common ground</li>
                <li><strong>Collaborate:</strong> Cùng nhau đưa ra solution win-win</li>
            </ol>
        </div>

        <h3>❓ Câu 9: "Làm gì khi deadline tight?"</h3>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Bước</th>
                    <th>Hành động</th>
                    <th>Ví dụ</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>1. Prioritize</td>
                    <td>Chia tasks theo độ quan trọng</td>
                    <td>Must-have vs Nice-to-have</td>
                </tr>
                <tr>
                    <td>2. Communicate</td>
                    <td>Thông báo với manager sớm</td>
                    <td>Update progress hàng ngày</td>
                </tr>
                <tr>
                    <td>3. Optimize</td>
                    <td>Tìm cách làm hiệu quả hơn</td>
                    <td>Automation, templates</td>
                </tr>
                <tr>
                    <td>4. Support</td>
                    <td>Nhờ help nếu cần thiết</td>
                    <td>Delegate hoặc ask for extension</td>
                </tr>
            </tbody>
        </table>

        <h2>🎯 NHÓM 4: Câu hỏi về company & role (5 câu)</h2>

        <h3>❓ Câu 10: "Bạn hiểu gì về vị trí này?"</h3>
        <div class="card border-success">
            <div class="card-body">
                <h6>📋 Cấu trúc trả lời:</h6>
                <ul>
                    <li><strong>Key responsibilities:</strong> 3-4 nhiệm vụ chính từ JD</li>
                    <li><strong>Required skills:</strong> Skills match với background của bạn</li>
                    <li><strong>Growth opportunities:</strong> Cơ hội phát triển trong role</li>
                    <li><strong>Questions:</strong> Đặt 1-2 câu hỏi thể hiện sự quan tâm</li>
                </ul>
            </div>
        </div>

        <h2>💡 NHÓM 5: Câu hỏi cuối phỏng vấn</h2>
        <img src="https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=600" alt="Interview questions" class="img-fluid my-3">

        <h3>❓ "Bạn có câu hỏi nào cho chúng tôi?"</h3>
        <div class="alert alert-info">
            <h6>🔥 TOP câu hỏi tạo ấn tượng:</h6>
            <ul>
                <li>"Thử thách lớn nhất của team hiện tại là gì?"</li>
                <li>"Success trong vị trí này được đo lường như thế nào?"</li>
                <li>"Cơ hội training và development cho employee ra sao?"</li>
                <li>"Văn hóa công ty có điều gì anh/chị thích nhất?"</li>
                <li>"Next steps trong quá trình tuyển dụng?"</li>
            </ul>
        </div>

        <h2>🚫 Những câu hỏi KHÔNG nên đặt</h2>
        <div class="alert alert-danger">
            <h6>❌ Tránh những câu hỏi này:</h6>
            <ul>
                <li>"Lương bao nhiêu?" (đã hỏi ở trên)</li>
                <li>"Bao giờ được tăng lương?"</li>
                <li>"Có được remote không?"</li>
                <li>"Overtime nhiều không?"</li>
                <li>"Công ty có bao nhiều nhân viên?" (thông tin cơ bản)</li>
            </ul>
        </div>

        <h2>🎯 Tips chuẩn bị phỏng vấn</h2>
        <div class="row">
            <div class="col-md-6">
                <div class="card border-primary">
                    <div class="card-header bg-primary text-white">
                        <h6 class="mb-0">📚 Chuẩn bị nội dung</h6>
                    </div>
                    <div class="card-body">
                        <ul>
                            <li>Research kỹ về công ty, sản phẩm</li>
                            <li>Chuẩn bị 3-5 stories theo STAR</li>
                            <li>Luyện tập trước gương hoặc với bạn</li>
                            <li>Chuẩn bị 5-7 câu hỏi cho interviewer</li>
                            <li>Print CV và portfolio</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-success">
                    <div class="card-header bg-success text-white">
                        <h6 class="mb-0">👔 Chuẩn bị hình thức</h6>
                    </div>
                    <div class="card-body">
                        <ul>
                            <li>Business attire phù hợp</li>
                            <li>Đến sớm 15 phút</li>
                            <li>Mang theo notepad và bút</li>
                            <li>Tắt điện thoại hoặc để silent</li>
                            <li>Mỉm cười và eye contact</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <h2>🏆 Kết luận</h2>
        <div class="card bg-primary text-white">
            <div class="card-body text-center">
                <h5 class="card-title">Thành công trong phỏng vấn = Chuẩn bị kỹ lưỡng + Tự tin + Chân thành</h5>
                <p class="card-text">Hãy nhớ rằng phỏng vấn là cuộc trò chuyện hai chiều. Bạn cũng đang đánh giá xem công ty có phù hợp không. Hãy tự tin, chân thành và thể hiện passion của mình!</p>
                <p class="card-text"><strong>Good luck! 🍀</strong></p>
            </div>
        </div>',
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800',
        'PUBLISHED',
        NOW(),
        NOW(),
        NOW(),
        'TOP 20 câu hỏi phỏng vấn thường gặp nhất và cách trả lời ấn tượng theo phương pháp STAR. Hướng dẫn chi tiết để chinh phục nhà tuyển dụng.');

-- Blog images cho bài TOP 20 câu hỏi phỏng vấn
INSERT INTO blog_image (blog_post_id, image_url, alt_text, caption, display_order, image_type)
VALUES 
-- Ảnh featured
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800', 'Interview tips', 'Chuẩn bị kỹ lưỡng cho phỏng vấn thành công', 0, 'FEATURED'),
-- Ảnh inline 1  
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600', 'Interview preparation', 'Phương pháp STAR cho câu trả lời ấn tượng', 1, 'INLINE'),
-- Ảnh inline 2
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=600', 'Work experience questions', 'Câu hỏi về kinh nghiệm làm việc', 2, 'INLINE'),
-- Ảnh inline 3
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=600', 'Interview questions', 'Những câu hỏi cuối phỏng vấn quan trọng', 3, 'INLINE'); 


-- =======================================================================
-- 3. QUOTES TRUYỀN CẢM HỨNG CHO SINH VIÊN (RESOURCE: QUOTES)
-- =======================================================================

INSERT INTO resource (resource_title, resource_content, image_url, resource_type, created_by, created_at, updated_at) 
VALUES ('50 câu quotes truyền cảm hứng cho sinh viên và người tìm việc', 
        'Tuyển tập những câu danh ngôn và quotes truyền cảm hứng mạnh mẽ nhất dành cho sinh viên và những người đang tìm kiếm cơ hội nghề nghiệp. Các câu quotes được phân loại theo chủ đề: thành công, học tập, sự nghiệp, kiên trì và lãnh đạo.', 
        'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800', 
        'quotes', 
        1, 
        NOW(), 
        NOW());

INSERT INTO blog_post (resource_id, title, summary, content, featured_image_url, status, created_at, updated_at, published_at, meta_description)
VALUES (LAST_INSERT_ID(), 
        '50 câu quotes truyền cảm hứng cho sinh viên và người tìm việc',
        'Những câu danh ngôn và quotes truyền cảm hứng mạnh mẽ nhất sẽ giúp bạn duy trì động lực trong học tập và tìm kiếm cơ hội nghề nghiệp. Hãy để những lời khuyên này trở thành nguồn động lực cho hành trình phát triển bản thân.',
        '<p class="lead">Trong hành trình học tập và tìm kiếm cơ hội nghề nghiệp, có những lúc chúng ta cần nguồn động lực để tiếp tục. Dưới đây là 50 câu quotes truyền cảm hứng sẽ giúp bạn vượt qua mọi thử thách.</p>

        <h2>💪 Quotes về thành công và động lực</h2>
        <img src="https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=600" alt="Success motivation" class="img-fluid my-3">
        
        <blockquote class="blockquote text-center bg-primary text-white p-4 mb-4">
            <p class="mb-0">"Thành công không phải là chìa khóa của hạnh phúc. Hạnh phúc chính là chìa khóa của thành công."</p>
            <footer class="blockquote-footer text-light">Albert Schweitzer</footer>
        </blockquote>

        <div class="row">
            <div class="col-md-6">
                <ul class="list-group">
                    <li class="list-group-item">"Cách duy nhất để thực hiện công việc tuyệt vời là yêu thích những gì bạn làm." - Steve Jobs</li>
                    <li class="list-group-item">"Thành công là tổng của những nỗ lực nhỏ được lặp đi lặp lại mỗi ngày." - Robert Collier</li>
                    <li class="list-group-item">"Đầu tư vào tri thức luôn mang lại lợi nhuận tốt nhất." - Benjamin Franklin</li>
                </ul>
            </div>
            <div class="col-md-6">
                <ul class="list-group">
                    <li class="list-group-item">"Bạn không cần phải vĩ đại để bắt đầu, nhưng bạn cần bắt đầu để trở nên vĩ đại." - Zig Ziglar</li>
                    <li class="list-group-item">"Cơ hội thường được ngụy trang dưới dạng công việc khó khăn." - Thomas Edison</li>
                    <li class="list-group-item">"Đừng để những gì bạn không thể làm cản trở những gì bạn có thể làm." - John Wooden</li>
                </ul>
            </div>
        </div>

        <h2>🎓 Quotes về học tập và phát triển</h2>
        <img src="https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=600" alt="Learning development" class="img-fluid my-3">
        
        <div class="alert alert-info">
            <h5>📚 Quotes về học tập:</h5>
            <ul>
                <li>"Giáo dục là vũ khí mạnh mẽ nhất để thay đổi thế giới." - Nelson Mandela</li>
                <li>"Hãy học như thể bạn sẽ sống mãi, hãy sống như thể bạn sẽ chết ngày mai." - Gandhi</li>
                <li>"Đầu tư vào bản thân là khoản đầu tư tốt nhất." - Warren Buffett</li>
            </ul>
        </div>

        <h2>💼 Quotes về sự nghiệp</h2>
        <blockquote class="blockquote text-center bg-success text-white p-4">
            <p class="mb-0">"Chọn công việc bạn yêu thích, và bạn sẽ không phải làm việc một ngày nào trong đời."</p>
            <footer class="blockquote-footer text-light">Confucius</footer>
        </blockquote>

        <div class="card-deck mb-4">
            <div class="card border-primary">
                <div class="card-body">
                    <p class="card-text">"Sự nghiệp của bạn là một doanh nghiệp. Hãy điều hành nó như vậy." - Cory Doctorow</p>
                </div>
            </div>
            <div class="card border-success">
                <div class="card-body">
                    <p class="card-text">"Đừng chờ đợi cơ hội. Hãy tạo ra chúng." - Roy T. Bennett</p>
                </div>
            </div>
            <div class="card border-warning">
                <div class="card-body">
                    <p class="card-text">"Thất bại chỉ là cơ hội để bắt đầu lại một cách thông minh hơn." - Henry Ford</p>
                </div>
            </div>
        </div>

        <h2>🔥 Quotes về kiên trì và vượt khó</h2>
        <img src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600" alt="Persistence motivation" class="img-fluid my-3">
        
        <table class="table table-striped">
            <tbody>
                <tr>
                    <td><strong>"Thành công không phải là cuối cùng, thất bại không phải là tận cùng: chính can đảm tiếp tục là điều quan trọng."</strong> - Winston Churchill</td>
                </tr>
                <tr>
                    <td><strong>"Khó khăn lớn nhất không phải là ngã xuống, mà là đứng dậy."</strong> - Vince Lombardi</td>
                </tr>
                <tr>
                    <td><strong>"Mọi chuyên gia đều từng là người mới bắt đầu. Mọi chuyên nghiệp đều từng là nghiệp dư."</strong> - Robin Sharma</td>
                </tr>
                <tr>
                    <td><strong>"Đừng từ bỏ. Khổ cực mà bạn đang trải qua ngày hôm nay đang phát triển sức mạnh mà bạn cần cho ngày mai."</strong> - Robert Tew</td>
                </tr>
            </tbody>
        </table>

        <h2>🤝 Quotes về lãnh đạo và teamwork</h2>
        <div class="alert alert-primary">
            <h5>🌟 Quotes về làm việc nhóm và lãnh đạo:</h5>
            <ul>
                <li>"Một mình chúng ta có thể làm được rất ít; cùng nhau chúng ta có thể làm được rất nhiều." - Helen Keller</li>
                <li>"Lãnh đạo không phải là về việc kiểm soát người khác. Đó là về việc làm gương để họ theo." - Simon Sinek</li>
                <li>"Nhóm tốt nhất không phải là nhóm có những người tài năng nhất, mà là nhóm có những người làm việc tốt nhất với nhau." - John C. Maxwell</li>
                <li>"Sức mạnh nằm trong sự khác biệt, không phải trong sự giống nhau." - Stephen Covey</li>
            </ul>
        </div>

        <h2>📱 Áp dụng quotes vào cuộc sống</h2>
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-light border-primary">
                    <h5 class="text-primary">🎯 Cách sử dụng quotes hiệu quả:</h5>
                    <ul>
                        <li><strong>Viết ra giấy:</strong> Ghi quotes yêu thích lên sticky notes dán màn hình</li>
                        <li><strong>Wallpaper động lực:</strong> Đặt làm hình nền điện thoại/laptop</li>
                        <li><strong>Chia sẻ tích cực:</strong> Post trên mạng xã hội để truyền cảm hứng</li>
                        <li><strong>Ritual buổi sáng:</strong> Đọc 1-2 câu quotes khi thức dậy</li>
                        <li><strong>Suy ngẫm sâu:</strong> Áp dụng thông điệp vào công việc thực tế</li>
                        <li><strong>Chia sẻ với bạn bè:</strong> Tạo nhóm động lực cùng nhau</li>
                    </ul>
                </div>
            </div>
        </div>

        <h2>💎 Kết luận</h2>
        <div class="card bg-dark text-white">
            <div class="card-body text-center">
                <h5 class="card-title">Hãy biến những quotes này thành động lực hành động</h5>
                <p class="card-text">Những câu quotes trên không chỉ là lời khuyên, mà còn là kim chỉ nam cho hành trình phát triển bản thân và sự nghiệp của bạn. Hãy chọn ra những câu phù hợp nhất và biến chúng thành động lực hành động mỗi ngày.</p>
                <p class="card-text"><strong>Nhớ rằng: "Hành trình nghìn dặm bắt đầu từ một bước chân." - Lão Tử</strong></p>
            </div>
        </div>',
        'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800',
        'PUBLISHED',
        NOW(),
        NOW(),
        NOW(),
        'Tuyển tập 50 câu quotes truyền cảm hứng mạnh mẽ cho sinh viên và người tìm việc về thành công, học tập, sự nghiệp và kiên trì.');

-- Blog images cho bài Quotes
INSERT INTO blog_image (blog_post_id, image_url, alt_text, caption, display_order, image_type)
VALUES 
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800', 'Inspirational quotes', 'Quotes truyền cảm hứng cho sinh viên', 0, 'FEATURED'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=600', 'Success motivation', 'Quotes về thành công và động lực', 1, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=600', 'Learning development', 'Quotes về học tập và phát triển', 2, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600', 'Persistence motivation', 'Quotes về kiên trì và vượt khó', 3, 'INLINE');

-- =======================================================================
-- 4. CÁCH VIẾT EMAIL XIN VIỆC (RESOURCE: APPLICATION_TIPS)
-- =======================================================================

INSERT INTO resource (resource_title, resource_content, image_url, resource_type, created_by, created_at, updated_at) 
VALUES ('Cách viết email xin việc chuyên nghiệp và hiệu quả', 
        'Hướng dẫn chi tiết cách viết email xin việc từ A-Z: tiêu đề email, cấu trúc nội dung, cách đính kèm CV và những lỗi thường gặp cần tránh. Bao gồm mẫu email chuẩn và checklist hoàn chỉnh.', 
        'https://images.unsplash.com/photo-1596526131083-e8c633c948d2?w=800', 
        'application_tips', 
        1, 
        NOW(), 
        NOW());

INSERT INTO blog_post (resource_id, title, summary, content, featured_image_url, status, created_at, updated_at, published_at, meta_description)
VALUES (LAST_INSERT_ID(), 
        'Cách viết email xin việc chuyên nghiệp và hiệu quả',
        'Email xin việc là bước đầu tiên để tạo ấn tượng với nhà tuyển dụng. Học cách viết email chuyên nghiệp sẽ giúp bạn tăng cơ hội được mời phỏng vấn và chinh phục vị trí mơ ước.',
        '<p class="lead">Email xin việc đóng vai trò quan trọng trong việc tạo ấn tượng đầu tiên. Một email được viết tốt có thể mở ra cánh cửa cơ hội.</p>

        <h2>📧 Cấu trúc email chuẩn</h2>
        <img src="https://images.unsplash.com/photo-1596526131083-e8c633c948d2?w=600" alt="Professional email" class="img-fluid my-3">
        
        <div class="alert alert-info">
            <h5>🎯 5 thành phần cốt lõi:</h5>
            <ol>
                <li>Tiêu đề email</li>
                <li>Lời chào</li>
                <li>Thân email</li>
                <li>Lời kết</li>
                <li>Đính kèm CV</li>
            </ol>
        </div>

        <h2>✍️ Cách viết tiêu đề email hiệu quả</h2>
        <table class="table table-bordered">
            <thead class="thead-dark">
                <tr>
                    <th>✅ Tiêu đề TỐT</th>
                    <th>❌ Tiêu đề TRÁNH</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Ứng tuyển vị trí Marketing Executive - Nguyễn Văn A</td>
                    <td>Xin việc</td>
                </tr>
                <tr>
                    <td>Application for Software Developer Position - ID: JOB001</td>
                    <td>CV của tôi</td>
                </tr>
                <tr>
                    <td>Ứng tuyển [Tên vị trí] - [Họ tên] - [Kinh nghiệm]</td>
                    <td>Tìm việc làm</td>
                </tr>
            </tbody>
        </table>

        <h2>📝 Mẫu email chuyên nghiệp đầy đủ</h2>
        <div class="card border-primary">
            <div class="card-header bg-primary text-white">
                <h6 class="mb-0">📧 Email template chuẩn</h6>
            </div>
            <div class="card-body">
                <p><strong>Subject:</strong> Ứng tuyển vị trí Marketing Executive - Nguyễn Văn A - 3 năm kinh nghiệm</p>
                <hr>
                <p><strong>Kính gửi:</strong> Ban Tuyển dụng/ Anh/Chị [Tên người nhận]</p>
                
                <p>Em tên là Nguyễn Văn A, hiện đang ứng tuyển vị trí <strong>Marketing Executive</strong> tại [Tên công ty] theo thông tin tuyển dụng trên [nguồn thông tin].</p>
                
                <p>Với <strong>3 năm kinh nghiệm</strong> trong lĩnh vực Digital Marketing, em đã:</p>
                <ul>
                    <li>Quản lý thành công các chiến dịch quảng cáo với ngân sách 500 triệu đồng</li>
                    <li>Tăng ROI lên 150% cho 5 dự án lớn</li>
                    <li>Có kinh nghiệm với Google Ads, Facebook Ads, SEO/SEM</li>
                </ul>
                
                <p>Em tin rằng kinh nghiệm và kỹ năng của mình phù hợp với yêu cầu công việc. Em rất mong được cơ hội trình bày chi tiết hơn trong buổi phỏng vấn.</p>
                
                <p>Em đã đính kèm CV và các tài liệu liên quan. Mong nhận được phản hồi từ Anh/Chị.</p>
                
                <p><strong>Trân trọng,</strong><br>
                Nguyễn Văn A<br>
                SĐT: 0987654321<br>
                Email: vana@email.com</p>
            </div>
        </div>

        <h2>⚠️ Những lỗi thường gặp cần tránh</h2>
        <img src="https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=600" alt="Email mistakes" class="img-fluid my-3">
        
        <div class="row">
            <div class="col-md-6">
                <div class="card border-danger">
                    <div class="card-header bg-danger text-white">
                        <h6 class="mb-0">❌ KHÔNG NÊN</h6>
                    </div>
                    <div class="card-body">
                        <ul>
                            <li>Viết tắt quá nhiều (u, vs, k...)</li>
                            <li>Sử dụng emoji trong email chính thức</li>
                            <li>Quên đính kèm CV</li>
                            <li>Email quá dài (> 300 từ)</li>
                            <li>Không ghi rõ vị trí ứng tuyển</li>
                            <li>Copy-paste template mà không chỉnh sửa</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-success">
                    <div class="card-header bg-success text-white">
                        <h6 class="mb-0">✅ NÊN LÀM</h6>
                    </div>
                    <div class="card-body">
                        <ul>
                            <li>Sử dụng địa chỉ email chuyên nghiệp</li>
                            <li>Kiểm tra chính tả và ngữ pháp</li>
                            <li>Tùy chỉnh nội dung theo từng công ty</li>
                            <li>Đề cập 2-3 thành tích nổi bật</li>
                            <li>Ghi rõ thông tin liên lạc</li>
                            <li>Gửi email trong giờ hành chính</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <h2>📎 Hướng dẫn đính kèm tài liệu</h2>
        <div class="alert alert-warning">
            <h5>📋 Checklist đính kèm:</h5>
            <ul class="mb-0">
                <li><strong>CV:</strong> Định dạng PDF, tên file: HoTen_CV_ViTri.pdf</li>
                <li><strong>Cover letter:</strong> Nếu yêu cầu cụ thể</li>
                <li><strong>Portfolio:</strong> Cho vị trí design, marketing</li>
                <li><strong>Bằng cấp:</strong> Scan rõ nét nếu cần thiết</li>
                <li><strong>Dung lượng:</strong> Tổng cộng < 10MB</li>
                <li><strong>Tên file:</strong> Rõ ràng, không ký tự đặc biệt</li>
            </ul>
        </div>

        <h2>🎯 Tips quan trọng</h2>
        <div class="card border-info">
            <div class="card-body">
                <h5 class="card-title text-info">💡 Bí quyết email thành công:</h5>
                <ul>
                    <li><strong>Thời gian gửi:</strong> 8h-11h hoặc 13h-16h các ngày trong tuần</li>
                    <li><strong>Follow up:</strong> Gửi email nhắc nhở sau 1 tuần nếu chưa có phản hồi</li>
                    <li><strong>Cá nhân hóa:</strong> Tìm hiểu tên người nhận và công ty</li>
                    <li><strong>Call to action:</strong> Kết thúc với lời mời phỏng vấn rõ ràng</li>
                </ul>
            </div>
        </div>',
        'https://images.unsplash.com/photo-1596526131083-e8c633c948d2?w=800',
        'PUBLISHED',
        NOW(),
        NOW(),
        NOW(),
        'Hướng dẫn chi tiết cách viết email xin việc chuyên nghiệp từ A-Z: tiêu đề, cấu trúc, mẫu email và tips thành công.');

-- Blog images cho bài Email xin việc
INSERT INTO blog_image (blog_post_id, image_url, alt_text, caption, display_order, image_type)
VALUES 
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1596526131083-e8c633c948d2?w=800', 'Professional email', 'Email xin việc chuyên nghiệp', 0, 'FEATURED'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1596526131083-e8c633c948d2?w=600', 'Professional email', 'Cấu trúc email chuẩn', 1, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=600', 'Email mistakes', 'Những lỗi thường gặp cần tránh', 2, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=600', 'Email tips', 'Tips viết email hiệu quả', 3, 'INLINE');

-- =======================================================================
-- 5. PHỎNG VẤN QUA VIDEO CALL (RESOURCE: INTERVIEW_GUIDE)
-- =======================================================================

INSERT INTO resource (resource_title, resource_content, image_url, resource_type, created_by, created_at, updated_at) 
VALUES ('Bí quyết phỏng vấn qua video call thành công', 
        'Hướng dẫn chi tiết cách chuẩn bị và thực hiện phỏng vấn online hiệu quả: thiết bị kỹ thuật, môi trường phỏng vấn, trang phục phù hợp và kỹ năng giao tiếp qua camera.', 
        'https://images.unsplash.com/photo-1587440871875-191322ee64b0?w=800', 
        'interview_guide', 
        1, 
        NOW(), 
        NOW());

INSERT INTO blog_post (resource_id, title, summary, content, featured_image_url, status, created_at, updated_at, published_at, meta_description)
VALUES (LAST_INSERT_ID(), 
        'Bí quyết phỏng vấn qua video call thành công',
        'Phỏng vấn online đang trở thành xu hướng phổ biến. Tìm hiểu các bí quyết chuẩn bị kỹ thuật, môi trường và kỹ năng giao tiếp để gây ấn tượng tốt qua màn hình.',
        '<p class="lead">Phỏng vấn qua video call đã trở thành xu hướng phổ biến. Việc thành công đòi hỏi sự chuẩn bị kỹ lưỡng về kỹ thuật lẫn kỹ năng.</p>

        <h2>🖥️ Chuẩn bị kỹ thuật</h2>
        <img src="https://images.unsplash.com/photo-1587440871875-191322ee64b0?w=600" alt="Video call setup" class="img-fluid my-3">
        
        <div class="alert alert-success">
            <h5>✅ Checklist kỹ thuật:</h5>
            <ul>
                <li>Kiểm tra camera và micro</li>
                <li>Internet ổn định (>10Mbps)</li>
                <li>Có plan B dự phòng</li>
                <li>Sạc đầy pin thiết bị</li>
                <li>Test ứng dụng video call</li>
            </ul>
        </div>

        <h2>🏠 Thiết lập môi trường phỏng vấn</h2>
        <div class="row">
            <div class="col-md-6">
                <h5>📱 Góc camera và ánh sáng:</h5>
                <ul>
                    <li>Camera ngang tầm mắt (không nhìn xuống/lên)</li>
                    <li>Ánh sáng tự nhiên chiếu từ phía trước</li>
                    <li>Background đơn giản, sạch sẽ (tường trắng/neutral)</li>
                    <li>Không có yếu tố gây xao nhãng phía sau</li>
                    <li>Khoảng cách camera 60-90cm</li>
                </ul>
            </div>
            <div class="col-md-6">
                <h5>🔇 Âm thanh và không gian:</h5>
                <ul>
                    <li>Chọn không gian yên tĩnh nhất trong nhà</li>
                    <li>Thông báo gia đình không làm ồn trong 1-2 giờ</li>
                    <li>Tắt tất cả notification điện thoại/máy tính</li>
                    <li>Chuẩn bị sẵn tai nghe có micro dự phòng</li>
                    <li>Test âm thanh với bạn bè trước 1 ngày</li>
                </ul>
            </div>
        </div>

        <h2>👔 Trang phục và hình ảnh chuyên nghiệp</h2>
        <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600" alt="Professional attire" class="img-fluid my-3">
        
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Yếu tố</th>
                    <th>Nên làm</th>
                    <th>Tránh</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Màu sắc trang phục</strong></td>
                    <td>Màu trơn: xanh navy, xám, trắng</td>
                    <td>Họa tiết phức tạp, kẻ sọc nhỏ</td>
                </tr>
                <tr>
                    <td><strong>Kiểu dáng</strong></td>
                    <td>Ăn mặc đầy đủ như phỏng vấn trực tiếp</td>
                    <td>Chỉ mặc áo đẹp, dưới quần đùi</td>
                </tr>
                <tr>
                    <td><strong>Makeup/Grooming</strong></td>
                    <td>Trang điểm nhẹ, tóc gọn gàng</td>
                    <td>Quá đậm hoặc hoàn toàn không trang điểm</td>
                </tr>
                <tr>
                    <td><strong>Phụ kiện</strong></td>
                    <td>Tối giản, không phản quang</td>
                    <td>Trang sức lớn, phản sáng</td>
                </tr>
            </tbody>
        </table>

        <h2>💬 Kỹ năng giao tiếp online hiệu quả</h2>
        <img src="https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=600" alt="Online communication" class="img-fluid my-3">
        
        <div class="alert alert-info">
            <h5>🗣️ Nguyên tắc giao tiếp qua camera:</h5>
            <ul>
                <li><strong>Eye contact:</strong> Nhìn vào camera (không nhìn vào màn hình) khi trả lời</li>
                <li><strong>Tốc độ nói:</strong> Chậm hơn 10-15% so với bình thường</li>
                <li><strong>Ngôn ngữ cơ thể:</strong> Ngồi thẳng, tay đặt tự nhiên, mỉm cười</li>
                <li><strong>Tạm dừng:</strong> Chờ 1-2 giây sau khi nhà tuyển dụng nói xong</li>
                <li><strong>Chuẩn bị notes:</strong> Ghi sẵn câu hỏi và điểm chính bên cạnh màn hình</li>
                <li><strong>Backup plan:</strong> Có số điện thoại để gọi nếu mất kết nối</li>
            </ul>
        </div>

        <h2>📋 Checklist chuẩn bị 24h trước phỏng vấn</h2>
        <div class="row">
            <div class="col-md-6">
                <div class="card border-warning">
                    <div class="card-header bg-warning text-dark">
                        <h6>⏰ 1 NGÀY TRƯỚC</h6>
                    </div>
                    <div class="card-body">
                        <ul class="small">
                            <li>Test đầy đủ thiết bị và mạng</li>
                            <li>Tải và login app phỏng vấn</li>
                            <li>Chuẩn bị câu hỏi cho nhà tuyển dụng</li>
                            <li>In CV và tài liệu tham khảo</li>
                            <li>Luyện tập với bạn bè qua video call</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-success">
                    <div class="card-header bg-success text-white">
                        <h6>🕐 30 PHÚT TRƯỚC</h6>
                    </div>
                    <div class="card-body">
                        <ul class="small">
                            <li>Kiểm tra camera, micro, mạng lần cuối</li>
                            <li>Ăn mặc hoàn thiện, kiểm tra appearance</li>
                            <li>Chuẩn bị nước uống và khăn giấy</li>
                            <li>Tắt tất cả app không cần thiết</li>
                            <li>Thông báo gia đình/bạn cùng phòng</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <h2>🚨 Xử lý sự cố thường gặp</h2>
        <table class="table table-bordered">
            <thead class="thead-light">
                <tr>
                    <th>Tình huống</th>
                    <th>Giải pháp</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>🌐 Mất kết nối internet</td>
                    <td>Gọi ngay số điện thoại đã chuẩn bị, xin lỗi và hẹn thời gian khác</td>
                </tr>
                <tr>
                    <td>🔇 Lỗi micro/loa</td>
                    <td>Chuyển sang tai nghe dự phòng hoặc điện thoại</td>
                </tr>
                <tr>
                    <td>📱 App không hoạt động</td>
                    <td>Có sẵn app backup (Zoom, Teams, Skype) hoặc dùng web browser</td>
                </tr>
                <tr>
                    <td>😨 Nervous/lo lắng</td>
                    <td>Thở sâu, uống nước, nhớ rằng đây chỉ là cuộc trò chuyện</td>
                </tr>
            </tbody>
        </table>',
        'https://images.unsplash.com/photo-1587440871875-191322ee64b0?w=800',
        'PUBLISHED',
        NOW(),
        NOW(),
        NOW(),
        'Hướng dẫn chi tiết cách chuẩn bị và thực hiện phỏng vấn online thành công: kỹ thuật, môi trường, trang phục và giao tiếp.');

-- Blog images cho bài Phỏng vấn video call
INSERT INTO blog_image (blog_post_id, image_url, alt_text, caption, display_order, image_type)
VALUES 
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1587440871875-191322ee64b0?w=800', 'Video call interview', 'Phỏng vấn qua video call thành công', 0, 'FEATURED'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1587440871875-191322ee64b0?w=600', 'Video call setup', 'Thiết lập kỹ thuật cho phỏng vấn online', 1, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600', 'Professional attire', 'Trang phục chuyên nghiệp cho video call', 2, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=600', 'Online communication', 'Kỹ năng giao tiếp qua camera', 3, 'INLINE');

-- =======================================================================
-- 5 BÀI BLOG POSTS CÒN LẠI - PHẦN 2: KỸ NĂNG MỀM VÀ QUOTES THÀNH CÔNG  
-- =======================================================================

-- =======================================================================
-- 6. KỸ NĂNG MỀM CHO SINH VIÊN (RESOURCE: APPLICATION_TIPS)
-- =======================================================================

INSERT INTO resource (resource_title, resource_content, image_url, resource_type, created_by, created_at, updated_at) 
VALUES ('Những kỹ năng mềm thiết yếu mà sinh viên cần phát triển', 
        'Tổng hợp các kỹ năng mềm quan trọng nhất mà nhà tuyển dụng tìm kiếm ứng viên trẻ: giao tiếp, làm việc nhóm, tư duy phê phán, quản lý thời gian và lãnh đạo, cùng cách phát triển hiệu quả.', 
        'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800', 
        'application_tips', 
        1, 
        NOW(), 
        NOW());

INSERT INTO blog_post (resource_id, title, summary, content, featured_image_url, status, created_at, updated_at, published_at, meta_description)
VALUES (LAST_INSERT_ID(), 
        'Những kỹ năng mềm thiết yếu mà sinh viên cần phát triển',
        'Khám phá 10 kỹ năng mềm quan trọng nhất mà nhà tuyển dụng đánh giá cao, cùng với hướng dẫn cụ thể để phát triển từng kỹ năng một cách hiệu quả và bền vững.',
        '<p class="lead">Trong thị trường lao động hiện đại, kỹ năng mềm đóng vai trò quan trọng không kém kỹ năng chuyên môn.</p>

        <h2>💡 Top 10 kỹ năng mềm quan trọng</h2>
        <img src="https://images.unsplash.com/photo-1552664730-d307ca884978?w=600" alt="Soft skills" class="img-fluid my-3">
        
        <div class="row">
            <div class="col-md-6">
                <div class="card border-primary mb-3">
                    <div class="card-header bg-primary text-white">
                        <h6 class="mb-0">🗣️ Kỹ năng giao tiếp</h6>
                    </div>
                    <div class="card-body">
                        <p>Khả năng truyền đạt ý tưởng rõ ràng, lắng nghe hiệu quả và thích ứng với nhiều đối tượng khác nhau.</p>
                        <p><strong>Cách phát triển:</strong> Tham gia CLB tranh biện, thuyết trình, viết blog</p>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-success mb-3">
                    <div class="card-header bg-success text-white">
                        <h6 class="mb-0">🤝 Làm việc nhóm</h6>
                    </div>
                    <div class="card-body">
                        <p>Khả năng hợp tác hiệu quả, chia sẻ trách nhiệm và đóng góp tích cực vào mục tiêu chung.</p>
                        <p><strong>Cách phát triển:</strong> Tham gia dự án nhóm, hoạt động tình nguyện</p>
                    </div>
                </div>
            </div>
        </div>

        <h2>🧠 Tư duy phê phán và giải quyết vấn đề</h2>
        <img src="https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=600" alt="Critical thinking" class="img-fluid my-3">
        
        <div class="alert alert-warning">
            <h5>🔍 Phát triển tư duy phê phán:</h5>
            <ul>
                <li>Đặt câu hỏi "Tại sao?" và "Làm thế nào?" cho mọi vấn đề</li>
                <li>Phân tích thông tin từ nhiều nguồn khác nhau</li>
                <li>Thực hành case study và tình huống thực tế</li>
                <li>Tham gia thảo luận và tranh luận có văn hóa</li>
                <li>Đọc sách, báo từ nhiều quan điểm khác nhau</li>
            </ul>
        </div>

        <h2>⏰ Quản lý thời gian hiệu quả</h2>
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Kỹ thuật</th>
                    <th>Mô tả chi tiết</th>
                    <th>Ứng dụng thực tế</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Pomodoro Technique</strong></td>
                    <td>Làm việc tập trung 25 phút, nghỉ 5 phút</td>
                    <td>Học tập, làm bài tập, research</td>
                </tr>
                <tr>
                    <td><strong>Time Blocking</strong></td>
                    <td>Chia thời gian thành các khối cụ thể cho từng công việc</td>
                    <td>Lập kế hoạch tuần, tháng</td>
                </tr>
                <tr>
                    <td><strong>Eisenhower Matrix</strong></td>
                    <td>Phân loại công việc: Khẩn cấp-Quan trọng</td>
                    <td>Quản lý deadline, ưu tiên task</td>
                </tr>
                <tr>
                    <td><strong>Getting Things Done (GTD)</strong></td>
                    <td>Ghi chép mọi việc vào hệ thống, xử lý theo quy trình</td>
                    <td>Quản lý project lớn, nhiều task</td>
                </tr>
            </tbody>
        </table>

        <h2>🎯 Lãnh đạo và ảnh hưởng</h2>
        <img src="https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=600" alt="Leadership skills" class="img-fluid my-3">
        
        <p>Kỹ năng lãnh đạo không chỉ dành cho quản lý mà còn cần thiết cho mọi vị trí công việc hiện đại.</p>
        
        <div class="row">
            <div class="col-md-4">
                <h6>💡 Khả năng truyền cảm hứng</h6>
                <ul>
                    <li>Tạo động lực cho người khác</li>
                    <li>Chia sẻ tầm nhìn rõ ràng</li>
                    <li>Động viên trong lúc khó khăn</li>
                </ul>
            </div>
            <div class="col-md-4">
                <h6>⚡ Ra quyết định</h6>
                <ul>
                    <li>Đưa ra lựa chọn đúng đắn dưới áp lực</li>
                    <li>Cân nhắc nhiều yếu tố</li>
                    <li>Chịu trách nhiệm về quyết định</li>
                </ul>
            </div>
            <div class="col-md-4">
                <h6>🤝 Giải quyết xung đột</h6>
                <ul>
                    <li>Hòa giải các bên liên quan</li>
                    <li>Tìm giải pháp win-win</li>
                    <li>Duy trì mối quan hệ tích cực</li>
                </ul>
            </div>
        </div>

        <h2>📚 Học hỏi liên tục (Growth Mindset)</h2>
        <div class="alert alert-info">
            <p><strong>Growth mindset</strong> là chìa khóa thành công trong thời đại thay đổi nhanh chóng này. Hãy:</p>
            <ul class="mb-0">
                <li><strong>Luôn tò mò:</strong> Đặt câu hỏi và tìm hiểu về mọi thứ xung quanh</li>
                <li><strong>Học từ thất bại:</strong> Xem thất bại là cơ hội học hỏi, không phải kết thúc</li>
                <li><strong>Nhận phản hồi:</strong> Lắng nghe và áp dụng feedback một cách tích cực</li>
                <li><strong>Cập nhật kiến thức:</strong> Theo dõi xu hướng ngành và công nghệ mới</li>
                <li><strong>Networking:</strong> Học hỏi từ những người xung quanh</li>
                <li><strong>Đọc sách:</strong> Ít nhất 1 cuốn/tháng về chuyên môn hoặc kỹ năng</li>
            </ul>
        </div>

        <h2>🔥 5 kỹ năng mềm khác cần phát triển</h2>
        <table class="table table-hover">
            <tbody>
                <tr>
                    <td><strong>🎨 Sáng tạo và đổi mới</strong></td>
                    <td>Khả năng tư duy outside the box, đưa ra ý tưởng mới</td>
                    <td>Brainstorming, mind mapping, design thinking</td>
                </tr>
                <tr>
                    <td><strong>🔄 Thích ứng và linh hoạt</strong></td>
                    <td>Khả năng thay đổi khi môi trường thay đổi</td>
                    <td>Học công nghệ mới, thử nghiệm vai trò khác</td>
                </tr>
                <tr>
                    <td><strong>😊 Trí tuệ cảm xúc (EQ)</strong></td>
                    <td>Hiểu và quản lý cảm xúc bản thân và người khác</td>
                    <td>Thực hành mindfulness, đọc sách tâm lý</td>
                </tr>
                <tr>
                    <td><strong>🎯 Định hướng mục tiêu</strong></td>
                    <td>Đặt và theo đuổi mục tiêu rõ ràng, có kế hoạch</td>
                    <td>SMART goals, OKR, personal planning</td>
                </tr>
                <tr>
                    <td><strong>🌐 Tư duy toàn cầu</strong></td>
                    <td>Hiểu biết về văn hóa, thị trường quốc tế</td>
                    <td>Học ngoại ngữ, làm việc với team đa quốc gia</td>
                </tr>
            </tbody>
        </table>',
        'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800',
        'PUBLISHED',
        NOW(),
        NOW(),
        NOW(),
        'Tổng hợp 10 kỹ năng mềm thiết yếu cho sinh viên: giao tiếp, teamwork, tư duy phê phán, quản lý thời gian và lãnh đạo.');

-- Blog images cho bài Kỹ năng mềm
INSERT INTO blog_image (blog_post_id, image_url, alt_text, caption, display_order, image_type)
VALUES 
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800', 'Soft skills development', 'Phát triển kỹ năng mềm cho sinh viên', 0, 'FEATURED'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=600', 'Soft skills', 'Top 10 kỹ năng mềm quan trọng', 1, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=600', 'Critical thinking', 'Tư duy phê phán và giải quyết vấn đề', 2, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=600', 'Leadership skills', 'Kỹ năng lãnh đạo và ảnh hưởng', 3, 'INLINE');

-- =======================================================================
-- 7. QUOTES ĐỘNG LỰC THÀNH CÔNG (RESOURCE: QUOTES)
-- =======================================================================

INSERT INTO resource (resource_title, resource_content, image_url, resource_type, created_by, created_at, updated_at) 
VALUES ('Quotes động lực cho hành trình thành công của bạn', 
        'Bộ sưu tập quotes truyền cảm hứng về thành công, kiên trì và vượt qua thử thách từ những nhân vật nổi tiếng thế giới như Elon Musk, Steve Jobs, Bill Gates và các CEO thành đạt khác.', 
        'https://i.pinimg.com/736x/1d/b8/b2/1db8b2fed861f2999767baa7b52312d8.jpg', 
        'quotes', 
        1, 
        NOW(), 
        NOW());

INSERT INTO blog_post (resource_id, title, summary, content, featured_image_url, status, created_at, updated_at, published_at, meta_description)
VALUES (LAST_INSERT_ID(), 
        'Quotes động lực cho hành trình thành công của bạn',
        '30 câu quotes truyền cảm hứng mạnh mẽ từ các nhân vật thành đạt sẽ tiếp thêm động lực cho hành trình chinh phục mục tiêu và vượt qua mọi thử thách trong cuộc sống.',
        '<p class="lead">Trên con đường theo đuổi thành công, chúng ta đều cần những nguồn động lực để tiếp tục bước đi.</p>

        <h2>🌟 Quotes về thành công</h2>
        <img src="https://i.pinimg.com/736x/d9/3c/f0/d93cf029b8dad26cfaf705764fee7c20.jpg" alt="Success quotes" class="img-fluid my-3">
        
        <blockquote class="blockquote text-center bg-primary text-white p-5">
            <p class="mb-0 h4">"Thành công không phải là điểm cuối. Thất bại không phải là tận cùng. Điều quan trọng là can đảm để tiếp tục."</p>
            <footer class="blockquote-footer text-light">Winston Churchill</footer>
        </blockquote>

        <div class="row">
            <div class="col-md-6">
                <h6 class="text-primary">💎 Elon Musk</h6>
                <p>"Khi điều gì đó đủ quan trọng, bạn làm nó ngay cả khi tỷ lệ thành công không đứng về phía bạn."</p>
                <h6 class="text-success">🚀 Jeff Bezos</h6>
                <p>"Tôi biết rằng nếu tôi thất bại, tôi sẽ không hối tiếc, nhưng tôi biết điều duy nhất tôi có thể hối tiếc là không thử."</p>
            </div>
            <div class="col-md-6">
                <h6 class="text-warning">⚡ Bill Gates</h6>
                <p>"Thành công là một người thầy tồi tệ. Nó dụ dỗ những con người thông minh nghĩ rằng họ không thể thất bại."</p>
                <h6 class="text-info">🎯 Steve Jobs</h6>
                <p>"Sự đổi mới phân biệt giữa người dẫn đầu và người theo sau."</p>
            </div>
        </div>

        <h2>🔥 Quotes về startup và kinh doanh</h2>
        <img src="https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=600" alt="Business quotes" class="img-fluid my-3">
        
        <div class="card-deck mb-4">
            <div class="card border-primary">
                <div class="card-header bg-primary text-white text-center">
                    <h6 class="mb-0">💼 Mark Cuban</h6>
                </div>
                <div class="card-body">
                    <p class="card-text text-center">"Điều duy nhất chắc chắn trong kinh doanh là không có gì là chắc chắn."</p>
                </div>
            </div>
            <div class="card border-success">
                <div class="card-header bg-success text-white text-center">
                    <h6 class="mb-0">🎯 Reid Hoffman</h6>
                </div>
                <div class="card-body">
                    <p class="card-text text-center">"Bắt đầu một công ty giống như nhảy xuống vách đá và lắp ráp máy bay trên đường xuống."</p>
                </div>
            </div>
            <div class="card border-warning">
                <div class="card-header bg-warning text-dark text-center">
                    <h6 class="mb-0">💡 Richard Branson</h6>
                </div>
                <div class="card-body">
                    <p class="card-text text-center">"Kinh doanh cơ hội để có tác động tích cực, và cuộc vui sẽ theo sau."</p>
                </div>
            </div>
        </div>

        <h2>🧠 Quotes về tư duy và học hỏi</h2>
        <table class="table table-striped">
            <tbody>
                <tr>
                    <td width="20%"><strong>Albert Einstein</strong></td>
                    <td>"Trí tuệ không phải là sản phẩm của sự giáo dục, mà là nỗ lực suốt đời để có được nó."</td>
                </tr>
                <tr>
                    <td><strong>Maya Angelou</strong></td>
                    <td>"Khi chúng ta biết nhiều hơn, chúng ta làm tốt hơn."</td>
                </tr>
                <tr>
                    <td><strong>Carol Dweck</strong></td>
                    <td>"Trở thành là quan trọng hơn việc trở thành. Becoming is better than being."</td>
                </tr>
                <tr>
                    <td><strong>John Dewey</strong></td>
                    <td>"Chúng ta không học từ kinh nghiệm... chúng ta học từ việc suy ngẫm về kinh nghiệm."</td>
                </tr>
                <tr>
                    <td><strong>Peter Drucker</strong></td>
                    <td>"Cách tốt nhất để dự đoán tương lai là tạo ra nó."</td>
                </tr>
            </tbody>
        </table>

        <h2>💪 Quotes về vượt qua khó khăn</h2>
        <img src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600" alt="Overcoming challenges" class="img-fluid my-3">
        
        <blockquote class="blockquote text-center bg-dark text-white p-5 my-4">
            <p class="mb-0 h5">"Khó khăn không đến để phá hủy bạn, mà để giúp bạn nhận ra rằng bạn mạnh mẽ đến mức nào."</p>
            <footer class="blockquote-footer text-light">Haruki Murakami</footer>
        </blockquote>

        <div class="alert alert-primary">
            <h5>💡 Quotes cho những lúc khó khăn:</h5>
            <ul>
                <li><strong>Thomas Edison:</strong> "Tôi không thất bại. Tôi chỉ tìm ra 10,000 cách không làm việc."</li>
                <li><strong>Michael Jordan:</strong> "Tôi đã thất bại hơn và hơn nữa trong cuộc đời. Và đó chính là lý do tôi thành công."</li>
                <li><strong>Oprah Winfrey:</strong> "Thử thách lớn nhất trong cuộc sống là trở thành chính mình trong một thế giới luôn cố gắng biến bạn thành người khác."</li>
                <li><strong>Nelson Mandela:</strong> "Không có passion để sống cuộc sống nhỏ bé và thu mình lại."</li>
            </ul>
        </div>

        <h2>🌟 Quotes về đam mê và mục đích</h2>
        <div class="row">
            <div class="col-md-6">
                <div class="card border-danger">
                    <div class="card-body">
                        <h6 class="text-danger">🔥 Gary Vaynerchuk</h6>
                        <p>"Không có lối tắt. KHÔNG. KHÔNG CÓ. Làm việc chăm chỉ, kiên trì và có một chút may mắn."</p>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-info">
                    <div class="card-body">
                        <h6 class="text-info">💎 Warren Buffett</h6>
                        <p>"Ai đó đang ngồi dưới bóng mát hôm nay vì ai đó đã trồng cây từ lâu."</p>
                    </div>
                </div>
            </div>
        </div>

        <h2>🎯 Áp dụng quotes vào cuộc sống</h2>
        <div class="alert alert-success">
            <h5 class="text-success">🌟 3 bước biến quotes thành hành động:</h5>
            <ol>
                <li><strong>Chọn quotes phù hợp:</strong> Tìm 3-5 câu quotes phù hợp với mục tiêu hiện tại</li>
                <li><strong>Áp dụng thực tế:</strong> Đặt câu hỏi "Làm thế nào tôi có thể áp dụng điều này hôm nay?"</li>
                <li><strong>Theo dõi tiến bộ:</strong> Review hàng tuần xem đã thực hiện được điều gì</li>
            </ol>
        </div>

        <div class="card bg-gradient text-white" style="background: linear-gradient(45deg, #667eea 0%, #764ba2 100%);">
            <div class="card-body text-center">
                <h5>🏆 Lời kết</h5>
                <p class="card-text">Thành công không phải là điểm đến mà là hành trình. Những quotes này không chỉ là lời khuyên mà còn là kim chỉ nam cho mỗi bước đi trong sự nghiệp và cuộc sống của bạn.</p>
                <p class="card-text"><strong>"Your only limit is your mind. Go beyond and achieve greatness!"</strong></p>
            </div>
        </div>',
        'https://images.unsplash.com/photo-1540500898170-85fb8a77ce57?w=800',
        'PUBLISHED',
        NOW(),
        NOW(),
        NOW(),
        'Bộ sưu tập 30 quotes động lực từ Elon Musk, Steve Jobs, Bill Gates về thành công, kiên trì và vượt qua thử thách.');

-- Blog images cho bài Quotes thành công
INSERT INTO blog_image (blog_post_id, image_url, alt_text, caption, display_order, image_type)
VALUES 
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1540500898170-85fb8a77ce57?w=800', 'Success motivation quotes', 'Quotes động lực cho hành trình thành công', 0, 'FEATURED'),
(LAST_INSERT_ID(), 'https://i.pinimg.com/736x/d9/3c/f0/d93cf029b8dad26cfaf705764fee7c20.jpg', 'Success quotes', 'Quotes về thành công từ các nhân vật nổi tiếng', 1, 'INLINE'),
(LAST_INSERT_ID(), 'https://i.pinimg.com/736x/d9/3c/f0/d93cf029b8dad26cfaf705764fee7c20.jpg', 'Business quotes', 'Quotes về startup và kinh doanh', 2, 'INLINE'),
(LAST_INSERT_ID(), 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600', 'Overcoming challenges', 'Quotes về vượt qua khó khăn', 3, 'INLINE');

-- =======================================================================
-- THỐNG KÊ CUỐI CÙNG
-- =======================================================================

-- Thống kê kết quả
SELECT 
    'RESOURCE SUMMARY' as Type,
    resource_type,
    COUNT(*) as Total
FROM resource 
GROUP BY resource_type
UNION ALL
SELECT 
    'BLOG POST SUMMARY' as Type,
    bp.status,
    COUNT(*) as Total
FROM blog_post bp
GROUP BY bp.status
UNION ALL
SELECT 
    'BLOG IMAGE SUMMARY' as Type,
    bi.image_type,
    COUNT(*) as Total
FROM blog_image bi
GROUP BY bi.image_type;

-- Final message
SELECT 
    '✅ HOÀN THÀNH DATABASE BLOG STUDENTJOB' as Message,
    '7 Resources + 7 Blog Posts + 28 Blog Images' as Content,
    'Cấu trúc BlogPost và BlogImage chính xác theo model' as Structure; 




