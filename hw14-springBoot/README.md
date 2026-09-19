# hw14-springBoot

CRUD клиентов на Spring Boot: Spring Data JDBC + Thymeleaf (без авторизации).

## Запуск

```bash
docker compose -f hw14-springBoot/docker-compose.yml up -d
# или локальный Postgres на :5430 с БД hw14db, usr/pwd

./gradlew :hw14-springBoot:bootRun
```

Откройте http://localhost:8080 — создание клиента и список.
