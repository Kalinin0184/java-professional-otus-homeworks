package ru.otus;

public class NumberSequenceDemo {

    public static void main(String[] args) throws InterruptedException {
        // 1..10..1..2..3..4 — как в примере задания
        new AlternatingNumberPrinter(22).start();
    }
}
