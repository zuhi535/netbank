package com.example.netbank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = "accounts")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Név kötelező")
    private String name;

    @Email(message = "Érvényes email címet adj meg")
    @NotBlank(message = "Email kötelező")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Jelszó kötelező")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    // Explicit getter/setter-ek a Lombok mellett (opcionális)
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getAccounts() {
        return accounts;
    }
}