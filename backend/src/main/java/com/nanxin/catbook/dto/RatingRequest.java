package com.nanxin.catbook.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class RatingRequest {
    @Min(1) @Max(5) private Integer rating;
}