package com.eventplatform.blog.infrastructure.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {
    
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            try {
                currentUserId.set(Long.parseLong(userIdHeader));
            } catch (NumberFormatException e) {
                // Invalid user ID format
                currentUserId.remove();
            }
        }
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        currentUserId.remove();
    }
    
    public static Long getCurrentUserId() {
        return currentUserId.get();
    }
}
