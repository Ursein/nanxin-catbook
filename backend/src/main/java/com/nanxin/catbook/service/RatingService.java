package com.nanxin.catbook.service;

import com.nanxin.catbook.dto.RatingStatsResponse;
import com.nanxin.catbook.entity.Cat;
import com.nanxin.catbook.entity.CatRating;
import com.nanxin.catbook.repository.CatRatingRepository;
import com.nanxin.catbook.repository.CatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingService {

    private final CatRatingRepository catRatingRepository;
    private final CatRepository catRepository;

    public RatingService(CatRatingRepository catRatingRepository, CatRepository catRepository) {
        this.catRatingRepository = catRatingRepository;
        this.catRepository = catRepository;
    }

    @Transactional
    public void submit(Long userId, Long catId, Integer r1, Integer r2, Integer r3, Integer r4, Integer r5) {
        boolean isNew = true;
        var existing = catRatingRepository.findByUserIdAndCatId(userId, catId);
        if (existing.isPresent()) {
            CatRating r = existing.get();
            if (r1 != null) r.setR1(r1);
            if (r2 != null) r.setR2(r2);
            if (r3 != null) r.setR3(r3);
            if (r4 != null) r.setR4(r4);
            if (r5 != null) r.setR5(r5);
            catRatingRepository.save(r);
            isNew = false;
        } else {
            CatRating r = new CatRating();
            r.setUserId(userId);
            r.setCatId(catId);
            r.setR1(r1 != null ? r1 : 0);
            r.setR2(r2 != null ? r2 : 0);
            r.setR3(r3 != null ? r3 : 0);
            r.setR4(r4 != null ? r4 : 0);
            r.setR5(r5 != null ? r5 : 0);
            r.setCreatedAt(LocalDateTime.now());
            catRatingRepository.save(r);
        }
        updateCatRatingStats(catId, isNew);
    }

    public void submitSimple(Long userId, Long catId, int rating) {
        submit(userId, catId, rating, rating, rating, rating, rating);
    }

    private void updateCatRatingStats(Long catId, boolean isNew) {
        catRepository.findById(catId).ifPresent(cat -> {
            long count = catRatingRepository.countByCatId(catId);

            Double avg1 = catRatingRepository.avgR1ByCatId(catId);
            Double avg2 = catRatingRepository.avgR2ByCatId(catId);
            Double avg3 = catRatingRepository.avgR3ByCatId(catId);
            Double avg4 = catRatingRepository.avgR4ByCatId(catId);
            Double avg5 = catRatingRepository.avgR5ByCatId(catId);

            cat.setAvgR1(bd(avg1));
            cat.setAvgR2(bd(avg2));
            cat.setAvgR3(bd(avg3));
            cat.setAvgR4(bd(avg4));
            cat.setAvgR5(bd(avg5));

            // 综合评分 = 五维平均
            double overall = (val(avg1) + val(avg2) + val(avg3) + val(avg4) + val(avg5)) / 5.0;
            cat.setAvgRating(BigDecimal.valueOf(overall).setScale(2, RoundingMode.HALF_UP));
            cat.setRatingCount((int) count);
            catRepository.save(cat);
        });
    }

    public RatingStatsResponse getStats(Long catId, Long userId) {
        RatingStatsResponse resp = new RatingStatsResponse();
        resp.setAvgR1(bdVal(catRatingRepository.avgR1ByCatId(catId)));
        resp.setAvgR2(bdVal(catRatingRepository.avgR2ByCatId(catId)));
        resp.setAvgR3(bdVal(catRatingRepository.avgR3ByCatId(catId)));
        resp.setAvgR4(bdVal(catRatingRepository.avgR4ByCatId(catId)));
        resp.setAvgR5(bdVal(catRatingRepository.avgR5ByCatId(catId)));
        resp.setRatingCount((int) catRatingRepository.countByCatId(catId));
        if (userId != null) {
            catRatingRepository.findByUserIdAndCatId(userId, catId).ifPresent(r -> {
                resp.setMyR1(r.getR1());
                resp.setMyR2(r.getR2());
                resp.setMyR3(r.getR3());
                resp.setMyR4(r.getR4());
                resp.setMyR5(r.getR5());
            });
        }
        return resp;
    }

    private BigDecimal bd(Double d) {
        return d != null ? BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private Double bdVal(Double d) {
        return d != null ? BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
    }

    private double val(Double d) {
        return d != null ? d : 0.0;
    }

    public List<CatRating> getMyRatings(Long userId) {
        return catRatingRepository.findByUserId(userId);
    }
}