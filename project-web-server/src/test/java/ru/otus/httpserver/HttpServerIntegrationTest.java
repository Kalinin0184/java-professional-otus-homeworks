package ru.otus.httpserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
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
        server = new HttpServer(new HttpServerConfig(0, 4, wwwRoot), router);
        server.start();
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
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
}
