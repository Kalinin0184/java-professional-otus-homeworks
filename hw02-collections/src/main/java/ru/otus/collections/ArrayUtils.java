package ru.otus.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * Меняет местами два элемента массива любого ссылочного типа.
     */
    public static <T> void swap(T[] array, int firstIndex, int secondIndex) {
        Objects.requireNonNull(array, "array must not be null");
        validateIndex(array, firstIndex);
        validateIndex(array, secondIndex);

        T temporary = array[firstIndex];
        array[firstIndex] = array[secondIndex];
        array[secondIndex] = temporary;
    }

    /**
     * Преобразует массив в {@link ArrayList}.
     */
    public static <T> ArrayList<T> toArrayList(T[] array) {
        Objects.requireNonNull(array, "array must not be null");
        return new ArrayList<>(Arrays.asList(array));
    }

    private static <T> void validateIndex(T[] array, int index) {
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is out of bounds for length " + array.length);
        }
    }
}
