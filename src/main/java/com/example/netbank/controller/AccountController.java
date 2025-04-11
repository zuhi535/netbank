package com.example.netbank.controller;

import com.example.netbank.model.Account;
import com.example.netbank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{accountId}")
    public String showAccount(@PathVariable Long accountId, Model model) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        model.addAttribute("account", account);
        return "account";
    }

    @PostMapping("/{accountId}/deposit")
    public String deposit(@PathVariable Long accountId,
                          @RequestParam BigDecimal amount) {
        // Pénzfeltöltés logika
        return "redirect:/account/" + accountId;
    }

    @PostMapping("/{accountId}/withdraw")
    public String withdraw(@PathVariable Long accountId,
                           @RequestParam BigDecimal amount) {
        // Pénzlevétel logika
        return "redirect:/account/" + accountId;
    }
}