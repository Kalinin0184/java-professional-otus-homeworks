package ru.otus.processor.homework;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

class ProcessorSwapField11AndField12Test {

    @Test
    void swapsFields() {
        var message = new Message.Builder(1L)
                .field11("eleven")
                .field12("twelve")
                .build();

        var result = new ProcessorSwapField11AndField12().process(message);

        assertThat(result.getField11()).isEqualTo("twelve");
        assertThat(result.getField12()).isEqualTo("eleven");
    }
}
