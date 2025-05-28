package com.example.swp391_d01_g3.service.security;


import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Service
public class AccountDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IAccountService IAccountService;
    
    @Autowired
    private IStudentService iStudentService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = IAccountService.findByEmail(email);

        if (account == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));

        // Tạo UserDetails object
        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword() != null ? account.getPassword() : "",
                authorities
        );
    }
    
    public void saveOAuthUser(Map<String,Object> attributes){
        String email = (String) attributes.get("email");
        String fullName = (String) attributes.get("name");
        Account existingAccount = IAccountService.findByEmail(email);
        if (existingAccount == null) {
            Account newAccount = new Account();
            newAccount.setEmail(email);
            newAccount.setFullName(fullName);
            newAccount.setRole(Account.Role.student);
            newAccount.setStatus(Account.Status.active);
            newAccount.setPassword("");
            Account savedAccount = IAccountService.save(newAccount);
            
            // Tạo Student record
            Student student = new Student();
            student.setAccount(savedAccount);
            iStudentService.saveStudent(student);
        }
    }
}