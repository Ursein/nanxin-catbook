package com.nanxin.catbook.config;

import com.nanxin.catbook.dto.UserInfo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 从请求中获取当前登录用户信息（由 JwtInterceptor 注入）
 */
public class CurrentUser {

    private static final String ATTR_USER = "_current_user";

    public static void set(HttpServletRequest request, UserInfo user) {
        request.setAttribute(ATTR_USER, user);
    }

    public static UserInfo get(HttpServletRequest request) {
        return (UserInfo) request.getAttribute(ATTR_USER);
    }

    public static Long getId(HttpServletRequest request) {
        UserInfo u = get(request);
        return u != null ? u.getId() : null;
    }
}