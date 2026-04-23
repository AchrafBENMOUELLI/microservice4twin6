# Blog Microservice - Complete Implementation Guide

## 🎯 Overview

The Blog microservice is a fully-featured Spring Boot application with:
- **User Context**: Header-based authentication with automatic user assignment
- **Event Integration**: Blog posts linked to validated events with enriched responses
- **Ownership Control**: Users can only modify their own posts
- **Data Validation**: Event existence validated before saving
- **Rich Responses**: Event details automatically included

## 🚀 Quick Start

### 1. Prerequisites
- Java 17+
- MySQL database
- Event service running on port 8088
- Maven

### 2. Database Setup
```sql
CREATE DATABASE blog_db;
```

### 3. Configuration
Update `application.properties`:
```properties
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/blog_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 4. Run the Application
```bash
cd BlogManagement
./mvnw spring-boot:run
```

### 5. Test with Postman
```http
POST http://localhost:8082/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "My First Blog Post",
  "content": "This is about the Spring Boot Conference",
  "eventId": 1,
  "published": true
}
```

## 📋 Features

### User Context
- ✅ Extract user ID from `X-User-Id` header
- ✅ Automatic user assignment on creation
- ✅ Ownership validation on updates/deletes
- ✅ No Spring Security or JWT required

### Event Integration
- ✅ Blog posts linked to events via `eventId`
- ✅ Event validation before saving
- ✅ Event details enrichment in responses
- ✅ Query blog posts by event
- ✅ Feign client integration

### Security
- ✅ Users can only modify their own posts
- ✅ Event existence validated
- ✅ Proper error handling (401, 403, 404, 500)

## 🔌 API Endpoints

### Create Blog Post
```http
POST /api/blog-posts
Headers: X-User-Id: 1
Body: { title, content, eventId, published }
Response: 201 Created
```

### Get All Blog Posts
```http
GET /api/blog-posts
Response: 200 OK
```

### Get Blog Post by ID
```http
GET /api/blog-posts/{id}
Response: 200 OK
```

### Get My Blog Posts
```http
GET /api/blog-posts/my-posts
Headers: X-User-Id: 1
Response: 200 OK
```

### Get Blog Posts by User
```http
GET /api/blog-posts?userId=1
Response: 200 OK
```

### Get Blog Posts by Event
```http
GET /api/blog-posts?eventId=1
Response: 200 OK
```

### Get Published Blog Posts
```http
GET /api/blog-posts?published=true
Response: 200 OK
```

### Update Blog Post
```http
PUT /api/blog-posts/{id}
Headers: X-User-Id: 1
Body: { title, content, eventId, published }
Response: 200 OK
```

### Delete Blog Post
```http
DELETE /api/blog-posts/{id}
Headers: X-User-Id: 1
Response: 204 No Content
```

## 📝 Request/Response Examples

### Create Request
```json
{
  "title": "Spring Boot Best Practices",
  "content": "Here are some best practices for Spring Boot development...",
  "eventId": 1,
  "published": true
}
```

### Response
```json
{
  "id": 1,
  "title": "Spring Boot Best Practices",
  "content": "Here are some best practices...",
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

## ⚠️ Error Responses

### 401 Unauthorized
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "No authenticated user found. Please provide X-User-Id header."
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You are not authorized to update this blog post"
}
```

### 404 Event Not Found
```json
{
  "status": 404,
  "error": "Event Not Found",
  "message": "Event not found with id: 999"
}
```

### 404 Blog Post Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Blog post not found with id: 999"
}
```

## 🗄️ Database Schema

```sql
CREATE TABLE blog_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_event_id (event_id)
);
```

## 🏗️ Architecture

```
Client (Postman)
    ↓ X-User-Id: 1, Body: { eventId: 1, ... }
BlogPostController
    ↓
BlogPostService
    ├─→ UserContext (extract userId from header)
    ├─→ EventClient (validate event exists)
    └─→ BlogPostRepository (save to database)
```

## 📚 Documentation Files

| File | Description |
|------|-------------|
| **EVENT_INTEGRATION.md** | Detailed event integration guide |
| **USER_CONTEXT_IMPLEMENTATION.md** | User context implementation details |
| **QUICK_REFERENCE_UPDATED.md** | Quick reference card |
| **POSTMAN_EXAMPLES_UPDATED.md** | Complete Postman test scenarios |
| **IMPLEMENTATION_SUMMARY_UPDATED.md** | Summary of all changes |
| **ARCHITECTURE_FLOW.md** | Architecture diagrams and flows |

