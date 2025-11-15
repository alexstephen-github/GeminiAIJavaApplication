Okay, I'd be happy to provide a comprehensive code template for a Spring Boot REST API framework. This template will cover the core components, follow good practices, and include explanations for each part.

We'll create a simple Product Management API with CRUD (Create, Read, Update, Delete) operations.

---

### Project Structure Overview

Here's the logical project structure we'll follow:

```
your-project-root/
├── .mvn/
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── productapp/
│   │   │               ├── ProductApplication.java                 <-- Main Spring Boot App
│   │   │               ├── controller/
│   │   │               │   └── ProductController.java              <-- REST Endpoints
│   │   │               ├── dto/
│   │   │               │   ├── ProductRequestDTO.java              <-- Data Transfer Object (Request)
│   │   │               │   └── ProductResponseDTO.java             <-- Data Transfer Object (Response)
│   │   │               ├── exception/
│   │   │               │   ├── ErrorResponse.java                  <-- Custom Error Structure
│   │   │               │   ├── GlobalExceptionHandler.java         <-- Centralized Error Handling
│   │   │               │   └── ResourceNotFoundException.java      <-- Custom Business Exception
│   │   │               ├── model/
│   │   │               │   └── Product.java                        <-- JPA Entity (Database Model)
│   │   │               ├── repository/
│   │   │               │   └── ProductRepository.java              <-- Data Access Layer
│   │   │               └── service/
│   │   │                   ├── ProductService.java                 <-- Service Interface (Business Logic)
│   │   │                   └── impl/
│   │   │                       └── ProductServiceImpl.java         <-- Service Implementation
│   │   └── resources/
│   │       └── application.properties                              <-- Application Configuration
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── productapp/
│                       └── ProductApplicationTests.java            <-- Basic Test Class
├── pom.xml                                                         <-- Maven Project Object Model
└── README.md
```

---

### 1. Maven Project Object Model (`pom.xml`)

This file defines your project's dependencies and build configuration.

$$$$your-project-root/
&&&&pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version> <!-- Use the latest stable Spring Boot version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>product-app</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>product-app</name>
    <description>Demo project for Spring Boot REST API</description>

    <properties>
        <java.version>17</java.version> <!-- Ensure this matches your JDK version -->
    </properties>

    <dependencies>
        <!-- Spring Boot Starter Web: Essential for building RESTful APIs -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Starter Data JPA: For database interaction using JPA/Hibernate -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 Database: In-memory database, great for development and testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok: Reduces boilerplate code (getters, setters, constructors) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Starter Validation: For input validation (e.g., @NotNull, @Size) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Spring Boot Starter Test: For writing unit and integration tests -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin: Packages the application as an executable JAR -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```
**Explanation:**
*   **`parent`**: Inherits default configurations from `spring-boot-starter-parent`, simplifying dependency management.
*   **`groupId`, `artifactId`, `version`**: Standard Maven project identifiers.
*   **`java.version`**: Specifies the Java version for compilation.
*   **`spring-boot-starter-web`**: Provides all necessary dependencies for building web applications, including Tomcat and Spring MVC.
*   **`spring-boot-starter-data-jpa`**: Includes Spring Data JPA and Hibernate for object-relational mapping.
*   **`h2`**: An in-memory database, perfect for local development as it doesn't require a separate database server.
*   **`lombok`**: A library that generates boilerplate code (getters, setters, constructors, etc.) at compile time, making your code cleaner.
*   **`spring-boot-starter-validation`**: Enables Java Bean Validation (JSR 380) using Hibernate Validator for easy data validation.
*   **`spring-boot-starter-test`**: Contains utilities for testing Spring Boot applications.
*   **`spring-boot-maven-plugin`**: Creates an executable JAR file that includes all dependencies.

---

### 2. Main Application Class

This is the entry point of your Spring Boot application.

$$$$src/main/java/com/example/productapp/
&&&&ProductApplication.java
```java
package com.example.productapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot Product Application.
 * The @SpringBootApplication annotation is a convenience annotation that adds:
 * - @Configuration: Tags the class as a source of bean definitions for the application context.
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *   other beans, and various property settings.
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 *   in the 'com.example.productapp' package, allowing it to find controllers, services, etc.
 */
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
```
**Explanation:**
*   **`@SpringBootApplication`**: This is a meta-annotation that combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. It's the most common annotation for a Spring Boot application.
    *   `@Configuration`: Marks the class as a source of bean definitions.
    *   `@EnableAutoConfiguration`: Automatically configures the Spring application based on the JAR dependencies you've added.
    *   `@ComponentScan`: Scans the current package and its sub-packages for components (like `@Controller`, `@Service`, `@Repository`, etc.) and registers them as Spring beans.
*   **`main` method**: Uses `SpringApplication.run()` to bootstrap and launch a Spring application.

---

### 3. JPA Entity (Database Model)

This class represents a table in your database and is managed by JPA (Hibernate).

$$$$src/main/java/com/example/productapp/model/
&&&&Product.java
```java
package com.example.productapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // For precise monetary values

