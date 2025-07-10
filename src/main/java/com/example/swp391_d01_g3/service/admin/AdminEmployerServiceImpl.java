package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.BanRecord;
import com.example.swp391_d01_g3.dto.BanRequestDTO;
import com.example.swp391_d01_g3.repository.IAdminRepository;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import com.example.swp391_d01_g3.repository.BanRecordRepository;
import com.example.swp391_d01_g3.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminEmployerServiceImpl implements IAdminEmployerService {

    @Autowired
    private IAdminRepository iAdminRepository;

    @Autowired
    private IEmployerRepository employerRepository;
    
    @Autowired
    private BanRecordRepository banRecordRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    public List<Account> getEmployers() {
        return iAdminRepository.getAllEmployer();
    }

    @Override
    public void banEmployer(Integer userId) {
        try {
            Optional<Account> optionalUser = iAdminRepository.findById(userId);
            if (optionalUser.isPresent()) {
                Account user = optionalUser.get();
                user.setStatus(Account.Status.inactive);
                iAdminRepository.save(user);
            } else {
                throw new RuntimeException("User not found with ID: " + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error banning employer: " + e.getMessage());
        }
    }

    @Override
    public void unbanEmployer(Integer userId) {
        try {
            Optional<Account> optionalUser = iAdminRepository.findById(userId);
            if (optionalUser.isPresent()) {
                Account user = optionalUser.get();
                user.setStatus(Account.Status.active);
                iAdminRepository.save(user);
            } else {
                throw new RuntimeException("User not found with ID: " + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error unbanning employer: " + e.getMessage());
        }
    }

    @Override
    public Account getEmployerById(Integer userId) {
        try {
            Optional<Account> optionalUser = iAdminRepository.findById(userId);
            if (optionalUser.isPresent() && optionalUser.get().getRole() == Account.Role.employer) {
                return optionalUser.get();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error getting employer by ID: " + e.getMessage());
        }
    }

    @Override
    public Employer getEmployerDetailsById(Integer userId) {
        try {
            Employer employer = employerRepository.findByAccount_UserId(userId);
            return employer;
        } catch (Exception e) {
            throw new RuntimeException("Error getting employer details by ID: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> getEmployersWithPagination(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return iAdminRepository.getAllEmployerWithPagination(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error getting employers with pagination: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> searchEmployers(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return iAdminRepository.searchEmployers(keyword, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error searching employers: " + e.getMessage());
        }
    }

    // THÊM: Filter by status methods
    @Override
    public Page<Account> findByStatus(String status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.findEmployersByStatus(accountStatus, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error finding employers by status: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> searchByKeywordAndStatus(String keyword, String status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.searchEmployersByKeywordAndStatus(keyword, accountStatus, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error searching employers by keyword and status: " + e.getMessage());
        }
    }

    // THÊM: Count methods
    @Override
    public long countAllEmployers() {
        try {
            return iAdminRepository.countByRole(Account.Role.employer);
        } catch (Exception e) {
            throw new RuntimeException("Error counting employers: " + e.getMessage());
        }
    }

    @Override
    public long countEmployersByStatus(String status) {
        try {
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.countByRoleAndStatus(Account.Role.employer, accountStatus);
        } catch (Exception e) {
            throw new RuntimeException("Error counting employers by status: " + e.getMessage());
        }
    }
    
    /**
     * Enhanced ban employer with reason and email notification
     */
    @Override
    public void banEmployerWithReason(BanRequestDTO banRequest, Integer adminId) {
        try {
            // Validate ban request
            if (!banRequest.isValid()) {
                throw new IllegalArgumentException("Invalid ban request data");
            }
            
            // Get user to ban
            Optional<Account> optionalUser = iAdminRepository.findById(banRequest.getUserId());
            if (!optionalUser.isPresent()) {
                throw new RuntimeException("Employer not found with ID: " + banRequest.getUserId());
            }
            
            Account userToBan = optionalUser.get();
            if (userToBan.getRole() != Account.Role.employer) {
                throw new RuntimeException("User is not an employer");
            }
            
            // Get admin account
            Optional<Account> optionalAdmin = iAdminRepository.findById(adminId);
            if (!optionalAdmin.isPresent()) {
                throw new RuntimeException("Admin not found with ID: " + adminId);
            }
            Account adminUser = optionalAdmin.get();
            
            // Check if user is already banned
            Optional<BanRecord> existingBan = banRecordRepository.findActiveBanByUserId(banRequest.getUserId());
            if (existingBan.isPresent()) {
                throw new RuntimeException("User is already banned");
            }
            
            // Create ban record
            BanRecord banRecord = new BanRecord();
            banRecord.setBannedUser(userToBan);
            banRecord.setBannedByAdmin(adminUser);
            banRecord.setBanReason(banRequest.getBanReason());
            banRecord.setBanDescription(banRequest.getBanDescription());
            banRecord.setBanDurationType(banRequest.getBanDurationType());
            banRecord.setBanDurationDays(banRequest.getBanDurationDays());
            banRecord.setBannedAt(LocalDateTime.now());
            
            // Set expiry date for temporary bans
            if (banRequest.getBanDurationType() == BanRecord.BanDurationType.TEMPORARY 
                && banRequest.getBanDurationDays() != null) {
                banRecord.setBanExpiresAt(LocalDateTime.now().plusDays(banRequest.getBanDurationDays()));
            }
            
            // Save ban record
            banRecordRepository.save(banRecord);
            
            // Update user status to inactive
            userToBan.setStatus(Account.Status.inactive);
            iAdminRepository.save(userToBan);
            
            // Send ban notification email
            String banExpiresAtStr = null;
            if (banRecord.getBanExpiresAt() != null) {
                banExpiresAtStr = banRecord.getBanExpiresAt()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
            
            emailService.sendBanNotificationEmail(
                userToBan.getEmail(),
                userToBan.getFullName(),
                banRequest.getBanReason().getDescription(),
                banRequest.getBanDescription(),
                banRequest.getBanDurationType().name(),
                banRequest.getBanDurationDays(),
                banExpiresAtStr
            );
            
            System.out.println("✅ Employer banned successfully: " + userToBan.getEmail() + 
                             " (Reason: " + banRequest.getBanReason().getDescription() + 
                             ", Duration: " + banRequest.getDurationText() + ")");
            
        } catch (Exception e) {
            throw new RuntimeException("Error banning employer with reason: " + e.getMessage());
        }
    }
    
    /**
     * Enhanced unban employer with notification
     */
    @Override
    public void unbanEmployerWithNotification(Integer userId, Integer adminId) {
        try {
            System.out.println("🔍 DEBUG: Starting unban employer process for userId: " + userId + ", adminId: " + adminId);
            
            // Get user to unban
            Optional<Account> optionalUser = iAdminRepository.findById(userId);
            if (!optionalUser.isPresent()) {
                throw new RuntimeException("Employer not found with ID: " + userId);
            }
            
            Account userToUnban = optionalUser.get();
            System.out.println("🔍 DEBUG: Found employer: " + userToUnban.getEmail() + ", status: " + userToUnban.getStatus());
            
            if (userToUnban.getRole() != Account.Role.employer) {
                throw new RuntimeException("User is not an employer");
            }
            
            // Get admin account
            Optional<Account> optionalAdmin = iAdminRepository.findById(adminId);
            if (!optionalAdmin.isPresent()) {
                throw new RuntimeException("Admin not found with ID: " + adminId);
            }
            Account adminUser = optionalAdmin.get();
            
            // Find active ban record
            Optional<BanRecord> optionalBanRecord = banRecordRepository.findActiveBanByUserId(userId);
            System.out.println("🔍 DEBUG: Active ban record found for employer: " + optionalBanRecord.isPresent());
            
            if (!optionalBanRecord.isPresent()) {
                // Check if user was banned with old system (just status = inactive)
                if (userToUnban.getStatus() == Account.Status.inactive) {
                    System.out.println("🔍 DEBUG: Employer is inactive but no ban record - activating with legacy unban");
                    userToUnban.setStatus(Account.Status.active);
                    iAdminRepository.save(userToUnban);
                    
                    System.out.println("✅ Employer unbanned (legacy) successfully: " + userToUnban.getEmail());
                    return;
                } else {
                    throw new RuntimeException("No active ban found for user and user is not inactive");
                }
            }
            
            BanRecord banRecord = optionalBanRecord.get();
            
            // Calculate ban duration for email
            LocalDateTime bannedAt = banRecord.getBannedAt();
            LocalDateTime now = LocalDateTime.now();
            long daysBanned = java.time.Duration.between(bannedAt, now).toDays();
            String banDuration = daysBanned > 0 ? daysBanned + " ngày" : "dưới 1 ngày";
            
            // Mark ban as unbanned
            banRecord.markAsUnbanned(adminUser);
            banRecordRepository.save(banRecord);
            
            // Update user status to active
            userToUnban.setStatus(Account.Status.active);
            iAdminRepository.save(userToUnban);
            
            // Send unban notification email
            emailService.sendUnbanNotificationEmail(
                userToUnban.getEmail(),
                userToUnban.getFullName(),
                banRecord.getBanReason().getDescription(),
                banDuration,
                adminUser.getFullName()
            );
            
            System.out.println("✅ Employer unbanned successfully: " + userToUnban.getEmail() + 
                             " (Was banned for: " + banRecord.getBanReason().getDescription() + 
                             ", Duration: " + banDuration + ")");
            
        } catch (Exception e) {
            throw new RuntimeException("Error unbanning employer: " + e.getMessage());
        }
    }
}
