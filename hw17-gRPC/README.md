# hw17-gRPC

gRPC «Убить босса»: сервер стримит числа каждые 2 секунды, клиент раз в секунду считает `currentValue`.

## Запуск

Терминал 1 — сервер:

```bash
./gradlew :hw17-gRPC:runServer
```

Терминал 2 — клиент:

```bash
./gradlew :hw17-gRPC:runClient
```

Клиент запрашивает последовательность `0..30`, цикл `0..50`, формула:
`currentValue = currentValue + lastServerValue + 1` (число от сервера учитывается один раз).
