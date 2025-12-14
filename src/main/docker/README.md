This folder contains docker-compose files for running services used by the application.

Postgres (development)
----------------------
This `docker-compose.yml` starts a local Postgres instance configured to match `src/main/resources/application.yaml`:

- DB name: `jobmanager_db`
- Username: `postgres`
- Password: `your_password`
- Port: `5432` (mapped to host)

Bring up Postgres:

```bash
# from project root
cd src/main/docker
docker compose up -d
```

Check Postgres container status and logs:

```bash
docker compose ps
docker compose logs -f postgres
```

Healthcheck: the compose file includes a healthcheck using `pg_isready`. Wait for the container to be healthy before running the Spring Boot app.

Notes:
- If your local machine already has Postgres running on 5432, change the host port mapping `"5432:5432"` in the compose file to another free port (e.g. `"5433:5432"`) and update `spring.datasource.url` in `application.yaml` accordingly.
- Don't use these credentials in production.

