# 🛡️ ContainerShield

**ContainerShield** is a container security and monitoring backend built with **Spring Boot**. It provides REST APIs for inspecting Docker containers and scanning Docker images for known security vulnerabilities using **Trivy**.

The project is designed as a foundation for a centralized container-security platform where developers can monitor their Docker environment and identify vulnerable container images through a REST API.

---

## 🚀 Features

### 🐳 Docker Container Monitoring

ContainerShield communicates with the locally installed Docker CLI to retrieve container information.

Currently supported:

* View running containers
* View all containers
* Retrieve container information in a structured JSON format
* Extract container ID, name, image and status

The Docker integration is implemented through a dedicated service layer and REST controller.

### 🔍 Container Image Vulnerability Scanning

ContainerShield integrates with **Trivy** to scan Docker images for known vulnerabilities.

The scanner:

1. Accepts a Docker image name
2. Executes a Trivy image scan
3. Requests JSON output
4. Parses the Trivy results
5. Extracts vulnerability information
6. Returns the results through a REST API

The returned vulnerability information includes:

* Vulnerability ID
* Package name
* Installed version
* Fixed version
* Severity
* Title
* Description

### 🗄️ PostgreSQL Persistence

The application is configured to use **PostgreSQL** with Spring Data JPA.

The current configuration uses:

```text
Database: containershield
Host: localhost
Port: 5432
Username: postgres
```

Hibernate is configured with `ddl-auto: update` for automatic schema updates during development.

---

## 🏗️ Architecture

ContainerShield follows a layered Spring Boot architecture:

```text
                    ┌──────────────────────┐
                    │       Client         │
                    │  Web / Frontend/API  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Controllers      │
                    │                      │
                    │ DockerController     │
                    │ TrivyController      │
                    │ HealthController     │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │       Services       │
                    │                      │
                    │ DockerService        │
                    │ TrivyService         │
                    └───────┬───────┬──────┘
                            │       │
                    ┌───────▼───┐ ┌─▼─────────┐
                    │   Docker  │ │   Trivy   │
                    │    CLI    │ │   Scanner │
                    └───────────┘ └───────────┘

                               │
                               ▼
                    ┌──────────────────────┐
                    │     PostgreSQL       │
                    │    + Spring Data JPA │
                    └──────────────────────┘
```

The source is organized into separate controller, DTO, entity and service packages.

---

## 🧰 Tech Stack

| Technology            | Purpose                         |
| --------------------- | ------------------------------- |
| **Java 17**           | Backend programming language    |
| **Spring Boot 4.1.1** | Application framework           |
| **Spring MVC**        | REST API development            |
| **Spring Data JPA**   | Database persistence            |
| **PostgreSQL**        | Relational database             |
| **Docker**            | Container runtime               |
| **Trivy**             | Container vulnerability scanner |
| **Maven**             | Build and dependency management |
| **Lombok**            | Boilerplate reduction           |

The Maven configuration currently targets Java 17 and includes Spring Web MVC, Spring Data JPA, PostgreSQL, validation, Lombok and testing dependencies.

---

# 📁 Project Structure

```text
containershield/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/containershield/
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── DockerController.java
│   │   │       │   ├── TrivyController.java
│   │   │       │   └── HealthController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   └── TrivyVulnerability.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── DockerContainerEntity.java
│   │   │       │   └── DockerImageEntity.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── DockerService.java
│   │   │       │   └── TrivyService.java
│   │   │       │
│   │   │       └── ContainershieldApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.yaml
│   │
│   └── test/
│
├── pom.xml
├── mvnw
└── mvnw.cmd
```

---

# ⚙️ Prerequisites

Before running ContainerShield, make sure the following are installed:

* **Java 17+**
* **Maven** or the included Maven Wrapper
* **Docker**
* **Trivy**
* **PostgreSQL**

You can verify the installations with:

```bash
java -version
docker --version
trivy --version
psql --version
```

Docker must be running because ContainerShield executes Docker CLI commands directly from the backend.

Trivy must also be available in the system `PATH`, since the application invokes the `trivy` command directly.

---

# 🗄️ Database Setup

Create the PostgreSQL database:

```psql
CREATE DATABASE containershield;
```

The default development configuration is:

```yaml
spring:
  application:
    name: containershield

  datasource:
    url: jdbc:postgresql://localhost:5432/containershield 
    username: postgres
    password: postgres

  jpa:
    hibernate:
      ddl-auto: update
```

Update the username and password in `application.yaml` if your PostgreSQL installation uses different credentials.

> **Security note:** Do not use default database credentials in a production deployment. Prefer environment variables or a secrets manager.

---

# ▶️ Running the Application

Clone the repository:

```bash
git clone https://github.com/chaitanya2850/ContainerShield.git
```

Navigate to the Spring Boot application:

```bash
cd ContainerShield/containershield
```

Run using the Maven Wrapper:

### Linux / macOS

```bash
./mvnw spring-boot:run
```

### Windows

```powershell
mvnw.cmd spring-boot:run
```

Or package the application:

```bash
./mvnw clean package
```

Then run the generated JAR:

```bash
java -jar target/containershield-0.0.1-SNAPSHOT.jar
```

---

# 🔌 REST API

## 🐳 Docker APIs

### Get Running Containers

```http
GET /api/docker/containers/running
```

Example:

