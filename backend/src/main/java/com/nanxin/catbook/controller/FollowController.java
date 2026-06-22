package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/cats/{catId}/follow")
    public ResponseEntity<ApiResponse<Boolean>> follow(@PathVariable Long catId, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        boolean followed = followService.toggleFollow(userId, catId);
        return ResponseEntity.ok(ApiResponse.success(followed));
    }

    @DeleteMapping("/cats/{catId}/follow")
    public ResponseEntity<ApiResponse<Boolean>> unfollow(@PathVariable Long catId, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        followService.toggleFollow(userId, catId);
        return ResponseEntity.ok(ApiResponse.success(false));
    }

    @GetMapping("/cats/{catId}/follow/status")
    public ResponseEntity<ApiResponse<FollowStatusResponse>> status(
            @PathVariable Long catId, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        FollowStatusResponse resp = new FollowStatusResponse();
        resp.setFollowed(userId != null && followService.isFollowed(userId, catId));
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @GetMapping("/user/follows")
    public ResponseEntity<ApiResponse<List<Long>>> myFollows(HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        List<Long> catIds = followService.getUserFollows(userId).stream()
                .map(f -> f.getCatId())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(catIds));
    }
}