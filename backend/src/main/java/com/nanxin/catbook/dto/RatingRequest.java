package com.nanxin.catbook.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class RatingRequest {
    @Min(1) @Max(5) private Integer r1;
    @Min(1) @Max(5) private Integer r2;
    @Min(1) @Max(5) private Integer r3;
    @Min(1) @Max(5) private Integer r4;
    @Min(1) @Max(5) private Integer r5;
}