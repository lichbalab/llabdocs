
# LLab Docs Application

## Overview
LLab Docs Application is an application designed to handle document signing and signature validation. 

## Prerequisites
- Java 11 or higher
- Maven 3.6.0 or higher

## Project Structure
LLab Docs Application is a Spring Boot application designed to handle document signing and signature validation. It leverages the `cmc-spring-sdk` for integration with CMC services.

## Prerequisites
- Java 11 or higher
- Maven 3.6.0 or higher

## Setup

### 1. Clone the Repository
git clone https://github.com/lichbalab/llab-docs.git
cd llab-docs

### 2. Build the Project

```bash
mvn clean package
```

### 3. Run the Application

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081 --cmc.sdk.base-url=http://localhost:8080"
```

### 4. Build Docker Image
If you prefer Docker, you can build an image using:
```bash
docker build -t lichbalab:llabdocs-2024.1 .
```
This command builds a Docker image tagged `lichbalab:llabdocs-2024.1`.

### 5. Run Docker Image
Run the Docker image with:
```bash
docker-compose up -d
```
This starts the Docker container in detached mode.

### 6. Deploy the Application in K8s
Run the Docker image with:
```bash
kubectl apply -f kubernetes-deployment.yaml 
```

### 7. Scale Application
Run the Docker image with:
```bash
kubectl scale deployment llabdocs-deployment --replicas=5 
```
