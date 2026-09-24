package ru.otus.httpserver.http;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    OPTIONS,
    PATCH;

    public static HttpMethod from(String raw) {
        try {
            return HttpMethod.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + raw, ex);
        }
    }
}
