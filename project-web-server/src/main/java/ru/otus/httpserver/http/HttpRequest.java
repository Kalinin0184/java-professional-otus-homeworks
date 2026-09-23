package ru.otus.httpserver.http;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final String query;
    private final String version;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpRequest(
            HttpMethod method,
            String path,
            String query,
            String version,
            Map<String, String> headers,
            byte[] body) {
        this.method = method;
        this.path = path;
        this.query = query == null ? "" : query;
        this.version = version;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.body = body == null ? new byte[0] : body.clone();
    }

    public HttpMethod method() {
        return method;
    }

    public String path() {
        return path;
    }

    public String query() {
        return query;
    }

    public String version() {
        return version;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Optional<String> header(String name) {
        return Optional.ofNullable(headers.get(name.toLowerCase(Locale.ROOT)));
    }

    public byte[] body() {
        return body.clone();
    }

    public String bodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
