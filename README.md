# ContainerShield

ContainerShield is a Spring Boot backend for Docker container monitoring and Docker image vulnerability scanning using Trivy.

## Features

* View running Docker containers
* View all Docker containers
* Scan Docker images for vulnerabilities
* Return vulnerability details through REST APIs
* PostgreSQL database integration
* Trivy runs as a Docker service using Docker Compose

## Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Docker
* Docker Compose
* Trivy 0.74.0
* Maven

## Project Structure

```text
containershield/
├── src/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
└── .gitignore
```

## Prerequisites

Install:

* Java 17 or later
* Docker
* PostgreSQL
* Git

Trivy does **not** need to be installed separately. It runs through Docker Compose.

Check:

```bash
java -version
docker --version
psql --version
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

### 3. Start Trivy

```bash
docker compose up -d
```

Check that Trivy is running:

```bash
docker ps
```

You should see:

```text
containershield-trivy
```

Test Trivy:

```bash
curl http://localhost:4954/healthz
```

Expected:

```text
ok
```

### 4. Start PostgreSQL

Make sure PostgreSQL is running and create the database:

```sql
CREATE DATABASE containershield;
```

Default configuration:

```text
Database: containershield
Host: localhost
Port: 5432
Username: postgres
Password: postgres
```

If your PostgreSQL username or password is different, update:

```text
src/main/resources/application.yaml
```

### 5. Start Spring Boot

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## Test the APIs

### Health

```bash
curl http://localhost:8080/health
```

### Running Containers

```bash
curl http://localhost:8080/api/docker/containers/running
```

### All Containers

```bash
curl http://localhost:8080/api/docker/containers/all
```

### Scan Docker Image

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

## Stopping the Application

Stop Spring Boot with:

```text
Ctrl + C
```

Stop Trivy:

```bash
docker compose down
```

## Development

Build:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test
```

## Author

Chaitanya Patil

GitHub: https://github.com/chaitanya2850

