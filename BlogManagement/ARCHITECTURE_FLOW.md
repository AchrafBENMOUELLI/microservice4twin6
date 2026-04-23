# Architecture Flow: User Context Implementation

## Request Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT (Postman)                        │
│                                                                 │
│  POST /api/blog-posts                                          │
│  Headers: X-User-Id: 1                                         │
│  Body: { title, content, published }                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BlogPostController                           │
│                                                                 │
│  @PostMapping                                                   │
│  createBlogPost(@RequestBody BlogPostRequest request)          │
│                                                                 │
│  • Validates request body                                       │
│  • Delegates to service layer                                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                     BlogPostService                             │
│                                                                 │
│  createBlogPost(BlogPostRequest request) {                     │
│    1. Long userId = userContext.requireCurrentUserId();        │
│    2. blogPost.setUserId(userId);                              │
│    3. return save(blogPost);                                    │
│  }                                                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        UserContext                              │
│                                                                 │
│  requireCurrentUserId() {                                       │
│    1. Get HttpServletRequest                                    │
│    2. Extract "X-User-Id" header                                │
│    3. Parse to Long                                             │
│    4. Return userId or throw exception                          │
│  }                                                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   HttpServletRequest                            │
│                                                                 │
│  Headers:                                                       │
│    X-User-Id: 1                                                 │
│    Content-Type: application/json                               │
└─────────────────────────────────────────────────────────────────┘
```

## Update/Delete Flow with Ownership Check

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT                                  │
│  PUT /api/blog-posts/5                                         │
│  Headers: X-User-Id: 1                                         │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BlogPostController                           │
│  updateBlogPost(id, request)                                    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                     BlogPostService                             │
│                                                                 │
│  updateBlogPost(id, request) {                                 │
│    1. currentUserId = userContext.requireCurrentUserId()       │
│    2. blogPost = findById(id)                                   │
│    3. if (blogPost.userId != currentUserId)                    │
│         throw UnauthorizedException                             │
│    4. update and save                                           │
│  }                                                              │
└─────────────────────────────────────────────────────────────────┘
                             │
                             ├─── Match ──────► Update Success (200)
                             │
                             └─── No Match ───► UnauthorizedException (403)
```

## Component Architecture

```
┌───────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                         │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │           BlogPostController                             │    │
│  │  • Handles HTTP requests                                 │    │
│  │  • Validates input                                       │    │
│  │  • Returns responses                                     │    │
│  └─────────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌───────────────────────────────────────────────────────────────────┐
│                       Application Layer                           │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │           BlogPostService                                │    │
│  │  • Business logic                                        │    │
│  │  • Uses UserContext                                      │    │
│  │  • Ownership validation                                  │    │
│  │  • Orchestrates operations                               │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │           DTOs                                           │    │
│  │  • BlogPostRequest (no author field)                     │    │
│  │  • BlogPostResponse (userId field)                       │    │
│  └─────────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌───────────────────────────────────────────────────────────────────┐
│                         Domain Layer                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │           BlogPost Entity                                │    │
│  │  • id, title, content                                    │    │
│  │  • userId (Long) ← Changed from author                   │    │
│  │  • published, timestamps                                 │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │           BlogPostRepository                             │    │
│  │  • findByUserId(Long userId)                             │    │
│  │  • findByPublishedTrue()                                 │    │
│  └─────────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌───────────────────────────────────────────────────────────────────┐
│                      Infrastructure Layer                         │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │           UserContext                                    │    │
│  │  • Extracts X-User-Id from headers                       │    │
│  │  • getCurrentUserId()                                    │    │
│  │  • requireCurrentUserId()                                │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │           Exception Handling                             │    │
│  │  • UnauthorizedException (403)                           │    │
│  │  • IllegalStateException (401)                           │    │
│  │  • ResourceNotFoundException (404)                       │    │
│  │  • GlobalExceptionHandler                                │    │
│  └─────────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────────┘
```

## Security Flow

