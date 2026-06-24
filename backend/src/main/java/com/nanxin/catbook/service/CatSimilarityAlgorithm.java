package com.nanxin.catbook.service;

import com.nanxin.catbook.dto.RecommendItem;
import com.nanxin.catbook.entity.Cat;
import com.nanxin.catbook.entity.Photo;
import com.nanxin.catbook.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 核心算法引擎：基于余弦相似度的内容推荐算法，融合热度因子。
 *
 * Score = α · cos(θ) + β₁ · LikeNorm + β₂ · FollowNorm + β₃ · RatingNorm
 * 默认权重：α=0.7, β₁=β₂=β₃=0.1
 */
@Service
public class CatSimilarityAlgorithm {

    private final CatRepository catRepository;
    private final PhotoRepository photoRepository;
    private final PhotoLikeRepository photoLikeRepository;
    private final CatFollowRepository catFollowRepository;
    private final CatRatingRepository catRatingRepository;

    // 权重配置
    private static final double ALPHA = 0.7;    // 余弦相似度
    private static final double BETA1 = 0.1;    // 点赞热度
    private static final double BETA2 = 0.1;    // 关注热度
    private static final double BETA3 = 0.1;    // 评分热度

    public CatSimilarityAlgorithm(CatRepository catRepository,
                                   PhotoRepository photoRepository,
                                   PhotoLikeRepository photoLikeRepository,
                                   CatFollowRepository catFollowRepository,
                                   CatRatingRepository catRatingRepository) {
        this.catRepository = catRepository;
        this.photoRepository = photoRepository;
        this.photoLikeRepository = photoLikeRepository;
        this.catFollowRepository = catFollowRepository;
        this.catRatingRepository = catRatingRepository;
    }

