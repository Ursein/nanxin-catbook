package com.nanxin.catbook.dto;

import lombok.Data;

@Data
public class CatItem {
    private Long id; private String name; private String nickname;
    private String colourTags; private String locationArea; private String gender;
    private String status; private Boolean sterilized; private String coverPhotoUrl;
    private Integer likeCount; private Integer followCount; private Double avgRating;
}