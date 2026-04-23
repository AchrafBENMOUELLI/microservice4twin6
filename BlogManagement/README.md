# Blog Module - Event Management Platform

Independent Spring Boot REST API for managing blog posts.

## Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- Maven

## Project Structure
```
src/main/java/com/eventplatform/blog/
├── BlogApplication.java
├── domain/
│   ├── entity/BlogPost.java
│   └── repository/BlogPostRepository.java
├── application/
│   ├── dto/
│   │   ├── BlogPostRequest.java
│   │   └── BlogPostResponse.java
│   └── service/BlogPostService.java
├── presentation/
│   └── controller/BlogPostController.java
└── infrastructure/
    └── exception/
        ├── ResourceNotFoundException.java
        ├── ErrorResponse.java
        └── GlobalExceptionHandler.java
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:9999`

## API Endpoints - Ready to Test

### 1. Create Blog Post
```bash
POST http://localhost:9999/api/blog-posts
Content-Type: application/json

{
  "title": "My First Blog Post",
  "content": "This is the content of my blog post about Spring Boot and clean architecture",
  "author": "John Doe",
  "published": true
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "title": "My First Blog Post",
  "content": "This is the content of my blog post about Spring Boot and clean architecture",
  "author": "John Doe",
  "published": true,
  "createdAt": "2026-02-26T10:30:00",
  "updatedAt": "2026-02-26T10:30:00"
}
```

### 2. Create Another Blog Post (Draft)
```bash
POST http://localhost:9999/api/blog-posts
Content-Type: application/json

{
  "title": "Understanding REST APIs",
  "content": "A comprehensive guide to building RESTful APIs with Spring Boot framework",
  "author": "Jane Smith",
  "published": false
}
```

**Expected Response (201 Created):**
```json
{
  "id": 2,
  "title": "Understanding REST APIs",
  "content": "A comprehensive guide to building RESTful APIs with Spring Boot framework",
  "author": "Jane Smith",
  "published": false,
  "createdAt": "2026-02-26T10:31:00",
  "updatedAt": "2026-02-26T10:31:00"
}
```

### 3. Get All Blog Posts
```bash
GET http://localhost:9999/api/blog-posts
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "My First Blog Post",
    "content": "This is the content of my blog post about Spring Boot and clean architecture",
    "author": "John Doe",
    "published": true,
    "createdAt": "2026-02-26T10:30:00",
    "updatedAt": "2026-02-26T10:30:00"
  },
  {
    "id": 2,
    "title": "Understanding REST APIs",
    "content": "A comprehensive guide to building RESTful APIs with Spring Boot framework",
    "author": "Jane Smith",
    "published": false,
    "createdAt": "2026-02-26T10:31:00",
    "updatedAt": "2026-02-26T10:31:00"
  }
]
```

### 4. Get Published Blog Posts Only
```bash
GET http://localhost:9999/api/blog-posts?published=true
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "My First Blog Post",
    "content": "This is the content of my blog post about Spring Boot and clean architecture",
    "author": "John Doe",
    "published": true,
    "createdAt": "2026-02-26T10:30:00",
    "updatedAt": "2026-02-26T10:30:00"
  }
]
```

### 5. Get Blog Posts by Author
```bash
GET http://localhost:9999/api/blog-posts?author=Jane Smith
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 2,
    "title": "Understanding REST APIs",
    "content": "A comprehensive guide to building RESTful APIs with Spring Boot framework",
    "author": "Jane Smith",
    "published": false,
    "createdAt": "2026-02-26T10:31:00",
    "updatedAt": "2026-02-26T10:31:00"
  }
]
```

### 6. Get Blog Post by ID
```bash
GET http://localhost:9999/api/blog-posts/1
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "title": "My First Blog Post",
  "content": "This is the content of my blog post about Spring Boot and clean architecture",
  "author": "John Doe",
  "published": true,
  "createdAt": "2026-02-26T10:30:00",
  "updatedAt": "2026-02-26T10:30:00"
}
```

### 7. Update Blog Post
```bash
PUT http://localhost:9999/api/blog-posts/1
Content-Type: application/json

{
  "title": "My Updated Blog Post Title",
  "content": "This is the updated content with more details about Spring Boot best practices",
  "author": "John Doe",
  "published": true
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "title": "My Updated Blog Post Title",
  "content": "This is the updated content with more details about Spring Boot best practices",
  "author": "John Doe",
  "published": true,
  "createdAt": "2026-02-26T10:30:00",
  "updatedAt": "2026-02-26T10:35:00"
}
```

### 8. Delete Blog Post
```bash
DELETE http://localhost:9999/api/blog-posts/2
```

**Expected Response (204 No Content):**
```
(Empty body)
```

### 9. Test Validation - Invalid Request
```bash
POST http://localhost:9999/api/blog-posts
Content-Type: application/json

{
  "title": "AB",
  "content": "Short",
  "author": "",
  "published": null
}
```

**Expected Response (400 Bad Request):**
```json
{
  "timestamp": "2026-02-26T10:40:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/blog-posts",
  "validationErrors": {
    "title": "Title must be between 3 and 200 characters",
    "content": "Content must be at least 10 characters",
    "author": "Author is required",
    "published": "Published status is required"
  }
}
```

### 10. Test Not Found Error
```bash
GET http://localhost:9999/api/blog-posts/999
```

**Expected Response (404 Not Found):**
```json
{
  "timestamp": "2026-02-26T10:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "Blog post not found with id: 999",
  "path": "/api/blog-posts/999"
}
```

## H2 Console
Access the H2 database console at: `http://localhost:9999/h2-console`
- JDBC URL: `jdbc:h2:mem:blogdb`
- Username: `sa`
- Password: (leave empty)

## Features
- Complete CRUD operations
- Input validation
- Global exception handling
- Clean architecture (domain, application, presentation, infrastructure layers)
- Automatic timestamps (createdAt, updatedAt)
- Query by published status and author
