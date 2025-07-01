package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.repository.IAdminRepository;
import com.example.swp391_d01_g3.repository.IEmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminEmployerServiceImpl implements IAdminEmployerService {

    @Autowired
    private IAdminRepository iAdminRepository;

    @Autowired
    private IEmployerRepository employerRepository;

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

    @Override
    public Page<Account> getEmployersWithPagination(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return iAdminRepository.getAllEmployerWithPagination(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error getting employers with pagination: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> searchEmployers(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return iAdminRepository.searchEmployers(keyword, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error searching employers: " + e.getMessage());
        }
    }

    // THÊM: Filter by status methods
    @Override
    public Page<Account> findByStatus(String status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.findEmployersByStatus(accountStatus, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error finding employers by status: " + e.getMessage());
        }
    }

    @Override
    public Page<Account> searchByKeywordAndStatus(String keyword, String status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.searchEmployersByKeywordAndStatus(keyword, accountStatus, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error searching employers by keyword and status: " + e.getMessage());
        }
    }

    // THÊM: Count methods
    @Override
    public long countAllEmployers() {
        try {
            return iAdminRepository.countByRole(Account.Role.employer);
        } catch (Exception e) {
            throw new RuntimeException("Error counting employers: " + e.getMessage());
        }
    }

    @Override
    public long countEmployersByStatus(String status) {
        try {
            Account.Status accountStatus = Account.Status.valueOf(status);
            return iAdminRepository.countByRoleAndStatus(Account.Role.employer, accountStatus);
        } catch (Exception e) {
            throw new RuntimeException("Error counting employers by status: " + e.getMessage());
        }
    }
}
