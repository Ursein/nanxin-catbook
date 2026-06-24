package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.entity.CatRating;
import com.nanxin.catbook.repository.CatRepository;
import com.nanxin.catbook.service.CatService;
import com.nanxin.catbook.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final CatService catService;
    private final RatingService ratingService;
    private final CatRepository catRepository;

    public UserController(CatService catService, RatingService ratingService,
                          CatRepository catRepository) {
        this.catService = catService;
        this.ratingService = ratingService;
        this.catRepository = catRepository;
    }

    @GetMapping("/cats")
    public ResponseEntity<ApiResponse<List<CatItem>>> myCats(HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        List<CatItem> cats = catService.getMyCats(userId);
        return ResponseEntity.ok(ApiResponse.success(cats));
    }

    @GetMapping("/ratings")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> myRatings(
            HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        List<CatRating> ratings = ratingService.getMyRatings(userId);
        List<Map<String, Object>> result = ratings.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("catId", r.getCatId());
            catRepository.findById(r.getCatId()).ifPresent(cat -> m.put("catName", cat.getName()));
            m.put("r1", r.getR1());
            m.put("r2", r.getR2());
            m.put("r3", r.getR3());
            m.put("r4", r.getR4());
            m.put("r5", r.getR5());
            m.put("createdAt", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}