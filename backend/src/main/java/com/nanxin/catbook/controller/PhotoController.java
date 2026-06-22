package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.ApiResponse;
import com.nanxin.catbook.dto.PhotoItem;
import com.nanxin.catbook.entity.Photo;
import com.nanxin.catbook.repository.PhotoRepository;
import com.nanxin.catbook.service.PhotoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class PhotoController {

    private final PhotoService photoService;
    private final PhotoRepository photoRepository;

    public PhotoController(PhotoService photoService, PhotoRepository photoRepository) {
        this.photoService = photoService;
        this.photoRepository = photoRepository;
    }

    @GetMapping("/cats/{catId}/photos")
    public ResponseEntity<ApiResponse<List<PhotoItem>>> listPhotos(@PathVariable Long catId) {
        List<Photo> photos = photoRepository.findByCatIdAndStatusOrderBySortOrder(catId, Photo.PhotoStatus.APPROVED);
        List<PhotoItem> items = photos.stream().map(p -> {
            PhotoItem pi = new PhotoItem();
            pi.setId(p.getId());
            pi.setUrl(p.getFilePath());
            pi.setDescription(p.getDescription());
            pi.setStatus(p.getStatus().name());
            pi.setLikeCount(p.getLikeCount());
            return pi;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping("/cats/{catId}/photos")
    public ResponseEntity<ApiResponse<PhotoItem>> upload(
            @PathVariable Long catId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        // 简化：直接保存路径
        String filePath = "/uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Photo photo = photoService.upload(catId, userId, filePath, description);
        PhotoItem pi = new PhotoItem();
        pi.setId(photo.getId());
        pi.setUrl(photo.getFilePath());
        pi.setStatus(photo.getStatus().name());
        return ResponseEntity.ok(ApiResponse.success(pi));
    }

    @PutMapping("/photos/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long id, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        photoService.approve(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/photos/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long id, @RequestBody String reason, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        photoService.reject(id, userId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/photos/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable Long id) {
        photoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/photos/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> like(@PathVariable Long id, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        boolean liked = photoService.toggleLike(id, userId);
        return ResponseEntity.ok(ApiResponse.success(liked));
    }

    @DeleteMapping("/photos/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> unlike(@PathVariable Long id, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        photoService.toggleLike(id, userId);
        return ResponseEntity.ok(ApiResponse.success(false));
    }
}