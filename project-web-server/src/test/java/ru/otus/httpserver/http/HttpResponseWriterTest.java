package ru.otus.httpserver.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseWriterTest {
    @Test
    void writesStatusHeadersAndBody() throws Exception {
        HttpResponse response = HttpResponse.json(HttpStatus.OK, "{\"ok\":true}");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        new HttpResponseWriter().write(out, response);

        String raw = out.toString(StandardCharsets.US_ASCII);
        assertThat(raw).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(raw).contains("Content-Type: application/json; charset=utf-8\r\n");
        assertThat(raw).contains("Content-Length: ");
        assertThat(raw).contains("Connection: close\r\n");
        assertThat(raw).endsWith("\r\n\r\n{\"ok\":true}");
    }
}
