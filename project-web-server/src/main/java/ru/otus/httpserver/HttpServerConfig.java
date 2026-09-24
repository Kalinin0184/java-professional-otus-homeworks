package ru.otus.httpserver;

import java.nio.file.Path;

public record HttpServerConfig(
        int port,
        int poolSize,
        Path wwwRoot,
        int soTimeoutMs,
        int maxRequestsPerConnection) {

    public HttpServerConfig(int port, int poolSize, Path wwwRoot) {
        this(port, poolSize, wwwRoot, 30_000, 100);
    }

    public static HttpServerConfig defaults() {
        return new HttpServerConfig(
                8080,
                16,
                Path.of("project-web-server", "src", "main", "resources", "www"),
                30_000,
                100);
    }
}
