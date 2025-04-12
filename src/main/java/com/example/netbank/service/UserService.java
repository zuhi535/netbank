package com.example.netbank.service;

import com.example.netbank.model.Account;
import com.example.netbank.model.Transaction;
import com.example.netbank.model.User;
import com.example.netbank.repository.AccountRepository;
import com.example.netbank.repository.TransactionRepository;
import com.example.netbank.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setUser(savedUser);
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Hibernate.initialize(user.getAccounts());
        return user;
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.deposit(amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setDescription("Pénzbetét");
        transaction.setTransactionType("DEPOSIT");
        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.withdraw(amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount.negate());
        transaction.setDescription("Pénzlevétel");
        transaction.setTransactionType("WITHDRAWAL");
        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    @Transactional
    public void transfer(Long sourceAccountId, String targetAccountNumber, BigDecimal amount) {
        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new RuntimeException("Forrás számla nem található"));

        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new RuntimeException("Cél számla nem található: " + targetAccountNumber));

        if (sourceAccount.getId().equals(targetAccount.getId())) {
            throw new RuntimeException("Nem utalhat önmagának");
        }

        if (!sourceAccount.transfer(targetAccount, amount)) {
            throw new RuntimeException("Sikertelen utalás: nincs elegendő fedezet vagy érvénytelen összeg");
        }

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        // Kimenő tranzakció
        Transaction outgoing = new Transaction();
        outgoing.setAccount(sourceAccount);
        outgoing.setAmount(amount.negate());
        outgoing.setDescription("Átutalás a " + targetAccountNumber + " számlára");
        outgoing.setTransactionType("TRANSFER_OUT");
        transactionRepository.save(outgoing);

        // Bejövő tranzakció
        Transaction incoming = new Transaction();
        incoming.setAccount(targetAccount);
        incoming.setAmount(amount);
        incoming.setDescription("Átutalás a " + sourceAccount.getAccountNumber() + " számláról");
        incoming.setTransactionType("TRANSFER_IN");
        transactionRepository.save(incoming);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsForUser(String email) {
        User user = findByEmail(email);
        List<Transaction> transactions = new ArrayList<>();
        for (Account account : user.getAccounts()) {
            transactions.addAll(transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId()));
        }
        return transactions;
    }
}