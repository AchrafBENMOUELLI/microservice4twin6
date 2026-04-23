# Implementation Summary: User Context for Blog Microservice

## What Was Implemented

A simple, header-based user authentication system that automatically associates blog posts with users without requiring Spring Security or JWT.

## Files Created

### 1. UserContext.java
**Path:** `infrastructure/context/UserContext.java`
- Extracts `X-User-Id` from HTTP request headers
- Provides methods to get current user ID
- Validates user authentication

### 2. UnauthorizedException.java
**Path:** `infrastructure/exception/UnauthorizedException.java`
- Custom exception for authorization failures
- Thrown when users try to modify posts they don't own

## Files Modified

### 1. BlogPost.java (Entity)
**Changes:**
- Replaced `author` (String) with `userId` (Long)
- Now stores the ID of the user who created the post

### 2. BlogPostRequest.java (DTO)
**Changes:**
- Removed `author` field
- User is now automatically set from headers

### 3. BlogPostResponse.java (DTO)
**Changes:**
- Changed `author` to `userId`
- Returns user ID instead of author name

### 4. BlogPostRepository.java
**Changes:**
- Replaced `findByAuthor(String)` with `findByUserId(Long)`

### 5. BlogPostService.java
**Changes:**
- Added `UserContext` dependency injection
- `createBlogPost()`: Automatically sets userId from headers
- `updateBlogPost()`: Validates ownership before update
- `deleteBlogPost()`: Validates ownership before delete
- Added `getCurrentUserBlogPosts()`: Get posts for authenticated user
- Renamed `getBlogPostsByAuthor()` to `getBlogPostsByUserId()`

### 6. BlogPostController.java
**Changes:**
- Updated query parameter from `author` to `userId`
- Added new endpoint: `GET /api/blog-posts/my-posts`

### 7. GlobalExceptionHandler.java
**Changes:**
- Added handler for `UnauthorizedException` (403 Forbidden)
- Added handler for `IllegalStateException` (401 Unauthorized)

## How It Works

### Request Flow
1. Client sends request with `X-User-Id` header
2. `UserContext` extracts user ID from header
3. Service layer uses `UserContext` to get current user
4. Blog post is created/updated with the authenticated user's ID

### Security Flow
1. On UPDATE/DELETE: Service checks if `blogPost.userId == currentUserId`
2. If match: Operation proceeds
3. If no match: `UnauthorizedException` thrown (403 Forbidden)
4. If no header: `IllegalStateException` thrown (401 Unauthorized)

## API Changes

### New Endpoints
- `GET /api/blog-posts/my-posts` - Get current user's posts (requires X-User-Id header)

### Modified Endpoints
- `GET /api/blog-posts?userId={id}` - Changed from `?author={name}`

### Request Changes
- All POST/PUT/DELETE operations now require `X-User-Id` header
- `author` field removed from request body

### Response Changes
- Responses now include `userId` instead of `author`

## Testing

### Required Header
All create/update/delete operations must include:
```
X-User-Id: 1
```

### Example Request
```bash
curl -X POST http://localhost:8082/api/blog-posts \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Blog Post",
    "content": "This is my content",
    "published": true
  }'
```

## Database Impact

### Schema Change
The `blog_posts` table now has:
- `user_id` (BIGINT) instead of `author` (VARCHAR)

### Migration Required
If you have existing data:
1. Option A: Drop and recreate table (development)
2. Option B: Create migration script to map authors to user IDs (production)

For development:
```sql
DROP TABLE IF EXISTS blog_posts;
```

## Benefits

1. **Automatic User Association**: No need to pass user data in request body
2. **Ownership Validation**: Users can only modify their own posts
3. **Simple Implementation**: No Spring Security or JWT complexity
4. **Future-Ready**: Easy to integrate with User microservice via Feign
5. **Clean API**: Request bodies only contain blog post data

## Future Enhancements

1. **User Service Integration**: Fetch user details via Feign client
2. **Response Enrichment**: Include user name/email in blog post responses
3. **Admin Override**: Allow admin users to modify any post
4. **Soft Delete**: Track who deleted posts
5. **Audit Trail**: Log all modifications with user information

## Constraints Met

✅ No Spring Security  
✅ No JWT  
✅ No hardcoded user assumptions  
✅ Reuses existing User service structure (userId)  
✅ Simple and minimal implementation  
✅ Works immediately with Postman  
✅ Designed for future Feign integration  

## Next Steps

1. Test all endpoints with Postman (see POSTMAN_EXAMPLES.md)
2. Verify ownership validation works correctly
3. (Optional) Integrate with User service to enrich responses
4. (Optional) Add admin role support for cross-user modifications
