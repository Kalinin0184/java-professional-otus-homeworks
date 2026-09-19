package ru.otus.crm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.cachehw.MyCache;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DbServiceClientCacheTest extends AbstractHibernateTest {

    @Test
    @DisplayName("при использовании кэша повторное чтение быстрее обращения к СУБД")
    void shouldBeFasterWithCache() {
        List<Long> ids = new ArrayList<>();
        int count = 40;
        for (int i = 0; i < count; i++) {
            var client = new Client(
                    null,
                    "Vasya" + i,
                    new Address(null, "AnyStreet" + i),
                    List.of(new Phone(null, "13-555-" + i), new Phone(null, "14-666-" + i)));
            ids.add(dbServiceClient.saveClient(client).getId());
        }

        // отдельный пустой кэш: первый проход — БД, второй — кэш
        MyCache<String, Client> cache = new MyCache<>();
        DBServiceClient reader = new DbServiceClientImpl(transactionManager, clientTemplate, cache);

        long start = System.nanoTime();
        for (Long id : ids) {
            reader.getClient(id);
        }
        long dbPassMs = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        for (Long id : ids) {
            reader.getClient(id);
        }
        long cachePassMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("time DB pass: " + dbPassMs + " ms");
        System.out.println("time cache pass: " + cachePassMs + " ms");

        assertThat(cachePassMs).isLessThan(dbPassMs);
    }
}
