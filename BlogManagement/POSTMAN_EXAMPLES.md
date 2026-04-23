# Postman Testing Examples

## Setup
1. Set base URL variable: `{{baseUrl}}` = `http://localhost:8082`
2. Create environment variable: `{{userId}}` = `1` (or any valid user ID from User service)

## Test Scenarios

### Scenario 1: Create Blog Post as User 1
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Spring Boot Best Practices",
  "content": "Here are some best practices for Spring Boot development...",
  "published": true
}

Expected: 201 Created
Response includes userId: 1
```

### Scenario 2: Create Blog Post as User 2
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 2
  Content-Type: application/json

Body:
{
  "title": "Microservices Architecture",
  "content": "Understanding microservices patterns...",
  "published": false
}

Expected: 201 Created
Response includes userId: 2
```

### Scenario 3: Try Creating Without Header (Should Fail)
```
POST {{baseUrl}}/api/blog-posts
Headers:
  Content-Type: application/json

Body:
{
  "title": "This Should Fail",
  "content": "No user header provided",
  "published": true
}

Expected: 401 Unauthorized
Message: "No authenticated user found. Please provide X-User-Id header."
```

### Scenario 4: Get All Blog Posts (No Auth Required)
```
GET {{baseUrl}}/api/blog-posts

Expected: 200 OK
Returns all blog posts with their userId fields
```

### Scenario 5: Get My Posts (User 1)
```
GET {{baseUrl}}/api/blog-posts/my-posts
Headers:
  X-User-Id: 1

Expected: 200 OK
Returns only posts where userId = 1
```

### Scenario 6: Get Posts by User ID
```
GET {{baseUrl}}/api/blog-posts?userId=2

Expected: 200 OK
Returns only posts where userId = 2
```

### Scenario 7: Get Published Posts Only
```
GET {{baseUrl}}/api/blog-posts?published=true

Expected: 200 OK
Returns only posts where published = true
```

### Scenario 8: Update Own Post (Should Succeed)
```
PUT {{baseUrl}}/api/blog-posts/1
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Updated Title",
  "content": "Updated content by the owner",
  "published": true
}

Expected: 200 OK
Post is updated successfully
```

### Scenario 9: Try Updating Another User's Post (Should Fail)
```
PUT {{baseUrl}}/api/blog-posts/1
Headers:
  X-User-Id: 2
  Content-Type: application/json

Body:
{
  "title": "Trying to Hack",
  "content": "This should not work",
  "published": true
}

Expected: 403 Forbidden
Message: "You are not authorized to update this blog post"
```

### Scenario 10: Delete Own Post (Should Succeed)
```
DELETE {{baseUrl}}/api/blog-posts/1
Headers:
  X-User-Id: 1

Expected: 204 No Content
Post is deleted successfully
```

### Scenario 11: Try Deleting Another User's Post (Should Fail)
```
DELETE {{baseUrl}}/api/blog-posts/2
Headers:
  X-User-Id: 1

Expected: 403 Forbidden
Message: "You are not authorized to delete this blog post"
```

### Scenario 12: Get Specific Post
```
GET {{baseUrl}}/api/blog-posts/1

Expected: 200 OK
Returns the blog post with all details including userId
```

## Quick Test Flow

1. **Setup Phase:**
   - Create 2-3 posts as User 1 (X-User-Id: 1)
   - Create 2-3 posts as User 2 (X-User-Id: 2)

2. **Read Phase:**
   - Get all posts (no header needed)
   - Get User 1's posts via `/my-posts` with X-User-Id: 1
   - Get User 2's posts via `?userId=2`

3. **Update Phase:**
   - User 1 updates their own post ✓
   - User 1 tries to update User 2's post ✗ (403)

4. **Delete Phase:**
   - User 2 deletes their own post ✓
   - User 2 tries to delete User 1's post ✗ (403)

5. **Error Cases:**
   - Try creating post without X-User-Id header ✗ (401)
   - Try updating post without X-User-Id header ✗ (401)
   - Try deleting post without X-User-Id header ✗ (401)

## Validation Tests

### Invalid Title (Too Short)
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Hi",
  "content": "Valid content here",
  "published": true
}

Expected: 400 Bad Request
Validation error: "Title must be between 3 and 200 characters"
```

### Invalid Content (Too Short)
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Valid Title",
  "content": "Short",
  "published": true
}

Expected: 400 Bad Request
Validation error: "Content must be at least 10 characters"
```

### Missing Required Fields
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Valid Title"
}

Expected: 400 Bad Request
Validation errors for missing fields
```

## Notes
- All POST/PUT/DELETE operations require `X-User-Id` header
- GET operations (except `/my-posts`) don't require authentication
- User can only modify their own posts
- The `userId` in response matches the `X-User-Id` header used during creation
