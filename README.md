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
