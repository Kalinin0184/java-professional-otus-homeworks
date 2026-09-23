# project-web-server

Проектная работа OTUS: **свой HTTP/1.1 сервер** с разбором протокола (не Jetty).

## Что реализовано

- blocking `ServerSocket` + thread pool;
- парсер request-line / headers / body (`Content-Length`);
- формирование HTTP-ответа;
- раздача статики из `www/` (с защитой от path traversal);
- роутинг handlers:
  - `GET /api/hello` → JSON;
  - `POST /api/echo` → эхо тела в JSON;
- коды ошибок `400`, `404`, `405`, `500`;
- graceful shutdown.

## Запуск

```bash
./gradlew :project-web-server:run
# http://localhost:8080
```

Порт можно передать аргументом:

```bash
./gradlew :project-web-server:run --args="9090"
```

## Тесты

```bash
./gradlew :project-web-server:test
```

## Архитектура

`Acceptor` принимает TCP-соединение → worker из пула → `HttpRequestParser` → `Router` → handler (`StaticFileHandler` / API) → `HttpResponseWriter`.

## Вне MVP (следующие шаги)

Keep-Alive, chunked encoding, HTTPS, NIO Selector, filters/session.
