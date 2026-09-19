package ru.otus.appcontainer;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfigs(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... configClasses) {
        processConfigs(configClasses);
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Class<?>[] configClasses = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class)
                .toArray(Class<?>[]::new);
        if (configClasses.length == 0) {
            throw new IllegalArgumentException("No config classes found in package " + packageName);
        }
        processConfigs(configClasses);
    }

    private void processConfigs(Class<?>... configClasses) {
        for (Class<?> configClass : configClasses) {
            checkConfigClass(configClass);
        }
        Arrays.stream(configClasses)
                .sorted(Comparator.comparingInt(configClass ->
                        configClass.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            List<Method> componentMethods = Arrays.stream(configClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(method -> method.getAnnotation(AppComponent.class).order()))
                    .toList();

            for (Method method : componentMethods) {
                method.setAccessible(true);
                Object[] args = Arrays.stream(method.getParameterTypes())
                        .map(this::getAppComponent)
                        .toArray();
                Object component = method.invoke(configInstance, args);
                String componentName = method.getAnnotation(AppComponent.class).name();
                if (appComponentsByName.containsKey(componentName)) {
                    throw new IllegalArgumentException("Duplicate component name: " + componentName);
                }
                appComponentsByName.put(componentName, component);
                appComponents.add(component);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process config " + configClass.getName(), e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> matched = appComponents.stream()
                .filter(component -> componentClass.isAssignableFrom(component.getClass()))
                .toList();
        if (matched.size() != 1) {
            throw new RuntimeException(String.format(
                    "Failed to determine unique component for type %s, found: %d",
                    componentClass.getName(),
                    matched.size()));
        }
        return (C) matched.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);
        if (component == null) {
            throw new RuntimeException("Failed to determine component by name: " + componentName);
        }
        return (C) component;
    }
}
