package ru.otus.numbers.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersServer {
    private static final Logger log = LoggerFactory.getLogger(NumbersServer.class);
    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(SERVER_PORT)
                .addService(new NumbersServiceImpl())
                .build();

        server.start();
        log.info("numbers Server is started on port {}", SERVER_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down numbers Server...");
            server.shutdown();
            log.info("numbers Server is stopped");
        }));

        server.awaitTermination();
    }
}
