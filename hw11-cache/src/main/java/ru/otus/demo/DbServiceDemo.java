package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        DBServiceClient writer = new DbServiceClientImpl(transactionManager, clientTemplate);
        List<Long> ids = new ArrayList<>();
        int count = 50;
        for (int i = 0; i < count; i++) {
            Client saved = writer.saveClient(new Client(
                    null,
                    "client-" + i,
                    new Address(null, "Street-" + i),
                    List.of(new Phone(null, "100-" + i))));
            ids.add(saved.getId());
        }

        MyCache<String, Client> cache = new MyCache<>();
        AtomicInteger hits = new AtomicInteger();
        AtomicInteger misses = new AtomicInteger();
        HwListener<String, Client> listener = (key, value, action) -> {
            if ("get".equals(action)) {
                if (value != null) {
                    hits.incrementAndGet();
                } else {
                    misses.incrementAndGet();
                }
            }
        };
        cache.addListener(listener);
        DBServiceClient reader = new DbServiceClientImpl(transactionManager, clientTemplate, cache);

        hits.set(0);
        misses.set(0);
        long dbStart = System.nanoTime();
        for (Long id : ids) {
            reader.getClient(id);
        }
        long dbMs = (System.nanoTime() - dbStart) / 1_000_000;
        log.info("First pass (DB → fill cache): {} ms, hits={}, misses={}, size={}",
                dbMs, hits.get(), misses.get(), cache.size());

        hits.set(0);
        misses.set(0);
        long cacheStart = System.nanoTime();
        for (Long id : ids) {
            reader.getClient(id);
        }
        long cacheMs = (System.nanoTime() - cacheStart) / 1_000_000;
        log.info("Second pass (cache): {} ms, hits={}, misses={}, size={}",
                cacheMs, hits.get(), misses.get(), cache.size());
        log.info("Cache is faster than DB: {}", cacheMs < dbMs);

        // WeakHashMap: без сильных ссылок на ключи записи вытесняются GC
        forceMemoryPressure();
        log.info("Cache size after memory pressure / GC: {}", cache.size());

        hits.set(0);
        misses.set(0);
        for (Long id : ids) {
            reader.getClient(id);
        }
        log.info("After GC: hits={}, misses={} (ожидаем misses ≈ {}, cache снова наполняется)",
                hits.get(), misses.get(), ids.size());

        cache.removeListener(listener);
    }

    private static void forceMemoryPressure() {
        try {
            List<byte[]> junk = new ArrayList<>();
            for (int i = 0; i < 80; i++) {
                junk.add(new byte[8 * 1024 * 1024]);
            }
            log.info("Allocated temporary buffers to encourage GC, chunks={}", junk.size());
            junk.clear();
        } catch (OutOfMemoryError e) {
            log.info("OOM while allocating junk — GC should clear WeakHashMap entries");
        }
        System.gc();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.gc();
    }
}
