# Event Integration - Blog Microservice

## Overview
Blog posts are now linked to Events. Each blog post must be associated with a valid event, and the Event service is called to validate event existence and enrich blog post responses.

## What Changed

### 1. BlogPost Entity
**Added field:**
- `eventId` (Long, NOT NULL) - Links the blog post to an event

### 2. BlogPostRequest DTO
**Added field:**
- `eventId` (Long, required) - Must be provided when creating/updating blog posts

### 3. BlogPostResponse DTO
**Added fields:**
- `eventId` (Long) - The ID of the associated event
- `event` (EventDTO) - Full event details fetched from Event service

### 4. BlogPostService
**New functionality:**
- Validates event exists before creating/updating blog posts
- Fetches event details from Event service for responses
- New method: `getBlogPostsByEventId(Long eventId)`

### 5. BlogPostRepository
**New method:**
- `findByEventId(Long eventId)` - Find all blog posts for a specific event

### 6. BlogPostController
**Updated endpoint:**
- `GET /api/blog-posts?eventId={id}` - Get all blog posts for a specific event

### 7. Exception Handling
**New exception:**
- `EventNotFoundException` - Thrown when event doesn't exist (404)

## API Changes

### Create Blog Post (Updated)
```http
POST http://localhost:8082/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "My Event Blog Post",
  "content": "This is a blog post about the event",
  "eventId": 1,
  "published": true
}
```

**Validation:**
- Event with ID 1 must exist in Event service
- If event doesn't exist → 404 Event Not Found

### Update Blog Post (Updated)
```http
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

**Validation:**
- User must own the blog post
- Event with new ID must exist

### Response Format (Updated)
```json
{
  "id": 1,
  "title": "My Event Blog Post",
  "content": "This is a blog post about the event",
  "userId": 1,
  "eventId": 1,
  "event": {
    "id": 1,
    "title": "Spring Boot Conference 2026",
    "description": "Annual Spring Boot conference",
    "date": "2026-06-15",
    "location": "San Francisco, CA",
    "price": 299.99,
    "imageUrl": "https://example.com/event.jpg"
  },
  "published": true,
  "createdAt": "2026-04-09T10:30:00",
  "updatedAt": "2026-04-09T10:30:00"
}
```

### Get Blog Posts by Event
```http
GET http://localhost:8082/api/blog-posts?eventId=1
```

Returns all blog posts associated with event ID 1.

## Query Parameters Summary

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| published | Boolean | Filter by published status | `?published=true` |
| userId | Long | Filter by user ID | `?userId=1` |
| eventId | Long | Filter by event ID | `?eventId=1` |

## Event Validation Flow

```
1. User creates blog post with eventId
                ↓
2. BlogPostService validates event exists
                ↓
3. Call Event service: GET /api/events/{eventId}
                ↓
        ┌───────┴───────┐
        ↓               ↓
   Event Found    Event Not Found
        ↓               ↓
   Save Post      Throw EventNotFoundException
        ↓               ↓
   Return 201     Return 404
```

## Event Enrichment Flow

```
1. User requests blog post(s)
                ↓
2. BlogPostService fetches from database
                ↓
3. For each blog post, fetch event details
                ↓
4. Call Event service: GET /api/events/{eventId}
                ↓
        ┌───────┴───────┐
        ↓               ↓
   Event Found    Event Not Found
        ↓               ↓
   Include Event   event = null
        ↓               ↓
   Return with     Return with
   event data      eventId only
```

## Error Responses

### Event Not Found (404)
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 404,
  "error": "Event Not Found",
  "message": "Event not found with id: 999",
  "path": "/api/blog-posts"
}
```

### Event Service Unavailable (500)
```json
{
  "timestamp": "2026-04-09T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error communicating with Event service: Connection refused",
  "path": "/api/blog-posts"
}
```

## Feign Client Configuration

The Event service is accessed via Feign client:

```java
@FeignClient(
    name = "event-service",
    url = "http://localhost:8088"
)
public interface EventClient {
    @GetMapping("/api/events/{id}")
    EventDTO getEventById(@PathVariable Long id);
}
```

**Configuration:**
- Event service URL: `http://localhost:8088`
- Endpoint: `GET /api/events/{id}`

## Testing Scenarios

### Scenario 1: Create Blog Post with Valid Event
```
1. Ensure Event service is running
2. Create an event (or use existing event ID)
3. Create blog post with that eventId
4. Verify response includes event details
```

### Scenario 2: Create Blog Post with Invalid Event
```
1. Try to create blog post with eventId=999 (non-existent)
2. Expect 404 Event Not Found error
3. Blog post should NOT be created
```

