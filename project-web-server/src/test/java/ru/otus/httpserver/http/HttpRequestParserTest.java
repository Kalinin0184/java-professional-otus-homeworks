package ru.otus.httpserver.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {
    private final HttpRequestParser parser = new HttpRequestParser();

    @Test
    void parsesGetWithHeadersAndQuery() throws Exception {
        String raw = ""
                + "GET /api/hello?x=1 HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "User-Agent: test\r\n"
                + "\r\n";

        HttpRequest request = parser.parse(new ByteArrayInputStream(raw.getBytes(StandardCharsets.US_ASCII)));

        assertThat(request.method()).isEqualTo(HttpMethod.GET);
        assertThat(request.path()).isEqualTo("/api/hello");
        assertThat(request.query()).isEqualTo("x=1");
        assertThat(request.version()).isEqualTo("HTTP/1.1");
        assertThat(request.header("host")).contains("localhost");
        assertThat(request.header("USER-AGENT")).contains("test");
        assertThat(request.body()).isEmpty();
    }

    @Test
    void parsesPostWithBody() throws Exception {
        String body = "hello";
        String raw = ""
                + "POST /api/echo HTTP/1.1\r\n"
                + "Content-Length: " + body.length() + "\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n"
                + body;

        HttpRequest request = parser.parse(new ByteArrayInputStream(raw.getBytes(StandardCharsets.US_ASCII)));

        assertThat(request.method()).isEqualTo(HttpMethod.POST);
        assertThat(request.path()).isEqualTo("/api/echo");
        assertThat(request.bodyAsString()).isEqualTo("hello");
    }

    @Test
    void rejectsBrokenRequestLine() {
        String raw = "GET /only-two-parts\r\n\r\n";
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(raw.getBytes(StandardCharsets.US_ASCII))))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectsInvalidContentLength() {
        String raw = ""
                + "POST /api/echo HTTP/1.1\r\n"
                + "Content-Length: abc\r\n"
                + "\r\n";
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(raw.getBytes(StandardCharsets.US_ASCII))))
                .isInstanceOf(BadRequestException.class);
    }
}
