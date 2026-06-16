# Task Manager

Professional Task Manager — a full-stack Spring Boot application for managing user tasks with JWT authentication, role-based access, and a simple web UI.

## Short Description

Task Manager is a lightweight project to create, update, and track tasks. It provides REST APIs secured with JWT tokens and a minimal static frontend under `src/main/resources/static`.

## Features

- User registration and authentication (JWT)
- Create, read, update, delete (CRUD) tasks
- Task fields: title, description, priority, status, due date
- Simple static frontend for demonstration
- Docker and Maven support

## Tech Stack

- Java 17+ and Spring Boot
- Spring Security with JWT
- JPA (configure your preferred database in `application.properties`)
- Maven for build and dependency management
- Optional: Docker / Docker Compose

## Prerequisites

- Java 17 or later
- Maven 3.6+
- Docker (optional, to run with containers)

## Getting Started

1. Clone the repository (already pushed to GitHub):

```bash
git clone https://github.com/Siddhant8055/TaskManager.git
cd "TaskManager"
```

2. Configure the database and JWT settings in `src/main/resources/application.properties` or `application-dev.properties`.

3. Build and run with Maven:

```bash
mvn clean package
java -jar target/taskmanager-0.0.1-SNAPSHOT.jar
```

Or run in development mode:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

4. (Optional) Use Docker Compose if you prefer containers:

```bash
docker-compose up --build
```

## API Overview

The application exposes REST endpoints under `/api`.

- Authentication:
  - `POST /api/auth/register` — register a new user (send `username`, `password`, etc.)
  - `POST /api/auth/login` — authenticate and receive a JWT token

- Tasks (require Authorization header `Bearer <token>`):
  - `GET /api/tasks` — list tasks
  - `GET /api/tasks/{id}` — get task by id
  - `POST /api/tasks` — create task
  - `PUT /api/tasks/{id}` — update task
  - `DELETE /api/tasks/{id}` — delete task

Refer to the controllers in `src/main/java/com/example/taskmanager/controller` for exact request/response DTOs.

## Configuration

- Environment and application properties are in `src/main/resources`.
- Provide database URL, username, password and `jwt.secret` in the properties file or via environment variables.

## Development Notes

- Frontend static files are in `src/main/resources/static`.
- Services are in `src/main/java/com/example/taskmanager/service`.
- Repositories are in `src/main/java/com/example/taskmanager/repository`.

## Contributing

Feel free to open issues or submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

## License

This project does not include a license file. Add one if you intend to publish under an open-source license.

---

Project description: Task Manager is a secure, simple-to-deploy task tracking backend with a minimal static frontend for demos and testing. It is designed as a learning/example project to show authentication, authorization, persistence, and a complete deployment path using Docker or a standalone JAR.
# Task Manager - Java Full Stack (Spring Boot)

This project is a learning-oriented Task Management application built with Java, Spring Boot, MySQL (production) and an H2 in-memory DB (development), with a Bootstrap frontend.

Quick start (development, uses in-memory H2 DB):

Windows PowerShell:
```powershell
$env:SPRING_PROFILES_ACTIVE='dev'
mvn spring-boot:run
```

Then open http://localhost:8080

To run against MySQL (production-like):

1. Ensure MySQL is running and create the DB/user, for example:

```sql
CREATE DATABASE task_manager_db;
CREATE USER 'task_user'@'localhost' IDENTIFIED BY 'task_password';
GRANT ALL PRIVILEGES ON task_manager_db.* TO 'task_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Start the app with the `prod` profile or set environment variables. Example (PowerShell):

```powershell
$env:SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/task_manager_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:SPRING_DATASOURCE_USERNAME='task_user'
$env:SPRING_DATASOURCE_PASSWORD='task_password'
$env:SPRING_PROFILES_ACTIVE='prod'
mvn spring-boot:run
```

Notes:
- The project defaults to `dev` profile when `SPRING_PROFILES_ACTIVE` is not set, so it will start with H2 locally to avoid MySQL connection errors.
- Keep secrets out of source control; use environment variables or a secrets manager in production.

If you'd like, I can add a Docker Compose file to start MySQL and the app together for a reproducible dev environment.
