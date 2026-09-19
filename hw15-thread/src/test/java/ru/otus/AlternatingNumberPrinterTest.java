package ru.otus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlternatingNumberPrinterTest {

    @Test
    @DisplayName("потоки чередуются, начинает Поток 1, числа идут 1..10..1..")
    void shouldAlternateAndStartWithFirstThread() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            new AlternatingNumberPrinter(19).start();
        } finally {
            System.setOut(originalOut);
        }

        List<String> lines = buffer.toString(StandardCharsets.UTF_8)
                .lines()
                .filter(line -> !line.isBlank())
                .toList();

        assertThat(lines).hasSize(38);

        List<Integer> thread1Values = new ArrayList<>();
        List<Integer> thread2Values = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i % 2 == 0) {
                assertThat(line).startsWith("Поток 1:");
                thread1Values.add(Integer.parseInt(line.substring("Поток 1:".length())));
            } else {
                assertThat(line).startsWith("Поток 2:");
                thread2Values.add(Integer.parseInt(line.substring("Поток 2:".length())));
            }
        }

        List<Integer> expected = List.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                9, 8, 7, 6, 5, 4, 3, 2, 1);

        assertThat(thread1Values).containsExactlyElementsOf(expected);
        assertThat(thread2Values).containsExactlyElementsOf(expected);
    }
}
