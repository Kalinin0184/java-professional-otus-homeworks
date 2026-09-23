package ru.otus.httpserver.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import ru.otus.httpserver.http.HttpMethod;
import ru.otus.httpserver.http.HttpRequest;
import ru.otus.httpserver.http.HttpResponse;
import ru.otus.httpserver.http.HttpStatus;
import ru.otus.httpserver.routing.Handler;
import ru.otus.httpserver.routing.Router;

public final class StaticFileHandler implements Handler {
    private final Path wwwRoot;

    public StaticFileHandler(Path wwwRoot) {
        this.wwwRoot = wwwRoot.toAbsolutePath().normalize();
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {
        if (request.method() != HttpMethod.GET && request.method() != HttpMethod.HEAD) {
            return HttpResponse.text(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
        }

        String requestPath = Router.normalize(request.path());
        if ("/".equals(requestPath)) {
            requestPath = "/index.html";
        }

        Optional<Path> resolved = resolveSafe(requestPath);
        if (resolved.isEmpty() || !Files.isRegularFile(resolved.get())) {
            Optional<byte[]> classpathResource = readClasspath(requestPath);
            if (classpathResource.isEmpty()) {
                return HttpResponse.text(HttpStatus.NOT_FOUND, "Not Found");
            }
            byte[] body = request.method() == HttpMethod.HEAD ? new byte[0] : classpathResource.get();
            return HttpResponse.ok(body, contentType(requestPath));
        }

        byte[] content = Files.readAllBytes(resolved.get());
        byte[] body = request.method() == HttpMethod.HEAD ? new byte[0] : content;
        HttpResponse.Builder builder = HttpResponse.status(HttpStatus.OK)
                .contentType(contentType(requestPath))
                .body(body);
        if (request.method() == HttpMethod.HEAD) {
            builder.header("Content-Length", String.valueOf(content.length));
        }
        return builder.build();
    }

    private Optional<Path> resolveSafe(String requestPath) {
        String relative = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;
        Path candidate = wwwRoot.resolve(relative).normalize();
        if (!candidate.startsWith(wwwRoot)) {
            return Optional.empty();
        }
        return Optional.of(candidate);
    }

    private Optional<byte[]> readClasspath(String requestPath) throws IOException {
        String resource = "www" + requestPath;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                return Optional.empty();
            }
            return Optional.of(in.readAllBytes());
        }
    }

    private static String contentType(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".html") || lower.endsWith(".htm")) {
            return "text/html; charset=utf-8";
        }
        if (lower.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (lower.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }
}
