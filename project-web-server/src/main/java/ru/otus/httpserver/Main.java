package ru.otus.httpserver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import ru.otus.httpserver.handlers.EchoHandler;
import ru.otus.httpserver.handlers.HelloHandler;
import ru.otus.httpserver.handlers.StaticFileHandler;
import ru.otus.httpserver.routing.Router;

public final class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        Path wwwRoot = resolveWwwRoot();

        Router router = new Router();
        StaticFileHandler staticHandler = new StaticFileHandler(wwwRoot);
        router.get("/api/hello", new HelloHandler())
                .post("/api/echo", new EchoHandler())
                .fallback(staticHandler);

        HttpServerConfig config = new HttpServerConfig(port, 16, wwwRoot);
        HttpServer server = new HttpServer(config, router);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(server::close, "http-shutdown"));
        log.info(() -> "Serving static files from " + wwwRoot.toAbsolutePath());
        log.info(() -> "Open http://localhost:" + server.port());
    }

    static Path resolveWwwRoot() {
        Path[] candidates = {
            Path.of("src", "main", "resources", "www"),
            Path.of("project-web-server", "src", "main", "resources", "www"),
            Path.of("www")
        };
        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }
        return Path.of("src", "main", "resources", "www").toAbsolutePath().normalize();
    }
}
