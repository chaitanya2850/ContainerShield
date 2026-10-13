<div align="center">

### ContainerShield : Docker Security Analysis Platform

**Scan. Understand. Investigate.**

A self-hosted security-analysis backend built with **Java 17 and Spring Boot** that brings together Docker inspection, image vulnerability scanning, Java source-code analysis, persistent scan history, and locally hosted AI-assisted investigation.

[Features](#-features) · [Architecture](#️-architecture) · [Quick Start](#-quick-start) · [API Reference](#-api-reference) · [Security](#-security-considerations)

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-Backend-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Trivy](https://img.shields.io/badge/Trivy-Image_Scanning-1904DA)
![Semgrep](https://img.shields.io/badge/Semgrep-Source_Analysis-42B883)
![Ollama](https://img.shields.io/badge/Ollama-Local_AI-black)

</div>

---

## 📌 Overview

ContainerShield is a backend platform for inspecting Docker resources and bringing security findings from multiple tools into one workflow.

It uses **Trivy** to scan container images for known vulnerabilities and **Semgrep** to analyze Java source code for security issues. Scan findings can be stored in **PostgreSQL**, reviewed through REST APIs, and used as context for an AI assistant powered by a locally hosted **Ollama** model.

The goal is not to display raw scanner output alone. ContainerShield is designed to help developers explore findings, understand their severity, and investigate possible remediation steps.

> **Project status:** Actively developed academic project. The current implementation focuses on backend APIs and security-analysis workflows; a web dashboard is future work.

## 🎯 Features

### Docker Inspection
- List running containers.
- List all containers, including stopped containers.
- List Docker images available on the connected Docker host.

### Container Image Vulnerability Scanning
- Scan Docker images using Trivy.
- Return vulnerability IDs, affected packages, installed versions, fixed versions, severity, titles, and descriptions.
- Persist scan summaries and individual findings in PostgreSQL.
- Revisit previous scans through the scan-history API.

### Maven Dependency Matching
- Compare Trivy findings with direct dependencies declared in a supplied `pom.xml`.
- Return matching dependency information and available fixed-version guidance where applicable.

**Current limitation:** The manifest matcher handles direct Maven dependencies only. It does not yet resolve vulnerabilities introduced through transitive dependencies.

### Java Source-Code Analysis
- Run Semgrep with the configured Java ruleset.
- Return rule IDs, file paths, line numbers, severity, messages, and vulnerability categories.
- Persist source-code findings in PostgreSQL.

### Persistent Scan History
- Store scan summaries and vulnerability findings.
- Retrieve previous scan summaries.
- Retrieve the detailed findings associated with a scan.
- Review vulnerability counts grouped by severity.

### Local AI-Assisted Investigation
- Ask natural-language questions about stored Trivy and Semgrep findings.
- Use Ollama with `qwen2.5-coder:7b` to generate explanations and remediation suggestions.
- Run the model locally without a hosted AI API key or per-request API charges.

AI-generated advice is a starting point for investigation, not a guarantee that a suggested change is correct or safe.

## Architecture

The following is a **3D isometric overview illustration** of ContainerShield's main components. It is a visual overview rather than a deployment diagram; use the sections below for the actual setup and service details.

<div align="center">
  <img src="https://github.com/chaitanya2850/ContainerShield/blob/main/containershield-architecture.png" alt="3D isometric illustration of ContainerShield connected to Docker, Trivy, Semgrep, PostgreSQL, Trivy cache, Ollama, and Maven dependency matching" width="100%">
</div>

### Component responsibilities

| Component | Responsibility |
|---|---|
| Spring Boot REST API | Receives requests and coordinates Docker inspection, scans, history, and AI queries |
| Docker integration | Reads container and image information from the connected Docker host |
| Trivy | Scans container images for known vulnerabilities |
| Semgrep | Analyzes Java source code using the configured ruleset |
| Manifest matcher | Matches image findings against direct dependencies in `pom.xml` |
| PostgreSQL | Stores scan history and findings |
| Trivy cache | Persists Trivy's cache between container restarts |
| Ollama | Runs the local language model used for AI-assisted investigation |
| Adminer | Optional browser-based interface for inspecting the PostgreSQL database during development |

### Analysis workflows

**Image scan**

1. Submit an image name to the Trivy endpoint.
2. Spring Boot invokes the configured Trivy service.
3. The API returns the vulnerability findings.
4. Scan summaries and findings are stored in PostgreSQL for later review.

**Source-code scan**

1. Submit a project directory to the Semgrep endpoint.
2. Semgrep analyzes the supplied Java project using the configured `p/java` ruleset.
3. Findings include rule information and source locations.
4. Findings are persisted and can be used as context by the AI assistant.

**AI-assisted investigation**

1. Submit a question and the matching project or image name.
2. ContainerShield retrieves stored findings associated with that name.
3. The backend sends the findings and question to the local Ollama model.
4. The API returns the model's explanation and suggested next steps.

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 17 |
| Backend framework | Spring Boot |
| Persistence | Spring Data JPA |
| Database | PostgreSQL 17 |
| Image vulnerability scanning | Trivy 0.74.0 |
| Source-code analysis | Semgrep |
| Local language model | Ollama — `qwen2.5-coder:7b` |
| Containers and services | Docker, Docker Compose |
| Database development UI | Adminer |
| Build tool | Maven |

## 🚀 Quick Start

### Prerequisites

Install the following on your development machine:

- Git
- Java 17 or later
- Docker Engine
- Docker Compose plugin

PostgreSQL, Trivy, Adminer, and Ollama are defined as supporting Docker Compose services. You do not need to install them separately.

Check your installation:

```bash
java -version
docker --version
docker compose version
```

### 1. Clone the repository

```bash
git clone https://github.com/chaitanya2850/ContainerShield.git
cd ContainerShield/containershield
```

### 2. Configure environment variables

Create your local environment file from the example:

```bash
cp .env.example .env
```

Review `.env` and configure the PostgreSQL database name, username, and password. Keep the `.env` file private; do not commit real credentials.

### 3. Start the supporting services

```bash
docker compose up -d
```

This starts the services defined in `docker-compose.yml`:

- PostgreSQL
- Trivy server
- Adminer
- Ollama

Check the container status:

```bash
docker compose ps
```

Follow service logs when troubleshooting:

```bash
docker compose logs -f
```

### 4. Check the Trivy service

```bash
curl http://localhost:4954/healthz
```

The expected healthy response is:

```text
ok
```

### 5. Wait for the Ollama model

On the first startup, the Ollama service pulls `qwen2.5-coder:7b`. The model download is several gigabytes and may take time depending on your connection.

Check the logs:

```bash
docker logs -f containershield-ollama
```

Verify that the model is available:

```bash
docker exec -it containershield-ollama ollama list
```

The model data is stored in a Docker volume so it can persist across ordinary container restarts.

### 6. Start the Spring Boot backend

In the `containershield` directory:

```bash
./mvnw spring-boot:run
```

On Windows, use:

```bat
mvnw.cmd spring-boot:run
```

The API is available at:

```text
http://localhost:8080
```

> The current Compose configuration starts the supporting services; the Spring Boot application is started separately with the Maven wrapper.

## API Reference

All endpoints below use the local base URL `http://localhost:8080`.

### Health check

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/health-check` | Checks whether the application responds |

```bash
curl http://localhost:8080/health-check
```

### Docker inspection

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/docker/containers/running` | Lists running containers |
| `GET` | `/api/docker/containers/all` | Lists all containers |
| `GET` | `/api/docker/images` | Lists local Docker images |

Examples:

```bash
curl http://localhost:8080/api/docker/containers/running
curl http://localhost:8080/api/docker/containers/all
curl http://localhost:8080/api/docker/images
```

These endpoints inspect the Docker host accessible to the backend. They do not automatically list containers on other machines.

### Scan a Docker image

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/trivy/scan?image=<image>` | Scans an image using Trivy |

Example:

```bash
curl "http://localhost:8080/api/trivy/scan?image=nginx:latest"
```

A response can include vulnerability IDs, package names, installed and fixed versions, severity, titles, and descriptions. Successful scans are also stored for scan-history queries.

### Scan an image and match a Maven manifest

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/trivy/scan-with-manifest?image=<image>` | Scans an image and compares findings against a supplied `pom.xml` |

Example:

```bash
curl -X POST \
  "http://localhost:8080/api/trivy/scan-with-manifest?image=myapp:1.0" \
  -F "manifest=@/absolute/path/to/pom.xml"
```

Use a real image name and an absolute path to the manifest. The current matcher supports direct Maven dependencies only.

### Scan Java source code

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/semgrep/scan?path=<path>&projectName=<name>` | Runs Semgrep against a project directory |

Example:

```bash
curl --get "http://localhost:8080/api/semgrep/scan" \
  --data-urlencode "path=/absolute/path/to/your/project" \
  --data-urlencode "projectName=myapp:1.0"
```

The supplied path must be accessible to the machine or container where the backend and Semgrep execution run. Findings can include the matched rule, file path, line numbers, severity, message, and vulnerability category.

### Scan history

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/scans` | Lists previous scan summaries |
| `GET` | `/api/scans/{id}` | Returns details for a scan |

Examples:

```bash
curl http://localhost:8080/api/scans
curl http://localhost:8080/api/scans/1
```

A scan summary includes its ID, image name, total vulnerability count, severity counts, and scan time. The detail endpoint returns the corresponding scan data and individual findings.

### Ask the AI assistant

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/chat` | Answers a question using stored findings and the local Ollama model |

Example:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "projectOrImageName": "myapp:1.0",
    "question": "What are the most critical issues and how should I investigate them?"
  }'
```

**Important:** `projectOrImageName` must match the name associated with the stored scan findings. If no matching findings are found, the assistant may not have relevant context.

AI response time depends on model availability, hardware, question length, and the number of findings supplied as context. CPU-only inference can be slow.

## 🗂️ Project Structure

```text
ContainerShield/
├── containershield/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/containershield/
│   │   │   │   ├── configurations/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       └── application.yaml
│   │   └── test/
│   ├── pom.xml
│   ├── docker-compose.yml
│   ├── .env.example
│   ├── mvnw
│   └── mvnw.cmd
├── docs/
│   └── images/
│       └── containershield-architecture.png
└── README.md
```

The Java packages are organized around REST controllers, data-transfer objects, persistence entities, repositories, and services. The exact files may evolve as the project develops.

## 🛠️ Development

Build the project:

```bash
./mvnw clean package
```

Run the tests:

```bash
./mvnw test
```

Start the supporting services:

```bash
docker compose up -d
```

Stop the supporting services:

```bash
docker compose down
```

The ordinary `docker compose down` command preserves named volumes. To remove the database, Trivy cache, and downloaded Ollama model as well, use `docker compose down -v` **only when you intentionally want to delete that persisted data**.

## 🔐 Security Considerations

ContainerShield interacts with Docker, scans supplied image names, and runs source-code analysis against supplied paths. These capabilities should be treated as privileged operations.

- **Use trusted environments.** The project is intended for local development or private, controlled infrastructure—not an unauthenticated public multi-tenant service.
- **Protect Docker access.** Access to the Docker daemon can provide powerful control over the host. Do not expose the Docker API publicly.
- **Protect database and model services.** Do not expose PostgreSQL, Adminer, Ollama, or internal scanner services to untrusted networks.
- **Restrict scan paths and image inputs.** Only allow trusted users to trigger scans. Validate and constrain user-supplied paths and inputs before using the application in a shared environment.
- **Keep secrets out of Git.** Use `.env` for local configuration, ensure real credentials are not committed, and rotate any credentials that may have been exposed.
- **Review AI suggestions.** Validate proposed code or dependency changes and run tests before applying them.

The included Compose file publishes several service ports on the host for local development. Review those port mappings and bind services to loopback or a protected network when external access is not required.

## Current Limitations

- The current project is backend/API-focused; a web dashboard is not included in the documented implementation.
- Maven manifest matching currently covers direct dependencies, not transitive dependency resolution.
- Semgrep scanning depends on the configured Java ruleset and the path being accessible to the backend environment.
- AI responses depend on the findings stored under the requested project or image name and may take time on CPU-only hardware.
- AI-generated explanations and fixes are suggestions and require human review.

## Roadmap

Potential next steps:

- [ ] Resolve transitive Maven dependencies when matching vulnerabilities to fixes.
- [ ] Add support for other dependency manifests, such as `build.gradle`, `package.json`, and `requirements.txt`.
- [ ] Build a web interface for triggering scans, viewing findings, and chatting with the assistant.
- [ ] Containerize the Spring Boot backend for a more integrated Compose deployment.
- [ ] Add configurable GPU support for Ollama.
- [ ] Improve automated tests and document additional failure/recovery scenarios.

## 🤝 Contributing

Suggestions, bug reports, and improvements are welcome.

1. Fork the repository.
2. Create a branch for your change.
3. Make the change and run the relevant tests.
4. Open a pull request describing the change and how it was verified.

For substantial changes, consider opening an issue first to discuss the proposed approach.

## License

> A license is not currently documented for this project.
It will be documented soon !!

## Author

**Chaitanya Patil**

- GitHub: [@chaitanya2850](https://github.com/chaitanya2850)
- Project: [ContainerShield](https://github.com/chaitanya2850/ContainerShield)

---

<div align="center">

**ContainerShield — Build safer containers with clearer security insights.**

</div>
