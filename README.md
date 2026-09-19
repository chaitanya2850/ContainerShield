# ContainerShield

ContainerShield is a Spring Boot backend for Docker container monitoring, Docker image vulnerability scanning (via Trivy), source code security scanning (via Semgrep), and an AI-powered security assistant (via Ollama) that answers questions grounded in your own scan results.

## Features

* View running Docker containers
* View all Docker containers
* View Docker images present on the host
* Scan Docker images for dependency/OS vulnerabilities using Trivy
* Return vulnerability details through REST APIs
* Store scan results in PostgreSQL
* Maintain persistent scan history
* View previous scan summaries
* View detailed results of previous scans
* Track vulnerability counts by severity
* Match Trivy findings against a `pom.xml`'s direct dependencies
* Scan project source code for real security issues using Semgrep (e.g. SQL injection), with exact file and line number
* Ask natural-language questions about scan results through an AI assistant (Ollama, runs locally, no API key or cost)
* PostgreSQL database integration
* Trivy, Postgres, Adminer, and Ollama all run as Docker services via Docker Compose

## Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Docker
* Docker Compose
* Trivy 0.74.0
* Semgrep
* Ollama (`qwen2.5-coder:7b`)
* Maven

## Project Structure

```text
containershield/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/containershield/
│   │   │       ├── configurations/
│   │   │       │   └── RestConfig.java
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── ChatController.java
│   │   │       │   ├── DockerController.java
│   │   │       │   ├── HealthController.java
│   │   │       │   ├── ScanHistoryController.java
│   │   │       │   ├── SemgrepController.java
│   │   │       │   └── TrivyController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── ChatRequest.java
│   │   │       │   ├── ChatResponse.java
│   │   │       │   ├── DockerImageDTO.java
│   │   │       │   ├── FixSuggestion.java
│   │   │       │   ├── ScanHistoryDetailsDTO.java
│   │   │       │   ├── ScanHistorySummary.java
│   │   │       │   ├── ScanVulnerabilityDTO.java
│   │   │       │   ├── SemgrepFinding.java
│   │   │       │   └── TrivyVulnerability.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── DockerContainerEntity.java
│   │   │       │   ├── ScanHistoryEntity.java
│   │   │       │   ├── ScanVulnerabilityEntity.java
│   │   │       │   ├── SourceCodeFinding.java
│   │   │       │   └── VulnerabilityFinding.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── ScanHistoryRepository.java
│   │   │       │   ├── SourceCodeFindingRepository.java
│   │   │       │   └── VulnerabilityFindingRepository.java
│   │   │       │
│   │   │       └── service/
│   │   │           ├── ChatService.java
│   │   │           ├── ContainerScanner.java
│   │   │           ├── DockerService.java
│   │   │           ├── ManifestMatcherService.java
│   │   │           ├── ScanHistoryService.java
│   │   │           ├── SemgrepService.java
│   │   │           └── TrivyService.java
│   │   │
│   │   └── resources/
│   │       └── application.yaml
│   │
│   └── test/
│
├── pom.xml
├── docker-compose.yml
├── .env.example
├── mvnw
├── mvnw.cmd
└── .gitignore
```

## Prerequisites

Install:

* Java 17 or later
* Docker
* Docker Compose
* Git

Postgres, Trivy, Adminer, and Ollama do not need to be installed separately — they all run through Docker Compose.

Check:

```bash
java -version
docker --version
docker compose version
```

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/chaitanya2850/ContainerShield.git
```

### 2. Go to the project

```bash
cd ContainerShield/containershield
```

### 3. Configure environment variables

```bash
cp .env.example .env
```

Adjust values in `.env` if desired (database name/user/password).

### 4. Start supporting services

```bash
docker compose up -d
```

This starts Postgres, Trivy, Adminer, and Ollama.

Check that everything is running:

```bash
docker ps
```

You should see:

```text
containershield-trivy
containershield-postgres
containershield-adminer
containershield-ollama
```

Test Trivy:

```bash
curl http://localhost:4954/healthz
```

Expected:

```text
ok
```

**First run only:** the Ollama container automatically downloads the `qwen2.5-coder:7b` model (~4.7GB) in the background — this can take several minutes. Check progress:

```bash
docker logs -f containershield-ollama
```

Confirm the model finished downloading:

```bash
docker exec -it containershield-ollama ollama list
```

You should see `qwen2.5-coder:7b` listed. On future restarts this step is skipped, since the model persists in a Docker volume.

### 5. Start Spring Boot

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## Test the APIs

### Health

```bash
curl http://localhost:8080/health-check
```

Returns:

```text
Ok
```

### Running Containers

```bash
curl http://localhost:8080/api/docker/containers/running
```

Returns a list of currently running containers on the host — ID, name, image, status.

### All Containers

```bash
curl http://localhost:8080/api/docker/containers/all
```

Returns all containers on the host, running or stopped.

### Docker Images

```bash
curl http://localhost:8080/api/docker/images
```

Returns Docker images present on the host — ID, repository, tag, size, created.

### Scan Docker Image (Trivy)

```bash
curl "http://localhost:8080/api/trivy/scan?image=redis:latest"
```

Example with nginx:

```bash
curl "http://localhost:8080/api/trivy/scan?image=nginx:latest"
```

The scan returns vulnerability information such as:

* Vulnerability ID
* Package name
* Installed version
* Fixed version
* Severity
* Title
* Description

Every successful scan is also automatically stored in PostgreSQL, both as individual findings and as a scan history summary.

### Scan Image + Match Against pom.xml

```bash
curl -X POST "http://localhost:8080/api/trivy/scan-with-manifest?image=myapp:1.0" \
  -F "manifest=@/path/to/pom.xml"