/**
 * Represents a Product entity in the database.
 * @Entity: Marks this class as a JPA entity, meaning it maps to a database table.
 * @Table: Specifies the name of the database table if it differs from the class name.
 * @Data: Lombok annotation to generate getters, setters, toString(), equals(), and hashCode().
 * @NoArgsConstructor: Lombok annotation to generate a no-argument constructor.
 * @AllArgsConstructor: Lombok annotation to generate a constructor with all fields.
 */
@Entity
@Table(name = "products") // Optional: Specify table name if different from class name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     * @Id: Marks this field as the primary key.
     * @GeneratedValue: Configures the way the primary key is generated.
     *   - GenerationType.IDENTITY: Relies on an auto-increment column in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Using Long for ID to allow for nullability (e.g., for new entities)

    /**
     * Name of the product.
     * @Column: Maps the field to a database column. Optional, but good for specifying constraints.
     *   - nullable = false: The column cannot contain null values.
     *   - unique = true: The column must contain unique values across all rows.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Description of the product.
     * @Column: Can be used to set length or other column properties.
     */
    @Column(length = 500)
    private String description;

    /**
     * Price of the product. Using BigDecimal for precise currency values.
     * @Column: Specifies precision and scale for decimal numbers.
     *   - precision: Total number of digits (before and after decimal point).
     *   - scale: Number of digits after the decimal point.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Stock quantity of the product.
     */
    @Column(nullable = false)
    private Integer stock;
}
```
**Explanation:**
*   **`@Entity`**: Marks the class as a JPA entity, mapping it to a database table.
*   **`@Table`**: (Optional) Specifies the table name if it differs from the class name.
*   **`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`**: Lombok annotations for boilerplate reduction.
*   **`@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Defines the primary key and its auto-increment generation strategy.
*   **`@Column`**: (Optional) Configures column properties like `nullable`, `unique`, `length`, `precision`, `scale`.
*   **`BigDecimal`**: Used for `price` to avoid floating-point inaccuracies when dealing with monetary values.

---

### 4. Data Transfer Objects (DTOs)

DTOs are used to transfer data between the client and the server. They decouple your internal entity models from your external API contract, allowing you to define specific data structures for requests and responses.

#### 4.1 ProductRequestDTO (For creating/updating products)

$$$$src/main/java/com/example/productapp/dto/
&&&&ProductRequestDTO.java
```java
package com.example.productapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a Product.
 * Used to receive product data from the client.
 * Includes validation annotations to ensure data integrity before processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Product description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Product stock cannot be null")
    @Min(value = 0, message = "Product stock cannot be negative")
    private Integer stock;
}
```
**Explanation:**
*   **`@NotBlank`**: Ensures the string is not null and not empty (after trimming whitespace).
*   **`@Size`**: Specifies the minimum and maximum length for a string.
*   **`@NotNull`**: Ensures the field is not null.
*   **`@DecimalMin`**: Ensures a `BigDecimal` value is not less than the specified minimum.
*   **`@Min`**: Ensures an integer value is not less than the specified minimum.
*   These annotations provide server-side validation, which is crucial for data integrity.

#### 4.2 ProductResponseDTO (For sending product data to the client)

$$$$src/main/java/com/example/productapp/dto/
&&&&ProductResponseDTO.java
```java
package com.example.productapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for sending Product data as a response to the client.
 * Decouples the internal Product entity from the external API representation.
 * Allows for flexibility in what data is exposed to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
}
```
**Explanation:**
*   This DTO might look similar to the `Product` entity, but it provides a clean separation. You could omit fields, rename them, or add computed fields here without affecting your JPA entity.

