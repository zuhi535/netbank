package com.example.netbank.controller;

import com.example.netbank.model.Transaction;
import com.example.netbank.model.User;
import com.example.netbank.repository.UserRepository;
import com.example.netbank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(user);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Regisztráció sikertelen: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Hibás email vagy jelszó");
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "redirect:/login";
            }

            User user = userService.findByEmail(auth.getName());
            List<Transaction> transactions = userService.getTransactionsForUser(auth.getName());
            Map<String, Object> financialAnalysis = userService.getFinancialAnalysis(auth.getName());

            model.addAttribute("username", user.getName());
            model.addAttribute("user", user);
            model.addAttribute("transactions", transactions);
            model.addAttribute("analysis", financialAnalysis);
            return "dashboard";
        } catch (UsernameNotFoundException e) {
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Hiba történt: " + e.getMessage());
            return "error";
        }
    }
}