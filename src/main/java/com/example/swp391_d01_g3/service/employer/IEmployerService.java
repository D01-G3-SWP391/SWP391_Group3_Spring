package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.JobPost;

import java.util.List;

public interface IEmployerService {
    void saveEmployer(Employer employer);
    Employer findByUserId(Integer userId);
    Employer findByAccount(Account account);
    Employer findByEmail(String email);

    void updateEmployer(Employer employer);

}
