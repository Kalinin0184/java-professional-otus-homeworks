package ru.otus.testframework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ru.otus.testframework.annotations.After;
import ru.otus.testframework.annotations.Before;
import ru.otus.testframework.annotations.Test;

public final class TestRunner {

    private TestRunner() {
    }

    public static void run(String className) {
        Class<?> testClass = loadClass(className);
        TestPlan plan = preparePlan(testClass);
        TestStatistics statistics = executePlan(plan);
        printStatistics(className, statistics);
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Test class not found: " + className, e);
        }
    }

    private static TestPlan preparePlan(Class<?> testClass) {
        Method[] declaredMethods = testClass.getDeclaredMethods();
        List<Method> beforeMethods = findAnnotatedMethods(declaredMethods, Before.class);
        List<Method> testMethods = findAnnotatedMethods(declaredMethods, Test.class);
        List<Method> afterMethods = findAnnotatedMethods(declaredMethods, After.class);
        return new TestPlan(testClass, beforeMethods, testMethods, afterMethods);
    }

    private static List<Method> findAnnotatedMethods(Method[] methods, Class<? extends java.lang.annotation.Annotation> annotation) {
        List<Method> annotated = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                method.setAccessible(true);
                annotated.add(method);
            }
        }
        annotated.sort(Comparator.comparing(Method::getName));
        return annotated;
    }

    private static TestStatistics executePlan(TestPlan plan) {
        TestStatistics statistics = new TestStatistics();
        for (Method testMethod : plan.testMethods()) {
            TestResult result = runSingleTest(plan, testMethod);
            statistics.add(result);
        }
        return statistics;
    }

    private static TestResult runSingleTest(TestPlan plan, Method testMethod) {
        Object testInstance;
        try {
            testInstance = createInstance(plan.testClass());
        } catch (Exception e) {
            return TestResult.failure(testMethod.getName(), unwrap(e));
        }

        Throwable failure = null;
        try {
            invokeMethods(plan.beforeMethods(), testInstance);
            invokeMethod(testMethod, testInstance);
        } catch (Throwable e) {
            failure = unwrap(e);
        } finally {
            try {
                invokeMethods(plan.afterMethods(), testInstance);
            } catch (Throwable e) {
                if (failure == null) {
                    failure = unwrap(e);
                } else {
                    failure.addSuppressed(unwrap(e));
                }
            }
        }

        if (failure == null) {
            return TestResult.success(testMethod.getName());
        }
        return TestResult.failure(testMethod.getName(), failure);
    }

    private static Object createInstance(Class<?> testClass) throws Exception {
        Constructor<?> constructor = testClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static void invokeMethods(List<Method> methods, Object instance) throws Exception {
        for (Method method : methods) {
            invokeMethod(method, instance);
        }
    }

    private static void invokeMethod(Method method, Object instance) throws Exception {
        method.invoke(instance);
    }

    private static Throwable unwrap(Throwable throwable) {
        if (throwable instanceof java.lang.reflect.InvocationTargetException invocationTargetException
                && invocationTargetException.getCause() != null) {
            return invocationTargetException.getCause();
        }
        return throwable;
    }

    private static void printStatistics(String className, TestStatistics statistics) {
        System.out.println("=== Test run: " + className + " ===");
        for (TestResult result : statistics.results()) {
            if (result.passed()) {
                System.out.println("[OK]   " + result.testName());
            } else {
                System.out.println("[FAIL] " + result.testName() + " -> " + result.error());
            }
        }
        System.out.println("---");
        System.out.println("Total:   " + statistics.total());
        System.out.println("Passed:  " + statistics.passed());
        System.out.println("Failed:  " + statistics.failed());
    }
}
