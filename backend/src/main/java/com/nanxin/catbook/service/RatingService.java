package com.nanxin.catbook.service;

import com.nanxin.catbook.dto.RatingStatsResponse;
import com.nanxin.catbook.entity.Cat;
import com.nanxin.catbook.entity.CatRating;
import com.nanxin.catbook.repository.CatRatingRepository;
import com.nanxin.catbook.repository.CatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RatingService {

    private final CatRatingRepository catRatingRepository;
    private final CatRepository catRepository;

    public RatingService(CatRatingRepository catRatingRepository, CatRepository catRepository) {
        this.catRatingRepository = catRatingRepository;
        this.catRepository = catRepository;
    }

    @Transactional
    public void submit(Long userId, Long catId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("评分必须在 1-5 之间");
        }
        boolean isNew = true;
        var existing = catRatingRepository.findByUserIdAndCatId(userId, catId);
        if (existing.isPresent()) {
            CatRating r = existing.get();
            r.setRating(rating);
            catRatingRepository.save(r);
            isNew = false;
        } else {
            CatRating r = new CatRating();
            r.setUserId(userId);
            r.setCatId(catId);
            r.setRating(rating);
            r.setCreatedAt(LocalDateTime.now());
            catRatingRepository.save(r);
        }
        updateCatRatingStats(catId, isNew);
    }

    private void updateCatRatingStats(Long catId, boolean isNew) {
        catRepository.findById(catId).ifPresent(cat -> {
            Double avg = catRatingRepository.avgRatingByCatId(catId);
            long count = catRatingRepository.countByCatId(catId);
            cat.setAvgRating(avg != null ? BigDecimal.valueOf(avg).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            cat.setRatingCount((int) count);
            catRepository.save(cat);
        });
    }

    public RatingStatsResponse getStats(Long catId, Long userId) {
        RatingStatsResponse resp = new RatingStatsResponse();
        Double avg = catRatingRepository.avgRatingByCatId(catId);
        resp.setAvgRating(avg != null ? BigDecimal.valueOf(avg).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() : 0.0);
        resp.setRatingCount((int) catRatingRepository.countByCatId(catId));
        if (userId != null) {
            catRatingRepository.findByUserIdAndCatId(userId, catId)
                    .ifPresent(r -> resp.setMyRating(r.getRating()));
        }
        return resp;
    }

    public Integer getMyRating(Long userId, Long catId) {
        return catRatingRepository.findByUserIdAndCatId(userId, catId)
                .map(CatRating::getRating)
                .orElse(null);
    }
}