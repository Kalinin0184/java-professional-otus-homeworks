package ru.otus.httpserver.handlers;

import ru.otus.httpserver.http.HttpRequest;
import ru.otus.httpserver.http.HttpResponse;
import ru.otus.httpserver.http.HttpStatus;
import ru.otus.httpserver.routing.Handler;

public final class HelloHandler implements Handler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.json(HttpStatus.OK, "{\"message\":\"Hello from custom HTTP server\"}");
    }
}
