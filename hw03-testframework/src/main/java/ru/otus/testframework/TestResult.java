package ru.otus.testframework;

record TestResult(String testName, boolean passed, Throwable error) {

    static TestResult success(String testName) {
        return new TestResult(testName, true, null);
    }

    static TestResult failure(String testName, Throwable error) {
        return new TestResult(testName, false, error);
    }
}
