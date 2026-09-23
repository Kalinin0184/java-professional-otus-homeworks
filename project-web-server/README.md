# project-web-server

Проектная работа OTUS Java Professional: **свой HTTP/1.1 сервер** с разбором протокола (не Jetty / Spring).

## Цель

Показать, как устроен web-сервер «изнутри»: TCP → разбор HTTP → маршрутизация → ответ, плюс конкурентная обработка соединений.

## Что реализовано

| Область | Детали |
|---|---|
| Транспорт | blocking `ServerSocket` + fixed thread pool |
| Протокол | request-line, headers (case-insensitive), body по `Content-Length` |
| Keep-Alive | HTTP/1.1 по умолчанию; `Connection` / `Keep-Alive` в ответе; лимит запросов на соединение |
| Таймауты | `SO_TIMEOUT` на сокете (idle timeout), закрытие по таймауту |
| Статика | `www/`, MIME, защита от path traversal |
| API | `GET /api/hello`, `POST /api/echo` |
| Ошибки | `400`, `404`, `405`, `500` |
| Наблюдаемость | access-log: method, path, status, latency, keep-alive/close |
| Shutdown | graceful stop acceptor + worker pool |

## Архитектура

```
Client --TCP--> Acceptor --Socket--> WorkerPool
                                      |
                                      v
                               ConnectionHandler (request loop)
                                      |
                      HttpRequestParser -> Router -> Handler
                                      |
                               HttpResponseWriter --> Client
```

Компоненты:
- `HttpRequestParser` / `HttpResponseWriter` — границы протокола;
- `Router` — mapping `METHOD + path → Handler`, fallback на статику;
- `ConnectionHandler` — keep-alive loop, таймауты, access-log;
- `HttpServer` — accept loop, пул, shutdown.

## Запуск

```bash
./gradlew :project-web-server:run
# http://localhost:8080
```

Порт:

```bash
./gradlew :project-web-server:run --args="9090"
```

## Тесты

```bash
./gradlew :project-web-server:test
```

Покрыто:
- парсер (валидный / битый request, body);
- роутер и keep-alive-политика;
- интеграция: UI + JSON API;
- keep-alive: два HTTP-запроса на одном TCP;
- load smoke: 16×25 concurrent `GET /api/hello` (все 200, RPS в логе теста).

## Для защиты (о чём говорить)

1. Где читается request-line и заголовки (`HttpRequestParser`).
2. Почему нужен thread pool (один accept-поток не блокируется на I/O клиента).
3. Как решается Keep-Alive (цикл в `ConnectionHandler`, `SO_TIMEOUT`, `maxRequestsPerConnection`).
4. Ограничения MVP: нет chunked, HTTPS, NIO Selector, pipelining.

## Вне текущего объёма

Chunked transfer encoding, HTTPS/TLS, NIO/`Selector` (как Netty), filters/session, полноценный Servlet API.
