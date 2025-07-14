package com.example.swp391_d01_g3.configuration;
import com.example.swp391_d01_g3.common.EncryptPasswordUtils;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.service.security.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeedingListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private IAccountService iAccountService;
    
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (iAccountService.findByEmail("admin@example.com") == null) {
                Account admin = new Account(
                        "Admin Name",
                        "admin@example.com",
                        EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                        "0123456789",
                        Account.Role.admin,
                        Account.Status.active
                );
                iAccountService.save(admin);
            }
            if (iAccountService.findByEmail("employer@example.com") == null) {
                Account employer = new Account(
                        "Employer",
                        "employer@example.com",
                        EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                        "0123456789",
                        Account.Role.employer,
                        Account.Status.active
                );
                iAccountService.save(employer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}