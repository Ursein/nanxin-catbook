package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.entity.Cat;
import com.nanxin.catbook.service.CatService;
import com.nanxin.catbook.service.CatSimilarityAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cats")
public class CatController {

    private final CatService catService;
    private final CatSimilarityAlgorithm similarityAlgorithm;

    public CatController(CatService catService, CatSimilarityAlgorithm similarityAlgorithm) {
        this.catService = catService;
        this.similarityAlgorithm = similarityAlgorithm;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CatListResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        CatListResponse resp = catService.listCats(keyword, status, location, page, size, userId);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CatDetailResponse>> detail(
            @PathVariable Long id, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        CatDetailResponse resp = catService.getCatDetail(id, userId);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CatItem>> create(
            @Valid @RequestBody CatRequest req, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        CatItem item = catService.createCat(req, userId);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CatItem>> update(
            @PathVariable Long id, @Valid @RequestBody CatRequest req,
            HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        CatItem item = catService.updateCat(id, req, userId);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        catService.deleteCat(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}/recommend")
    public ResponseEntity<ApiResponse<List<RecommendItem>>> recommend(@PathVariable Long id) {
        List<RecommendItem> items = similarityAlgorithm.recommend(id, 5);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
}