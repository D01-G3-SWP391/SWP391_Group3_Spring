package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;

public interface IEmployerService {
    void save (Account account);
    void saveEmployer(Employer employer);
    Employer findByUserId(Integer userId);
}
