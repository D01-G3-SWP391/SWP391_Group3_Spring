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
    // Ban nhà tuyển dụng (thay đổi trạng thái thành INACTIVE)
    void banEmployer(Integer userId);

    // Unban nhà tuyển dụng (thay đổi trạng thái thành ACTIVE)
    void unbanEmployer(Integer userId);

    Account getEmployerById(Integer userId);

    Employer getEmployerDetailsById(Integer userId);
}
