# hw12-webserver

Веб-сервер (Jetty) поверх Hibernate ORM: логин администратора и страница клиентов.

## Запуск

```bash
# Postgres на localhost:5430 (usr/pwd/demoDB)
docker compose -f hw12-webserver/docker-compose.yml up -d

./gradlew :hw12-webserver:run
```

Откройте http://localhost:8080

- логин: `admin` / `admin` (также `user7` / `11111`)
- после входа: создание клиента и список на `/clients`

## Что сделано

- стартовая страница + `/login` с сессионной аутентификацией (`AuthorizationFilter`)
- админская страница `/clients`: создать клиента, получить список
- данные клиентов в PostgreSQL через `DBServiceClient` (Hibernate)
