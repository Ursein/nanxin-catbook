package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.ApiResponse;
import com.nanxin.catbook.dto.PhotoItem;
import com.nanxin.catbook.entity.Photo;
import com.nanxin.catbook.repository.PhotoLikeRepository;
import com.nanxin.catbook.repository.PhotoRepository;
import com.nanxin.catbook.service.PhotoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class PhotoController {

    private final PhotoService photoService;
    private final PhotoRepository photoRepository;
    private final PhotoLikeRepository photoLikeRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public PhotoController(PhotoService photoService, PhotoRepository photoRepository,
                           PhotoLikeRepository photoLikeRepository) {
        this.photoService = photoService;
        this.photoRepository = photoRepository;
        this.photoLikeRepository = photoLikeRepository;
    }

    @GetMapping("/cats/{catId}/photos")
    public ResponseEntity<ApiResponse<List<PhotoItem>>> listPhotos(
            @PathVariable Long catId, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        List<Photo> photos = photoRepository.findByCatIdAndStatusOrderBySortOrder(catId, Photo.PhotoStatus.APPROVED);
        List<PhotoItem> items = photos.stream().map(p -> {
            PhotoItem pi = new PhotoItem();
            pi.setId(p.getId());
            pi.setUrl(p.getFilePath());
            pi.setCompressedUrl(p.getFilePathCompressed() != null ? p.getFilePathCompressed() : p.getFilePath());
            pi.setDescription(p.getDescription());
            pi.setStatus(p.getStatus().name());
            pi.setLikeCount(p.getLikeCount());
            if (userId != null) {
                pi.setIsLiked(photoLikeRepository.existsByPhotoIdAndUserId(p.getId(), userId));
            }
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
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Cannot create upload directory"));
        }

        // 保存文件到磁盘
        String originalName = file.getOriginalFilename();
        String baseName = System.currentTimeMillis() + "_" + (originalName != null ? originalName.replaceAll("[^a-zA-Z0-9._-]", "_") : "photo");
        String filePath = "/uploads/" + baseName;
        Path targetPath = uploadPath.resolve(baseName);
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "File save failed: " + e.getMessage()));
        }

        // 生成压缩图（最大宽 800px）
        String compressedName = "comp_" + baseName;
        String compressedPath = "/uploads/" + compressedName;
        Path compressedTarget = uploadPath.resolve(compressedName);
        try (InputStream is = file.getInputStream()) {
            net.coobird.thumbnailator.Thumbnails.of(is)
                    .width(800)
                    .keepAspectRatio(true)
                    .outputQuality(0.8)
                    .toFile(compressedTarget.toFile());
        } catch (Exception e) {
            // 压缩失败时不阻塞，回退使用原图
            compressedPath = filePath;
        }

        Photo photo = photoService.upload(catId, userId, filePath, description);
        photo.setFilePathCompressed(compressedPath);
        photoRepository.save(photo);
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
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        boolean liked = photoService.toggleLike(id, userId);
        return ResponseEntity.ok(ApiResponse.success(liked));
    }

    @DeleteMapping("/photos/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> unlike(@PathVariable Long id, HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Please login first"));
        }
        photoService.toggleLike(id, userId);
        return ResponseEntity.ok(ApiResponse.success(false));
    }
}