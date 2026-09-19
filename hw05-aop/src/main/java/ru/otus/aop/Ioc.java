package ru.otus.aop;

import java.lang.reflect.Proxy;

public final class Ioc {

    private Ioc() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T createLoggedInstance(T instance, Class<T> interfaceType) {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[] {interfaceType},
                new LogInvocationHandler(instance));
    }
}
