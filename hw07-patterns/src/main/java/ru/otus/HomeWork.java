package ru.otus;

import java.time.LocalDateTime;
import java.util.List;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.homework.ProcessorSwapField11AndField12;
import ru.otus.processor.homework.ProcessorThrowOnEvenSecond;

public class HomeWork {

    /*
     Реализовать to do:
       1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
       2. Сделать процессор, который поменяет местами значения field11 и field12
       3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
             Секунда должна определяьться во время выполнения.
             Тест - важная часть задания
             Обязательно посмотрите пример к паттерну Мементо!
       4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
          Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
          Для него уже есть тест, убедитесь, что тест проходит
     */

    public static void main(String[] args) {
        var processors = List.of(
                new ProcessorSwapField11AndField12(),
                new ProcessorThrowOnEvenSecond(LocalDateTime::now));

        var complexProcessor = new ComplexProcessor(processors, ex -> System.out.println("error: " + ex.getMessage()));
        var historyListener = new HistoryListener();
        var listenerPrinter = new ListenerPrinterConsole();
        complexProcessor.addListener(historyListener);
        complexProcessor.addListener(listenerPrinter);

        var field13 = new ObjectForMessage();
        field13.setData(List.of("a", "b", "c"));

        var message = new Message.Builder(1L)
                .field11("field11")
                .field12("field12")
                .field13(field13)
                .build();

        var result = complexProcessor.handle(message);
        System.out.println("result:" + result);
        System.out.println("history:" + historyListener.findMessageById(1L));

        complexProcessor.removeListener(historyListener);
        complexProcessor.removeListener(listenerPrinter);
    }
}
