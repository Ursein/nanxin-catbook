package com.nanxin.catbook.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CatDetailResponse {
    private Long id; private String name; private String nickname;
    private String gender; private Integer birthYear; private String colour;
    private String colourTags; private String locationArea; private String locationDetail;
    private String personalityTags; private String personalityDesc; private String healthStatus;
    private Boolean sterilized; private String status; private BigDecimal weight;
    private Long fatherId; private Long motherId;
    private String firstSightingTime; private String notes;
    private Integer likeCount; private Integer followCount;
    private Integer ratingCount; private Double avgRating;
    private Double avgR1; private Double avgR2;
    private Double avgR3; private Double avgR4; private Double avgR5;
    private Integer myR1; private Integer myR2;
    private Integer myR3; private Integer myR4; private Integer myR5;
    private Boolean isFollowed; private Integer myRating;
    private List<PhotoItem> photos;
    private List<CommentItem> comments;
    private List<RecommendItem> recommendCats;
    private String coverPhotoUrl;
}