---

### 5. Repository Layer

This layer handles data access operations to the database.

$$$$src/main/java/com/example/productapp/repository/
&&&&ProductRepository.java
```java
package com.example.productapp.repository;

import com.example.productapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to inherit standard CRUD operations (Create, Read, Update, Delete).
 * JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.
 *
 * @Repository: Indicates that this interface is a "Repository" component,
 *              a mechanism for encapsulating storage, retrieval, and search behavior.
 *              Spring will automatically implement this interface at runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be defined here if needed,
    // e.g., Product findByName(String name);
    // Spring Data JPA can automatically implement these based on method names.
}
```
**Explanation:**
*   **`@Repository`**: Marks this interface as a repository, enabling Spring's component scanning to find it.
*   **`JpaRepository<Product, Long>`**: Extends `JpaRepository`, which is part of Spring Data JPA. It provides generic CRUD methods (e.g., `save`, `findById`, `findAll`, `deleteById`) for the `Product` entity with `Long` as its primary key type.
*   You can add custom query methods by simply declaring them, and Spring Data JPA will generate the implementation based on the method name (e.g., `findByName(String name)`).

---

### 6. Service Layer

This layer contains the business logic. It orchestrates calls to the repository and performs any necessary transformations or validations.

#### 6.1 ProductService Interface

Defines the contract for the service layer.

$$$$src/main/java/com/example/productapp/service/
&&&&ProductService.java
```java
package com.example.productapp.service;

import com.example.productapp.dto.ProductRequestDTO;
import com.example.productapp.dto.ProductResponseDTO;

import java.util.List;

/**
 * Interface defining the business operations for Product management.
 * This promotes a clean separation of concerns and allows for multiple implementations
 * if needed (e.g., for different data sources or testing purposes).
 */
public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long id);
}
```

#### 6.2 ProductServiceImpl Implementation

Provides the actual implementation of the service methods.

$$$$src/main/java/com/example/productapp/service/impl/
&&&&ProductServiceImpl.java
```java
package com.example.productapp.service.impl;

import com.example.productapp.dto.ProductRequestDTO;
import com.example.productapp.dto.ProductResponseDTO;
import com.example.productapp.exception.ResourceNotFoundException;
import com.example.productapp.model.Product;
import com.example.productapp.repository.ProductRepository;
import com.example.productapp.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transaction management

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductService interface.
 * Contains the core business logic for managing products.
 *
 * @Service: Indicates that an annotated class is a "Service". This is a specialization
 *           of @Component and is used to denote that a class contains business logic.
 * @Transactional: Ensures that methods are executed within a database transaction.
 *                 If an unchecked exception occurs, the transaction will be rolled back.
 */
@Service
@Transactional // Apply transactional behavior to all methods in this service by default
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor-based dependency injection for ProductRepository.
     * Spring will automatically provide an instance of ProductRepository.
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates a new product.
     * Maps the DTO to an entity, saves it, and maps the saved entity back to a response DTO.
     * @param productRequestDTO The DTO containing product creation data.
     * @return ProductResponseDTO of the created product.
     */
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());

        Product savedProduct = productRepository.save(product);
        return mapToProductResponseDTO(savedProduct);
    }

    /**
     * Retrieves a product by its ID.
     * Uses Optional to handle cases where the product might not be found.
     * Throws ResourceNotFoundException if the product does not exist.
     * @param id The ID of the product to retrieve.
     * @return ProductResponseDTO of the found product.
     * @throws ResourceNotFoundException if no product is found with the given ID.
     */
    @Override
    @Transactional(readOnly = true) // Optimize for read-only operations
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponseDTO(product);
    }

    /**
     * Retrieves all products.
     * @return A list of ProductResponseDTOs.
     */
    @Override
    @Transactional(readOnly = true) // Optimize for read-only operations
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing product.
     * Retrieves the existing product, updates its fields from the DTO, and saves it.
     * Throws ResourceNotFoundException if the product does not exist.
     * @param id The ID of the product to update.
     * @param productRequestDTO The DTO containing product update data.
     * @return ProductResponseDTO of the updated product.
     * @throws ResourceNotFoundException if no product is found with the given ID.
     */
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setPrice(productRequestDTO.getPrice());
        existingProduct.setStock(productRequestDTO.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponseDTO(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     * Checks if the product exists before deleting.
     * Throws ResourceNotFoundException if the product does not exist.
     * @param id The ID of the product to delete.
     * @throws ResourceNotFoundException if no product is found with the given ID.
     */
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Helper method to map a Product entity to a ProductResponseDTO.
     * @param product The Product entity to map.
     * @return The corresponding ProductResponseDTO.
     */
    private ProductResponseDTO mapToProductResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
```
**Explanation:**
*   **`@Service`**: Marks this class as a Spring service component.
*   **`@Transactional`**: Manages database transactions. Methods annotated with `@Transactional` (or if the class is annotated) will run within a transaction. If an unchecked exception is thrown, the transaction is rolled back. `readOnly = true` is an optimization for methods that only read data.
*   **Constructor Injection**: The `ProductRepository` is injected via the constructor, which is the recommended way for dependency injection in Spring.
*   **DTO to Entity Mapping**: In `createProduct` and `updateProduct`, `ProductRequestDTO` objects are converted to `Product` entities.
*   **Entity to DTO Mapping**: In methods returning `ProductResponseDTO`, `Product` entities are converted to `ProductResponseDTO` objects using a private helper method `mapToProductResponseDTO`. This ensures that the service layer only exposes DTOs and not the internal entities.
*   **`orElseThrow()` with `ResourceNotFoundException`**: This pattern handles cases where a product might not be found by its ID, throwing a custom exception.
*   **`existsById()`**: Used in `deleteProduct` to check for existence before attempting deletion, providing a cleaner error message if the resource is already gone or never existed.