### Scenario 3: Update Blog Post to Different Event
```
1. Create blog post with eventId=1
2. Update blog post to eventId=2
3. Verify event is validated
4. Verify response includes new event details
```

### Scenario 4: Get Blog Posts by Event
```
1. Create multiple blog posts for eventId=1
2. Create blog posts for eventId=2
3. GET /api/blog-posts?eventId=1
4. Verify only posts for event 1 are returned
```

### Scenario 5: Event Service Down
```
1. Stop Event service
2. Try to create blog post
3. Expect 500 Internal Server Error
4. Try to get existing blog post
5. Response should include eventId but event=null
```

## Database Schema

```sql
CREATE TABLE blog_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,  -- NEW FIELD
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Recommended index for performance
CREATE INDEX idx_blog_posts_event_id ON blog_posts(event_id);
```

## Migration from Previous Version

If you have existing blog posts without eventId:

**Development (Quick):**
```sql
DROP TABLE IF EXISTS blog_posts;
-- Restart application to recreate with new schema
```

**Production (Safe):**
```sql
-- 1. Add new column (nullable first)
ALTER TABLE blog_posts ADD COLUMN event_id BIGINT;

-- 2. Update existing records with a default event ID
UPDATE blog_posts SET event_id = 1 WHERE event_id IS NULL;

-- 3. Make column NOT NULL
ALTER TABLE blog_posts MODIFY event_id BIGINT NOT NULL;

-- 4. Add index for performance
CREATE INDEX idx_blog_posts_event_id ON blog_posts(event_id);
```

## Postman Examples

### Create Blog Post for Event
```
POST http://localhost:8082/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Highlights from Spring Boot Conference",
  "content": "Here are the key takeaways from the conference...",
  "eventId": 1,
  "published": true
}

Expected: 201 Created
Response includes full event details
```

### Get All Blog Posts for an Event
```
GET http://localhost:8082/api/blog-posts?eventId=1

Expected: 200 OK
Returns array of blog posts for event 1
Each post includes full event details
```

### Try Creating with Non-Existent Event
```
POST http://localhost:8082/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "This Should Fail",
  "content": "Event doesn't exist",
  "eventId": 999,
  "published": true
}

Expected: 404 Not Found
Message: "Event not found with id: 999"
```

## Benefits

1. **Data Integrity**: Blog posts can only reference valid events
2. **Rich Responses**: Event details automatically included in responses
3. **Event Discovery**: Easy to find all blog posts for a specific event
4. **Validation**: Event existence validated before saving
5. **Graceful Degradation**: If Event service is down, blog posts still return (without event details)

## Best Practices

1. **Always validate eventId** before creating/updating blog posts
2. **Handle Feign exceptions** gracefully (service unavailable, timeouts)
3. **Cache event data** if Event service is slow (future enhancement)
4. **Index eventId** in database for query performance
5. **Monitor Event service** health to ensure blog post creation works

## Future Enhancements

1. **Event Caching**: Cache event details to reduce Feign calls
2. **Batch Event Fetching**: Fetch multiple events in one call for list endpoints
3. **Event Webhooks**: Update blog posts when events are deleted/updated
4. **Event Validation**: Check event date (don't allow posts for past events)
5. **Event Categories**: Filter blog posts by event category

## Troubleshooting

**Problem:** 404 Event Not Found when creating blog post  
**Solution:** Verify event exists in Event service, check Event service is running

**Problem:** Blog posts return with event=null  
**Solution:** Event service may be down or event was deleted, check Event service health

**Problem:** Slow response times  
**Solution:** Event service may be slow, consider implementing caching

**Problem:** Can't create blog post (500 error)  
**Solution:** Check Event service connectivity, verify Feign client configuration

## Configuration

Ensure Event service URL is configured correctly:

**application.properties:**
```properties
# Event Service Configuration (if using properties)
event.service.url=http://localhost:8088
```

Or update the `@FeignClient` annotation in `EventClient.java`:
```java
@FeignClient(
    name = "event-service",
    url = "${event.service.url:http://localhost:8088}"
)
```

## Testing Checklist

- [ ] Create blog post with valid eventId
- [ ] Create blog post with invalid eventId (should fail)
- [ ] Update blog post to different eventId
- [ ] Get blog posts by eventId
- [ ] Verify event details included in responses
- [ ] Test with Event service down (graceful degradation)
- [ ] Verify ownership validation still works
- [ ] Test all query parameters (published, userId, eventId)
