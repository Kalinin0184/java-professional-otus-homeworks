package ru.otus.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import ru.otus.aop.annotation.Log;

class LogInvocationHandler implements InvocationHandler {

    private final Object target;
    private final Set<String> loggedMethods;

    LogInvocationHandler(Object target) {
        this.target = target;
        this.loggedMethods = findLoggedMethods(target.getClass());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (loggedMethods.contains(methodKey(method))) {
            System.out.println(formatLog(method, args));
        }
        return method.invoke(target, args);
    }

    private static Set<String> findLoggedMethods(Class<?> targetClass) {
        Set<String> methods = new HashSet<>();
        for (Method method : targetClass.getMethods()) {
            if (method.isAnnotationPresent(Log.class)) {
                methods.add(methodKey(method));
            }
        }
        return methods;
    }

    private static String methodKey(Method method) {
        return method.getName() + Arrays.toString(method.getParameterTypes());
    }

    private static String formatLog(Method method, Object[] args) {
        String params = args == null || args.length == 0
                ? ""
                : Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", "));
        return "executed method: " + method.getName() + ", param: " + params;
    }
}
