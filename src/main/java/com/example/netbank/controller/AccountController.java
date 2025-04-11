package com.example.netbank.controller;

import com.example.netbank.model.Account;
import com.example.netbank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/{accountId}")
    public String showAccount(@PathVariable Long accountId, Model model) {
        Account account = userService.getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        model.addAttribute("account", account);

        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        }
        if (model.containsAttribute("success")) {
            model.addAttribute("success", model.getAttribute("success"));
        }
        return "account";
    }

    @PostMapping("/{accountId}/deposit")
    public String deposit(@PathVariable Long accountId,
                          @RequestParam BigDecimal amount,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.deposit(accountId, amount);
            redirectAttributes.addFlashAttribute("success", "Sikeres pénzfeltöltés: " + amount + " Ft");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/" + accountId;
    }

    @PostMapping("/{accountId}/withdraw")
    public String withdraw(@PathVariable Long accountId,
                           @RequestParam BigDecimal amount,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.withdraw(accountId, amount);
            redirectAttributes.addFlashAttribute("success", "Sikeres pénzlevétel: " + amount + " Ft");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/" + accountId;
    }
}