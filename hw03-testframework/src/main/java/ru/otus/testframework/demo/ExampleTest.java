package ru.otus.testframework.demo;

import ru.otus.testframework.annotations.After;
import ru.otus.testframework.annotations.Before;
import ru.otus.testframework.annotations.Test;

public class ExampleTest {

    private int counter;

    @Before
    public void setUp() {
        counter = 1;
        System.out.println("  @Before, instance=" + System.identityHashCode(this) + ", counter=" + counter);
    }

    @Test
    public void shouldPassAddition() {
        int result = counter + 1;
        if (result != 2) {
            throw new AssertionError("expected 2 but was " + result);
        }
        System.out.println("  @Test shouldPassAddition");
    }

    @Test
    public void shouldFailIntentionally() {
        System.out.println("  @Test shouldFailIntentionally");
        throw new AssertionError("intentional failure");
    }

    @Test
    public void shouldPassMultiplication() {
        int result = counter * 5;
        if (result != 5) {
            throw new AssertionError("expected 5 but was " + result);
        }
        System.out.println("  @Test shouldPassMultiplication");
    }

    @After
    public void tearDown() {
        System.out.println("  @After, instance=" + System.identityHashCode(this) + ", counter=" + counter);
        counter = 0;
    }
}
