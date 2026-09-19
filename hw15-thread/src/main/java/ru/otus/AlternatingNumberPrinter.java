package ru.otus;

/**
 * Два потока печатают числа 1..10..1.., чередуясь.
 * Всегда начинает «Поток 1».
 */
public class AlternatingNumberPrinter {

    private static final int MIN = 1;
    private static final int MAX = 10;

    private final Object monitor = new Object();
    private boolean firstThreadTurn = true;
    private final int stepsPerThread;

    public AlternatingNumberPrinter(int stepsPerThread) {
        if (stepsPerThread <= 0) {
            throw new IllegalArgumentException("stepsPerThread must be positive");
        }
        this.stepsPerThread = stepsPerThread;
    }

    public void start() throws InterruptedException {
        Thread thread1 = new Thread(() -> printSequence(true), "Поток 1");
        Thread thread2 = new Thread(() -> printSequence(false), "Поток 2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    private void printSequence(boolean firstThread) {
        int value = MIN;
        int direction = 1;

        for (int step = 0; step < stepsPerThread; step++) {
            synchronized (monitor) {
                while (firstThreadTurn != firstThread) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                System.out.println(Thread.currentThread().getName() + ":" + value);

                if (value == MAX) {
                    direction = -1;
                } else if (value == MIN && direction < 0) {
                    direction = 1;
                }
                value += direction;

                firstThreadTurn = !firstThreadTurn;
                monitor.notifyAll();
            }
        }
    }
}
