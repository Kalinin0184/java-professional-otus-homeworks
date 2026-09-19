package ru.otus.numbers.client;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.numbers.protobuf.NumberResponse;

public class ClientStreamObserver implements StreamObserver<NumberResponse> {
    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private long lastValue;

    @Override
    public void onNext(NumberResponse value) {
        long received = value.getValue();
        setLastValue(received);
        log.info("new value:{}", received);
    }

    @Override
    public void onError(Throwable t) {
        log.error("stream error", t);
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
    }

    private synchronized void setLastValue(long value) {
        this.lastValue = value;
    }

    public synchronized long getLastValueAndReset() {
        long value = lastValue;
        lastValue = 0;
        return value;
    }
}
