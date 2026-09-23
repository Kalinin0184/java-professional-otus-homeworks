package ru.otus.httpserver.routing;

import ru.otus.httpserver.http.HttpRequest;
import ru.otus.httpserver.http.HttpResponse;

@FunctionalInterface
public interface Handler {
    HttpResponse handle(HttpRequest request) throws Exception;
}
