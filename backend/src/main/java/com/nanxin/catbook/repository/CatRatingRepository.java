package com.nanxin.catbook.repository;

import com.nanxin.catbook.entity.CatRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CatRatingRepository extends JpaRepository<CatRating, Long> {
    Optional<CatRating> findByUserIdAndCatId(Long userId, Long catId);
    List<CatRating> findByUserId(Long userId);
    long countByCatId(Long catId);
    void deleteByCatId(Long catId);

    @Query("SELECT AVG(r.r1) FROM CatRating r WHERE r.catId = :catId")
    Double avgR1ByCatId(Long catId);

    @Query("SELECT AVG(r.r2) FROM CatRating r WHERE r.catId = :catId")
    Double avgR2ByCatId(Long catId);

    @Query("SELECT AVG(r.r3) FROM CatRating r WHERE r.catId = :catId")
    Double avgR3ByCatId(Long catId);

    @Query("SELECT AVG(r.r4) FROM CatRating r WHERE r.catId = :catId")
    Double avgR4ByCatId(Long catId);

    @Query("SELECT AVG(r.r5) FROM CatRating r WHERE r.catId = :catId")
    Double avgR5ByCatId(Long catId);
}