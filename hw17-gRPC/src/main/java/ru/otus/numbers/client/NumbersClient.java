package ru.otus.numbers.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.numbers.protobuf.NumberRequest;
import ru.otus.numbers.protobuf.NumbersServiceGrpc;
import ru.otus.numbers.server.NumbersServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class NumbersClient {
    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);

    private static final String SERVER_HOST = "localhost";
    private static final long FIRST_VALUE = 0;
    private static final long LAST_VALUE = 30;
    private static final int LOOP_LIMIT = 50;
    private static final long CLIENT_PERIOD_SECONDS = 1L;

    public static void main(String[] args) throws InterruptedException {
        log.info("numbers Client is starting...");

        ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_HOST, NumbersServer.SERVER_PORT)
                .usePlaintext()
                .build();

        NumbersServiceGrpc.NumbersServiceStub asyncStub = NumbersServiceGrpc.newStub(channel);
        ClientStreamObserver streamObserver = new ClientStreamObserver();

        NumberRequest request = NumberRequest.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();
        asyncStub.getNumbers(request, streamObserver);

        AtomicLong currentValue = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(LOOP_LIMIT);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            try {
                long next = currentValue.get() + streamObserver.getLastValueAndReset() + 1;
                currentValue.set(next);
                log.info("currentValue:{}", next);
            } finally {
                latch.countDown();
            }
        }, 0, CLIENT_PERIOD_SECONDS, TimeUnit.SECONDS);

        latch.await();
        executor.shutdown();
        channel.shutdown();
        log.info("numbers Client is stopped");
    }
}
