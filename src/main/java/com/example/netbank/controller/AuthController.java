package com.example.netbank.controller;

import com.example.netbank.model.User;
import com.example.netbank.repository.UserRepository;
import com.example.netbank.service.UserService;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        userService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            try {
                // 1. Alternatív megoldás: UserService segítségével
                User user = userService.findByEmail(auth.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // 2. Vagy direkt repository hozzáférés
                // User user = userRepository.findByEmail(auth.getName())
                //         .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Hibernate.initialize(user.getAccounts());

                model.addAttribute("username", user.getName());
                model.addAttribute("user", user);
                return "dashboard";
            } catch (Exception e) {
                model.addAttribute("error", "Hiba történt az adatok betöltésekor");
                return "error";
            }
        }
        return "redirect:/login";
    }
}