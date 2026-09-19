# hw13-di

Собственный IoC-контейнер по аннотациям `@AppComponentsContainerConfig` / `@AppComponent`.

## Запуск

```bash
./gradlew :hw13-di:test
./gradlew :hw13-di:run
```

## Варианты контейнера

1. `new AppComponentsContainerImpl(AppConfig.class)` — обязательный
2. `new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class)` — несколько конфигураций
3. `new AppComponentsContainerImpl("ru.otus.config.modular")` — сканирование пакета (Reflections)
