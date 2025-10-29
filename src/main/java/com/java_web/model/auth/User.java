package com.java_web.model.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String email;
    private byte[] passwordHash;
    private byte[] salt;
    private String role; // Candidate, Recruiter, EmployerAdmin, Admin (or use UserRoles)
    private boolean isEmailConfirmed;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean isActive;


    public User(Integer userId) { this.userId = userId; }


}
