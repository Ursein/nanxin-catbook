package com.nanxin.catbook.repository;

import com.nanxin.catbook.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByCatIdAndStatusOrderBySortOrder(Long catId, Photo.PhotoStatus status);
    List<Photo> findByCatIdOrderBySortOrder(Long catId);
    long countByCatIdAndStatus(Long catId, String status);
}