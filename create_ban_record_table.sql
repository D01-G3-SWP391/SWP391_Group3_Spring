CREATE TABLE ban_record (
                            ban_record_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            banned_by INT NULL,
                            ban_reason ENUM(
        'SPAM',
        'INAPPROPRIATE_CONTENT',
        'FAKE_INFORMATION',
        'HARASSMENT',
        'FRAUD',
        'MULTIPLE_ACCOUNTS',
        'VIOLATION_TERMS',
        'SYSTEM_ABUSE',
        'OTHER'
    ) NOT NULL,
                            ban_description TEXT,
                            ban_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            ban_end_date TIMESTAMP NULL,
                            duration_type ENUM('TEMPORARY', 'PERMANENT') NOT NULL,
                            duration_days INT NULL,
                            status ENUM('ACTIVE', 'EXPIRED', 'UNBANNED') DEFAULT 'ACTIVE',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            INDEX idx_user_id (user_id),
                            INDEX idx_status (status),
                            INDEX idx_ban_end_date (ban_end_date)
);

-- Step 3: Add foreign key constraints
ALTER TABLE ban_record
    ADD CONSTRAINT fk_ban_record_user
        FOREIGN KEY (user_id) REFERENCES account(user_id) ON DELETE CASCADE;

ALTER TABLE ban_record
    ADD CONSTRAINT fk_ban_record_banned_by
        FOREIGN KEY (banned_by) REFERENCES account(user_id) ON DELETE SET NULL;

-- Step 4: Verify table creation
DESCRIBE ban_record;