package com.nanxin.catbook.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CommentRequest {
    @NotBlank private String content;
}