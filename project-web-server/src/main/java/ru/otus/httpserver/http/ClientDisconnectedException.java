package ru.otus.httpserver.http;

/** Client closed the TCP connection before sending the next request (normal for keep-alive). */
public final class ClientDisconnectedException extends RuntimeException {
    public ClientDisconnectedException() {
        super("Client disconnected");
    }
}