```
┌──────────────────────────────────────────────────────────────┐
│                    Request Arrives                           │
│                    X-User-Id: 1                              │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │  Header Present?     │
              └──────────┬───────────┘
                         │
           ┌─────────────┴─────────────┐
           │                           │
          YES                         NO
           │                           │
           ▼                           ▼
    ┌─────────────┐           ┌──────────────┐
    │ Extract ID  │           │ Throw 401    │
    │ Parse Long  │           │ Unauthorized │
    └──────┬──────┘           └──────────────┘
           │
           ▼
    ┌─────────────┐
    │ Valid Long? │
    └──────┬──────┘
           │
    ┌──────┴──────┐
    │             │
   YES           NO
    │             │
    ▼             ▼
┌────────┐   ┌──────────┐
│Proceed │   │Throw 400 │
└────────┘   └──────────┘
    │
    ▼
┌─────────────────────────┐
│  For Update/Delete:     │
│  Check Ownership        │
└────────┬────────────────┘
         │
    ┌────┴────┐
    │         │
  Match    No Match
    │         │
    ▼         ▼
┌────────┐ ┌──────────┐
│Success │ │Throw 403 │
└────────┘ └──────────┘
```

## Data Flow: Create Blog Post

```
1. Client Request
   ┌──────────────────────────────────┐
   │ POST /api/blog-posts             │
   │ X-User-Id: 1                     │
   │ {                                │
   │   "title": "My Post",            │
   │   "content": "Content...",       │
   │   "published": true              │
   │ }                                │
   └──────────────────────────────────┘
                  │
                  ▼
2. Controller receives BlogPostRequest
   ┌──────────────────────────────────┐
   │ BlogPostRequest {                │
   │   title: "My Post"               │
   │   content: "Content..."          │
   │   published: true                │
   │ }                                │
   │ (No userId field!)               │
   └──────────────────────────────────┘
                  │
                  ▼
3. Service extracts userId from header
   ┌──────────────────────────────────┐
   │ userContext.requireCurrentUserId()│
   │ → Returns: 1                     │
   └──────────────────────────────────┘
                  │
                  ▼
4. Service creates BlogPost entity
   ┌──────────────────────────────────┐
   │ BlogPost {                       │
   │   title: "My Post"               │
   │   content: "Content..."          │
   │   userId: 1  ← Automatically set │
   │   published: true                │
   │ }                                │
   └──────────────────────────────────┘
                  │
                  ▼
5. Save to database
   ┌──────────────────────────────────┐
   │ blog_posts table                 │
   │ ┌────┬─────────┬──────────┬─────┐│
   │ │id  │title    │user_id   │...  ││
   │ ├────┼─────────┼──────────┼─────┤│
   │ │1   │My Post  │1         │...  ││
   │ └────┴─────────┴──────────┴─────┘│
   └──────────────────────────────────┘
                  │
                  ▼
6. Return BlogPostResponse
   ┌──────────────────────────────────┐
   │ {                                │
   │   "id": 1,                       │
   │   "title": "My Post",            │
   │   "content": "Content...",       │
   │   "userId": 1,  ← In response    │
   │   "published": true,             │
   │   "createdAt": "2026-04-09...",  │
   │   "updatedAt": "2026-04-09..."   │
   │ }                                │
   └──────────────────────────────────┘
```

## Integration Points

```
┌─────────────────────────────────────────────────────────────┐
│                    Blog Microservice                        │
│                                                             │
│  ┌──────────────┐         ┌──────────────┐                │
│  │ UserContext  │         │ BlogPost     │                │
│  │ (uses userId)│         │ (stores      │                │
│  │              │         │  userId)     │                │
│  └──────────────┘         └──────────────┘                │
│         │                        │                         │
└─────────┼────────────────────────┼─────────────────────────┘
          │                        │
          │                        │
          │ Future Integration     │
          │ via Feign Client       │
          │                        │
          ▼                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    User Microservice                        │
│                                                             │
│  ┌──────────────────────────────────────────────────┐     │
│  │ GET /api/users/{id}                              │     │
│  │ Returns: { id, firstName, lastName, email, ... } │     │
│  └──────────────────────────────────────────────────┘     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Key Design Decisions

1. **Header-Based Auth**: Simple, no complex security framework
2. **userId Storage**: Uses Long ID for easy integration with User service
3. **Automatic Assignment**: User info extracted in service layer, not controller
4. **Ownership Validation**: Enforced at service layer before any modification
5. **Clean DTOs**: Request DTOs don't contain user fields
6. **Future-Ready**: Easy to add Feign client for user enrichment

## Benefits of This Architecture

✅ **Separation of Concerns**: Each layer has clear responsibility  
✅ **Security**: Ownership checks prevent unauthorized modifications  
✅ **Simplicity**: No complex auth framework needed  
✅ **Testability**: UserContext can be easily mocked  
✅ **Maintainability**: Clear flow from request to database  
✅ **Extensibility**: Easy to add user enrichment later  
