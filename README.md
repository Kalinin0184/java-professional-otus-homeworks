# otus-java-professional

Домашние работы по курсу [OTUS Java Professional](https://otus.ru/lessons/java-professional/).

## hw01-gradle

Многомодульный Gradle-проект, зависимость Guava, исполняемый «толстый» jar.

Сборка и запуск:

```bash
./gradlew :hw01-gradle:build
java -jar hw01-gradle/build/libs/hw01-gradle-1.0.jar
```

## hw02-collections

Применение коллекций и обобщений: swap элементов массива, преобразование в `ArrayList`, уникальные слова и частота.

```bash
./gradlew :hw02-collections:run
# или
./gradlew :hw02-collections:build
java -jar hw02-collections/build/libs/hw02-collections-1.0.jar
```

## hw03-testframework

Свой тестовый фреймворк на reflection: `@Before`, `@Test`, `@After`.

```bash
./gradlew :hw03-testframework:build
java -jar hw03-testframework/build/libs/hw03-testframework-1.0.jar
# или с явным именем класса:
java -jar hw03-testframework/build/libs/hw03-testframework-1.0.jar ru.otus.testframework.demo.ExampleTest
```

## hw04-gc

Определение нужного размера хипа и влияние GC. Результаты замеров — в [hw04-gc/RESULTS.md](hw04-gc/RESULTS.md).

```bash
./gradlew :hw04-gc:build
java -Xms256m -Xmx256m -XX:+UseG1GC -jar hw04-gc/build/libs/hw04-gc-1.0.jar
```

## hw05-aop

Автоматическое логирование параметров через Dynamic Proxy и аннотацию `@Log`.

```bash
./gradlew :hw05-aop:build
java -jar hw05-aop/build/libs/hw05-aop-1.0.jar
```

## hw06-atm

Эмулятор банкомата с акцентом на SOLID. Описание — в [hw06-atm/README.md](hw06-atm/README.md).

```bash
./gradlew :hw06-atm:build
java -jar hw06-atm/build/libs/hw06-atm-1.0.jar
```

## hw07-patterns

Обработчик сообщений (паттерны): `field11-13`, swap-процессор, исключение на чётной секунде, `HistoryListener` (memento).

```bash
./gradlew :hw07-patterns:test
./gradlew :hw07-patterns:run
```

## hw08-json

Обработчик JSON: чтение measurements, агрегация по name, запись результата в файл.

```bash
./gradlew :hw08-json:test
```
