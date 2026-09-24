package ru.otus.httpserver.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public final class HttpResponseWriter {
    public void write(OutputStream outputStream, HttpResponse response) throws IOException {
        StringBuilder head = new StringBuilder();
        head.append("HTTP/1.1 ")
                .append(response.status().code())
                .append(' ')
                .append(response.status().reason())
                .append("\r\n");

        for (Map.Entry<String, String> header : response.headers().entrySet()) {
            String name = capitalize(header.getKey());
            head.append(name).append(": ").append(header.getValue()).append("\r\n");
        }
        head.append("\r\n");

        outputStream.write(head.toString().getBytes(StandardCharsets.US_ASCII));
        byte[] body = response.body();
        if (body.length > 0) {
            outputStream.write(body);
        }
        outputStream.flush();
    }

    private static String capitalize(String name) {
        String[] parts = name.toLowerCase(Locale.ROOT).split("-");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append('-');
            }
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    result.append(parts[i].substring(1));
                }
            }
        }
        return result.toString();
    }
}
