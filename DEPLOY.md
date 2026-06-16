# Deployment (Docker)

This project includes a `Dockerfile` and `docker-compose.yml` to run the application and a MySQL database locally.

Quick steps (Docker & Docker Compose must be installed):

1. Build and run using Docker Compose:
```bash
docker compose up --build
```

2. The backend will be available at `http://localhost:8080/`.

Configuration notes:
- `docker-compose.yml` creates a MySQL service (`db`) and `app` service. It injects environment variables into the Spring Boot app using relaxed binding (e.g. `JWT_SECRET` -> `jwt.secret`).
- Replace the `JWT_SECRET` value with a strong secret before deploying to production. Use a secrets manager in production, not plain env vars.
- The MySQL data is persisted in a Docker volume named `db_data`.

Production tips:
- Do not use `spring.jpa.hibernate.ddl-auto=update` in production. Use migrations (Flyway/Liquibase).
- Use an external managed database for production and secure credentials.
- Use HTTPS (TLS termination) in front of the app.
