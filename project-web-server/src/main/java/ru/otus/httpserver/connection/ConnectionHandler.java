package ru.otus.httpserver.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.otus.httpserver.HttpServerConfig;
import ru.otus.httpserver.http.BadRequestException;
import ru.otus.httpserver.http.ClientDisconnectedException;
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
    private final HttpServerConfig config;
    private final HttpRequestParser parser = new HttpRequestParser();
    private final HttpResponseWriter writer = new HttpResponseWriter();

    public ConnectionHandler(Socket socket, Router router, HttpServerConfig config) {
        this.socket = socket;
        this.router = router;
        this.config = config;
    }

    @Override
    public void run() {
        try (socket) {
            BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
            OutputStream output = new BufferedOutputStream(socket.getOutputStream());
            int handled = 0;

            while (handled < config.maxRequestsPerConnection()) {
                long started = System.nanoTime();
                try {
                    HttpRequest request = parser.parse(input);
                    HttpResponse dispatched = router.dispatch(request);
                    boolean keepAlive = request.wantsKeepAlive()
                            && handled + 1 < config.maxRequestsPerConnection();
                    HttpResponse response = applyConnectionHeaders(dispatched, keepAlive);
                    writer.write(output, response);

                    long elapsedMs = (System.nanoTime() - started) / 1_000_000L;
                    handled++;
                    int statusCode = response.status().code();
                    String method = request.method().name();
                    String path = request.path();
                    String conn = keepAlive ? "keep-alive" : "close";
                    log.info(() -> method + " " + path
                            + " " + statusCode
                            + " " + elapsedMs + "ms"
                            + " conn=" + conn
                            + " from=" + socket.getRemoteSocketAddress());

                    if (!keepAlive) {
                        break;
                    }
                } catch (ClientDisconnectedException ex) {
                    break;
                } catch (SocketTimeoutException ex) {
                    log.fine(() -> "Idle timeout from " + socket.getRemoteSocketAddress());
                    break;
                } catch (BadRequestException ex) {
                    log.log(Level.WARNING, "Bad request: {0}", ex.getMessage());
                    writer.write(output, applyConnectionHeaders(
                            HttpResponse.text(HttpStatus.BAD_REQUEST, "Bad Request"), false));
                    break;
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Request handling failed", ex);
                    writer.write(output, applyConnectionHeaders(
                            HttpResponse.text(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
                            false));
                    break;
                }
            }
        } catch (IOException ex) {
            log.log(Level.FINE, "Connection closed", ex);
        }
    }

    private HttpResponse applyConnectionHeaders(HttpResponse response, boolean keepAlive) {
        HttpResponse result = response.withHeader("Connection", keepAlive ? "keep-alive" : "close");
        if (keepAlive) {
            int timeoutSec = Math.max(1, config.soTimeoutMs() / 1000);
            result = result.withHeader(
                    "Keep-Alive",
                    "timeout=" + timeoutSec + ", max=" + config.maxRequestsPerConnection());
        }
        return result;
    }
}
