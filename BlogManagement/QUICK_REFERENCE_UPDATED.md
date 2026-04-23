# Quick Reference: Blog Microservice (User Context & Event Integration)

## Headers Required
All create/update/delete operations need:
```
X-User-Id: <user_id>
```

## API Endpoints

| Method | Endpoint | Header Required | Description |
|--------|----------|----------------|-------------|
| POST | `/api/blog-posts` | ✅ X-User-Id | Create blog post (requires eventId) |
| GET | `/api/blog-posts` | ❌ | Get all posts |
| GET | `/api/blog-posts/{id}` | ❌ | Get specific post |
| GET | `/api/blog-posts/my-posts` | ✅ X-User-Id | Get current user's posts |
| GET | `/api/blog-posts?userId={id}` | ❌ | Get posts by user ID |
| GET | `/api/blog-posts?eventId={id}` | ❌ | Get posts by event ID |
| GET | `/api/blog-posts?published=true` | ❌ | Get published posts |
| PUT | `/api/blog-posts/{id}` | ✅ X-User-Id | Update post (owner only) |
| DELETE | `/api/blog-posts/{id}` | ✅ X-User-Id | Delete post (owner only) |

## Request Body (Create/Update)
```json
{
  "title": "Blog Post Title (3-200 chars)",
  "content": "Blog post content (min 10 chars)",
  "eventId": 1,
  "published": true
}
```

**Notes:**
- `userId` is automatically set from `X-User-Id` header
- `eventId` is required and must reference a valid event
- Event is validated before saving

## Response Body
```json
{
  "id": 1,
  "title": "Blog Post Title",
  "content": "Blog post content",
  "userId": 1,
  "eventId": 1,
  "event": {
    "id": 1,
    "title": "Spring Boot Conference",
    "description": "Annual conference",
    "date": "2026-06-15",
    "location": "San Francisco",
    "price": 299.99,
    "imageUrl": "https://example.com/event.jpg"
  },
  "published": true,
  "createdAt": "2026-04-09T10:30:00",
  "updatedAt": "2026-04-09T10:30:00"
}
```

## Query Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| published | Boolean | Filter by published status | `?published=true` |
| userId | Long | Filter by user ID | `?userId=1` |
| eventId | Long | Filter by event ID | `?eventId=1` |

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

### 404 Event Not Found
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 404,
  "error": "Event Not Found",
  "message": "Event not found with id: 999",
  "path": "/api/blog-posts"
}
```

### 404 Blog Post Not Found
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Blog post not found with id: 999",
  "path": "/api/blog-posts/999"
}
```

## Postman Examples

### Create Blog Post
```
POST http://localhost:8082/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Highlights from Spring Conference",
  "content": "Here are the key takeaways...",
  "eventId": 1,
  "published": true
}
```

### Get Blog Posts for an Event
```
GET http://localhost:8082/api/blog-posts?eventId=1
```

### Get My Blog Posts
```
GET http://localhost:8082/api/blog-posts/my-posts
Headers:
  X-User-Id: 1
```

### Update Blog Post
```
PUT http://localhost:8082/api/blog-posts/1
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Updated Title",
  "content": "Updated content",
  "eventId": 2,
  "published": true
}
```

## Validation Rules

### Title
- Required
- Min length: 3 characters
- Max length: 200 characters

### Content
- Required
- Min length: 10 characters

### EventId
- Required
- Must reference a valid event in Event service
- Validated before saving

### Published
- Required
- Boolean value (true/false)

## Ownership Rules

✅ **Allowed:**
- Create post with your user ID and valid eventId
- Update your own posts
- Delete your own posts
- Read any post

❌ **Not Allowed:**
- Update another user's post → 403 Forbidden
- Delete another user's post → 403 Forbidden
- Create/update/delete without header → 401 Unauthorized
- Create/update with invalid eventId → 404 Event Not Found

## Database Schema

```sql
CREATE TABLE blog_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_blog_posts_user_id ON blog_posts(user_id);
CREATE INDEX idx_blog_posts_event_id ON blog_posts(event_id);
```

## Key Features

1. **User Context**: Automatic user assignment via headers
2. **Event Integration**: Blog posts linked to valid events
3. **Event Validation**: Event existence checked before saving
4. **Event Enrichment**: Event details included in responses
5. **Ownership Validation**: Users can only modify their own posts
6. **Flexible Querying**: Filter by user, event, or published status

## Testing Checklist

- [ ] Create post with valid eventId
- [ ] Create post with invalid eventId (should fail)
- [ ] Create post without X-User-Id header (should fail)
- [ ] Update own post with new eventId
- [ ] Update another user's post (should fail)
- [ ] Delete own post
- [ ] Delete another user's post (should fail)
- [ ] Get posts by eventId
- [ ] Get posts by userId
- [ ] Get my posts
- [ ] Verify event details in responses

## Common Issues

**Issue:** 404 Event Not Found  
**Solution:** Ensure Event service is running and event exists

**Issue:** 401 Unauthorized  
**Solution:** Include X-User-Id header in request

**Issue:** 403 Forbidden  
**Solution:** Verify you own the blog post you're trying to modify

**Issue:** event=null in response  
**Solution:** Event service may be down or event was deleted

## Service Dependencies

- **Event Service**: http://localhost:8088
  - Used to validate events
  - Used to fetch event details for responses
  
- **User Service**: http://localhost:8080 (for future integration)
  - User IDs referenced but not validated yet
  - Can be integrated via Feign client later

## Documentation Files

- **EVENT_INTEGRATION.md** - Detailed event integration guide
- **USER_CONTEXT_IMPLEMENTATION.md** - User context implementation
- **POSTMAN_EXAMPLES.md** - Complete Postman test scenarios
- **ARCHITECTURE_FLOW.md** - Architecture diagrams
