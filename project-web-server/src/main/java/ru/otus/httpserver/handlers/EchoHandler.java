package ru.otus.httpserver.handlers;

import ru.otus.httpserver.http.HttpRequest;
import ru.otus.httpserver.http.HttpResponse;
import ru.otus.httpserver.http.HttpStatus;
import ru.otus.httpserver.routing.Handler;

public final class EchoHandler implements Handler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        String body = request.bodyAsString();
        String json = "{\"echo\":" + quote(body) + "}";
        return HttpResponse.json(HttpStatus.OK, json);
    }

    private static String quote(String value) {
        String escaped = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }
}
