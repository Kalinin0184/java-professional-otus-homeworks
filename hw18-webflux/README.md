# hw18-webflux — Комната 1408

Реактивный чат (WebFlux + WebSocket/STOMP + R2DBC) со специальной комнатой `1408`:

- в комнату `1408` нельзя отправлять сообщения;
- все сообщения из остальных комнат дублируются в `1408`;
- при входе в `1408` подгружается история сообщений из всех комнат.

## Запуск

```bash
docker compose -f hw18-webflux/docker-compose.yml up -d
./gradlew :hw18-webflux:datastore-service:bootRun
./gradlew :hw18-webflux:client-service:bootRun
```

Открыть http://localhost:8080 — указать номер комнаты (например `1` или `1408`) и подключиться.
