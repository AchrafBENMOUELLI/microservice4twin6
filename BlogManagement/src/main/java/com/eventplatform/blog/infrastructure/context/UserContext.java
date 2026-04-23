package com.eventplatform.blog.infrastructure.context;

import com.eventplatform.blog.infrastructure.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserContext {
    
    private static final String USER_ID_HEADER = "X-User-Id";
    
    /**
     * Get the current user ID from the request headers
     * @return the user ID, or null if not present
     */
    public Long getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format in header: " + userIdHeader);
        }
    }
    
    /**
     * Check if a user is currently authenticated (has a valid user ID in headers)
     * @return true if user ID is present, false otherwise
     */
    public boolean isUserAuthenticated() {
        return getCurrentUserId() != null;
    }
    
    /**
     * Get the current user ID or throw an exception if not authenticated
     * @return the user ID
     * @throws UnauthorizedException if no user is authenticated
     */
    public Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("No authenticated user found. Please provide X-User-Id header.");
        }
        return userId;
    }
    
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
