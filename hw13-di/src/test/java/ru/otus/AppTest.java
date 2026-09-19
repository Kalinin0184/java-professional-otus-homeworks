package ru.otus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.appcontainer.AppComponentsContainerImpl;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.config.AppConfig;
import ru.otus.config.modular.AppConfig1;
import ru.otus.config.modular.AppConfig2;
import ru.otus.services.EquationPreparer;
import ru.otus.services.GameProcessor;
import ru.otus.services.IOService;
import ru.otus.services.PlayerService;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    static Stream<AppComponentsContainer> containers() {
        return Stream.of(
                new AppComponentsContainerImpl(AppConfig.class),
                new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class),
                new AppComponentsContainerImpl("ru.otus.config.modular"));
    }

    @DisplayName("Из контекста тремя способами должен корректно доставаться компонент с проставленными полями")
    @ParameterizedTest(name = "Достаем по: {0}")
    @CsvSource(value = {
            "GameProcessor, ru.otus.services.GameProcessor",
            "GameProcessorImpl, ru.otus.services.GameProcessor",
            "gameProcessor, ru.otus.services.GameProcessor",

            "IOService, ru.otus.services.IOService",
            "IOServiceStreams, ru.otus.services.IOService",
            "ioService, ru.otus.services.IOService",

            "PlayerService, ru.otus.services.PlayerService",
            "PlayerServiceImpl, ru.otus.services.PlayerService",
            "playerService, ru.otus.services.PlayerService",

            "EquationPreparer, ru.otus.services.EquationPreparer",
            "EquationPreparerImpl, ru.otus.services.EquationPreparer",
            "equationPreparer, ru.otus.services.EquationPreparer"
    })
    public void shouldExtractFromContextCorrectComponentWithNotNullFields(String classNameOrBeanId, Class<?> rootClass)
            throws Exception {
        var ctx = new AppComponentsContainerImpl(AppConfig.class);
        assertComponent(ctx, classNameOrBeanId, rootClass);
    }

    @DisplayName("Компоненты корректно достаются для всех вариантов создания контейнера")
    @ParameterizedTest(name = "container variant #{index}")
    @MethodSource("containers")
    void shouldWorkForAllContainerVariants(AppComponentsContainer ctx) throws Exception {
        assertComponent(ctx, "GameProcessor", GameProcessor.class);
        assertComponent(ctx, "gameProcessor", GameProcessor.class);
        assertComponent(ctx, "ioService", IOService.class);
        assertComponent(ctx, "playerService", PlayerService.class);
        assertComponent(ctx, "equationPreparer", EquationPreparer.class);
    }

    private void assertComponent(AppComponentsContainer ctx, String classNameOrBeanId, Class<?> rootClass)
            throws Exception {
        assertThat(classNameOrBeanId).isNotEmpty();
        Object component;
        if (classNameOrBeanId.charAt(0) == classNameOrBeanId.toUpperCase().charAt(0)) {
            Class<?> componentClass = Class.forName("ru.otus.services." + classNameOrBeanId);
            assertThat(rootClass).isAssignableFrom(componentClass);
            component = ctx.getAppComponent(componentClass);
        } else {
            component = ctx.getAppComponent(classNameOrBeanId);
        }
        assertThat(component).isNotNull();
        assertThat(rootClass).isAssignableFrom(component.getClass());

        var fields = Arrays.stream(component.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());

        for (var field : fields) {
            var fieldValue = field.get(component);
            assertThat(fieldValue)
                    .isNotNull()
                    .isInstanceOfAny(
                            IOService.class,
                            PlayerService.class,
                            EquationPreparer.class,
                            PrintStream.class,
                            Scanner.class);
        }
    }
}
