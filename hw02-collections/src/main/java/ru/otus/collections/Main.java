package ru.otus.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        demonstrateSwapAndToArrayList();
        System.out.println();
        demonstrateWordAnalysis();
    }

    private static void demonstrateSwapAndToArrayList() {
        String[] words = {"alpha", "beta", "gamma", "delta"};
        System.out.println("Исходный массив: " + Arrays.toString(words));

        ArrayUtils.swap(words, 0, 3);
        System.out.println("После swap(0, 3): " + Arrays.toString(words));

        ArrayList<String> list = ArrayUtils.toArrayList(words);
        System.out.println("ArrayList: " + list);
        System.out.println("Тип списка: " + list.getClass().getName());
    }

    private static void demonstrateWordAnalysis() {
        String[] words = {
                "java", "otus", "gradle", "java", "collections",
                "otus", "map", "list", "set", "java",
                "generics", "list", "otus", "stream", "map",
                "optional", "java", "gradle"
        };

        System.out.println("Массив слов: " + Arrays.toString(words));

        Set<String> unique = WordAnalyzer.uniqueWords(words);
        System.out.println("Уникальные слова (" + unique.size() + "): " + unique);

        Map<String, Integer> frequency = WordAnalyzer.wordFrequency(words);
        System.out.println("Частота слов:");
        frequency.forEach((word, count) -> System.out.println("  " + word + " -> " + count));
    }
}
