package com.nanxin.catbook.dto;

import lombok.Data;

@Data
public class PhotoItem {
    private Long id; private String url; private String compressedUrl;
    private String description;
    private String status; private Integer likeCount; private Boolean isLiked;
}