# Quick Reference: User Context Implementation

## Header Required
All create/update/delete operations need:
```
X-User-Id: <user_id>
```

## Key Classes

### UserContext
```java
@Autowired
private UserContext userContext;

// Get current user ID (returns null if not present)
Long userId = userContext.getCurrentUserId();

// Get current user ID (throws exception if not present)
Long userId = userContext.requireCurrentUserId();

// Check if user is authenticated
boolean isAuth = userContext.isUserAuthenticated();
```

## API Endpoints

| Method | Endpoint | Header Required | Description |
|--------|----------|----------------|-------------|
| POST | `/api/blog-posts` | ✅ X-User-Id | Create blog post |
| GET | `/api/blog-posts` | ❌ | Get all posts |
| GET | `/api/blog-posts/{id}` | ❌ | Get specific post |
| GET | `/api/blog-posts/my-posts` | ✅ X-User-Id | Get current user's posts |
| GET | `/api/blog-posts?userId={id}` | ❌ | Get posts by user ID |
| GET | `/api/blog-posts?published=true` | ❌ | Get published posts |
| PUT | `/api/blog-posts/{id}` | ✅ X-User-Id | Update post (owner only) |
| DELETE | `/api/blog-posts/{id}` | ✅ X-User-Id | Delete post (owner only) |

## Request Body (Create/Update)
```json
{
  "title": "Blog Post Title",
  "content": "Blog post content (min 10 chars)",
  "published": true
}
```

Note: No `author` or `userId` field needed - automatically set from header!

## Response Body
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

## Error Responses

### 401 Unauthorized (Missing Header)
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "No authenticated user found. Please provide X-User-Id header.",
  "path": "/api/blog-posts"
}
```

### 403 Forbidden (Not Owner)
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You are not authorized to update this blog post",
  "path": "/api/blog-posts/1"
}
```

### 404 Not Found
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Blog post not found with id: 999",
  "path": "/api/blog-posts/999"
}
```

## Ownership Rules

✅ **Allowed:**
- Create post with your user ID
- Update your own posts
- Delete your own posts
- Read any post

❌ **Not Allowed:**
- Update another user's post → 403 Forbidden
- Delete another user's post → 403 Forbidden
- Create/update/delete without header → 401 Unauthorized

## Postman Setup

1. Create environment variable:
   - `baseUrl` = `http://localhost:8082`
   - `userId` = `1`

2. Add header to requests:
   - Key: `X-User-Id`
   - Value: `{{userId}}`

3. Test scenarios:
   - Create posts as User 1 (X-User-Id: 1)
   - Create posts as User 2 (X-User-Id: 2)
   - Try User 1 updating User 2's post (should fail)
   - Try creating without header (should fail)

## Code Examples

### Service Layer
```java
@Service
@RequiredArgsConstructor
public class BlogPostService {
    private final UserContext userContext;
    
    public BlogPostResponse createBlogPost(BlogPostRequest request) {
        Long userId = userContext.requireCurrentUserId();
        // userId is automatically extracted from X-User-Id header
        blogPost.setUserId(userId);
        // ...
    }
}
```

### Controller Layer
```java
@PostMapping
public ResponseEntity<BlogPostResponse> createBlogPost(
        @Valid @RequestBody BlogPostRequest request) {
    // No need to pass user info - handled by service
    BlogPostResponse response = blogPostService.createBlogPost(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

## Database Schema

```sql
CREATE TABLE blog_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,  -- Changed from 'author'
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## Migration from Old Schema

If you have existing data with `author` field:

**Development (Quick):**
```sql
DROP TABLE IF EXISTS blog_posts;
-- Restart application to recreate with new schema
```

**Production (Safe):**
```sql
-- 1. Add new column
ALTER TABLE blog_posts ADD COLUMN user_id BIGINT;

-- 2. Map authors to user IDs (manual or script)
UPDATE blog_posts SET user_id = 1 WHERE author = 'John Doe';

-- 3. Make column NOT NULL
ALTER TABLE blog_posts MODIFY user_id BIGINT NOT NULL;

-- 4. Drop old column
ALTER TABLE blog_posts DROP COLUMN author;
```

## Testing Checklist

- [ ] Create post with X-User-Id header
- [ ] Create post without header (should fail)
- [ ] Update own post (should succeed)
- [ ] Update another user's post (should fail)
- [ ] Delete own post (should succeed)
- [ ] Delete another user's post (should fail)
- [ ] Get all posts (no header needed)
- [ ] Get my posts with header
- [ ] Get posts by userId parameter
- [ ] Verify userId in response matches header

## Common Issues

**Issue:** 401 Unauthorized on all requests  
**Solution:** Make sure `X-User-Id` header is included

**Issue:** 403 Forbidden when updating post  
**Solution:** Verify the post belongs to the user in the header

**Issue:** Invalid user ID format  
**Solution:** Ensure X-User-Id contains a valid number (Long)

**Issue:** Posts not showing userId  
**Solution:** Check database schema was updated (drop table if needed)
