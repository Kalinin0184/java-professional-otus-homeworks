package ru.otus.httpserver.http;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class HttpResponse {
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpResponse(HttpStatus status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static Builder status(HttpStatus status) {
        return new Builder(status);
    }

    public static HttpResponse ok(byte[] body, String contentType) {
        return status(HttpStatus.OK).contentType(contentType).body(body).build();
    }

    public static HttpResponse text(HttpStatus status, String text) {
        return status(status).contentType("text/plain; charset=utf-8").body(text).build();
    }

    public static HttpResponse json(HttpStatus status, String json) {
        return status(status).contentType("application/json; charset=utf-8").body(json).build();
    }

    public HttpStatus status() {
        return status;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public byte[] body() {
        return body.clone();
    }

    public static final class Builder {
        private final HttpStatus status;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private byte[] body = new byte[0];

        private Builder(HttpStatus status) {
            this.status = status;
        }

        public Builder header(String name, String value) {
            headers.put(name.toLowerCase(Locale.ROOT), value);
            return this;
        }

        public Builder contentType(String contentType) {
            return header("Content-Type", contentType);
        }

        public Builder body(byte[] body) {
            this.body = body == null ? new byte[0] : body.clone();
            return this;
        }

        public Builder body(String body) {
            return body(body.getBytes(StandardCharsets.UTF_8));
        }

        public HttpResponse build() {
            Map<String, String> result = new LinkedHashMap<>(headers);
            result.putIfAbsent("content-length", String.valueOf(body.length));
            result.putIfAbsent("connection", "close");
            return new HttpResponse(status, result, body);
        }
    }
}