    /**
     * 推荐与 targetCatId 相似的猫咪
     * @param targetCatId 目标猫咪ID
     * @param topN 返回数量
     * @return 推荐列表（已按分数降序排列）
     */
    public List<RecommendItem> recommend(Long targetCatId, int topN) {
        // 1. 获取目标猫咪
        Cat target = catRepository.findById(targetCatId)
                .orElseThrow(() -> new IllegalArgumentException("猫咪不存在"));
        if (target.getDeleted() != null && target.getDeleted() == 1) {
            throw new IllegalArgumentException("猫咪不存在");
        }

        // 2. 获取所有活跃猫咪作为候选池（排除已删除）
        List<Cat> allCats = catRepository.findActiveByStatus(Cat.CatStatus.ACTIVE);
        allCats = allCats.stream()
                .filter(c -> !c.getId().equals(targetCatId))
                .collect(Collectors.toList());

        if (allCats.isEmpty()) return Collections.emptyList();

        // 3. 构建特征词典
        FeatureDictionary dict = buildDictionary(allCats);

        // 4. 编码目标猫咪特征向量
        double[] targetVector = encode(target, dict);

        // 5. 计算每只猫咪的余弦相似度
        Map<Long, Double> similarityScores = new HashMap<>();
        Map<Long, double[]> catVectors = new HashMap<>();

        for (Cat cat : allCats) {
            double[] vector = encode(cat, dict);
            catVectors.put(cat.getId(), vector);
            double sim = cosineSimilarity(targetVector, vector);
            similarityScores.put(cat.getId(), sim);
        }

        // 6. 获取热度数据并归一化
        Map<Long, Double> likeNorm = normalize(getLikeCounts(allCats));
        Map<Long, Double> followNorm = normalize(getFollowCounts(allCats));
        Map<Long, Double> ratingNorm = normalize(getAvgRatings(allCats));

        // 7. 融合热度因子
        Map<Long, Double> finalScores = new HashMap<>();
        for (Cat cat : allCats) {
            Long id = cat.getId();
            double score = ALPHA * similarityScores.getOrDefault(id, 0.0)
                    + BETA1 * likeNorm.getOrDefault(id, 0.0)
                    + BETA2 * followNorm.getOrDefault(id, 0.0)
                    + BETA3 * ratingNorm.getOrDefault(id, 0.0);
            finalScores.put(id, score);
        }

        // 8. 排序取 Top-N
        return finalScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> {
                    Cat cat = catRepository.findById(entry.getKey()).orElse(null);
                    RecommendItem item = new RecommendItem();
                    item.setCatId(entry.getKey());
                    item.setName(cat != null ? cat.getName() : "未知");
                    item.setNickname(cat != null ? cat.getNickname() : null);
                    item.setScore(entry.getValue());
                    // 封面图
                    item.setCoverPhotoUrl(getCoverPhotoUrl(cat));
                    return item;
                })
                .collect(Collectors.toList());
    }

    // ===== 特征工程 =====

    /** 全局特征词典 */
    private static class FeatureDictionary {
        List<String> colours;      // 所有毛色标签
        List<String> personalities; // 所有性格标签
        List<String> locations;    // 所有位置
        int totalDimensions;       // 总维度

        FeatureDictionary(List<String> colours, List<String> personalities,
                          List<String> locations) {
            this.colours = colours;
            this.personalities = personalities;
            this.locations = locations;
            this.totalDimensions = colours.size() + personalities.size()
                    + locations.size() + 3; // +3 为性别三维
        }
    }

    private FeatureDictionary buildDictionary(List<Cat> cats) {
        Set<String> colours = new HashSet<>();
        Set<String> personalities = new HashSet<>();
        Set<String> locations = new HashSet<>();

        for (Cat cat : cats) {
            if (cat.getColourTags() != null) {
                for (String tag : cat.getColourTags().split(";")) {
                    colours.add(tag.trim());
                }
            }
            if (cat.getPersonalityTags() != null) {
                for (String tag : cat.getPersonalityTags().split(";")) {
                    personalities.add(tag.trim());
                }
            }
            if (cat.getLocationArea() != null) {
                locations.add(cat.getLocationArea());
            }
        }
        return new FeatureDictionary(
                new ArrayList<>(colours),
                new ArrayList<>(personalities),
                new ArrayList<>(locations));
    }

    /** One-Hot 编码 */
    private double[] encode(Cat cat, FeatureDictionary dict) {
        double[] vector = new double[dict.totalDimensions];
        int offset = 0;

        // 毛色标签
        Set<String> catColours = cat.getColourTags() != null
                ? new HashSet<>(Arrays.asList(cat.getColourTags().split(";")))
                : new HashSet<>();
        for (String colour : dict.colours) {
            if (catColours.contains(colour.trim())) vector[offset] = 1;
            offset++;
        }

        // 性格标签
        Set<String> catPersonalities = cat.getPersonalityTags() != null
                ? new HashSet<>(Arrays.asList(cat.getPersonalityTags().split(";")))
                : new HashSet<>();
        for (String personality : dict.personalities) {
            if (catPersonalities.contains(personality.trim())) vector[offset] = 1;
            offset++;
        }

        // 位置
        for (String location : dict.locations) {
            if (location.equals(cat.getLocationArea())) vector[offset] = 1;
            offset++;
        }

        // 性别 (MALE/FEMALE/UNKNOWN)
        if (cat.getGender() != null) {
            switch (cat.getGender()) {
                case MALE:   vector[offset] = 1; break;
                case FEMALE: vector[offset + 1] = 1; break;
                case UNKNOWN: vector[offset + 2] = 1; break;
            }
        } else {
            vector[offset + 2] = 1; // 默认 UNKNOWN
        }

        return vector;
    }

    // ===== 相似度计算 =====

    /** 余弦相似度 */
    private double cosineSimilarity(double[] vecA, double[] vecB) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += vecA[i] * vecA[i];
            normB += vecB[i] * vecB[i];
        }
        double denom = Math.sqrt(normA) * Math.sqrt(normB);
        return denom == 0 ? 0 : dotProduct / denom;
    }

    // ===== 热度因子 =====

    private Map<Long, Double> getLikeCounts(List<Cat> cats) {
        Map<Long, Double> counts = new HashMap<>();
        for (Cat cat : cats) {
            counts.put(cat.getId(), (double) (cat.getLikeCount() != null ? cat.getLikeCount() : 0));
        }
        return counts;
    }

    private Map<Long, Double> getFollowCounts(List<Cat> cats) {
        Map<Long, Double> counts = new HashMap<>();
        for (Cat cat : cats) {
            counts.put(cat.getId(), (double) (cat.getFollowCount() != null ? cat.getFollowCount() : 0));
        }
        return counts;
    }

    private Map<Long, Double> getAvgRatings(List<Cat> cats) {
        Map<Long, Double> ratings = new HashMap<>();
        for (Cat cat : cats) {
            double avg = cat.getAvgRating() != null ? cat.getAvgRating().doubleValue() : 0.0;
            ratings.put(cat.getId(), avg);
        }
        return ratings;
    }

    /** Min-Max 归一化 */
    private Map<Long, Double> normalize(Map<Long, Double> values) {
        if (values.isEmpty()) return values;

        double min = Collections.min(values.values());
        double max = Collections.max(values.values());

        Map<Long, Double> normalized = new HashMap<>();
        for (Map.Entry<Long, Double> entry : values.entrySet()) {
            if (max == min) {
                normalized.put(entry.getKey(), 0.5);
            } else {
                normalized.put(entry.getKey(), (entry.getValue() - min) / (max - min));
            }
        }
        return normalized;
    }

    /** 获取猫咪封面图 URL */
    private String getCoverPhotoUrl(Cat cat) {
        if (cat == null) return null;
        if (cat.getCoverPhotoId() != null) {
            return photoRepository.findById(cat.getCoverPhotoId())
                    .map(Photo::getFilePath)
                    .orElse(null);
        }
        List<Photo> photos = photoRepository.findByCatIdAndStatusOrderBySortOrder(
                cat.getId(), Photo.PhotoStatus.APPROVED);
        return photos.isEmpty() ? null : photos.get(0).getFilePath();
    }
}