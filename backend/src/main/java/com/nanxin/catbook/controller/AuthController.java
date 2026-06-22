package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse resp = userService.login(req);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> me(HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "未登录"));
        }
        UserInfo user = userService.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}