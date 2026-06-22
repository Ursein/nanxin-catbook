package com.nanxin.catbook.config;

import com.nanxin.catbook.dto.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtConfig jwtConfig;

    public JwtInterceptor(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return true; // 没有 Token 也能通过，Controller 自己判断是否需要登录
        }

        String token = auth.substring(7);
        if (jwtConfig.validateToken(token)) {
            Long userId = jwtConfig.getUserIdFromToken(token);
            String role = jwtConfig.getRoleFromToken(token);
            UserInfo userInfo = new UserInfo();
            userInfo.setId(userId);
            userInfo.setRole(role);
            CurrentUser.set(request, userInfo);
        }
        return true;
    }
}