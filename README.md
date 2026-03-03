# User Management System

A **Spring Boot** web application that provides user registration, authentication, and a personal dashboard. It supports both a Thymeleaf-based MVC UI and a REST API, backed by PostgreSQL.

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Running Locally (with Docker Compose)](#running-locally-with-docker-compose)
  - [Running Locally (without Docker)](#running-locally-without-docker)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Testing](#testing)

---

## Features

- **User Registration** – Form-based sign-up with first name, last name, username, password (8–20 characters), and optional profile image upload. Duplicate usernames are rejected gracefully.
- **User Authentication** – Form login backed by Spring Security and BCrypt password hashing.
- **Profile Images** – Optional JPEG/PNG/GIF/WebP upload during registration; stored in S3 (or LocalStack when using Docker). Dashboard shows profile photo or initials.
- **User Dashboard** – Authenticated users can view their profile information (and profile image) after logging in.
- **REST API** – Fetch all registered users (returns data without passwords).
- **API Documentation** – Interactive Swagger UI available at `/swagger-ui/index.html`.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Web | Spring MVC + Thymeleaf |
| Security | Spring Security 6 (BCrypt) |
| Persistence | Spring Data JPA + PostgreSQL |
| Testing DB | H2 (in-memory) |
| Object Mapping | ModelMapper 3.2.4 |
| API Docs | SpringDoc OpenAPI 2.7.0 |
| Build | Maven (mvnw wrapper included) |
| Containerization | Docker + Docker Compose |
| Local AWS Emulation | LocalStack (S3) |

---

## Project Structure

```
src/
├── main/
│   ├── java/za/co/userdashboard/
│   │   ├── UserdashboardApplication.java   # Application entry point
│   │   ├── config/
│   │   │   ├── MapperConfig.java           # ModelMapper bean
│   │   │   ├── SwaggerConfig.java          # OpenAPI/Swagger configuration
│   │   │   ├── SecurityConfig.java         # Security rules, login/logout setup
│   │   │   ├── S3Config.java               #Configuration of S3 service for storage of image.
│   │   │   └── S3BucketInitializer.java    #Intialising the S3 bucket
│   │   ├── controller/
│   │   │   ├── UserController.java         # REST API controller (/user)
│   │   │   └── WebController.java          # MVC controller (login, register, dashboard)
│   │   ├── dto/
│   │   │   ├── UserCreationDTO.java        # Registration request payload
│   │   │   ├── UserLoginDTO.java           # Login request payload
│   │   │   └── UserResponseDTO.java        # API response (no password)
│   │   ├── entity/
│   │   │   └── AppUser.java               # JPA entity mapped to `users` table
│   │   ├── exception/
│   │   │   └── UserAlreadyExistsException.java
│   │   ├── repository/
│   │   │   └── UserRepository.java        # JPA repository with findByUserName()
│   │   ├── security/
│   │   │   └── CustomerUserDetailsService.java  # Loads user from DB for Spring Security
│   │   │   
│   │   └── service/
│   │       ├── UserService.java                # Service interface
│   │       ├── UserServiceImpl.java            # Service implementation
│   │       ├── ProfileImageService.java        # ProfileImage interface
│   │       ├── ProfileImageServiceImpl.java    # ProfileImage implementation
│   │       └── ImageStreamResult.java          # ImageStreamResult
│   └── resources/
│       ├── templates/
│       │   ├── login.html                  # Login page (Thymeleaf)
│       │   ├── register.html               # Registration page (Thymeleaf)
│       │   └── dashboard.html              # User dashboard (Thymeleaf)
│       ├── application.properties          # Production configuration (PostgreSQL)
│       └── application-dev.properties      # Development configuration (H2)
└── test/
    └── java/za/co/userdashboard/
        ├── controller/
        │   ├── UserControllerTest.java     # MockMvc tests for UserController (/user/fetch)
        │   └── WebControllerTest.java      # MockMvc tests for WebController (login, register, dashboard)
        ├── service/
        │   └── AppUserServiceImplTest.java # Unit tests for UserServiceImpl
        └── UserdashboardApplicationTests.java  # Spring context smoke test
```

---

## Getting Started

### Prerequisites

- **Java 21** or later
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **Docker & Docker Compose** (for containerised setup)
- **PostgreSQL 16** (if running without Docker)
- **LocalStack** (if running without Docker)

### Running Locally (with Docker Compose)


```bash
docker-compose up --build
```

This starts:
- A **PostgreSQL 16** container (`userdashboard-postgres`) on port `5432`
- A **LocalStack** container (`userdashboard-localstack`) on port `4566`, emulating AWS S3 for profile image storage
- The **application** container (`userdashboard-app`) on port `8080` (configured to use LocalStack S3 via `APP_S3_ENDPOINT_OVERRIDE`)

Open your browser at [http://localhost:8080/login](http://localhost:8080/login).

To stop and remove containers:

```bash
docker-compose down
```

### Running Locally (without Docker)

1. **Start PostgreSQL** and create a database named `Users_db` with a user `userdashboard_user`.

2. **Update `src/main/resources/application.properties`** with your database credentials if they differ from the defaults.

3. **Start the LocalStack 'localstack start'**

```bash
 localstack start
```

4. **Build and run** the application:

```bash
 ./mvnw spring-boot:run
```

Or build the JAR and run it directly:

```bash
 ./mvnw clean package -DskipTests
 java -jar target/userdashboard-0.0.1-SNAPSHOT.jar
```

5. **For development** (uses H2 in-memory database – no PostgreSQL needed):

```bash
 ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The H2 console is available at [http://localhost:8080/h2-console](http://localhost:8080/h2-console) in dev mode.

---

## Configuration

### `application.properties` 



| Property | Default Value |
|---|---|
| `server.port` | `8080` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/Users_db` |
| `spring.datasource.username` | `userdashboard_user` |
| `spring.datasource.password` | `userdashboard_password` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `spring.thymeleaf.cache` | `false` |
| `app.s3.bucket-name` | `userdashboard-profiles` |
| `app.s3.endpoint-override` | *(empty = real AWS)* Set to `http://localhost:4566` for LocalStack |
| `app.s3.public-base-url` | *(optional)* If set, profile image URLs use this base; otherwise the app proxies images at `/profile-image?key=...` |

### `application-dev.properties` 

Uses an H2 in-memory database with `create-drop` DDL mode. The H2 console is enabled for easy inspection.

---

## API Endpoints

### Web (MVC / Thymeleaf)

| Method | Path | Description | Auth Required |
|---|---|---|---|
| `GET` | `/login` | Login page | No |
| `GET` | `/register` | Registration page | No |
| `POST` | `/register` | Submit registration form | No |
| `GET` | `/dashboard` | User dashboard | Yes |

### REST API

| Method | Path | Description | Auth Required |
|---|---|---|---|
| `GET` | `/user/fetch` | Get all users (without passwords) | Yes |

### API Documentation

| Path | Description |
|---|---|
| `/swagger-ui/index.html` | Interactive Swagger UI |
| `/v3/api-docs` | Raw OpenAPI JSON spec |

---

## Security

- All routes require authentication except `/register`, `/login`, `/error`, static assets (`/css/**`, `/js/**`), and Swagger/OpenAPI paths.
- Passwords are hashed using **BCrypt** before storage.
- Form login redirects to `/dashboard` on success.
- Logout invalidates the HTTP session and redirects to `/login`.
- A maximum of **one concurrent session** per user is enforced.
- CSRF protection is disabled (suitable for development; enable for production deployments).

---

## Testing

Run all tests with:

```bash
 ./mvnw test
```

Tests use the **H2 in-memory database** automatically (via the `dev` profile or test configuration), so no external database is needed.

| Test Class | Coverage |
|---|---|
| `controller/UserControllerTest` | MockMvc tests for `GET /user/fetch`: authenticated user returns user list, empty list returns empty array |
| `controller/WebControllerTest` | MockMvc tests for login page, register page, registration (valid data, password mismatch, blank fields, short password, duplicate user), and authenticated dashboard |
| `service/AppUserServiceImplTest` | Unit tests for `createUser()`, `getUser()` (found and not found), and `getAllUsers()` (with results and empty) using Mockito |
| `UserdashboardApplicationTests` | Spring application context smoke test |

---

## 📝 License

This project is created for educational purposes.

---

## 👨‍💻 Author

**Subham Dey**

For questions or support, please contact: de.subham1@gmail.com

