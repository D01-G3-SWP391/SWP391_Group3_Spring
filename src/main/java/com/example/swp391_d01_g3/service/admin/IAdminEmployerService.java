package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminEmployerService {
    List<Account> getEmployers();

    // Ban nhà tuyển dụng (thay đổi trạng thái thành INACTIVE)
    void banEmployer(Integer userId);

    // Unban nhà tuyển dụng (thay đổi trạng thái thành ACTIVE)
    void unbanEmployer(Integer userId);
}
