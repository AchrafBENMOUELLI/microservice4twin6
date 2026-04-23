# Postman Testing Examples - With Event Integration

## Setup
1. Set base URL variable: `{{baseUrl}}` = `http://localhost:8082`
2. Set Event service URL: `{{eventUrl}}` = `http://localhost:8088`
3. Create environment variables:
   - `{{userId}}` = `1`
   - `{{eventId}}` = `1`

## Prerequisites
- Event service must be running on port 8088
- At least one event must exist in Event service

## Test Scenarios

### Scenario 1: Create Event First (Event Service)
```
POST {{eventUrl}}/api/events
Headers:
  Content-Type: application/json

Body:
{
  "title": "Spring Boot Conference 2026",
  "description": "Annual Spring Boot conference",
  "date": "2026-06-15",
  "location": "San Francisco, CA",
  "price": 299.99,
  "imageUrl": "https://example.com/event.jpg"
}

Expected: 201 Created
Save the event ID for next steps
```

### Scenario 2: Create Blog Post with Valid Event
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Spring Boot Best Practices",
  "content": "Here are some best practices for Spring Boot development...",
  "eventId": 1,
  "published": true
}

Expected: 201 Created
Response includes:
- userId: 1
- eventId: 1
- event: { full event details }
```

### Scenario 3: Create Blog Post with Invalid Event (Should Fail)
```
POST {{baseUrl}}/api/blog-posts
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
Blog post should NOT be created
```

### Scenario 4: Create Blog Post Without EventId (Should Fail)
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Missing Event ID",
  "content": "No event ID provided",
  "published": true
}

Expected: 400 Bad Request
Validation error: "Event ID is required"
```

### Scenario 5: Create Multiple Blog Posts for Same Event
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Day 1 Highlights",
  "content": "First day of the conference was amazing...",
  "eventId": 1,
  "published": true
}

---

POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 2
  Content-Type: application/json

Body:
{
  "title": "Day 2 Highlights",
  "content": "Second day featured great speakers...",
  "eventId": 1,
  "published": true
}

Expected: Both created successfully
Both linked to same event
```

### Scenario 6: Get All Blog Posts for an Event
```
GET {{baseUrl}}/api/blog-posts?eventId=1

Expected: 200 OK
Returns all blog posts where eventId = 1
Each post includes full event details
```

### Scenario 7: Get Specific Blog Post with Event Details
```
GET {{baseUrl}}/api/blog-posts/1

Expected: 200 OK
Response includes:
- Blog post details
- userId
- eventId
- event: { full event details }
```

### Scenario 8: Update Blog Post to Different Event
```
PUT {{baseUrl}}/api/blog-posts/1
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Updated Title",
  "content": "Updated content for different event",
  "eventId": 2,
  "published": true
}

Expected: 200 OK (if event 2 exists)
Or: 404 Not Found (if event 2 doesn't exist)
```

### Scenario 9: Get My Posts (User 1)
```
GET {{baseUrl}}/api/blog-posts/my-posts
Headers:
  X-User-Id: 1

Expected: 200 OK
Returns only posts where userId = 1
Each post includes event details
```

### Scenario 10: Get Posts by User and Event (Combined)
```
# First get all posts for user 1
GET {{baseUrl}}/api/blog-posts?userId=1

# Then get all posts for event 1
GET {{baseUrl}}/api/blog-posts?eventId=1

# Compare results to see overlap
```

### Scenario 11: Try Creating Without User Header (Should Fail)
```
POST {{baseUrl}}/api/blog-posts
Headers:
  Content-Type: application/json

Body:
{
  "title": "No User Header",
  "content": "This should fail",
  "eventId": 1,
  "published": true
}

Expected: 401 Unauthorized
Message: "No authenticated user found. Please provide X-User-Id header."
```

### Scenario 12: Try Updating Another User's Post (Should Fail)
```
# Assume post 1 belongs to User 1
PUT {{baseUrl}}/api/blog-posts/1
Headers:
  X-User-Id: 2
  Content-Type: application/json

Body:
{
  "title": "Trying to Hack",
  "content": "This should not work",
  "eventId": 1,
  "published": true
}

Expected: 403 Forbidden
Message: "You are not authorized to update this blog post"
```

### Scenario 13: Delete Blog Post (Owner Only)
```
DELETE {{baseUrl}}/api/blog-posts/1
Headers:
  X-User-Id: 1

Expected: 204 No Content
Post is deleted successfully
```

### Scenario 14: Get Published Posts Only
```
GET {{baseUrl}}/api/blog-posts?published=true

Expected: 200 OK
Returns only posts where published = true
Each includes event details
```

### Scenario 15: Event Service Down Scenario
```
1. Stop Event service
2. Try to create blog post:

POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Event Service Down",
  "content": "This should fail",
  "eventId": 1,
  "published": true
}

