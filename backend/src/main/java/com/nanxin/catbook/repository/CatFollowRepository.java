package com.nanxin.catbook.repository;

import com.nanxin.catbook.entity.CatFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CatFollowRepository extends JpaRepository<CatFollow, Long> {
    Optional<CatFollow> findByUserIdAndCatId(Long userId, Long catId);
    boolean existsByUserIdAndCatId(Long userId, Long catId);
    long countByCatId(Long catId);
    List<CatFollow> findByUserId(Long userId);
    void deleteByCatId(Long catId);
}