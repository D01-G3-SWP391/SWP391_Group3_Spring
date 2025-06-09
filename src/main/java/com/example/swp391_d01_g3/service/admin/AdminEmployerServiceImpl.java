package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.repository.IAdminRepository;
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
    }