Expected: 500 Internal Server Error
Message: "Error communicating with Event service..."

3. Try to get existing blog post:

GET {{baseUrl}}/api/blog-posts/1

Expected: 200 OK
Response includes eventId but event = null
(Graceful degradation)
```

## Quick Test Flow

### Phase 1: Setup
1. Start Event service (port 8088)
2. Start Blog service (port 8082)
3. Create 2-3 events in Event service
4. Note the event IDs

### Phase 2: Create Blog Posts
1. Create 2 posts for Event 1 as User 1
2. Create 2 posts for Event 1 as User 2
3. Create 1 post for Event 2 as User 1
4. Try creating post with invalid eventId (should fail)

### Phase 3: Query Blog Posts
1. Get all posts (no filter)
2. Get posts for Event 1 (`?eventId=1`)
3. Get posts for User 1 (`?userId=1`)
4. Get User 1's posts via `/my-posts`
5. Get published posts only

### Phase 4: Update Operations
1. User 1 updates their own post ✓
2. User 1 tries to update User 2's post ✗ (403)
3. Update post to different eventId ✓
4. Update post to invalid eventId ✗ (404)

### Phase 5: Delete Operations
1. User 1 deletes their own post ✓
2. User 2 tries to delete User 1's post ✗ (403)

### Phase 6: Error Cases
1. Create without X-User-Id header ✗ (401)
2. Create with invalid eventId ✗ (404)
3. Create without eventId ✗ (400)
4. Update without X-User-Id header ✗ (401)

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
  "eventId": 1,
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
  "eventId": 1,
  "published": true
}

Expected: 400 Bad Request
Validation error: "Content must be at least 10 characters"
```

### Missing EventId
```
POST {{baseUrl}}/api/blog-posts
Headers:
  X-User-Id: 1
  Content-Type: application/json

Body:
{
  "title": "Valid Title",
  "content": "Valid content here",
  "published": true
}

Expected: 400 Bad Request
Validation error: "Event ID is required"
```

## Response Verification

### Verify Event Details in Response
When you get a blog post, verify the response includes:
```json
{
  "id": 1,
  "title": "...",
  "content": "...",
  "userId": 1,
  "eventId": 1,
  "event": {
    "id": 1,
    "title": "Spring Boot Conference 2026",
    "description": "...",
    "date": "2026-06-15",
    "location": "San Francisco, CA",
    "price": 299.99,
    "imageUrl": "..."
  },
  "published": true,
  "createdAt": "...",
  "updatedAt": "..."
}
```

## Collection Variables

Set these in your Postman environment:

```
baseUrl = http://localhost:8082
eventUrl = http://localhost:8088
userId = 1
eventId = 1
blogPostId = 1
```

## Pre-request Scripts

### Auto-set User Header
```javascript
pm.request.headers.add({
    key: 'X-User-Id',
    value: pm.environment.get('userId')
});
```

### Save Event ID from Response
```javascript
if (pm.response.code === 201) {
    var response = pm.response.json();
    pm.environment.set('eventId', response.id);
}
```

### Save Blog Post ID from Response
```javascript
if (pm.response.code === 201) {
    var response = pm.response.json();
    pm.environment.set('blogPostId', response.id);
}
```

## Tests Scripts

### Verify Event Details Included
```javascript
pm.test("Response includes event details", function() {
    var response = pm.response.json();
    pm.expect(response).to.have.property('event');
    pm.expect(response.event).to.have.property('id');
    pm.expect(response.event).to.have.property('title');
});
```

### Verify User ID Matches Header
```javascript
pm.test("User ID matches header", function() {
    var response = pm.response.json();
    var headerUserId = pm.request.headers.get('X-User-Id');
    pm.expect(response.userId.toString()).to.equal(headerUserId);
});
```

### Verify Event ID Matches Request
```javascript
pm.test("Event ID matches request", function() {
    var response = pm.response.json();
    var requestBody = JSON.parse(pm.request.body.raw);
    pm.expect(response.eventId).to.equal(requestBody.eventId);
});
```

## Notes
- All POST/PUT/DELETE operations require `X-User-Id` header
- All POST/PUT operations require valid `eventId` in body
- Event service must be running for blog post creation
- GET operations don't require authentication (except `/my-posts`)
- Event details are automatically included in all responses
- If Event service is down, existing posts return with `event=null`
