package ru.otus.testframework;

public class Main {

    public static void main(String[] args) {
        String className = args.length > 0
                ? args[0]
                : "ru.otus.testframework.demo.ExampleTest";
        TestRunner.run(className);
    }
}
