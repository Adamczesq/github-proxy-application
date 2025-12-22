# GitHub Proxy Application

Implementation of a REST API proxy service for GitHub, developed as a recruitment task. The application retrieves a list of non-fork repositories for a specified user, including branch details for each repository.

## Technologies

* Java 25
* Spring Boot 4.0.0
* Maven
* WireMock (Integration Testing)

## Prerequisites

To run this application, you need:
* JDK 25 installed and configured.
* Maven (or use the provided mvnw wrapper).

## Configuration

The application is configured to use Virtual Threads (Project Loom) to handle blocking I/O operations efficiently without the complexity of reactive programming.

Properties definition (`application.properties`):
* `spring.threads.virtual.enabled=true`
* `github.api.url` - defaults to [https://api.github.com](https://api.github.com) (can be overridden for testing purposes).

## Build and Run

### Running Tests
Integration tests use WireMock to simulate GitHub API responses.

```bash
./mvnw clean verify
```

### Running the Application
The application will start on port 8080.

```bash
./mvnw spring-boot:run
```

## API Documentation

### Get User Repositories

**Request:**
`GET /api/v1/repositories/{username}`

**Headers:**
`Accept: application/json`

**Success Response (200 OK):**
```json
[
  {
    "repositoryName": "example-repo",
    "ownerLogin": "username",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "d0dd1f61b031..."
      }
    ]
  }
]
```

**Error Response (404 Not Found):**
```json
{
  "status": 404,
  "message": "User not found"
}
```

## Architecture & Design Decisions

The implementation strictly follows the requirements provided in the task description. Below are the justifications for key architectural choices:

1.  **Single Package Structure**
    All classes are located in a single package. This decision complies with the requirement to avoid DDD/Hexagonal architecture and keeps the structure flat and simple for this specific scope.

2.  **No DTO/Domain Split**
    In accordance with the requirements, the application minimizes the number of models. API responses are mapped directly using Java Records, avoiding redundant mapping layers.

3.  **Synchronous Processing with Virtual Threads**
    The requirement explicitly forbade the use of WebFlux. To address the N+1 request problem (fetching branches for each repository) efficiently, the application leverages Java 25 Virtual Threads. This allows for high-throughput blocking I/O without the resource overhead of platform threads.

4.  **Error Handling**
    A global exception handler (`@RestControllerAdvice`) is used to ensure the 404 response format strictly matches the acceptance criteria, sanitizing the upstream error messages from GitHub API.