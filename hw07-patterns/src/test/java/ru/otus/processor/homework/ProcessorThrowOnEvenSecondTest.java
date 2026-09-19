package ru.otus.processor.homework;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

class ProcessorThrowOnEvenSecondTest {

    @Test
    @DisplayName("На четной секунде процессор выбрасывает исключение")
    void throwsOnEvenSecond() {
        DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
        when(dateTimeProvider.now()).thenReturn(LocalDateTime.of(2026, 1, 1, 12, 0, 2));
        var processor = new ProcessorThrowOnEvenSecond(dateTimeProvider);
        var message = new Message.Builder(1L).build();

        assertThatThrownBy(() -> processor.process(message))
                .isInstanceOf(EvenSecondException.class)
                .hasMessageContaining("Even second: 2");
    }

    @Test
    @DisplayName("На нечетной секунде процессор возвращает сообщение")
    void passesOnOddSecond() {
        DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
        when(dateTimeProvider.now()).thenReturn(LocalDateTime.of(2026, 1, 1, 12, 0, 3));
        var processor = new ProcessorThrowOnEvenSecond(dateTimeProvider);
        var message = new Message.Builder(1L).field11("a").build();

        assertThat(processor.process(message)).isSameAs(message);
    }
}
