package com.nanxin.catbook.dto;

import lombok.Data;

@Data
public class RatingStatsResponse {
    private Double avgRating; private Integer ratingCount; private Integer myRating;
}