-- Kiểm tra student IDs thực tế
USE swp391;

-- Xem tất cả students
SELECT student_id, account_id, full_name, email FROM student s
JOIN account a ON s.account_id = a.user_id
ORDER BY student_id;

-- Xem tất cả employers  
SELECT employer_id, account_id, company_name, email FROM employer e
JOIN account a ON e.account_id = a.user_id
ORDER BY employer_id;

-- Xem job_post_id 31 có tồn tại không
SELECT job_post_id, job_title, employer_id FROM jobs_post WHERE job_post_id = 31;

-- Xem job applications để biết student nào apply job nào
SELECT ja.application_id, ja.student_id, ja.job_post_id, s.full_name as student_name, 
       jp.job_title, e.company_name
FROM job_application ja
JOIN student s ON ja.student_id = s.student_id  
JOIN jobs_post jp ON ja.job_post_id = jp.job_post_id
JOIN employer e ON jp.employer_id = e.employer_id
ORDER BY ja.application_id DESC
LIMIT 10; 