package com.example.netbank.service;

import com.example.netbank.model.Account;
import com.example.netbank.model.User;
import com.example.netbank.repository.AccountRepository;
import com.example.netbank.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Régi funkcionalitás: automatikus számlalétrehozás
        Account account = new Account();
        account.setUser(savedUser);
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Új funkcionalitás: CustomUserDetails bővítve userId-val
        return new CustomUserDetails(user);
    }

    // Pénzkezelés - régi funkcionalitás
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.deposit(amount);
        accountRepository.save(account);
    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.withdraw(amount);
        accountRepository.save(account);
    }

    // Új metódusok a dashboard működéséhez
    public static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final String fullName;
        private final Long userId;

        public CustomUserDetails(User user) {
            super(user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            this.fullName = user.getName();
            this.userId = user.getId();
        }

        public String getFullName() {
            return fullName;
        }

        public Long getUserId() {
            return userId;
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    Hibernate.initialize(user.getAccounts());
                    return user;
                });
    }

    // Régi metódus kompatibilitás miatt
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    Hibernate.initialize(user.getAccounts());
                    return user;
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}