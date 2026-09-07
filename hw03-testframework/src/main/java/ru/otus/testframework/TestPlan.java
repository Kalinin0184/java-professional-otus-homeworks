package ru.otus.testframework;

import java.lang.reflect.Method;
import java.util.List;

record TestPlan(Class<?> testClass, List<Method> beforeMethods, List<Method> testMethods, List<Method> afterMethods) {
}
