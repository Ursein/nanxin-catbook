package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/cats/{catId}/rating")
    public ResponseEntity<ApiResponse<Void>> submit(
            @PathVariable Long catId, @Valid @RequestBody RatingRequest req,
            HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        ratingService.submit(userId, catId, req.getRating());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/cats/{catId}/rating")
    public ResponseEntity<ApiResponse<RatingStatsResponse>> stats(
            @PathVariable Long catId, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        RatingStatsResponse resp = ratingService.getStats(catId, userId);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @GetMapping("/cats/{catId}/rating/mine")
    public ResponseEntity<ApiResponse<Integer>> mine(
            @PathVariable Long catId, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        Integer myRating = ratingService.getMyRating(userId, catId);
        return ResponseEntity.ok(ApiResponse.success(myRating));
    }
}