# Back User Synchronization Summary

## Overview
Successfully synchronized both back_user directories to be identical:
- `back_user` (D:\CODING WORKSPACE\Springboot projects\microservices\TRY1\back_user)
- `back_user1/back_user` (D:\CODING WORKSPACE\Springboot projects\microservices\TRY1\back_user1\back_user)

## Changes Made

### Files Copied to back_user1/back_user

#### Configuration Files
- `pom.xml` - Maven configuration with all dependencies including RabbitMQ
- `src/main/resources/application.properties` - Database, Eureka, and RabbitMQ configuration
- `README.md` - Project documentation

#### Main Application
- `src/main/java/tn/esprit/spring/event/EventApplication.java` - Main Spring Boot application with @EnableDiscoveryClient

#### Configuration Classes
- `src/main/java/tn/esprit/spring/event/config/RabbitMQConfig.java` - RabbitMQ configuration with JSR310 support

#### Model Classes
- `src/main/java/tn/esprit/spring/event/demo/Model/User.java` - User entity with all fields
- `src/main/java/tn/esprit/spring/event/demo/Model/Role.java` - Role enum (ADMIN, CLIENT, USER)
- `src/main/java/tn/esprit/spring/event/demo/Model/LoginRequest.java` - Login request DTO
- `src/main/java/tn/esprit/spring/event/demo/Model/LoginResponse.java` - Login response DTO

#### DTO Classes
- `src/main/java/tn/esprit/spring/event/demo/dto/UserDTO.java` - User data transfer object for RabbitMQ

#### Repository
- `src/main/java/tn/esprit/spring/event/demo/Repository/UserRepository.java` - JPA repository

#### Service Layer
- `src/main/java/tn/esprit/spring/event/demo/Service/UserService.java` - Complete user service with RabbitMQ integration

#### Controller
- `src/main/java/tn/esprit/spring/event/demo/Controller/UserController.java` - REST API endpoints

#### Messaging
- `src/main/java/tn/esprit/spring/event/demo/messaging/UserProducer.java` - RabbitMQ producer with enhanced logging

#### Test Classes
- `src/test/java/tn/esprit/spring/event/EventApplicationTests.java` - Basic test class

### Files Removed from back_user1/back_user

These files existed only in back_user1 and were removed to match back_user:
- `src/main/java/tn/esprit/spring/event/demo/feign/TicketClient.java` - Feign client (not in back_user)
- `src/main/java/tn/esprit/spring/event/demo/feign/EventClient.java` - Feign client (not in back_user)
- `src/main/java/tn/esprit/spring/event/demo/Model/TicketDTO.java` - DTO (not in back_user)
- `src/main/java/tn/esprit/spring/event/demo/Model/EventDTO.java` - DTO (not in back_user)

## Key Features Now Identical in Both

### 1. RabbitMQ Integration
Both services now have:
- RabbitMQ configuration with JavaTimeModule for date/time serialization
- UserProducer with enhanced logging
- Sends messages to both `userTicketQueue` and `userFeedbackQueue`

### 2. User Management
- Complete CRUD operations
- Authentication (login/register/logout)
- Role-based user types
- Address and phone management

### 3. Database Configuration
- MySQL database: `userdb`
- Port: 8082
- Eureka integration enabled

### 4. Dependencies
Both have identical dependencies:
- Spring Boot 3.3.2
- Spring Cloud 2023.0.3
- Spring Data JPA
- MySQL Connector
- Lombok
- Eureka Client
- RabbitMQ (spring-boot-starter-amqp)

## Configuration Details

### Application Properties (Identical)
```properties
spring.application.name=Event
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/userdb?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

# Eureka
eureka.client.register-with-eureka=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### RabbitMQ Queues
Both services send to:
- `userTicketQueue` - For Ticket microservice
- `userFeedbackQueue` - For Feedback microservice

## API Endpoints (Identical)

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/current/{userId}` - Get current user
- `GET /api/users/connected` - Get connected user
- `GET /api/users/isConnected` - Check if user is connected
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Testing

### Rebuild Both Services
```bash
# back_user
cd back_user
mvn clean install

# back_user1/back_user
cd back_user1/back_user
mvn clean install
```

### Verify Identical Behavior
Both services should:
1. Start on port 8082 (only run one at a time)
2. Connect to MySQL database `userdb`
3. Register with Eureka
4. Send RabbitMQ messages on user registration/update
5. Show identical logging output

## Important Notes

1. **Only run ONE service at a time** - Both use port 8082
2. **Same database** - Both connect to `userdb`
3. **Same Eureka registration** - Both register as "Event" service
4. **Identical functionality** - All features, endpoints, and behavior are now the same

## Verification Checklist

- [x] All Java source files copied
- [x] Configuration files synchronized
- [x] Dependencies matched in pom.xml
- [x] RabbitMQ configuration identical
- [x] Extra files removed from back_user1
- [x] Application properties identical
- [x] README documentation copied

## Next Steps

1. Choose which directory to use as your primary user service
2. Delete or archive the other directory to avoid confusion
3. Update any documentation or scripts that reference the user service location
4. Rebuild the chosen service with `mvn clean install`
5. Test the service to ensure everything works correctly
