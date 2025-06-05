package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.repository.IAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service


public class AdminEmployerServiceImpl implements IAdminEmployerService {

    @Autowired
    private IAdminRepository iAdminRepository;

    @Override
    public List<Account> getEmployers() {
        return iAdminRepository.getAllEmployer();
    }

}

