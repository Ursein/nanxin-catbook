package com.nanxin.catbook.service;

import com.nanxin.catbook.entity.Cat;
import com.nanxin.catbook.entity.CatFollow;
import com.nanxin.catbook.repository.CatFollowRepository;
import com.nanxin.catbook.repository.CatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowService {

    private final CatFollowRepository catFollowRepository;
    private final CatRepository catRepository;

    public FollowService(CatFollowRepository catFollowRepository, CatRepository catRepository) {
        this.catFollowRepository = catFollowRepository;
        this.catRepository = catRepository;
    }

    @Transactional
    public boolean toggleFollow(Long userId, Long catId) {
        var existing = catFollowRepository.findByUserIdAndCatId(userId, catId);
        if (existing.isPresent()) {
            catFollowRepository.delete(existing.get());
            updateFollowCount(catId, -1);
            return false;
        } else {
            CatFollow follow = new CatFollow();
            follow.setUserId(userId);
            follow.setCatId(catId);
            follow.setCreatedAt(LocalDateTime.now());
            catFollowRepository.save(follow);
            updateFollowCount(catId, 1);
            return true;
        }
    }

    private void updateFollowCount(Long catId, int delta) {
        catRepository.findById(catId).ifPresent(cat -> {
            int count = cat.getFollowCount() != null ? cat.getFollowCount() + delta : delta;
            cat.setFollowCount(Math.max(0, count));
            catRepository.save(cat);
        });
    }

    public boolean isFollowed(Long userId, Long catId) {
        return catFollowRepository.existsByUserIdAndCatId(userId, catId);
    }

    public List<CatFollow> getUserFollows(Long userId) {
        return catFollowRepository.findByUserId(userId);
    }
}