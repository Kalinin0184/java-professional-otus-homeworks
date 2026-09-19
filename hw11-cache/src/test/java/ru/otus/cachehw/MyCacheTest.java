package ru.otus.cachehw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class MyCacheTest {

    @Test
    @DisplayName("кэш хранит значения и уведомляет слушателей")
    void shouldStoreValuesAndNotifyListeners() {
        MyCache<String, String> cache = new MyCache<>();
        AtomicInteger notifications = new AtomicInteger();
        HwListener<String, String> listener = (key, value, action) -> notifications.incrementAndGet();
        cache.addListener(listener);

        cache.put("a", "1");
        assertThat(cache.get("a")).isEqualTo("1");
        cache.remove("a");
        assertThat(cache.get("a")).isNull();
        assertThat(notifications.get()).isEqualTo(4);

        cache.removeListener(listener);
        cache.put("b", "2");
        assertThat(notifications.get()).isEqualTo(4);
    }

    @Test
    @DisplayName("записи WeakHashMap исчезают после GC, если ключи больше не удерживаются")
    void shouldClearWhenKeysAreCollected() {
        MyCache<Object, String> cache = new MyCache<>();
        List<Object> strongKeys = new ArrayList<>();
        List<WeakReference<Object>> weakKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Object key = new Object();
            strongKeys.add(key);
            weakKeys.add(new WeakReference<>(key));
            cache.put(key, "value-" + i);
        }

        assertThat(cache.size()).isEqualTo(100);

        strongKeys.clear();
        System.gc();

        // ждём, пока хотя бы часть ключей будет собрана GC
        boolean cleared = false;
        for (int attempt = 0; attempt < 20; attempt++) {
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            long aliveKeys = weakKeys.stream().filter(ref -> ref.get() != null).count();
            if (aliveKeys == 0 && cache.size() == 0) {
                cleared = true;
                break;
            }
        }

        assertThat(cleared)
                .as("WeakHashMap должен очиститься после потери сильных ссылок на ключи")
                .isTrue();
    }
}
