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
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        ratingService.submit(userId, catId, req.getR1(), req.getR2(), req.getR3(), req.getR4(), req.getR5());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/cats/{catId}/rating")
    public ResponseEntity<ApiResponse<RatingStatsResponse>> stats(
            @PathVariable Long catId, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        RatingStatsResponse resp = ratingService.getStats(catId, userId);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }
}