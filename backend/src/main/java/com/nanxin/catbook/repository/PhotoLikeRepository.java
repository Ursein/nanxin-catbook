package com.nanxin.catbook.repository;

import com.nanxin.catbook.entity.PhotoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PhotoLikeRepository extends JpaRepository<PhotoLike, Long> {
    Optional<PhotoLike> findByPhotoIdAndUserId(Long photoId, Long userId);
    boolean existsByPhotoIdAndUserId(Long photoId, Long userId);
    long countByPhotoId(Long photoId);
    void deleteByPhotoId(Long photoId);
}