---

### 7. REST Controller

This layer exposes your API endpoints to clients.

$$$$src/main/java/com/example/productapp/controller/
&&&&ProductController.java
```java
package com.example.productapp.controller;

import com.example.productapp.dto.ProductRequestDTO;
import com.example.productapp.dto.ProductResponseDTO;
import com.example.productapp.service.ProductService;
import jakarta.validation.Valid; // For DTO validation
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Product resources.
 * Handles HTTP requests and delegates business logic to the ProductService.
 *
 * @RestController: Combines @Controller and @ResponseBody, meaning methods
 *                  return data directly, not view names.
 * @RequestMapping: Specifies the base URL path for all endpoints in this controller.
 */
@RestController
@RequestMapping("/api/products") // Base path for all product-related endpoints
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor-based dependency injection for ProductService.
     * Spring will automatically provide an instance of ProductService.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Handles POST requests to create a new product.
     * @param productRequestDTO The product data received from the client.
     * @return ResponseEntity with the created product and HTTP status 201 (Created).
     * @Valid: Triggers validation on the productRequestDTO based on annotations in the DTO.
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Handles GET requests to retrieve a product by ID.
     * @param id The ID of the product to retrieve from the URL path.
     * @return ResponseEntity with the product data and HTTP status 200 (OK).
     * @PathVariable: Binds a method parameter to a URI template variable.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Handles GET requests to retrieve all products.
     * @return ResponseEntity with a list of all products and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Handles PUT requests to update an existing product.
     * @param id The ID of the product to update from the URL path.
     * @param productRequestDTO The updated product data received from the client.
     * @return ResponseEntity with the updated product and HTTP status 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * Handles DELETE requests to delete a product by ID.
     * @param id The ID of the product to delete from the URL path.
     * @return ResponseEntity with no content and HTTP status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
```
**Explanation:**
*   **`@RestController`**: Marks this class as a REST controller, combining `@Controller` and `@ResponseBody`.
*   **`@RequestMapping("/api/products")`**: Sets the base path for all endpoints in this controller to `/api/products`.
*   **`@PostMapping`, `@GetMapping`, `@PutMapping`, `@DeleteMapping`**: Map HTTP methods to specific controller methods.
*   **`@PathVariable Long id`**: Extracts the `id` from the URL path (e.g., `/api/products/1`).
*   **`@RequestBody ProductRequestDTO productRequestDTO`**: Binds the request body (JSON/XML) to the `ProductRequestDTO` object.
*   **`@Valid`**: Triggers the validation rules defined in `ProductRequestDTO`. If validation fails, a `MethodArgumentNotValidException` is thrown, which we'll handle globally.
*   **`ResponseEntity`**: Provides full control over the HTTP response, including status code, headers, and body.
    *   `HttpStatus.CREATED` (201): For successful creation.
    *   `HttpStatus.OK` (200): For successful retrieval or update.
    *   `HttpStatus.NO_CONTENT` (204): For successful deletion with no content to return.

