package com.example.swp391_d01_g3.service.scheduled;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.BanRecord;
import com.example.swp391_d01_g3.repository.BanRecordRepository;
import com.example.swp391_d01_g3.repository.IAccountRepository;
import com.example.swp391_d01_g3.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 🕒 Ban Expiry Scheduled Service
 * 
 * Service này chạy định kỳ để:
 * 1. Tự động unban users khi hết thời gian ban
 * 2. Gửi email thông báo ban sắp hết hạn
 * 3. Cập nhật trạng thái ban records
 */
@Service
public class BanExpiryScheduledService {
    
    private static final Logger logger = LoggerFactory.getLogger(BanExpiryScheduledService.class);
    
    @Autowired
    private BanRecordRepository banRecordRepository;
    
    @Autowired
    private IAccountRepository accountRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Chạy mỗi 5 phút để kiểm tra ban đã hết hạn
     * Cron: "0 *5 * * * *" = mỗi 5 phút
     *      */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void processExpiredBans() {
        try {
            logger.info("🕒 Starting expired ban check...");
            
            LocalDateTime now = LocalDateTime.now();
            
            // Tìm tất cả ban records đã hết hạn
            List<BanRecord> expiredBans = banRecordRepository.findExpiredBans(now);
            
            if (expiredBans.isEmpty()) {
                logger.info("✅ No expired bans found");
                return;
            }
            
            logger.info("🔍 Found {} expired bans to process", expiredBans.size());
            
            int successCount = 0;
            int errorCount = 0;
            
            for (BanRecord banRecord : expiredBans) {
                try {
                    // Unban user
                    unbanExpiredUser(banRecord);
                    successCount++;
                    
                } catch (Exception e) {
                    logger.error("❌ Error processing expired ban for user {}: {}", 
                               banRecord.getBannedUser().getUserId(), e.getMessage());
                    errorCount++;
                }
            }
            
            logger.info("✅ Expired ban processing completed: {} success, {} errors", 
                       successCount, errorCount);
            
        } catch (Exception e) {
            logger.error("❌ Error in expired ban check: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Chạy mỗi ngày lúc 9:00 AM để nhắc nhở users ban sắp hết hạn
     * Cron: "0 0 9 * * *" = 9:00 AM mỗi ngày
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendBanExpiryReminders() {
        try {
            logger.info("📅 Starting ban expiry reminder check...");
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threeDaysLater = now.plusDays(3);
            
            // Tìm ban records sắp hết hạn trong 3 ngày tới
            List<BanRecord> expiringBans = banRecordRepository.findBansExpiringWithin(now, threeDaysLater);
            
            if (expiringBans.isEmpty()) {
                logger.info("✅ No bans expiring soon");
                return;
            }
            
            logger.info("📧 Found {} bans expiring within 3 days, sending reminders...", expiringBans.size());
            
            int emailsSent = 0;
            
            for (BanRecord banRecord : expiringBans) {
                try {
                    sendExpiryReminderEmail(banRecord);
                    emailsSent++;
                    
                } catch (Exception e) {
                    logger.error("❌ Error sending expiry reminder for user {}: {}", 
                               banRecord.getBannedUser().getUserId(), e.getMessage());
                }
            }
            
            logger.info("✅ Ban expiry reminders sent: {} emails", emailsSent);
            
        } catch (Exception e) {
            logger.error("❌ Error in ban expiry reminder check: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Chạy mỗi tuần vào Chủ nhật lúc 2:00 AM để cleanup old ban records
     * Cron: "0 0 2 * * SUN" = 2:00 AM mỗi Chủ nhật
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    @Transactional
    public void cleanupOldBanRecords() {
        try {
            logger.info("🧹 Starting old ban records cleanup...");
            
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            
            // Tìm ban records đã unbanned hoặc expired cách đây 6 tháng
            List<BanRecord> oldBanRecords = banRecordRepository.findBanRecordsByDateRange(
                LocalDateTime.now().minusYears(10), sixMonthsAgo, 
                org.springframework.data.domain.PageRequest.of(0, 1000)
            ).getContent().stream()
            .filter(br -> br.getStatus() == BanRecord.BanStatus.UNBANNED || 
                         br.getStatus() == BanRecord.BanStatus.EXPIRED)
            .collect(java.util.stream.Collectors.toList());
            
            if (oldBanRecords.isEmpty()) {
                logger.info("✅ No old ban records to cleanup");
                return;
            }
            
            logger.info("🗑️ Found {} old ban records to archive", oldBanRecords.size());
            
            // Note: Thay vì xóa, chúng ta có thể archive hoặc mark as archived
            // Để giữ audit trail, ở đây chỉ log thông tin
            for (BanRecord banRecord : oldBanRecords) {
                logger.debug("📝 Old ban record: User {} - {} - {}", 
                           banRecord.getBannedUser().getUserId(),
                           banRecord.getBanReason().getDescription(),
                           banRecord.getBannedAt());
            }
            
            logger.info("✅ Old ban records cleanup completed");
            
        } catch (Exception e) {
            logger.error("❌ Error in old ban records cleanup: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Unban user khi hết thời gian ban
     */
    private void unbanExpiredUser(BanRecord banRecord) {
        try {
            Account bannedUser = banRecord.getBannedUser();
            
            // Mark ban record as expired
            banRecord.markAsExpired();
            banRecordRepository.save(banRecord);
            
            // Activate user account
            bannedUser.setStatus(Account.Status.active);
            accountRepository.save(bannedUser);
            
            // Send unban notification email
            long daysBanned = java.time.Duration.between(banRecord.getBannedAt(), LocalDateTime.now()).toDays();
            String banDuration = daysBanned > 0 ? daysBanned + " ngày" : "dưới 1 ngày";
            
            emailService.sendUnbanNotificationEmail(
                bannedUser.getEmail(),
                bannedUser.getFullName(),
                banRecord.getBanReason().getDescription(),
                banDuration,
                "System Auto-Unban"
            );
            
            logger.info("✅ Auto-unbanned user: {} ({})", 
                       bannedUser.getEmail(), banRecord.getBanReason().getDescription());
            
        } catch (Exception e) {
            logger.error("❌ Error unbanning expired user {}: {}", 
                       banRecord.getBannedUser().getUserId(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Gửi email nhắc nhở ban sắp hết hạn
     */
    private void sendExpiryReminderEmail(BanRecord banRecord) {
        try {
            Account bannedUser = banRecord.getBannedUser();
            
            if (banRecord.getBanExpiresAt() != null) {
                long daysRemaining = java.time.Duration.between(
                    LocalDateTime.now(), banRecord.getBanExpiresAt()
                ).toDays();
                
                if (daysRemaining >= 0 && daysRemaining <= 3) {
                    String expiryDateStr = banRecord.getBanExpiresAt()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    
                    emailService.sendBanExpiryReminderEmail(
                        bannedUser.getEmail(),
                        bannedUser.getFullName(),
                        (int) daysRemaining,
                        expiryDateStr
                    );
                    
                    logger.info("📧 Sent ban expiry reminder to: {} ({} days remaining)", 
                               bannedUser.getEmail(), daysRemaining);
                }
            }
            
        } catch (Exception e) {
            logger.error("❌ Error sending ban expiry reminder to user {}: {}", 
                       banRecord.getBannedUser().getUserId(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Manual method để force check expired bans (cho testing)
     */
    public void forceCheckExpiredBans() {
        logger.info("🔧 Manual force check for expired bans triggered");
        processExpiredBans();
    }
    
    /**
     * Manual method để force send expiry reminders (cho testing)
     */
    public void forceSendExpiryReminders() {
        logger.info("🔧 Manual force send expiry reminders triggered");
        sendBanExpiryReminders();
    }
} 