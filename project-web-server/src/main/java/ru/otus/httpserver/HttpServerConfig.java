package ru.otus.httpserver;

import java.nio.file.Path;

public record HttpServerConfig(int port, int poolSize, Path wwwRoot) {
    public static HttpServerConfig defaults() {
        return new HttpServerConfig(8080, 16, Path.of("project-web-server", "src", "main", "resources", "www"));
    }
}
