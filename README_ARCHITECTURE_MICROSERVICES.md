# 🏗️ Architecture Microservices - Plateforme de Gestion d'Événements

## 📋 Table des Matières
1. [Vue d'Ensemble](#vue-densemble)
2. [Architecture Globale](#architecture-globale)
3. [Microservices](#microservices)
4. [Patterns et Technologies](#patterns-et-technologies)
5. [Infrastructure](#infrastructure)
6. [Sécurité](#sécurité)
7. [Communication](#communication)
8. [Déploiement](#déploiement)

---

## 🎯 Vue d'Ensemble

Cette plateforme est une **architecture microservices complète** pour la gestion d'événements, construite avec **Spring Boot** et **Spring Cloud**. Elle implémente les meilleures pratiques de l'architecture distribuée moderne.

### Caractéristiques Principales
- ✅ **8 Microservices indépendants**
- ✅ **Architecture hexagonale (Clean Architecture)**
- ✅ **Communication asynchrone avec RabbitMQ**
- ✅ **Service Discovery avec Eureka**
- ✅ **Configuration centralisée**
- ✅ **API Gateway pour le routage**
- ✅ **Sécurité OAuth2 avec Keycloak**
- ✅ **Conteneurisation avec Docker**

---

## 🏛️ Architecture Globale


```
┌─────────────────────────────────────────────────────────────────────┐
│                           CLIENTS                                   │
│                    (Web, Mobile, Postman)                           │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      🔐 KEYCLOAK                                    │
│                   (Authentification OAuth2)                         │
│                      Port: 8080/9090                                │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    🚪 API GATEWAY                                   │
│                  (Spring Cloud Gateway)                             │
│                      Port: 8087                                     │
│  • Routage des requêtes                                             │
│  • Load Balancing                                                   │
│  • Validation JWT                                                   │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    🔍 EUREKA SERVER                                 │
│                  (Service Discovery)                                │
│                      Port: 8761                                     │
│  • Enregistrement des services                                      │
│  • Health checks                                                    │
│  • Load balancing côté client                                       │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    ⚙️ CONFIG SERVER                                 │
│              (Configuration Centralisée)                            │
│                      Port: 8888                                     │
│  • Configuration externalisée                                       │
│  • Rafraîchissement dynamique                                       │
└─────────────────────────────────────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   USER MS    │    │  EVENT MS    │    │  TICKET MS   │
│  Port: 8082  │    │  Port: 8088  │    │  Port: 8091  │
└──────┬───────┘    └──────┬───────┘    └──────┬───────┘
       │                   │                    │
       │                   │                    │
       ▼                   ▼                    ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ FEEDBACK MS  │    │   BLOG MS    │    │              │
│  Port: 8090  │    │  Port: 8092  │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
       │                   │                    │
       └───────────────────┴────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    🐰 RABBITMQ                                      │
│                (Message Broker)                                     │
│                   Port: 5672                                        │
│  • Communication asynchrone                                         │
│  • Event-driven architecture                                        │
│  • Queues: userTicketQueue, userFeedbackQueue, eventQueue          │
└─────────────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    🗄️ MYSQL DATABASES                              │
│  • userdb (User Service)                                            │
│  • events_db (Event Service)                                        │
│  • ticketdatabase (Ticket Service)                                  │
│  • feedbackdb (Feedback Service)                                    │
│  • blogdb (Blog Service)                                            │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔧 Microservices

### 1. 🚪 API Gateway (Port: 8087)

**Rôle**: Point d'entrée unique pour tous les clients

**Technologies**:
- Spring Cloud Gateway
- Spring Security OAuth2 Resource Server
- Spring WebFlux (Reactive)
- Netflix Eureka Client

**Fonctionnalités**:
- ✅ Routage dynamique vers les microservices
- ✅ Load balancing automatique
- ✅ Validation des tokens JWT
- ✅ CORS configuration
- ✅ Rate limiting (possible)
- ✅ Circuit breaker (possible)

**Configuration**:
```properties
server.port=8087
eureka.client.service-url.defaultZone=http://containereureka:8761/eureka/
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://host.docker.internal:8080/realms/Eventy_Realm
```

**Patterns Utilisés**:
- 🔹 **API Gateway Pattern**: Point d'entrée unique
- 🔹 **Backend for Frontend (BFF)**: Adaptation des réponses
- 🔹 **Service Discovery**: Découverte automatique des services

---

### 2. 🔍 Eureka Server (Port: 8761)

**Rôle**: Service de découverte et registre des microservices

**Technologies**:
- Spring Cloud Netflix Eureka Server
- Spring Boot 4.0.3

**Fonctionnalités**:
- ✅ Enregistrement automatique des services
- ✅ Health checks périodiques
- ✅ Dashboard de monitoring
- ✅ Load balancing côté client
- ✅ Failover automatique

**Configuration**:
```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

**Patterns Utilisés**:
- 🔹 **Service Registry Pattern**: Registre centralisé
- 🔹 **Service Discovery Pattern**: Découverte dynamique
- 🔹 **Health Check Pattern**: Surveillance de la santé

---

### 3. ⚙️ Config Server (Port: 8888)

**Rôle**: Gestion centralisée de la configuration

**Technologies**:
- Spring Cloud Config Server
- Netflix Eureka Client
- Native profile (fichiers locaux)

**Fonctionnalités**:
- ✅ Configuration externalisée
- ✅ Profils d'environnement (dev, prod, test)
- ✅ Rafraîchissement dynamique avec Spring Cloud Bus
- ✅ Chiffrement des propriétés sensibles
- ✅ Support Git ou système de fichiers local

**Configuration**:
```properties
server.port=8888
spring.profiles.active=native
spring.cloud.config.server.native.searchLocations=classpath:/config
```

**Patterns Utilisés**:
- 🔹 **Externalized Configuration Pattern**: Configuration externe
- 🔹 **Configuration Server Pattern**: Serveur centralisé

---

### 4. 👤 User Service (Port: 8082)

**Rôle**: Gestion des utilisateurs et authentification

**Technologies**:
- Spring Boot 3.3.2
- Spring Data JPA
- MySQL
- Spring Security OAuth2
- RabbitMQ (AMQP)
- OpenFeign Client
- Lombok

**Fonctionnalités**:
- ✅ CRUD utilisateurs
- ✅ Authentification (login/register/logout)
- ✅ Gestion des rôles (ADMIN, CLIENT, USER)
- ✅ Publication d'événements utilisateur via RabbitMQ
- ✅ Intégration Keycloak OAuth2
- ✅ Validation des données

**Base de Données**: `userdb`

**Entités**:
- `User`: id, firstName, lastName, email, password, role, address, phone

**API Endpoints**:
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
GET    /api/users
GET    /api/users/{id}
GET    /api/users/email/{email}
PUT    /api/users/{id}
DELETE /api/users/{id}
```

**Messages RabbitMQ**:
- Queue: `userTicketQueue` (vers Ticket Service)
- Queue: `userFeedbackQueue` (vers Feedback Service)

**Patterns Utilisés**:
- 🔹 **Event-Driven Architecture**: Publication d'événements
- 🔹 **CQRS (partiel)**: Séparation lecture/écriture
- 🔹 **Repository Pattern**: Accès aux données

---

### 5. 🎉 Event Service (Port: 8088)

**Rôle**: Gestion des événements

**Technologies**:
- Spring Boot 3.4.5
- Spring Data JPA
- MySQL
- Spring Security OAuth2
- RabbitMQ (AMQP)
- OpenFeign Client
- Jackson JSR310 (dates)

**Fonctionnalités**:
- ✅ CRUD événements
- ✅ Gestion des dates et lieux
- ✅ Publication d'événements via RabbitMQ
- ✅ Intégration avec autres services via Feign
- ✅ Validation des données
- ✅ Sécurité OAuth2

**Base de Données**: `events_db`

**Configuration**:
```properties
server.port=8088
spring.jpa.hibernate.ddl-auto=create
spring.rabbitmq.host=host.docker.internal
```

**Patterns Utilisés**:
- 🔹 **Domain-Driven Design (DDD)**: Modélisation métier
- 🔹 **Event Sourcing (partiel)**: Historique des événements
- 🔹 **Saga Pattern**: Transactions distribuées

---

### 6. 🎫 Ticket Service (Port: 8091)

**Rôle**: Gestion des billets d'événements

**Technologies**:
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL
- Spring Security OAuth2
- RabbitMQ (AMQP)
- OpenFeign Client

**Fonctionnalités**:
- ✅ CRUD billets
- ✅ Réservation de billets
- ✅ Gestion des stocks
- ✅ Consommation d'événements utilisateur (RabbitMQ)
- ✅ Communication avec Event Service (Feign)
- ✅ Validation des réservations

**Base de Données**: `ticketdatabase`

**Consumer RabbitMQ**:
- Queue: `userTicketQueue` (depuis User Service)

**Patterns Utilisés**:
- 🔹 **Event-Driven Consumer**: Consommation d'événements
- 🔹 **Saga Pattern**: Gestion des transactions distribuées
- 🔹 **Inventory Pattern**: Gestion des stocks

---

### 7. 💬 Feedback Service (Port: 8090)

**Rôle**: Gestion des retours et commentaires

**Technologies**:
- Spring Boot 3.1.5
- Spring Data JPA
- MySQL
- Keycloak Spring Boot Starter
- RabbitMQ (AMQP)
- OpenFeign Client

**Fonctionnalités**:
- ✅ CRUD feedbacks
- ✅ Notation des événements
- ✅ Commentaires utilisateurs
- ✅ Consommation d'événements utilisateur
- ✅ Intégration Keycloak avancée
- ✅ Validation des retours

**Base de Données**: `feedbackdb`

**Consumer RabbitMQ**:
- Queue: `userFeedbackQueue` (depuis User Service)

**Configuration Keycloak**:
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://host.docker.internal:9090/realms/Eventy_Realm
```

**Patterns Utilisés**:
- 🔹 **Event-Driven Consumer**: Réaction aux événements
- 🔹 **CQRS**: Séparation des opérations

---

### 8. 📝 Blog Service (Port: 8092)

**Rôle**: Gestion des articles de blog

**Technologies**:
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL
- Spring Security OAuth2
- RabbitMQ (AMQP)
- OpenFeign Client

**Fonctionnalités**:
- ✅ CRUD articles de blog
- ✅ Gestion du contexte utilisateur (UserContext)
- ✅ Validation de propriété (ownership)
- ✅ Consommation d'événements
- ✅ Communication avec Event Service
- ✅ Architecture hexagonale complète

**Base de Données**: `blogdb`

**Architecture**:
```
src/main/java/com/eventplatform/blog/
├── domain/          (Entités, Repositories)
├── application/     (Services, DTOs, Feign Clients)
├── presentation/    (Controllers)
└── infrastructure/  (Config, Context, Messaging, Exceptions)
```

**Patterns Utilisés**:
- 🔹 **Hexagonal Architecture**: Séparation claire des couches
- 🔹 **Clean Architecture**: Indépendance des frameworks
- 🔹 **Context Pattern**: Gestion du contexte utilisateur

---

## 🎨 Patterns et Technologies

### Patterns Architecturaux

#### 1. **Microservices Pattern**
- Décomposition en services indépendants
- Déploiement indépendant
- Scalabilité horizontale
- Résilience et isolation des pannes

#### 2. **API Gateway Pattern**
- Point d'entrée unique
- Routage intelligent
- Agrégation de réponses
- Sécurité centralisée

#### 3. **Service Discovery Pattern**
- Enregistrement automatique
- Découverte dynamique
- Load balancing
- Health checks

#### 4. **Externalized Configuration Pattern**
- Configuration centralisée
- Gestion des environnements
- Rafraîchissement dynamique
- Sécurité des secrets

#### 5. **Event-Driven Architecture**
- Communication asynchrone
- Découplage des services
- Scalabilité
- Résilience

#### 6. **Hexagonal Architecture (Clean Architecture)**
- Séparation des couches
- Indépendance des frameworks
- Testabilité
- Maintenabilité

#### 7. **CQRS (Command Query Responsibility Segregation)**
- Séparation lecture/écriture
- Optimisation des performances
- Scalabilité indépendante

#### 8. **Saga Pattern**
- Transactions distribuées
- Compensation en cas d'échec
- Cohérence éventuelle

#### 9. **Circuit Breaker Pattern** (Possible avec Resilience4j)
- Protection contre les pannes en cascade
- Fallback automatique
- Récupération progressive

---

### Technologies Utilisées

#### Backend Framework
- **Spring Boot** (3.1.5 - 4.0.5)
- **Spring Cloud** (2022.0.4 - 2025.1.1)

#### Service Discovery & Configuration
- **Netflix Eureka** (Service Registry)
- **Spring Cloud Config** (Configuration Server)

#### API Gateway
- **Spring Cloud Gateway** (Reactive Gateway)

#### Communication
- **OpenFeign** (REST Client synchrone)
- **RabbitMQ** (Message Broker asynchrone)
- **Spring AMQP** (RabbitMQ Integration)

#### Sécurité
- **Keycloak** (Identity Provider)
- **Spring Security OAuth2 Resource Server**
- **JWT** (JSON Web Tokens)

#### Persistence
- **Spring Data JPA** (ORM)
- **Hibernate** (JPA Implementation)
- **MySQL** (Base de données relationnelle)

#### Utilitaires
- **Lombok** (Réduction du boilerplate)
- **Jackson** (Sérialisation JSON)
- **Jackson JSR310** (Support dates Java 8+)
- **Spring Validation** (Validation des données)

#### Monitoring & Observabilité
- **Spring Boot Actuator** (Métriques et health checks)
- **Eureka Dashboard** (Monitoring des services)

#### Conteneurisation
- **Docker** (Containerization)
- **Docker Compose** (Orchestration locale)

---

## 🏗️ Infrastructure

### Ports des Services

| Service | Port | Base de Données |
|---------|------|-----------------|
| Eureka Server | 8761 | - |
| Config Server | 8888 | - |
| API Gateway | 8087 | - |
| User Service | 8082 | userdb |
| Event Service | 8088 | events_db |
| Feedback Service | 8090 | feedbackdb |
| Ticket Service | 8091 | ticketdatabase |
| Blog Service | 8092 | blogdb |
| Keycloak | 8080/9090 | - |
| RabbitMQ | 5672 | - |
| RabbitMQ Management | 15672 | - |
| MySQL | 3306 | - |

### Configuration Docker

Tous les services utilisent `host.docker.internal` pour communiquer avec:
- MySQL: `host.docker.internal:3306`
- RabbitMQ: `host.docker.internal:5672`
- Keycloak: `host.docker.internal:8080`

Les services communiquent entre eux via:
- Eureka: `containereureka:8761`
- Config Server: `containerconfig:8888`

---

## 🔐 Sécurité

### Keycloak OAuth2

**Configuration**:
- Realm: `Eventy_Realm`
- Issuer URI: `http://host.docker.internal:8080/realms/Eventy_Realm`
- Protocol: OAuth2 / OpenID Connect

**Flux d'Authentification**:
```
1. Client → Keycloak: Demande de token (login)
2. Keycloak → Client: JWT Token
3. Client → API Gateway: Requête + JWT Token
4. API Gateway: Validation du token
5. API Gateway → Microservice: Requête avec contexte utilisateur
6. Microservice: Traitement avec autorisation
```

### Sécurité par Service

Tous les microservices métier implémentent:
- ✅ **OAuth2 Resource Server**: Validation JWT
- ✅ **Spring Security**: Protection des endpoints
- ✅ **Role-Based Access Control (RBAC)**: Gestion des rôles
- ✅ **User Context**: Extraction de l'utilisateur courant

**Configuration Type**:
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://host.docker.internal:8080/realms/Eventy_Realm
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
```

---

## 📡 Communication

### Communication Synchrone (OpenFeign)

**Exemple**: Blog Service → Event Service
```java
@FeignClient(name = "eventproject")
public interface EventClient {
    @GetMapping("/api/events/{id}")
    EventDTO getEventById(@PathVariable Long id);
}
```

**Avantages**:
- ✅ Réponse immédiate
- ✅ Simple à implémenter
- ✅ Load balancing automatique via Eureka

**Inconvénients**:
- ❌ Couplage temporel
- ❌ Dépendance à la disponibilité du service

---

### Communication Asynchrone (RabbitMQ)

**Architecture**:
```
User Service (Producer)
    │
    ├─→ userTicketQueue → Ticket Service (Consumer)
    └─→ userFeedbackQueue → Feedback Service (Consumer)

Event Service (Producer)
    │
    └─→ eventQueue → Blog Service (Consumer)
```

**Configuration RabbitMQ**:
```java
@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue userTicketQueue() {
        return new Queue("userTicketQueue", true);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }
}
```

**Producer Example**:
```java
@Service
public class UserProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendUserEvent(UserDTO user) {
        rabbitTemplate.convertAndSend("userTicketQueue", user);
        rabbitTemplate.convertAndSend("userFeedbackQueue", user);
    }
}
```

**Consumer Example**:
```java
@Component
public class EventConsumer {
    @RabbitListener(queues = "eventQueue")
    public void handleEvent(EventDTO event) {
        // Traitement de l'événement
    }
}
```

**Avantages**:
- ✅ Découplage total
- ✅ Résilience (retry, DLQ)
- ✅ Scalabilité
- ✅ Asynchronisme

---

## 🚀 Déploiement

### Prérequis

- Java 17
- Maven 3.8+
- MySQL 8.0+
- RabbitMQ 3.x
- Docker & Docker Compose
- Keycloak 23.x

### Ordre de Démarrage

1. **Infrastructure**:
   ```bash
   # MySQL
   docker run -d -p 3306:3306 --name mysql \
     -e MYSQL_ROOT_PASSWORD= \
     mysql:8.0
   
   # RabbitMQ
   docker run -d -p 5672:5672 -p 15672:15672 \
     --name rabbitmq rabbitmq:3-management
   
   # Keycloak
   docker run -d -p 8080:8080 --name keycloak \
     -e KEYCLOAK_ADMIN=admin \
     -e KEYCLOAK_ADMIN_PASSWORD=admin \
     quay.io/keycloak/keycloak:23.0.5 start-dev
   ```

2. **Service Discovery**:
   ```bash
   cd eurekaserverdemo
   mvn spring-boot:run
   ```

3. **Configuration Server**:
   ```bash
   cd configServer
   mvn spring-boot:run
   ```

4. **API Gateway**:
   ```bash
   cd ApiGatewayDemo
   mvn spring-boot:run
   ```

5. **Microservices Métier** (ordre flexible):
   ```bash
   # User Service
   cd useruser
   mvn spring-boot:run
   
   # Event Service
   cd crud_event
   mvn spring-boot:run
   
   # Ticket Service
   cd TicketManagement
   mvn spring-boot:run
   
   # Feedback Service
   cd feedbackproject
   mvn spring-boot:run
   
   # Blog Service
   cd BlogManagement
   mvn spring-boot:run
   ```

### Vérification

- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Keycloak Admin**: http://localhost:8080/admin (admin/admin)
- **API Gateway**: http://localhost:8087

---

## 📊 Monitoring et Observabilité

### Spring Boot Actuator

Tous les services exposent:
```properties
management.endpoints.web.exposure.include=*
```

**Endpoints disponibles**:
- `/actuator/health` - État de santé
- `/actuator/info` - Informations du service
- `/actuator/metrics` - Métriques
- `/actuator/env` - Variables d'environnement
- `/actuator/loggers` - Configuration des logs

### Eureka Dashboard

Interface web pour:
- ✅ Visualiser tous les services enregistrés
- ✅ Voir le statut de santé
- ✅ Nombre d'instances par service
- ✅ Métadonnées des services

---

## 🧪 Tests

### Tests Postman

Chaque service peut être testé via:
- API Gateway: `http://localhost:8087/{service-name}/api/...`
- Direct: `http://localhost:{port}/api/...`

**Exemple - Créer un utilisateur**:
```http
POST http://localhost:8087/Event/api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "CLIENT"
}
```

---

## 📚 Documentation Complémentaire

- **Blog Service**: Voir `BlogManagement/README.md`
- **Architecture Flow**: Voir `BlogManagement/ARCHITECTURE_FLOW.md`
- **User Sync**: Voir `BACK_USER_SYNC_SUMMARY.md`
- **Postman Examples**: Voir `BlogManagement/POSTMAN_EXAMPLES.md`

---

## 🎯 Bonnes Pratiques Implémentées

### Architecture
- ✅ Séparation des préoccupations (SoC)
- ✅ Single Responsibility Principle (SRP)
- ✅ Dependency Inversion Principle (DIP)
- ✅ Domain-Driven Design (DDD)

### Code
- ✅ Lombok pour réduire le boilerplate
- ✅ DTOs pour la communication
- ✅ Validation des données (@Valid)
- ✅ Exception handling global
- ✅ Logging structuré

### Sécurité
- ✅ OAuth2 / JWT
- ✅ HTTPS ready
- ✅ CORS configuration
- ✅ Role-based access control

### Performance
- ✅ Connection pooling (HikariCP)
- ✅ Lazy loading JPA
- ✅ Caching (possible avec Redis)
- ✅ Async messaging

### Résilience
- ✅ Health checks
- ✅ Retry logic (RabbitMQ)
- ✅ Dead Letter Queues
- ✅ Circuit breaker ready

---

## 🔄 Évolutions Futures

### Court Terme
- [ ] Ajouter Resilience4j (Circuit Breaker, Rate Limiter)
- [ ] Implémenter Redis pour le caching
- [ ] Ajouter Zipkin/Sleuth pour le tracing distribué
- [ ] Créer des tests d'intégration

### Moyen Terme
- [ ] Implémenter GraphQL pour certains services
- [ ] Ajouter Kafka pour l'event streaming
- [ ] Mettre en place ELK Stack (Elasticsearch, Logstash, Kibana)
- [ ] Implémenter API versioning

### Long Terme
- [ ] Migration vers Kubernetes
- [ ] Service Mesh (Istio)
- [ ] Observabilité avancée (Prometheus, Grafana)
- [ ] CI/CD complet (Jenkins, GitLab CI)

---

## 👥 Équipe et Contributions

Ce projet démontre une architecture microservices complète et professionnelle, implémentant les meilleures pratiques de l'industrie.

### Technologies Maîtrisées
- ✅ Spring Boot / Spring Cloud
- ✅ Architecture Microservices
- ✅ Event-Driven Architecture
- ✅ OAuth2 / Keycloak
- ✅ RabbitMQ
- ✅ Docker
- ✅ Clean Architecture

---

## 📝 Licence

Ce projet est développé dans un cadre éducatif.

---

## 📞 Support

Pour toute question sur l'architecture ou l'implémentation, consultez:
- La documentation Spring Cloud: https://spring.io/projects/spring-cloud
- La documentation Keycloak: https://www.keycloak.org/documentation
- La documentation RabbitMQ: https://www.rabbitmq.com/documentation.html

---

**Dernière mise à jour**: Avril 2026
**Version**: 1.0.0
