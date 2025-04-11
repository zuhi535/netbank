package com.example.netbank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
