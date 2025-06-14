package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.repository.IAdminRepository;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service


public class AdminEmployerServiceImpl implements IAdminEmployerService {

    @Autowired
    private IAdminRepository iAdminRepository;

    @Override
    public List<Account> getEmployers() {
        return iAdminRepository.getAllEmployer();
    }

    @Autowired
    private IEmployerRepository employerRepository;

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
}




