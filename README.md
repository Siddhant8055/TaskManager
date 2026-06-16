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
