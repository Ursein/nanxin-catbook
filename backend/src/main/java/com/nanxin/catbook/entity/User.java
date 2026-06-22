package com.nanxin.catbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String avatar;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Integer status = 1;

    private LocalDateTime lastLogin;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum Role { USER, VERIFIED, ADMIN }
}