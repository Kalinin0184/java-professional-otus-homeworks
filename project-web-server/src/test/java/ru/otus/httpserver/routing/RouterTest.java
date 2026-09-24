package ru.otus.httpserver.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import ru.otus.httpserver.http.HttpMethod;
import ru.otus.httpserver.http.HttpRequest;
import ru.otus.httpserver.http.HttpResponse;
import ru.otus.httpserver.http.HttpStatus;

class RouterTest {
    @Test
    void normalizesPaths() {
        assertThat(Router.normalize("")).isEqualTo("/");
        assertThat(Router.normalize("/api/hello/")).isEqualTo("/api/hello");
        assertThat(Router.normalize("api/hello")).isEqualTo("/api/hello");
    }

    @Test
    void dispatchesExactRoute() throws Exception {
        Router router = new Router().get("/api/hello", request -> HttpResponse.text(HttpStatus.OK, "hi"));

        HttpResponse response = router.dispatch(request(HttpMethod.GET, "/api/hello"));

        assertThat(response.status()).isEqualTo(HttpStatus.OK);
        assertThat(new String(response.body())).isEqualTo("hi");
    }

    @Test
    void returnsMethodNotAllowedWhenPathExistsForOtherMethod() throws Exception {
        Router router = new Router().get("/api/hello", request -> HttpResponse.text(HttpStatus.OK, "hi"));

        HttpResponse response = router.dispatch(request(HttpMethod.POST, "/api/hello"));

        assertThat(response.status()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void usesFallbackForUnknownPath() throws Exception {
        Router router = new Router()
                .fallback(request -> HttpResponse.text(HttpStatus.NOT_FOUND, "missing"));

        HttpResponse response = router.dispatch(request(HttpMethod.GET, "/nope"));

        assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(new String(response.body())).isEqualTo("missing");
    }

    private static HttpRequest request(HttpMethod method, String path) {
        return new HttpRequest(method, path, "", "HTTP/1.1", Map.of(), new byte[0]);
    }
}
