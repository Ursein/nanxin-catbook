package com.nanxin.catbook.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class RatingRequest {
    private Integer r1;
    private Integer r2;
    private Integer r3;
    private Integer r4;
    private Integer r5;
}