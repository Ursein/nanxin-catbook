package com.nanxin.catbook.dto;

import lombok.Data;

@Data
public class RecommendItem {
    private Long catId; private String name; private String nickname; private Double score;
    private String coverPhotoUrl;
}