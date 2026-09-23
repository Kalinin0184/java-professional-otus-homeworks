package ru.otus.httpserver.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.otus.httpserver.http.BadRequestException;
import ru.otus.httpserver.http.HttpRequest;
import ru.otus.httpserver.http.HttpRequestParser;
import ru.otus.httpserver.http.HttpResponse;
import ru.otus.httpserver.http.HttpResponseWriter;
import ru.otus.httpserver.http.HttpStatus;
import ru.otus.httpserver.routing.Router;

public final class ConnectionHandler implements Runnable {
    private static final Logger log = Logger.getLogger(ConnectionHandler.class.getName());

    private final Socket socket;
    private final Router router;
    private final HttpRequestParser parser = new HttpRequestParser();
    private final HttpResponseWriter writer = new HttpResponseWriter();

    public ConnectionHandler(Socket socket, Router router) {
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void run() {
        try (socket) {
            BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
            OutputStream output = new BufferedOutputStream(socket.getOutputStream());
            try {
                HttpRequest request = parser.parse(input);
                log.info(() -> request.method() + " " + request.path() + " from " + socket.getRemoteSocketAddress());
                HttpResponse response = router.dispatch(request);
                writer.write(output, response);
            } catch (BadRequestException ex) {
                log.log(Level.WARNING, "Bad request: {0}", ex.getMessage());
                writer.write(output, HttpResponse.text(HttpStatus.BAD_REQUEST, "Bad Request"));
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Request handling failed", ex);
                writer.write(output, HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
            }
        } catch (IOException ex) {
            log.log(Level.WARNING, "Connection failed", ex);
        }
    }
}
