package com.example.swp391_d01_g3.service.employer;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;

public interface IEmployerService {
    Account saveAccount (Account account);
    Employer saveEmployer(Employer employer);
}
