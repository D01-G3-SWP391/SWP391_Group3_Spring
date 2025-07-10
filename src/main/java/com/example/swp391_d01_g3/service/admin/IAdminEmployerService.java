package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminEmployerService {
    List<Account> getEmployers();

    Page<Account> getEmployersWithPagination(int page, int size);
    Page<Account> searchEmployers(String keyword, int page, int size);

    // THÊM: Filter methods
    Page<Account> findByStatus(String status, int page, int size);
    Page<Account> searchByKeywordAndStatus(String keyword, String status, int page, int size);

    // THÊM: Count methods cho badges
    long countAllEmployers();
    long countEmployersByStatus(String status);

    // Ban nhà tuyển dụng (thay đổi trạng thái thành INACTIVE)
    void banEmployer(Integer userId);

    // Unban nhà tuyển dụng (thay đổi trạng thái thành ACTIVE)
    void unbanEmployer(Integer userId);
    
    // Enhanced ban methods with reason and email notification
    void banEmployerWithReason(com.example.swp391_d01_g3.dto.BanRequestDTO banRequest, Integer adminId);
    void unbanEmployerWithNotification(Integer userId, Integer adminId);

    Account getEmployerById(Integer userId);

    Employer getEmployerDetailsById(Integer userId);
}
