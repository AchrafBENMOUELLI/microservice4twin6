# User Context Implementation - Complete Guide

## 📋 Overview

This implementation adds simple user authentication to the Blog microservice using HTTP headers. Blog posts are automatically associated with users without requiring Spring Security or JWT.

## 🎯 What Was Accomplished

✅ User context extraction from HTTP headers (`X-User-Id`)  
✅ Automatic user assignment on blog post creation  
✅ Ownership validation on update/delete operations  
✅ Clean API - no user data in request bodies  
✅ Proper error handling (401, 403, 404)  
✅ Future-ready for User service integration via Feign  

## 📁 Files Created

1. **UserContext.java** - Extracts user ID from request headers
2. **UnauthorizedException.java** - Custom exception for authorization failures

## 📝 Files Modified

1. **BlogPost.java** - Changed `author` (String) to `userId` (Long)
2. **BlogPostRequest.java** - Removed `author` field
3. **BlogPostResponse.java** - Changed `author` to `userId`
4. **BlogPostRepository.java** - Updated query methods
5. **BlogPostService.java** - Added user context and ownership checks
6. **BlogPostController.java** - Added `/my-posts` endpoint
7. **GlobalExceptionHandler.java** - Added exception handlers

## 🚀 Quick Start

### 1. Database Migration (Development)
```sql
DROP TABLE IF EXISTS blog_posts;
```
Restart the application - Spring Boot will recreate the table with the new schema.

### 2. Test with Postman

**Create a blog post:**
```http
POST http://localhost:8082/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "My First Blog Post",
  "content": "This is my blog post content",
  "published": true
}
```

**Get your posts:**
```http
GET http://localhost:8082/api/blog-posts/my-posts
Headers:
  X-User-Id: 1
```

**Update your post:**
```http
PUT http://localhost:8082/api/blog-posts/1
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Updated Title",
  "content": "Updated content",
  "published": true
}
```

## 🔐 Security Features

### Ownership Validation
- Users can only update/delete their own posts
- Attempting to modify another user's post returns `403 Forbidden`

### Authentication Check
- All create/update/delete operations require `X-User-Id` header
- Missing header returns `401 Unauthorized`

### Example Scenarios

| Action | User ID | Post Owner | Result |
|--------|---------|------------|--------|
| Create | 1 | - | ✅ Success (userId=1) |
| Update | 1 | 1 | ✅ Success |
| Update | 1 | 2 | ❌ 403 Forbidden |
| Delete | 2 | 2 | ✅ Success |
| Delete | 2 | 1 | ❌ 403 Forbidden |
| Create | (none) | - | ❌ 401 Unauthorized |

## 📚 API Reference

### Endpoints

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/blog-posts` | ✅ | Create blog post |
| GET | `/api/blog-posts` | ❌ | Get all posts |
| GET | `/api/blog-posts/{id}` | ❌ | Get specific post |
| GET | `/api/blog-posts/my-posts` | ✅ | Get current user's posts |
| GET | `/api/blog-posts?userId={id}` | ❌ | Get posts by user |
| GET | `/api/blog-posts?published=true` | ❌ | Get published posts |
| PUT | `/api/blog-posts/{id}` | ✅ | Update post (owner only) |
| DELETE | `/api/blog-posts/{id}` | ✅ | Delete post (owner only) |

### Request Format (Create/Update)
```json
{
  "title": "Blog Post Title (3-200 chars)",
  "content": "Blog post content (min 10 chars)",
  "published": true
}
```

### Response Format
```json
{
  "id": 1,
  "title": "Blog Post Title",
  "content": "Blog post content",
  "userId": 1,
  "published": true,
  "createdAt": "2026-04-09T10:30:00",
  "updatedAt": "2026-04-09T10:30:00"
}
```

## 🛠️ Technical Implementation

### UserContext Class
```java
@Component
public class UserContext {
    public Long getCurrentUserId() {
        // Extracts X-User-Id from request headers
        // Returns null if not present
    }
    
    public Long requireCurrentUserId() {
        // Extracts X-User-Id from request headers
        // Throws IllegalStateException if not present
    }
}
```

### Service Layer Usage
```java
@Service
@RequiredArgsConstructor
public class BlogPostService {
    private final UserContext userContext;
    
    public BlogPostResponse createBlogPost(BlogPostRequest request) {
        // Automatically get user ID from headers
        Long userId = userContext.requireCurrentUserId();
        blogPost.setUserId(userId);
        // ...
    }
    
    public BlogPostResponse updateBlogPost(Long id, BlogPostRequest request) {
        Long currentUserId = userContext.requireCurrentUserId();
        BlogPost blogPost = findById(id);
        
        // Ownership check
        if (!blogPost.getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized");
        }
        // ...
    }
}
```

## 📖 Documentation Files

- **USER_CONTEXT_IMPLEMENTATION.md** - Detailed implementation guide
- **POSTMAN_EXAMPLES.md** - Complete Postman test scenarios
- **QUICK_REFERENCE.md** - Quick reference card
- **ARCHITECTURE_FLOW.md** - Architecture diagrams and flows
- **IMPLEMENTATION_SUMMARY.md** - Summary of all changes

## 🔄 Future Enhancements

### User Service Integration (via Feign)
```java
@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}

// Enrich blog post responses with user details
public BlogPostResponseWithUser getBlogPostWithUser(Long id) {
    BlogPost post = findById(id);
    UserDTO user = userClient.getUserById(post.getUserId());
    return new BlogPostResponseWithUser(post, user);
}
```

### Admin Role Support
```java
public void deleteBlogPost(Long id) {
    Long currentUserId = userContext.requireCurrentUserId();
    BlogPost blogPost = findById(id);
    
    // Allow admins to delete any post
    if (!blogPost.getUserId().equals(currentUserId) && !isAdmin()) {
        throw new UnauthorizedException("Not authorized");
    }
    // ...
}
```

## ✅ Testing Checklist

- [ ] Create post with valid header → Success
- [ ] Create post without header → 401 Unauthorized
- [ ] Update own post → Success
- [ ] Update another user's post → 403 Forbidden
- [ ] Delete own post → Success
- [ ] Delete another user's post → 403 Forbidden
- [ ] Get all posts (no auth) → Success
- [ ] Get my posts with header → Success
- [ ] Get posts by userId → Success
- [ ] Verify userId in response matches header

## 🐛 Troubleshooting

**Problem:** 401 Unauthorized on all requests  
**Solution:** Ensure `X-User-Id` header is included in request

**Problem:** 403 Forbidden when updating own post  
**Solution:** Verify the post's userId matches the X-User-Id header

**Problem:** Posts showing null userId  
**Solution:** Drop and recreate the blog_posts table

**Problem:** Invalid user ID format error  
**Solution:** Ensure X-User-Id contains a valid number (e.g., "1", not "abc")

## 📞 Support

For questions or issues:
1. Check the documentation files in this directory
2. Review POSTMAN_EXAMPLES.md for test scenarios
3. Check QUICK_REFERENCE.md for common patterns
4. Review ARCHITECTURE_FLOW.md for understanding the flow

## 🎉 Summary

You now have a fully functional user context system that:
- Automatically associates blog posts with users
- Validates ownership on modifications
- Provides clean, simple API
- Works immediately with Postman
- Is ready for future User service integration

Start testing with Postman using the examples in POSTMAN_EXAMPLES.md!
