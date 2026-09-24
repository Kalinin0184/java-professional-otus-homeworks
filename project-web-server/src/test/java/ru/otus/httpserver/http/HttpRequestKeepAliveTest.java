package ru.otus.httpserver.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpRequestKeepAliveTest {
    @Test
    void http11DefaultsToKeepAlive() {
        HttpRequest request = new HttpRequest(
                HttpMethod.GET, "/", "", "HTTP/1.1", Map.of(), new byte[0]);
        assertThat(request.wantsKeepAlive()).isTrue();
    }

    @Test
    void http11HonorsConnectionClose() {
        HttpRequest request = new HttpRequest(
                HttpMethod.GET, "/", "", "HTTP/1.1", Map.of("connection", "close"), new byte[0]);
        assertThat(request.wantsKeepAlive()).isFalse();
    }

    @Test
    void http10RequiresKeepAliveHeader() {
        HttpRequest without = new HttpRequest(
                HttpMethod.GET, "/", "", "HTTP/1.0", Map.of(), new byte[0]);
        HttpRequest with = new HttpRequest(
                HttpMethod.GET, "/", "", "HTTP/1.0", Map.of("connection", "keep-alive"), new byte[0]);
        assertThat(without.wantsKeepAlive()).isFalse();
        assertThat(with.wantsKeepAlive()).isTrue();
    }
}
