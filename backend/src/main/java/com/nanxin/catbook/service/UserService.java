package com.nanxin.catbook.service;

import com.nanxin.catbook.config.JwtConfig;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.entity.User;
import com.nanxin.catbook.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtConfig jwtConfig) {
        this.userRepository = userRepository;
        this.jwtConfig = jwtConfig;
    }

    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setRole(User.Role.USER);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (user.getStatus() != 1) {
            throw new IllegalArgumentException("Account disabled");
        }

        String role = user.getRole().name();
        String token = jwtConfig.generateToken(user.getId(), role);
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setUser(toUserInfo(user));
        return resp;
    }

    public UserInfo getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toUserInfo(user);
    }

    public UserInfo updateMe(Long userId, UpdateUserRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }
        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        user.setUpdatedAt(java.time.LocalDateTime.now());
        user = userRepository.save(user);
        return toUserInfo(user);
    }

    public UserInfo updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAvatar(avatarUrl);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        user = userRepository.save(user);
        return toUserInfo(user);
    }

    private UserInfo toUserInfo(User user) {
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setEmail(user.getEmail());
        info.setAvatar(user.getAvatar());
        info.setRole(user.getRole().name());
        return info;
    }
}