```bash
curl http://localhost:8080/api/docker/containers/running
```

The endpoint returns currently running Docker containers.

---

### Get All Containers

```http
GET /api/docker/containers/all
```

Example:

```bash
curl http://localhost:8080/api/docker/containers/all
```

This uses Docker's `ps -a` functionality to retrieve containers, including stopped containers.

---

## 🔍 Trivy Vulnerability Scan

### Scan an Image

```http
GET /api/trivy/scan?image=<IMAGE_NAME>
```

Example:

```bash
curl "http://localhost:8080/api/trivy/scan?image=nginx:latest"
```

Another example:

```bash
curl "http://localhost:8080/api/trivy/scan?image=postgres:17"
```

ContainerShield internally executes a command equivalent to:

```bash
trivy image --format json <IMAGE_NAME>
```

and converts the vulnerability results into the application's `TrivyVulnerability` DTO.

Example response:

```json
[
  {
    "vulnerabilityId": "CVE-XXXX-XXXX",
    "packageName": "example-package",
    "installedVersion": "1.0.0",
    "fixedVersion": "1.0.1",
    "severity": "HIGH",
    "title": "Example vulnerability",
    "description": "Description of the vulnerability."
  }
]
```

---

# 🔄 Request Flow

A Trivy scan follows this flow:

```text
Client
  │
  │ GET /api/trivy/scan?image=nginx:latest
  ▼
TrivyController
  │
  ▼
TrivyService
  │
  │ Executes:
  │ trivy image --format json nginx:latest
  ▼
Trivy CLI
  │
  │ JSON vulnerability report
  ▼
TrivyService
  │
  │ Parses JSON
  ▼
TrivyVulnerability DTO
  │
  ▼
JSON Response
```

Similarly, Docker container information follows:

```text
Client
  │
  │ GET /api/docker/containers/running
  ▼
DockerController
  │
  ▼
DockerService
  │
  │ docker ps --format "{{json .}}"
  ▼
Docker CLI
  │
  ▼
JSON parsing
  │
  ▼
DockerContainerEntity
  │
  ▼
JSON Response
```

The current implementation uses Java `ProcessBuilder` to invoke both Docker and Trivy from the backend.

---

# 🛡️ Why ContainerShield?

Modern applications frequently rely on Docker containers, but simply running a container does not guarantee that its underlying image is secure.

Container images can contain:

* Outdated dependencies
* Known CVEs
* Vulnerable operating-system packages
* Unpatched libraries
* Security issues introduced through transitive dependencies

ContainerShield aims to bring **container visibility and vulnerability scanning behind a single backend API**.

Instead of manually running:

```bash
docker ps
```

and:

```bash
trivy image <image>
```

developers can interact with a unified REST API.

---

# 🧩 Current Architecture

ContainerShield currently contains the following major components:

### Controllers

**DockerController**

Responsible for exposing Docker-related REST endpoints.

**TrivyController**

Provides the image vulnerability scanning endpoint.

**HealthController**

Provides application health-related functionality.

### Services

**DockerService**

Handles communication with the Docker CLI and converts Docker command output into application objects.

**TrivyService**

Executes Trivy scans, parses the generated JSON and maps vulnerabilities into DTOs.

### Entities

```text
DockerContainerEntity
DockerImageEntity
```

These represent Docker-related application data.

### DTO

```text
TrivyVulnerability
```

Represents vulnerability information returned by Trivy.

---

# 🚧 Roadmap

ContainerShield is currently under active development.

Planned improvements include:

* [ ] Docker image listing API
* [ ] Individual container details
* [ ] Container start/stop/restart operations
* [ ] Container resource monitoring
* [ ] Individual image inspection
* [ ] Vulnerability severity filtering
* [ ] Vulnerability statistics
* [ ] Vulnerability history
* [ ] Persistent scan results
* [ ] Security dashboard
* [ ] Authentication and authorization
* [ ] Role-based access control
* [ ] Global exception handling
* [ ] API documentation with Swagger/OpenAPI
* [ ] Docker Compose deployment
* [ ] Containerized deployment
* [ ] CI/CD security scanning
* [ ] Production-ready configuration using environment variables
* [ ] Improved Docker API integration instead of relying solely on CLI execution

---

# 🔐 Security Considerations

ContainerShield interacts directly with the Docker environment.

Therefore, the application should **not be exposed directly to an untrusted network in its current development state**.

In particular:

* Protect the Docker daemon.
* Do not expose Docker's socket unnecessarily.
* Add authentication before production deployment.
* Validate image names and request parameters.
* Avoid hard-coded database credentials.
* Use environment variables for secrets.
* Add authorization for container-management operations.
* Restrict access to the ContainerShield API.
* Run the application with the minimum required privileges.

---

# 🧪 Development

Build the project:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test
```

Run the application:

```bash
./mvnw spring-boot:run
```

---

# 📌 Project Status

**Status:** 🚧 Active Development

ContainerShield currently provides the backend foundation for:

```text
Docker Monitoring
        +
Container Image Security Scanning
        +
REST API
        +
PostgreSQL Persistence
```

The project is intended to evolve into a more complete **container security and management platform**.

---

# 👨‍💻 Author

**Chaitanya Patil**

GitHub:
https://github.com/chaitanya2850

---

# 📄 License

License information will be added as the project is finalized.
