# hw11-cache

Свой cache engine: `MyCache` на `WeakHashMap`, слушатели `HwListener`, кэш в `DbServiceClientImpl`.

## Запуск

```bash
docker compose -f hw11-cache/docker-compose.yml up -d
./gradlew :hw11-cache:run
```

Демо сравнивает время чтения из БД и из кэша, затем провоцирует GC и показывает сброс `WeakHashMap`.

## Тесты

```bash
./gradlew :hw11-cache:test --tests ru.otus.cachehw.MyCacheTest
# полный набор (нужен Docker для Testcontainers):
./gradlew :hw11-cache:test
```
