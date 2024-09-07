
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