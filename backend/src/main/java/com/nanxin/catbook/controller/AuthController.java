package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @Value("${app.upload.dir}")
    private String uploadDir;

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
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Not logged in"));
        }
        UserInfo user = userService.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> updateMe(
            @Valid @RequestBody UpdateUserRequest req, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Not logged in"));
        }
        UserInfo user = userService.updateMe(userId, req);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserInfo>> uploadAvatar(
            @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Not logged in"));
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "File is empty"));
        }
        try {
            Path avatarDir = Paths.get(uploadDir, "avatars");
            Files.createDirectories(avatarDir);

            String ext = file.getOriginalFilename() != null
                    && file.getOriginalFilename().contains(".")
                    ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
                    : ".jpg";
            String filename = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filepath = avatarDir.resolve(filename);
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            String avatarUrl = "/uploads/avatars/" + filename;
            UserInfo user = userService.updateAvatar(userId, avatarUrl);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Upload failed: " + e.getMessage()));
        }
    }
}