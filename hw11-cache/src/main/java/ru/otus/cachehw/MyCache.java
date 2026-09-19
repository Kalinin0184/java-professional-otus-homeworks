package ru.otus.cachehw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final String ACTION_PUT = "put";
    private static final String ACTION_GET = "get";
    private static final String ACTION_REMOVE = "remove";

    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, ACTION_PUT);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
        notifyListeners(key, null, ACTION_REMOVE);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        notifyListeners(key, value, ACTION_GET);
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    public int size() {
        return cache.size();
    }

    private void notifyListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            try {
                listener.notify(key, value, action);
            } catch (Exception ignored) {
                // listener must not break cache operations
            }
        }
    }
}
