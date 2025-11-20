# 🤖 Agent Protocol: Building Enterprise-Grade Java Microservices
 
This document provides a comprehensive guide for AI coding agents to build secure, scalable, and maintainable microservices using Java 21 and Spring Boot 3. Adherence to these principles is mandatory for all generated projects.
 
---
 
## 1. 🎯 Core Principles
 
- **Security First**: Implement security controls at every layer. Assume a zero-trust environment.

- **Production Ready**: All code must be suitable for production, with robust configuration, logging, and error handling.

- **Developer Experience**: Ensure the project is easy to set up, run, and understand. Prioritize clear documentation and clean code.

- **Performance by Design**: Build for low latency and high throughput. Implement caching and efficient data access patterns.

- **Observability**: The system's internal state must be inferable from its external outputs (logs, metrics, traces).
 
---
 
## 2. 🛠️ Standard Technology Stack
 
| Component | Technology | Version/Standard |

|---|---|---|

| **Language** | Java | **21** |

| **Framework** | Spring Boot | **3.3+** |

| **Build Tool** | Maven | 3.9+ |

| **Database** | PostgreSQL (Prod), H2 (Dev/Test) | Latest |

| **API Spec** | OpenAPI | 3.0 |

| **Container** | Docker | Latest |

| **Caching** | Caffeine (local), Redis (distributed) | Latest |

| **Validation** | Jakarta Bean Validation | 3.0 |

| **Testing** | JUnit 5, Mockito | Latest |
 
---
 
## 3. 🏗️ Project Initialization & Structure
 
### Initialization

Generate new projects using **Spring Initializr** with the following default dependencies:

- Spring Web

- Spring Data JPA

- Spring Boot Actuator

- Spring Cache

- Jakarta Bean Validation

- Lombok

- PostgreSQL Driver

- H2 Database

- SpringDoc OpenAPI (`org.springdoc:springdoc-openapi-starter-webmvc-ui`)
 
### Standard Package Structure

Organize all source code under a root package (e.g., `com.hcl.servicename`).
 
```

com/hcl/servicename/

├── ParcelServiceApplication.java   # Main application class

├── config/                         # @Configuration beans (Cache, Security, OpenAPI)

├── controller/                     # REST controllers (API layer)

├── dto/                            # Data Transfer Objects for API requests/responses

├── model/                          # JPA Entities

├── repository/                     # Spring Data JPA repositories

├── service/                        # Business logic

└── exception/                      # Custom exceptions and global handler

```
 
---
 
## 4. 📝 Development Workflow & Best Practices
 
### Step 1: API & DTO Design (Controller Layer)

1.  **Define DTOs**: Create separate Plain Old Java Objects (POJOs) for API requests and responses in the `dto` package. Use Jakarta Bean Validation annotations (`@NotBlank`, `@Size`, `@Email`, etc.) for all incoming data. **Never expose JPA entities directly in the API.**

2.  **Create Controller**: In the `controller` package, create a `@RestController`.

3.  **Map Endpoints**: Use `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`.

4.  **API Versioning**: Prefix all routes with `/api/v1`. Example: `@RequestMapping("/api/v1/parcels")`.

5.  **Input/Output**: Accept DTOs as `@RequestBody` and return `ResponseEntity<Dto>`.
 
### Step 2: Business Logic (Service Layer)

1.  **Create Service**: In the `service` package, create a `@Service` class.

2.  **Inject Dependencies**: Use constructor injection for repositories and other services.

3.  **Implement Logic**: Write the core business logic here. This layer is responsible for coordinating data access, calling other services, and enforcing business rules.

4.  **Use Transactions**: Use `@Transactional` for methods that modify data. Use `@Transactional(readOnly = true)` for read operations to optimize performance.

5.  **Map DTOs to Entities**: The service layer is responsible for converting incoming DTOs to JPA entities before passing them to the repository, and converting entities to DTOs before returning them to the controller.
 
### Step 3: Data Persistence (Repository & Model Layer)

1.  **Create Entity**: In the `model` package, define a JPA `@Entity`. Use `@Id`, `@GeneratedValue`, and appropriate `@Column` annotations. Avoid Lombok's `@Data` on entities; prefer `@Getter`, `@Setter`, `@ToString`.

