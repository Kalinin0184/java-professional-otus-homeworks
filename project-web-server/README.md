# project-web-server

Свой простой HTTP-сервер на сокетах (без Jetty/Spring). Раздаёт статику и пару JSON-эндпоинтов.

## Запуск

```bash
./gradlew :project-web-server:run
```

Открыть http://localhost:8080

Порт можно указать аргументом:

```bash
./gradlew :project-web-server:run --args="9090"
```

## Тесты

```bash
./gradlew :project-web-server:test
```

## Что есть

- разбор HTTP-запроса и сборка ответа
- пул потоков для клиентов
- keep-alive и таймаут сокета
- статика из `www/`
- `GET /api/hello`, `POST /api/echo`
