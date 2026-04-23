# User Context Implementation - Blog Microservice

## Overview
This implementation adds simple user authentication via HTTP headers to the Blog microservice. Blog posts are now automatically associated with the authenticated user without requiring user data in request bodies.

## Key Changes

### 1. UserContext Utility Class
**Location:** `infrastructure/context/UserContext.java`

Extracts user information from HTTP request headers:
- `getCurrentUserId()` - Returns the user ID from `X-User-Id` header (or null)
- `requireCurrentUserId()` - Returns user ID or throws exception if not present
- `isUserAuthenticated()` - Checks if user ID header exists

### 2. Updated Domain Model
**BlogPost Entity** now uses:
- `userId` (Long) instead of `author` (String)
- Stores the ID of the user who created the post

### 3. Updated DTOs
**BlogPostRequest:**
- Removed `author` field
- User is automatically set from headers

**BlogPostResponse:**
- Changed from `author` to `userId`
- Returns the user ID who owns the post

### 4. Enhanced Service Layer
**BlogPostService** now includes:
- Automatic user assignment on create
- Ownership validation on update/delete
- New method: `getCurrentUserBlogPosts()` - Get posts for authenticated user

### 5. New Endpoints
**BlogPostController:**
- `GET /api/blog-posts/my-posts` - Get all posts for the current user
- `GET /api/blog-posts?userId={id}` - Get posts by specific user ID

### 6. Exception Handling
- `UnauthorizedException` - Thrown when user tries to modify posts they don't own
- `IllegalStateException` - Thrown when `X-User-Id` header is missing

## Usage with Postman

### Creating a Blog Post
```http
POST http://localhost:8082/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "My First Blog Post",
  "content": "This is the content of my blog post",
  "published": true
}
```

### Updating a Blog Post (Only Owner)
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

### Deleting a Blog Post (Only Owner)
```http
DELETE http://localhost:8082/api/blog-posts/1
Headers:
  X-User-Id: 1
```

### Get Current User's Posts
```http
GET http://localhost:8082/api/blog-posts/my-posts
Headers:
  X-User-Id: 1
```

### Get Posts by User ID
```http
GET http://localhost:8082/api/blog-posts?userId=1
```

## Security Behavior

### Ownership Validation
- Users can only UPDATE or DELETE their own blog posts
- Attempting to modify another user's post returns `403 Forbidden`

### Missing Authentication
- Requests without `X-User-Id` header return `401 Unauthorized`
- Error message: "No authenticated user found. Please provide X-User-Id header."

## Response Examples

### Success Response
```json
{
  "id": 1,
  "title": "My Blog Post",
  "content": "Content here",
  "userId": 1,
  "published": true,
  "createdAt": "2026-04-09T10:30:00",
  "updatedAt": "2026-04-09T10:30:00"
}
```

### Unauthorized Error (403)
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You are not authorized to update this blog post",
  "path": "/api/blog-posts/1"
}
```

### Missing Header Error (401)
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "No authenticated user found. Please provide X-User-Id header.",
  "path": "/api/blog-posts"
}
```

## Future Integration with User Service

The current implementation is designed to easily integrate with the User microservice via Feign:

1. The `userId` field can be used to fetch user details from the User service
2. Add a Feign client method to get user information
3. Optionally enrich responses with user details (firstName, lastName, email)

Example future enhancement:
```java
@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
```

## Database Migration Note

If you have existing blog posts with `author` field, you'll need to:
1. Drop the existing `blog_posts` table, or
2. Create a migration script to convert `author` names to `userId` values

For development, the easiest approach is to drop and recreate the table:
```sql
DROP TABLE IF EXISTS blog_posts;
```

Spring Boot will automatically recreate the table with the new schema on next startup.

## Testing Checklist

- [ ] Create blog post with valid `X-User-Id` header
- [ ] Try creating without header (should fail with 401)
- [ ] Update own blog post (should succeed)
- [ ] Try updating another user's post (should fail with 403)
- [ ] Delete own blog post (should succeed)
- [ ] Try deleting another user's post (should fail with 403)
- [ ] Get current user's posts via `/my-posts`
- [ ] Filter posts by userId parameter
