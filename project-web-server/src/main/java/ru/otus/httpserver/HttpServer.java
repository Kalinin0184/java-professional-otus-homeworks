package ru.otus.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.otus.httpserver.connection.ConnectionHandler;
import ru.otus.httpserver.routing.Router;

public final class HttpServer implements AutoCloseable {
    private static final Logger log = Logger.getLogger(HttpServer.class.getName());

    private final HttpServerConfig config;
    private final Router router;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong acceptedConnections = new AtomicLong();
    private ServerSocket serverSocket;
    private ExecutorService workerPool;
    private Thread acceptorThread;

    public HttpServer(HttpServerConfig config, Router router) {
        this.config = config;
        this.router = router;
    }

    public synchronized void start() throws IOException {
        if (running.get()) {
            return;
        }
        serverSocket = new ServerSocket(config.port());
        workerPool = Executors.newFixedThreadPool(config.poolSize(), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("http-worker-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
        running.set(true);
        acceptorThread = new Thread(this::acceptLoop, "http-acceptor");
        acceptorThread.setDaemon(false);
        acceptorThread.start();
        log.info(() -> "HTTP server started on port " + port()
                + " (pool=" + config.poolSize()
                + ", soTimeout=" + config.soTimeoutMs() + "ms"
                + ", maxReq/conn=" + config.maxRequestsPerConnection() + ")");
    }

    public int port() {
        return serverSocket == null ? config.port() : serverSocket.getLocalPort();
    }

    public boolean isRunning() {
        return running.get();
    }

    public long acceptedConnections() {
        return acceptedConnections.get();
    }

    private void acceptLoop() {
        while (running.get()) {
            try {
                Socket socket = serverSocket.accept();
                acceptedConnections.incrementAndGet();
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(config.soTimeoutMs());
                workerPool.execute(new ConnectionHandler(socket, router, config));
            } catch (SocketException ex) {
                if (running.get()) {
                    log.log(Level.SEVERE, "Accept failed", ex);
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Accept failed", ex);
            }
        }
    }

    @Override
    public synchronized void close() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            log.log(Level.WARNING, "Error closing server socket", ex);
        }
        if (workerPool != null) {
            workerPool.shutdown();
            try {
                if (!workerPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    workerPool.shutdownNow();
                }
            } catch (InterruptedException ex) {
                workerPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        if (acceptorThread != null) {
            try {
                acceptorThread.join(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        log.info(() -> "HTTP server stopped, acceptedConnections=" + acceptedConnections.get());
    }
}