2.  **Create Repository**: In the `repository` package, create an interface that extends `JpaRepository<Entity, IdType>`. Add custom query methods as needed.
 
### Step 4: Exception Handling

1.  **Custom Exceptions**: Create specific, unchecked exceptions in the `exception` package (e.g., `ResourceNotFoundException extends RuntimeException`).

2.  **Global Handler**: Create a `@RestControllerAdvice` class in the `exception` package.

3.  **Handle Exceptions**: Add `@ExceptionHandler` methods for custom exceptions, validation exceptions (`MethodArgumentNotValidException`), and a fallback generic `Exception`. Return a standardized `ErrorResponse` DTO with a message and HTTP status. **Never expose stack traces.**
 
### Step 5: Caching

1.  **Enable Caching**: Add `@EnableCaching` to the main application class.

2.  **Configure Cache**: Create a `CacheConfig` class to define cache managers (e.g., Caffeine for local, Redis for distributed).

3.  **Apply Caching**: Use `@Cacheable`, `@CachePut`, and `@CacheEvict` on service methods to cache results of expensive operations.
 
### Step 6: Configuration

- **Base Config (`application.properties`)**: Define common properties like `spring.application.name`.

- **Dev Profile (`application-dev.properties`)**: Configure H2 in-memory database, enable the H2 console, and set logging to `DEBUG`.

- **Prod Profile (`application-prod.properties`)**: Configure PostgreSQL connection details. Use placeholders for secrets (e.g., `${DB_PASSWORD}`). Disable detailed health info (`management.endpoint.health.show-details=never`).

### Step 7: `catalog-info.yaml` File Structure
 
The `catalog-info.yaml` file should be placed in the root directory of the component's source code repository.
 
```yaml
apiVersion: backstage.io/v1alpha1
kind: Component
metadata:
  name: # required, unique name
  description: # required, human-readable description
  tags:
    - # list of tags for discoverability
  annotations:
    # key-value pairs for tool integrations
  links:
    - url: # URL to a relevant resource
      title: # Display title for the link
      icon: # Optional: e.g., 'dashboard'
spec:
  type: # required, e.g., 'service', 'library'
  lifecycle: # required, e.g., 'experimental', 'production'
  owner: # required, the owning team or user
  system: # optional, the larger system this component belongs to
  dependsOn:
    - # list of components/resources this component depends on
  providesApis:
    - # list of APIs this component provides
```
 
---
 ### Step 8: #Coding standards#
 
 ### Step 9: Documentation

- **OpenAPI**: Configure a `OpenApiConfig` bean to set the API title, version, and description.

- **JavaDoc**: Add JavaDoc comments to all public methods in controllers and services, explaining their purpose, parameters, and return values.
 
### Step 10: Testing

- **Unit Tests**: Test services and controllers by mocking their dependencies (e.g., repositories). Place tests in `src/test/java`.

- **Integration Tests**: Use `@SpringBootTest` and `@ActiveProfiles("test")` to test the full application flow, from controller to database. Use Testcontainers for database integration tests.

- **Coverage**: Aim for a minimum of 80% test coverage.
 
---
 
## 5. 🐳 Containerization & Deployment
 
### Dockerfile

Create a multi-stage `Dockerfile`:

1.  **Build Stage**: Use a `maven` base image to build the application JAR.

2.  **Runtime Stage**: Use a minimal JRE image (e.g., `eclipse-temurin:21-jre-alpine`).

3.  **Security**: Create and run as a non-root user.

4.  **Health Check**: Add a `HEALTHCHECK` instruction that calls the `/actuator/health` endpoint.

5.  **Execution**: Use `ENTRYPOINT ["java", "-jar", "app.jar"]`.
 
### Environment Variables

All secrets (database passwords, API keys) **must** be supplied via environment variables, not hardcoded in properties files.
 
---
 
This protocol ensures consistency, security, and quality across all generated microservices. The agent must validate its output against these guidelines before completing a task.
 
Sample Agent.md
 
Platform engineer agent can create this..  
 
Here the details and the best practices we need to give link to appropriate confluence, team or github link so that it will be more realistic 
 
then the consumer while building a microservice, he can download this agent.md file from IDP and use his specific development
 