package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.BanRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 🚫 BanRecord Repository
 * 
 * Repository để thao tác với database cho BanRecord
 */
@Repository
public interface BanRecordRepository extends JpaRepository<BanRecord, Long> {
    
    /**
     * Tìm ban record hiện tại (active) của user
     */
    @Query("SELECT br FROM BanRecord br WHERE br.bannedUser.userId = :userId AND br.status = 'ACTIVE'")
    Optional<BanRecord> findActiveBanByUserId(@Param("userId") Integer userId);
    
    /**
     * Tìm tất cả ban records của user (bao gồm cả expired và unbanned)
     */
    @Query("SELECT br FROM BanRecord br WHERE br.bannedUser.userId = :userId ORDER BY br.bannedAt DESC")
    List<BanRecord> findAllBanRecordsByUserId(@Param("userId") Integer userId);
    
    /**
     * Tìm ban records của user với phân trang
     */
    @Query("SELECT br FROM BanRecord br WHERE br.bannedUser.userId = :userId ORDER BY br.bannedAt DESC")
    Page<BanRecord> findBanRecordsByUserId(@Param("userId") Integer userId, Pageable pageable);
    
    /**
     * Tìm tất cả ban records active
     */
    @Query("SELECT br FROM BanRecord br WHERE br.status = 'ACTIVE' ORDER BY br.bannedAt DESC")
    Page<BanRecord> findAllActiveBans(Pageable pageable);
    
    /**
     * Tìm ban records đã hết hạn nhưng chưa được update status
     */
    @Query("SELECT br FROM BanRecord br WHERE br.status = 'ACTIVE' AND br.banDurationType = 'TEMPORARY' AND br.banExpiresAt < :currentTime")
    List<BanRecord> findExpiredBans(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Tìm ban records theo admin đã ban
     */
    @Query("SELECT br FROM BanRecord br WHERE br.bannedByAdmin.userId = :adminId ORDER BY br.bannedAt DESC")
    Page<BanRecord> findBanRecordsByAdmin(@Param("adminId") Integer adminId, Pageable pageable);
    
    /**
     * Đếm số lượng user đang bị ban
     */
    @Query("SELECT COUNT(br) FROM BanRecord br WHERE br.status = 'ACTIVE'")
    long countActiveBans();
    
    /**
     * Đếm số lượng ban theo lý do
     */
    @Query("SELECT COUNT(br) FROM BanRecord br WHERE br.banReason = :reason AND br.status = 'ACTIVE'")
    long countActiveBansByReason(@Param("reason") BanRecord.BanReason reason);
    
    /**
     * Tìm ban records theo thời gian
     */
    @Query("SELECT br FROM BanRecord br WHERE br.bannedAt BETWEEN :startDate AND :endDate ORDER BY br.bannedAt DESC")
    Page<BanRecord> findBanRecordsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate, 
                                             Pageable pageable);
    
    /**
     * Kiểm tra user có đang bị ban không
     */
    @Query("SELECT CASE WHEN COUNT(br) > 0 THEN true ELSE false END FROM BanRecord br WHERE br.bannedUser.userId = :userId AND br.status = 'ACTIVE'")
    boolean isUserCurrentlyBanned(@Param("userId") Integer userId);
    
    /**
     * Tìm tất cả users đang bị ban permanent
     */
    @Query("SELECT br FROM BanRecord br WHERE br.status = 'ACTIVE' AND br.banDurationType = 'PERMANENT' ORDER BY br.bannedAt DESC")
    Page<BanRecord> findAllPermanentBans(Pageable pageable);
    
    /**
     * Tìm ban records sắp hết hạn (trong vòng X ngày)
     */
    @Query("SELECT br FROM BanRecord br WHERE br.status = 'ACTIVE' AND br.banDurationType = 'TEMPORARY' AND br.banExpiresAt BETWEEN :currentTime AND :endTime ORDER BY br.banExpiresAt ASC")
    List<BanRecord> findBansExpiringWithin(@Param("currentTime") LocalDateTime currentTime, 
                                          @Param("endTime") LocalDateTime endTime);
} 