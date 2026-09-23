package ru.otus.httpserver.routing;

import java.util.Optional;
import ru.otus.httpserver.http.HttpMethod;
import ru.otus.httpserver.http.HttpRequest;
import ru.otus.httpserver.http.HttpResponse;
import ru.otus.httpserver.http.HttpStatus;

public final class Router {
    private final java.util.Map<RouteKey, Handler> routes = new java.util.LinkedHashMap<>();
    private Handler fallback = request -> HttpResponse.text(HttpStatus.NOT_FOUND, "Not Found");

    public Router get(String path, Handler handler) {
        return add(HttpMethod.GET, path, handler);
    }

    public Router post(String path, Handler handler) {
        return add(HttpMethod.POST, path, handler);
    }

    public Router add(HttpMethod method, String path, Handler handler) {
        routes.put(new RouteKey(method, normalize(path)), handler);
        return this;
    }

    public Router fallback(Handler fallback) {
        this.fallback = fallback;
        return this;
    }

    public HttpResponse dispatch(HttpRequest request) throws Exception {
        String path = normalize(request.path());
        Handler exact = routes.get(new RouteKey(request.method(), path));
        if (exact != null) {
            return exact.handle(request);
        }

        boolean pathExists = routes.keySet().stream().anyMatch(key -> key.path().equals(path));
        if (pathExists) {
            return HttpResponse.text(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
        }
        return fallback.handle(request);
    }

    public Optional<Handler> find(HttpMethod method, String path) {
        return Optional.ofNullable(routes.get(new RouteKey(method, normalize(path))));
    }

    public static String normalize(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private record RouteKey(HttpMethod method, String path) {}
}
