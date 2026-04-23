# Event Management - User Service

A simple Spring Boot application for user management without security features.

## Features

- Basic CRUD operations for users
- User model with personal information (name, email, address, phones)
- Role-based user types (ADMIN, CLIENT, USER)
- MySQL database integration
- Eureka service discovery integration

## Technologies

- Spring Boot 3.3.2
- Spring Data JPA
- MySQL
- Lombok
- Spring Cloud Eureka Client

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login with email and password
- `POST /api/auth/logout` - Logout current user

### User Management

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/current/{userId}` - Get current logged in user
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## How It Works

This is a simple user management system WITHOUT security features:
- Login checks email and password (stored in plain text)
- No JWT tokens or session management on the backend
- Frontend should store userId in localStorage/sessionStorage after login
- Use the stored userId to fetch current user information

See [API_USAGE.md](API_USAGE.md) for detailed examples and frontend integration.

## Configuration

Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/event_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
```

## Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8082.
