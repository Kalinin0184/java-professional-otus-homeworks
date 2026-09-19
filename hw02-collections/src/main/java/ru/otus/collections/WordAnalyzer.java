package ru.otus.collections;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class WordAnalyzer {

    private WordAnalyzer() {
    }

    /**
     * Возвращает уникальные слова в порядке первого появления.
     */
    public static Set<String> uniqueWords(String[] words) {
        Objects.requireNonNull(words, "words must not be null");
        Set<String> unique = new LinkedHashSet<>();
        Collections.addAll(unique, words);
        return unique;
    }

    /**
     * Подсчитывает, сколько раз встречается каждое слово.
     * Порядок ключей совпадает с порядком первого появления слова.
     */
    public static Map<String, Integer> wordFrequency(String[] words) {
        Objects.requireNonNull(words, "words must not be null");
        Map<String, Integer> frequency = new LinkedHashMap<>();
        for (String word : words) {
            frequency.merge(word, 1, Integer::sum);
        }
        return frequency;
    }
}
