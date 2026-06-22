package com.nanxin.catbook.repository;

import com.nanxin.catbook.entity.CatRating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CatRatingRepository extends JpaRepository<CatRating, Long> {
    Optional<CatRating> findByUserIdAndCatId(Long userId, Long catId);
    long countByCatId(Long catId);
    void deleteByCatId(Long catId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT AVG(r.rating) FROM CatRating r WHERE r.catId = :catId")
    Double avgRatingByCatId(Long catId);
}