```

Returns each Trivy finding alongside whether it matches a direct dependency in the given `pom.xml`, and the fixed version to bump to.

**Note:** only direct dependencies are matched — vulnerabilities in transitive dependencies (pulled in automatically by something like `spring-boot-starter-web`) are not matched by this endpoint yet.

### Scan History

Get all previous scan summaries:

```bash
curl http://localhost:8080/api/scans
```

The response contains information such as:

* Scan ID
* Image name
* Total vulnerabilities
* Critical vulnerabilities
* High vulnerabilities
* Medium vulnerabilities
* Low vulnerabilities
* Unknown vulnerabilities
* Scan time

Example:

```json
[
  {
    "id": 1,
    "imageName": "redis:latest",
    "totalVulnerabilities": 195,
    "critical": 3,
    "high": 55,
    "medium": 69,
    "low": 67,
    "unknown": 1,
    "scanTime": "2026-09-17T11:53:03"
  }
]
```

### Get Scan Details

To view the complete details of a previous scan:

```bash
curl http://localhost:8080/api/scans/1
```

This returns the scan information along with the individual vulnerabilities found during that scan.

### Scan Source Code (Semgrep)

```bash
curl "http://localhost:8080/api/semgrep/scan?path=/path/to/your/project&projectName=myapp:1.0"
```

Runs Semgrep against the given local project folder (Java, using the `p/java` ruleset) and returns:

* Check ID (which rule matched)
* File path
* Start/end line number
* Severity
* Message (plain-English explanation)
* Vulnerability class (e.g. "SQL Injection")

Every finding is also stored in PostgreSQL.

### Ask the AI Assistant

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "projectOrImageName": "myapp:1.0",
    "question": "What are the most critical issues here and how do I fix them?"
  }'
```

Fetches every Trivy finding and every Semgrep finding stored under the given name, and returns a plain-English answer from a locally-run LLM — including prioritization and concrete fixes (exact `pom.xml` version bumps, corrected code snippets, etc.).

**Important:** `projectOrImageName` must exactly match the name used in both the Trivy scan and the Semgrep scan (including any version tag), or the assistant will have nothing to answer from.

**Performance note:** response time depends on whether Ollama is running on CPU or GPU. On CPU-only hardware, a response can take one to several minutes for a large scan.

## Scan History Flow

```text
Docker Image
     │
     ▼
Trivy Scan
     │
     ▼
Vulnerability Results
     │
     ├──────────────► REST API Response
     │
     ├──────────────► VulnerabilityFinding (used by AI Assistant)
     │
     ▼
ScanHistoryService
     │
     ▼
PostgreSQL
     │
     ▼
Scan History (summaries + details)
```

## Stopping the Application

Stop Spring Boot with:

```text
Ctrl + C
```

Stop supporting services:

```bash
docker compose down
```

**Note:** `docker compose down -v` additionally removes all volumes — this wipes the database, Trivy's cache, and the downloaded Ollama model. Only use `-v` if you intend to reset everything from scratch.

## Development

Build:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test
```

## Design Notes

* ContainerShield is intended to be deployed either on a company's own private infrastructure or run locally by an individual developer — it is not designed as a public multi-tenant service, since that would mean running untrusted Docker commands against images/code it doesn't control.
* The AI assistant runs entirely locally via Ollama — no cloud API key, no per-query cost, and scan data never leaves the machine/network it runs on.
* All scan data is passed directly into the assistant's prompt rather than using a vector database, since scan sizes (a handful to a few hundred findings) don't require retrieval search to stay within context limits.

## Future Work

* Trace transitive dependencies (via `mvn dependency:tree`) so vulnerabilities pulled in indirectly can also be matched to a fix
* Support additional manifest formats (`package.json`, `build.gradle`, `requirements.txt`)
* Web frontend for triggering scans and chatting with the assistant
* Containerize the Spring Boot backend itself once the API surface stabilizes
* Optional GPU passthrough configuration for Ollama

## Author

Chaitanya Patil

GitHub: https://github.com/chaitanya2850
