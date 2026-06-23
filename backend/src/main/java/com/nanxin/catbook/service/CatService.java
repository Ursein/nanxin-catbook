package com.nanxin.catbook.service;

import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.entity.*;
import com.nanxin.catbook.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatService {

    private final CatRepository catRepository;
    private final PhotoRepository photoRepository;
    private final CommentRepository commentRepository;
    private final CatFollowRepository catFollowRepository;
    private final CatRatingRepository catRatingRepository;
    private final PhotoLikeRepository photoLikeRepository;
    private final UserRepository userRepository;
    private final CatSimilarityAlgorithm similarityAlgorithm;

    public CatService(CatRepository catRepository, PhotoRepository photoRepository,
                      CommentRepository commentRepository, CatFollowRepository catFollowRepository,
                      CatRatingRepository catRatingRepository, PhotoLikeRepository photoLikeRepository,
                      UserRepository userRepository, CatSimilarityAlgorithm similarityAlgorithm) {
        this.catRepository = catRepository;
        this.photoRepository = photoRepository;
        this.commentRepository = commentRepository;
        this.catFollowRepository = catFollowRepository;
        this.catRatingRepository = catRatingRepository;
        this.photoLikeRepository = photoLikeRepository;
        this.userRepository = userRepository;
        this.similarityAlgorithm = similarityAlgorithm;
    }

    public CatListResponse listCats(String keyword, String statusStr, String location,
                                     int page, int size, Long userId) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Cat.CatStatus status = statusStr != null ? Cat.CatStatus.valueOf(statusStr) : null;

        // 统一使用 search 查询（含 deleted 过滤）
        Page<Cat> catPage = catRepository.search(keyword, status, location, pageReq);

        List<CatItem> items = catPage.getContent().stream()
                .map(this::toCatItem)
                .collect(Collectors.toList());

        CatListResponse resp = new CatListResponse();
        resp.setContent(items);
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal(catPage.getTotalElements());
        resp.setTotalPages(catPage.getTotalPages());
        return resp;
    }

    public CatDetailResponse getCatDetail(Long catId, Long userId) {
        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("猫咪不存在"));
        if (cat.getDeleted() != null && cat.getDeleted() == 1) {
            throw new IllegalArgumentException("猫咪不存在");
        }

        CatDetailResponse resp = new CatDetailResponse();
        resp.setId(cat.getId());
        resp.setName(cat.getName());
        resp.setNickname(cat.getNickname());
        resp.setGender(cat.getGender() != null ? cat.getGender().name() : null);
        resp.setBirthYear(cat.getBirthYear());
        resp.setColour(cat.getColour());
        resp.setColourTags(cat.getColourTags());
        resp.setLocationArea(cat.getLocationArea());
        resp.setLocationDetail(cat.getLocationDetail());
        resp.setPersonalityTags(cat.getPersonalityTags());
        resp.setPersonalityDesc(cat.getPersonalityDesc());
        resp.setHealthStatus(cat.getHealthStatus());
        resp.setSterilized(cat.getSterilized());
        resp.setStatus(cat.getStatus() != null ? cat.getStatus().name() : null);
        resp.setWeight(cat.getWeight());
        resp.setFatherId(cat.getFatherId());
        resp.setMotherId(cat.getMotherId());
        resp.setFirstSightingTime(cat.getFirstSightingTime() != null ? cat.getFirstSightingTime().toString() : null);
        resp.setNotes(cat.getNotes());
        resp.setLikeCount(cat.getLikeCount());
        resp.setFollowCount(cat.getFollowCount());
        resp.setRatingCount(cat.getRatingCount());
        resp.setAvgRating(cat.getAvgRating() != null ? cat.getAvgRating().doubleValue() : 0.0);

        // 照片
        List<Photo> photos = photoRepository.findByCatIdAndStatusOrderBySortOrder(catId, Photo.PhotoStatus.APPROVED);
        resp.setPhotos(photos.stream().map(p -> {
            PhotoItem pi = new PhotoItem();
            pi.setId(p.getId());
            pi.setUrl(p.getFilePath());
            pi.setDescription(p.getDescription());
            pi.setStatus(p.getStatus().name());
            pi.setLikeCount(p.getLikeCount());
            if (userId != null) {
                pi.setIsLiked(photoLikeRepository.existsByPhotoIdAndUserId(p.getId(), userId));
            }
            return pi;
        }).collect(Collectors.toList()));

        // 封面图：优先 coverPhotoId，无则取第一张 APPROVED 照片
        if (cat.getCoverPhotoId() != null) {
            photoRepository.findById(cat.getCoverPhotoId()).ifPresent(p ->
                resp.setCoverPhotoUrl(p.getFilePath()));
        } else if (!photos.isEmpty()) {
            resp.setCoverPhotoUrl(photos.get(0).getFilePath());
        }

        // 评论
        List<Comment> comments = commentRepository.findByCatIdOrderByCreatedAtDesc(catId);
        resp.setComments(comments.stream().map(c -> {
            CommentItem ci = new CommentItem();
            ci.setId(c.getId());
            ci.setContent(c.getContent());
            ci.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
            // 获取评论者的用户名
            ci.setUsername(userRepository.findById(c.getUserId())
                    .map(u -> u.getNickname() != null ? u.getNickname() : u.getUsername())
                    .orElse("未知用户"));
            return ci;
        }).collect(Collectors.toList()));

        // 关注状态
        if (userId != null) {
            resp.setIsFollowed(catFollowRepository.existsByUserIdAndCatId(userId, catId));
            Optional<CatRating> myRating = catRatingRepository.findByUserIdAndCatId(userId, catId);
            myRating.ifPresent(r -> resp.setMyRating(r.getRating()));
        }

        // 推荐猫咪
        List<RecommendItem> recommends = similarityAlgorithm.recommend(catId, 5);
        resp.setRecommendCats(recommends);

        return resp;
    }

    @Transactional
    public CatItem createCat(CatRequest req, Long userId) {
        Cat cat = new Cat();
        applyRequest(cat, req);
        cat.setCreatorId(userId);
        cat.setCreatedAt(LocalDateTime.now());
        cat.setUpdatedAt(LocalDateTime.now());
        cat = catRepository.save(cat);
        return toCatItem(cat);
    }

    @Transactional
    public CatItem updateCat(Long catId, CatRequest req, Long userId) {
        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("猫咪不存在"));
        applyRequest(cat, req);
        cat.setUpdatedAt(LocalDateTime.now());
        cat = catRepository.save(cat);
        return toCatItem(cat);
    }

    @Transactional
    public void deleteCat(Long catId) {
        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("猫咪不存在"));
        cat.setDeleted(1);
        cat.setUpdatedAt(LocalDateTime.now());
        catRepository.save(cat);
    }

    private void applyRequest(Cat cat, CatRequest req) {
        cat.setName(req.getName());
        cat.setNickname(req.getNickname());
        if (req.getGender() != null) cat.setGender(Cat.Gender.valueOf(req.getGender()));
        cat.setBirthYear(req.getBirthYear());
        cat.setColour(req.getColour());
        cat.setColourTags(req.getColourTags());
        cat.setLocationArea(req.getLocationArea());
        cat.setLocationDetail(req.getLocationDetail());
        cat.setPersonalityTags(req.getPersonalityTags());
        cat.setPersonalityDesc(req.getPersonalityDesc());
        cat.setSterilized(req.getSterilized());
        if (req.getStatus() != null) cat.setStatus(Cat.CatStatus.valueOf(req.getStatus()));
        cat.setFatherId(req.getFatherId());
        cat.setMotherId(req.getMotherId());
        cat.setNotes(req.getNotes());
        if (req.getWeight() != null) cat.setWeight(req.getWeight());
        if (req.getCoverPhotoId() != null) cat.setCoverPhotoId(req.getCoverPhotoId());
    }

    private CatItem toCatItem(Cat cat) {
        CatItem item = new CatItem();
        item.setId(cat.getId());
        item.setName(cat.getName());
        item.setNickname(cat.getNickname());
        item.setColourTags(cat.getColourTags());
        item.setLocationArea(cat.getLocationArea());
        item.setGender(cat.getGender() != null ? cat.getGender().name() : null);
        item.setStatus(cat.getStatus() != null ? cat.getStatus().name() : null);
        item.setSterilized(cat.getSterilized());
        item.setLikeCount(cat.getLikeCount());
        item.setFollowCount(cat.getFollowCount());
        item.setAvgRating(cat.getAvgRating() != null ? cat.getAvgRating().doubleValue() : 0.0);
        // 封面图URL
        if (cat.getCoverPhotoId() != null) {
            photoRepository.findById(cat.getCoverPhotoId()).ifPresent(p ->
                item.setCoverPhotoUrl(p.getFilePath()));
        }
        if (item.getCoverPhotoUrl() == null) {
            // 无封面图时取第一张 APPROVED 照片
            List<Photo> photos = photoRepository.findByCatIdAndStatusOrderBySortOrder(
                    cat.getId(), Photo.PhotoStatus.APPROVED);
            if (!photos.isEmpty()) {
                item.setCoverPhotoUrl(photos.get(0).getFilePath());
            }
        }
        return item;
    }
}