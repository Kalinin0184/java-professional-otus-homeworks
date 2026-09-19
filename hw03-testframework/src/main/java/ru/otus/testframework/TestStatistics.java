package ru.otus.testframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class TestStatistics {

    private final List<TestResult> results = new ArrayList<>();

    void add(TestResult result) {
        results.add(result);
    }

    int total() {
        return results.size();
    }

    int passed() {
        return (int) results.stream().filter(TestResult::passed).count();
    }

    int failed() {
        return total() - passed();
    }

    List<TestResult> results() {
        return Collections.unmodifiableList(results);
    }
}
