package com.nanxin.catbook.dto;

import lombok.Data;

@Data
public class CommentItem {
    private Long id; private String username; private String avatar; private String content; private String createdAt;
}