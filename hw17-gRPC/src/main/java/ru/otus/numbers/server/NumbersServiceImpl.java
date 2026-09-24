package ru.otus.numbers.server;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.numbers.protobuf.NumberRequest;
import ru.otus.numbers.protobuf.NumberResponse;
import ru.otus.numbers.protobuf.NumbersServiceGrpc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(NumbersServiceImpl.class);

    private static final long PERIOD_SECONDS = 2L;

    @Override
    public void getNumbers(NumberRequest request, StreamObserver<NumberResponse> responseObserver) {
        long firstValue = request.getFirstValue();
        long lastValue = request.getLastValue();
        log.info("Start sequence: firstValue={}, lastValue={}", firstValue, lastValue);

        AtomicLong current = new AtomicLong(firstValue);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            try {
                long value = current.incrementAndGet();
                responseObserver.onNext(NumberResponse.newBuilder().setValue(value).build());
                log.info("stream value: {}", value);

                if (value >= lastValue) {
                    executor.shutdown();
                    responseObserver.onCompleted();
                    log.info("Sequence completed");
                }
            } catch (Exception e) {
                log.error("Error while streaming numbers", e);
                executor.shutdown();
                responseObserver.onError(e);
            }
        }, 0, PERIOD_SECONDS, TimeUnit.SECONDS);
    }
}
