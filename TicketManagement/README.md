# Ticket Service

A Spring Boot REST API for managing tickets in an event management platform.

## Architecture

This project follows Clean Architecture principles with clear separation of concerns:

- **Domain Layer**: Core business logic and entities
- **Application Layer**: REST controllers, DTOs, and mappers
- **Infrastructure Layer**: Database persistence implementation

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL Database (default)
- H2 Database (optional, for testing)
- Lombok
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (running on localhost:3306)

### Database Setup

The application is configured to automatically create the database if it doesn't exist. Default credentials:
- Database: `ticketdb`
- Username: `root`
- Password: `root`

Update credentials in `src/main/resources/application-mysql.yml` if needed.

### Running the Application

**With MySQL (default):**
```bash
mvn spring-boot:run
```

**With H2 (in-memory, for testing):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

The application will start on `http://localhost:8080`

### H2 Console (when using H2 profile)

Access the H2 database console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:ticketdb`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

Base URL: `http://localhost:8080/api/tickets`

### 1. Create Ticket

Creates a new ticket with auto-generated ticket code and purchase date.

**Endpoint:** `POST /api/tickets`

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "ticketType": "VIP",
  "price": 150.00,
  "purchaserEmail": "john.doe@example.com"
}
```

**Success Response:** `201 Created`
```json
{
  "id": 1,
  "ticketCode": "a3f5c8d2-4b1e-4c9a-8f2d-1e3b5c7d9f0a",
  "ticketType": "VIP",
  "price": 150.00,
  "status": "ACTIVE",
  "purchaseDate": "2026-02-26T10:30:00",
  "purchaserEmail": "john.doe@example.com"
}
```

**Validation Errors:** `400 Bad Request`
```json
{
  "ticketType": "Ticket type is required",
  "price": "Price must be positive",
  "purchaserEmail": "Invalid email format"
}
```

---

### 2. Get All Tickets

Retrieves all tickets in the system.

**Endpoint:** `GET /api/tickets`

**Success Response:** `200 OK`
```json
[
  {
    "id": 1,
    "ticketCode": "a3f5c8d2-4b1e-4c9a-8f2d-1e3b5c7d9f0a",
    "ticketType": "VIP",
    "price": 150.00,
    "status": "ACTIVE",
    "purchaseDate": "2026-02-26T10:30:00",
    "purchaserEmail": "john.doe@example.com"
  },
  {
    "id": 2,
    "ticketCode": "b7e9d1f3-5c2a-4d8b-9e3f-2a4c6d8e0b1c",
    "ticketType": "REGULAR",
    "price": 50.00,
    "status": "ACTIVE",
    "purchaseDate": "2026-02-26T11:15:00",
    "purchaserEmail": "jane.smith@example.com"
  }
]
```

---

### 3. Get Ticket by ID

Retrieves a specific ticket by its ID.

**Endpoint:** `GET /api/tickets/{id}`

**Example:** `GET /api/tickets/1`

**Success Response:** `200 OK`
```json
{
  "id": 1,
  "ticketCode": "a3f5c8d2-4b1e-4c9a-8f2d-1e3b5c7d9f0a",
  "ticketType": "VIP",
  "price": 150.00,
  "status": "ACTIVE",
  "purchaseDate": "2026-02-26T10:30:00",
  "purchaserEmail": "john.doe@example.com"
}
```

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2026-02-26T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket not found with id: 1"
}
```

---

### 4. Update Ticket

Updates an existing ticket.

**Endpoint:** `PUT /api/tickets/{id}`

**Example:** `PUT /api/tickets/1`

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "ticketType": "PREMIUM",
  "price": 200.00,
  "purchaserEmail": "john.doe@example.com"
}
```

**Success Response:** `200 OK`
```json
{
  "id": 1,
  "ticketCode": "a3f5c8d2-4b1e-4c9a-8f2d-1e3b5c7d9f0a",
  "ticketType": "PREMIUM",
  "price": 200.00,
  "status": "ACTIVE",
  "purchaseDate": "2026-02-26T10:30:00",
  "purchaserEmail": "john.doe@example.com"
}
```

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2026-02-26T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket not found with id: 1"
}
```

---

### 5. Delete Ticket

Deletes a ticket by its ID.

**Endpoint:** `DELETE /api/tickets/{id}`

**Example:** `DELETE /api/tickets/1`

**Success Response:** `204 No Content`

(No response body)

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2026-02-26T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket not found with id: 1"
}
```

---

## Testing with cURL

### Create a ticket
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d "{\"ticketType\":\"VIP\",\"price\":150.00,\"purchaserEmail\":\"john.doe@example.com\"}"
```

### Get all tickets
```bash
curl -X GET http://localhost:8080/api/tickets
```

### Get ticket by ID
```bash
curl -X GET http://localhost:8080/api/tickets/1
```

### Update ticket
```bash
curl -X PUT http://localhost:8080/api/tickets/1 \
  -H "Content-Type: application/json" \
  -d "{\"ticketType\":\"PREMIUM\",\"price\":200.00,\"purchaserEmail\":\"john.doe@example.com\"}"
```

### Delete ticket
```bash
curl -X DELETE http://localhost:8080/api/tickets/1
```

---

## Testing with Postman

Import the following collection or create requests manually:

1. **Create Ticket** - POST `http://localhost:8080/api/tickets`
2. **Get All Tickets** - GET `http://localhost:8080/api/tickets`
3. **Get Ticket by ID** - GET `http://localhost:8080/api/tickets/1`
4. **Update Ticket** - PUT `http://localhost:8080/api/tickets/1`
5. **Delete Ticket** - DELETE `http://localhost:8080/api/tickets/1`

## Project Structure

```
src/main/java/com/eventplatform/ticket/
├── application/
│   ├── controller/      # REST controllers
│   ├── dto/            # Request/Response DTOs
│   ├── mapper/         # Domain-DTO mappers
│   └── exception/      # Exception handlers
├── domain/
│   ├── model/          # Domain entities
│   ├── port/           # Repository interfaces
│   └── service/        # Business logic
└── infrastructure/
    └── persistence/    # JPA entities and implementations
```