---

### 8. Exception Handling

Robust error handling is crucial for any production-ready API.

#### 8.1 Custom Exception

$$$$src/main/java/com/example/productapp/exception/
&&&&ResourceNotFoundException.java
```java
package com.example.productapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a resource (e.g., Product) was not found.
 * @ResponseStatus: When this exception is thrown, Spring will automatically
 *                  return an HTTP 404 Not Found status.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Sets the HTTP status code for this exception
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
**Explanation:**
*   **`@ResponseStatus(HttpStatus.NOT_FOUND)`**: When this exception is thrown from a controller or service method, Spring automatically maps it to an HTTP 404 (Not Found) status code.

#### 8.2 Error Response DTO

Defines a standard structure for error responses.

$$$$src/main/java/com/example/productapp/exception/
&&&&ErrorResponse.java
```java
package com.example.productapp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for a standardized error response body.
 * Provides consistent error messages to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private int statusCode;
    private String message;
    private List<String> details; // For validation errors, to list specific field errors
    private String path; // The request path that caused the error
}
```
**Explanation:**
*   Provides fields to give clients useful information about an error, including `timestamp`, `status`, `message`, `details` (especially useful for validation errors), and the `path` of the request.

#### 8.3 Global Exception Handler

A centralized place to handle exceptions across all controllers.

$$$$src/main/java/com/example/productapp/exception/
&&&&GlobalExceptionHandler.java
```java
package com.example.productapp.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Uses @ControllerAdvice to provide centralized exception handling across all @Controller classes.
 * Extends ResponseEntityExceptionHandler to leverage Spring's default handling for common MVC exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles custom ResourceNotFoundException.
     * Returns HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null, // No specific details for this general error
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles global/generic exceptions (e.g., NullPointerException, general RuntimeException).
     * Returns HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(), // General message for client
                List.of(ex.getLocalizedMessage()), // More specific for internal logging/debugging
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Custom handler for @Valid / @Validated validation failures.
     * Overrides Spring's default handling to return a custom ErrorResponse.
     * Returns HTTP 400 Bad Request.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // You can add more @ExceptionHandler methods for other specific exceptions as needed
    // e.g., @ExceptionHandler(DataIntegrityViolationException.class) for database constraint violations
}
```
**Explanation:**
*   **`@ControllerAdvice`**: This annotation allows you to consolidate your `(@ExceptionHandler, @InitBinder, @ModelAttribute)` annotations into a single, global error handling component.
*   **`extends ResponseEntityExceptionHandler`**: Provides a convenient base class for handling common Spring MVC exceptions. By overriding its methods (like `handleMethodArgumentNotValid`), you can customize the error response for those exceptions.
*   **`@ExceptionHandler(ResourceNotFoundException.class)`**: Catches `ResourceNotFoundException` specifically and returns a `404 Not Found` with our custom `ErrorResponse`.
*   **`@ExceptionHandler(Exception.class)`**: A generic handler that catches any other unhandled `Exception`, returning a `500 Internal Server Error`. This acts as a fallback.
*   **`handleMethodArgumentNotValid`**: Overrides the default handling for validation errors (`MethodArgumentNotValidException`), which occur when `@Valid` fails. It extracts specific field error messages and populates our `ErrorResponse`.
*   **`ServletWebRequest`**: Used to extract the request URI for the error response.

---

### 9. Application Configuration (`application.properties`)

This file holds application-specific settings.

$$$$src/main/resources/
&&&&application.properties
```properties
# Server Port
server.port=8080