## ✅ Validation Rules

| Field | Rules |
|-------|-------|
| title | Required, 3-200 characters |
| content | Required, min 10 characters |
| eventId | Required, must exist in Event service |
| published | Required, boolean |
| X-User-Id | Required for create/update/delete |

## 🔐 Security Rules

| Action | Rule |
|--------|------|
| Create | Requires X-User-Id header |
| Update | Must be post owner |
| Delete | Must be post owner |
| Read | No authentication required |

## 🧪 Testing

### Test Checklist
- [ ] Create blog post with valid eventId
- [ ] Create blog post with invalid eventId (should fail)
- [ ] Create blog post without X-User-Id (should fail)
- [ ] Update own blog post
- [ ] Update another user's post (should fail)
- [ ] Delete own blog post
- [ ] Delete another user's post (should fail)
- [ ] Get blog posts by eventId
- [ ] Get blog posts by userId
- [ ] Get my blog posts
- [ ] Verify event details in responses

### Postman Collection
See **POSTMAN_EXAMPLES_UPDATED.md** for complete test scenarios.

## 🔧 Configuration

### Event Service URL
Update in `EventClient.java`:
```java
@FeignClient(
    name = "event-service",
    url = "http://localhost:8088"
)
```

### Database Connection
Update in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/blog_db
spring.datasource.username=root
spring.datasource.password=your_password
```

## 🚨 Troubleshooting

| Problem | Solution |
|---------|----------|
| 401 Unauthorized | Add X-User-Id header |
| 403 Forbidden | Verify you own the post |
| 404 Event Not Found | Check Event service is running and event exists |
| 500 Internal Server Error | Check Event service connectivity |
| event=null in response | Event service may be down (graceful degradation) |

## 🎓 Key Concepts

### UserContext
Utility class that extracts user information from HTTP request headers.

```java
@Autowired
private UserContext userContext;

Long userId = userContext.requireCurrentUserId();
```

### Event Validation
Before saving a blog post, the service validates that the event exists:

```java
EventDTO event = eventClient.getEventById(eventId);
// Throws EventNotFoundException if not found
```

### Event Enrichment
When returning blog posts, event details are automatically fetched and included:

```json
{
  "eventId": 1,
  "event": { /* full event details */ }
}
```

### Ownership Validation
Users can only modify their own posts:

```java
if (!blogPost.getUserId().equals(currentUserId)) {
    throw new UnauthorizedException();
}
```

## 🔄 Migration

### From Previous Version

**Development:**
```sql
DROP TABLE IF EXISTS blog_posts;
-- Restart application
```

**Production:**
```sql
ALTER TABLE blog_posts ADD COLUMN event_id BIGINT;
UPDATE blog_posts SET event_id = 1 WHERE event_id IS NULL;
ALTER TABLE blog_posts MODIFY event_id BIGINT NOT NULL;
CREATE INDEX idx_blog_posts_event_id ON blog_posts(event_id);
```

## 🌟 Best Practices

1. **Always include X-User-Id header** for create/update/delete operations
2. **Validate eventId** exists before creating blog posts
3. **Handle Feign exceptions** gracefully
4. **Index database columns** (user_id, event_id) for performance
5. **Monitor Event service** health

## 🚀 Future Enhancements

- [ ] User service integration for user validation
- [ ] Event caching to reduce Feign calls
- [ ] Batch event fetching for list endpoints
- [ ] Event webhooks for updates/deletes
- [ ] Admin role support
- [ ] Soft delete with audit trail
- [ ] Full-text search on content

## 📞 Support

For detailed information, refer to:
- **EVENT_INTEGRATION.md** - Event integration details
- **USER_CONTEXT_IMPLEMENTATION.md** - User context details
- **POSTMAN_EXAMPLES_UPDATED.md** - Testing examples
- **QUICK_REFERENCE_UPDATED.md** - Quick reference

## 📄 License

This is a Spring Boot microservice project for educational purposes.

---

**Ready to use!** Start by creating an event in the Event service, then create blog posts linked to that event.
