package com.nanxin.catbook.dto;

import lombok.Data;

@Data
public class UserInfo {
    private Long id; private String username; private String nickname;
    private String email; private String avatar; private String role;
}