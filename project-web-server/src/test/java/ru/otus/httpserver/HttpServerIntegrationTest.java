package ru.otus.httpserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.httpserver.handlers.EchoHandler;
import ru.otus.httpserver.handlers.HelloHandler;
import ru.otus.httpserver.handlers.StaticFileHandler;
import ru.otus.httpserver.routing.Router;

class HttpServerIntegrationTest {
    private HttpServer server;
    private HttpClient client;

    @BeforeEach
    void setUp() throws Exception {
        Path wwwRoot = Path.of("src", "main", "resources", "www").toAbsolutePath().normalize();
        Router router = new Router()
                .get("/api/hello", new HelloHandler())
                .post("/api/echo", new EchoHandler())
                .fallback(new StaticFileHandler(wwwRoot));
        server = new HttpServer(new HttpServerConfig(0, 8, wwwRoot, 5_000, 50), router);
        server.start();
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.close();
        }
    }

    @Test
    void servesIndexAndJsonApi() throws Exception {
        HttpResponse<String> index = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + "/"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(index.statusCode()).isEqualTo(200);
        assertThat(index.body()).contains("Свой HTTP-сервер");

        HttpResponse<String> hello = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + "/api/hello"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(hello.statusCode()).isEqualTo(200);
        assertThat(hello.body()).contains("Hello from custom HTTP server");

        HttpResponse<String> echo = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + "/api/echo"))
                        .header("Content-Type", "text/plain")
                        .POST(HttpRequest.BodyPublishers.ofString("ping"))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(echo.statusCode()).isEqualTo(200);
        assertThat(echo.body()).contains("\"echo\":\"ping\"");
    }

    @Test
    void returns404ForMissingStatic() throws Exception {
        HttpResponse<String> missing = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + "/no-such-file.html"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(missing.statusCode()).isEqualTo(404);
    }

    @Test
    void reusesTcpConnectionWithKeepAlive() throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", server.port()), 2000);
            socket.setSoTimeout(3000);
            OutputStream out = new BufferedOutputStream(socket.getOutputStream());
            InputStream in = new BufferedInputStream(socket.getInputStream());

            writeGet(out, "/api/hello", true);
            String first = readRawResponse(in);
            assertThat(first).contains("HTTP/1.1 200 OK");
            assertThat(first.toLowerCase()).contains("connection: keep-alive");
            assertThat(first).contains("Hello from custom HTTP server");

            writeGet(out, "/api/hello", true);
            String second = readRawResponse(in);
            assertThat(second).contains("HTTP/1.1 200 OK");
            assertThat(second).contains("Hello from custom HTTP server");
        }

        assertThat(server.acceptedConnections()).isEqualTo(1);
    }

    @Test
    void loadSmokeConcurrentRequests() throws Exception {
        int clients = 16;
        int requestsPerClient = 25;
        AtomicInteger ok = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(clients);
        long started = System.nanoTime();

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int c = 0; c < clients; c++) {
            tasks.add(() -> {
                for (int i = 0; i < requestsPerClient; i++) {
                    HttpResponse<String> response = client.send(
                            HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + "/api/hello"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        ok.incrementAndGet();
                    }
                }
                return null;
            });
        }

        List<Future<Void>> futures = pool.invokeAll(tasks, 30, TimeUnit.SECONDS);
        pool.shutdownNow();
        for (Future<Void> future : futures) {
            assertThat(future.isCancelled()).isFalse();
            future.get(1, TimeUnit.SECONDS);
        }

        long elapsedMs = Math.max(1, (System.nanoTime() - started) / 1_000_000L);
        int total = clients * requestsPerClient;
        double rps = total * 1000.0 / elapsedMs;
        System.out.printf(
                "Load smoke: %d/%d OK in %d ms (%.0f req/s), acceptedConnections=%d%n",
                ok.get(),
                total,
                elapsedMs,
                rps,
                server.acceptedConnections());

        assertThat(ok.get()).isEqualTo(total);
        assertThat(rps).isGreaterThan(10);
    }

    private static void writeGet(OutputStream out, String path, boolean keepAlive) throws Exception {
        String request = ""
                + "GET " + path + " HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "Connection: " + (keepAlive ? "keep-alive" : "close") + "\r\n"
                + "\r\n";
        out.write(request.getBytes(StandardCharsets.US_ASCII));
        out.flush();
    }

    private static String readRawResponse(InputStream in) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int contentLength = -1;
        while (true) {
            String line = readLine(in);
            if (line == null) {
                break;
            }
            buffer.write(line.getBytes(StandardCharsets.US_ASCII));
            buffer.write('\r');
            buffer.write('\n');
            if (line.isEmpty()) {
                break;
            }
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
            }
        }
        if (contentLength > 0) {
            buffer.write(in.readNBytes(contentLength));
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static String readLine(InputStream in) throws Exception {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int prev = -1;
        while (true) {
            int current = in.read();
            if (current == -1) {
                return line.size() == 0 ? null : line.toString(StandardCharsets.US_ASCII);
            }
            if (prev == '\r' && current == '\n') {
                break;
            }
            if (prev != -1) {
                line.write(prev);
            }
            prev = current;
        }
        return line.toString(StandardCharsets.US_ASCII);
    }
}
