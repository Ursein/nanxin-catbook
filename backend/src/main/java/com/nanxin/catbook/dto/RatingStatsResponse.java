package com.nanxin.catbook.dto;

import lombok.Data;

@Data
public class RatingStatsResponse {
    private Double avgR1; private Double avgR2;
    private Double avgR3; private Double avgR4; private Double avgR5;
    private Integer ratingCount;
    private Integer myR1; private Integer myR2;
    private Integer myR3; private Integer myR4; private Integer myR5;
}