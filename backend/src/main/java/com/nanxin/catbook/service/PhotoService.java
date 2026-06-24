package com.nanxin.catbook.service;

import com.nanxin.catbook.entity.Photo;
import com.nanxin.catbook.entity.PhotoLike;
import com.nanxin.catbook.repository.CatRepository;
import com.nanxin.catbook.repository.PhotoLikeRepository;
import com.nanxin.catbook.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoLikeRepository photoLikeRepository;
    private final CatRepository catRepository;

    public PhotoService(PhotoRepository photoRepository,
                        PhotoLikeRepository photoLikeRepository,
                        CatRepository catRepository) {
        this.photoRepository = photoRepository;
        this.photoLikeRepository = photoLikeRepository;
        this.catRepository = catRepository;
    }

    @Transactional
    public Photo upload(Long catId, Long userId, String filePath, String description) {
        Photo photo = new Photo();
        photo.setCatId(catId);
        photo.setUploaderId(userId);
        photo.setFilePath(filePath);
        photo.setDescription(description);
        photo.setStatus(Photo.PhotoStatus.APPROVED);
        photo.setCreatedAt(LocalDateTime.now());
        return photoRepository.save(photo);
    }

    @Transactional
    public Photo approve(Long photoId, Long reviewerId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
        photo.setStatus(Photo.PhotoStatus.APPROVED);
        photo.setReviewerId(reviewerId);
        return photoRepository.save(photo);
    }

    @Transactional
    public Photo reject(Long photoId, Long reviewerId, String reason) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
        photo.setStatus(Photo.PhotoStatus.REJECTED);
        photo.setReviewerId(reviewerId);
        photo.setRejectReason(reason);
        return photoRepository.save(photo);
    }

    @Transactional
    public void delete(Long photoId) {
        photoRepository.deleteById(photoId);
    }

    @Transactional
    public boolean toggleLike(Long photoId, Long userId) {
        var existing = photoLikeRepository.findByPhotoIdAndUserId(photoId, userId);
        if (existing.isPresent()) {
            photoLikeRepository.delete(existing.get());
            decrementLikeCount(photoId);
            return false; // 取消点赞
        } else {
            PhotoLike like = new PhotoLike();
            like.setPhotoId(photoId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            photoLikeRepository.save(like);
            incrementLikeCount(photoId);
            return true; // 点赞成功
        }
    }

    private void incrementLikeCount(Long photoId) {
        photoRepository.findById(photoId).ifPresent(p -> {
            p.setLikeCount(p.getLikeCount() == null ? 1 : p.getLikeCount() + 1);
            photoRepository.save(p);
        });
    }

    private void decrementLikeCount(Long photoId) {
        photoRepository.findById(photoId).ifPresent(p -> {
            int count = p.getLikeCount() == null ? 0 : p.getLikeCount();
            p.setLikeCount(Math.max(0, count - 1));
            photoRepository.save(p);
        });
    }
}