# H2 Database Configuration
# H2 Console is a web-based console for managing the H2 database.
# Access it at http://localhost:8080/h2-console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate Configuration for H2
spring.datasource.url=jdbc:h2:mem:productdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# DDL (Data Definition Language) Auto property
# 'update': Hibernate will update the schema based on your entities.
# 'create-drop': Creates schema on startup and drops on shutdown (good for testing).
# 'none': Disables DDL generation (use for production with managed schema).
spring.jpa.hibernate.ddl-auto=update

# Show SQL queries in console (useful for debugging)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging Level (Optional)
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE
```
**Explanation:**
*   **`server.port`**: Sets the port on which the application will run.
*   **`spring.h2.console.enabled`**: Enables the H2 database console.
*   **`spring.datasource.url`**: Configures the H2 in-memory database. `DB_CLOSE_DELAY=-1` keeps the database alive as long as the JVM is running.
*   **`spring.jpa.hibernate.ddl-auto`**: Controls Hibernate's schema generation behavior. `update` is common for development, but `none` or `validate` is preferred for production environments where database schema is managed externally.
*   **`spring.jpa.show-sql`, `spring.jpa.properties.hibernate.format_sql`**: Useful for debugging, showing generated SQL queries in the console.

---

### How to Run This Template

1.  **Prerequisites**:
    *   Java Development Kit (JDK) 17 or higher.
    *   Maven (usually bundled with IDEs like IntelliJ, VS Code with Java extensions).
2.  **Clone/Create Project**: Create a new Spring Boot project using Spring Initializr (start.spring.io) with the dependencies: `Spring Web`, `Spring Data JPA`, `H2 Database`, `Lombok`, `Validation`. Then replace the generated files with the ones provided above. Or, manually create the folders and files.
3.  **Build**: Open a terminal in the project root and run:
    ```bash
    mvn clean install
    ```
4.  **Run**:
    ```bash
    mvn spring-boot:run
    ```
    Or, run the `ProductApplication.java` file directly from your IDE.

### Testing the API (using cURL or Postman)

Once the application is running (on `http://localhost:8080`):

**1. Create a Product (POST)**
*   **URL**: `http://localhost:8080/api/products`
*   **Method**: `POST`
*   **Headers**: `Content-Type: application/json`
*   **Body (JSON)**:
    ```json
    {
        "name": "Laptop Pro",
        "description": "Powerful laptop for professionals",
        "price": 1200.50,
        "stock": 100
    }
    ```
*   **Expected Response**: `201 Created` with the created product details (including ID).

**2. Get All Products (GET)**
*   **URL**: `http://localhost:8080/api/products`
*   **Method**: `GET`
*   **Expected Response**: `200 OK` with a list of products.

**3. Get Product by ID (GET)**
*   **URL**: `http://localhost:8080/api/products/1` (replace `1` with an actual product ID)
*   **Method**: `GET`
*   **Expected Response**: `200 OK` with the specific product.
*   **If not found**: `404 Not Found` with `ErrorResponse`.

**4. Update a Product (PUT)**
*   **URL**: `http://localhost:8080/api/products/1` (replace `1` with the product ID to update)
*   **Method**: `PUT`
*   **Headers**: `Content-Type: application/json`
*   **Body (JSON)**:
    ```json
    {
        "name": "Laptop Pro (Updated)",
        "description": "New description for the powerful laptop",
        "price": 1250.00,
        "stock": 95
    }
    ```
*   **Expected Response**: `200 OK` with the updated product details.

**5. Delete a Product (DELETE)**
*   **URL**: `http://localhost:8080/api/products/1` (replace `1` with the product ID to delete)
*   **Method**: `DELETE`
*   **Expected Response**: `204 No Content`.

**6. Test Validation (POST with invalid data)**
*   **URL**: `http://localhost:8080/api/products`
*   **Method**: `POST`
*   **Headers**: `Content-Type: application/json`
*   **Body (JSON)**:
    ```json
    {
        "name": "",  // Invalid: Blank
        "description": "Short desc",
        "price": -5.00, // Invalid: Negative
        "stock": null // Invalid: Null
    }
    ```
*   **Expected Response**: `400 Bad Request` with `ErrorResponse` detailing validation errors.

---

This template provides a solid foundation for building RESTful APIs with Spring Boot, incorporating common best practices like layered architecture, DTOs, validation, and global exception handling. You can extend this template by adding security, logging, more complex 