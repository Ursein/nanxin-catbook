package com.nanxin.catbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cat")
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer birthYear;

    @Column(length = 50)
    private String breed;

    @Column(length = 50)
    private String colour;

    @Column(length = 100)
    private String colourTags;

    @Column(length = 50)
    private String locationArea;

    @Column(length = 200)
    private String locationDetail;

    @Column(length = 200)
    private String personalityTags;

    @Column(columnDefinition = "TEXT")
    private String personalityDesc;

    @Column(length = 100)
    private String healthStatus;

    private Boolean sterilized = false;

    @Enumerated(EnumType.STRING)
    private AdoptStatus adoptStatus = AdoptStatus.UNCLAIMED;

    @Enumerated(EnumType.STRING)
    private CatStatus status = CatStatus.ACTIVE;

    @Column(precision = 4, scale = 1)
    private BigDecimal weight;

    private Long coverPhotoId;

    private Long fatherId;

    private Long motherId;

    @Column(length = 255)
    private String friendIds;

    private LocalDate firstSightingTime;

    @Column(length = 200)
    private String firstSightingLocation;

    private LocalDateTime missingTime;

    @Column(length = 200)
    private String deathReason;

    private LocalDate deliveryTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String featureVector;

    private Integer likeCount = 0;

    private Integer followCount = 0;

    private Integer ratingCount = 0;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgR1 = BigDecimal.ZERO;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgR2 = BigDecimal.ZERO;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgR3 = BigDecimal.ZERO;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgR4 = BigDecimal.ZERO;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgR5 = BigDecimal.ZERO;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    private Long creatorId;

    private Integer deleted = 0;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum Gender { MALE, FEMALE, UNKNOWN }
    public enum AdoptStatus { UNCLAIMED, ADOPTED, SEEKING }
    public enum CatStatus { ACTIVE, SEEKING_ADOPT, MISSING, DECEASED }
}