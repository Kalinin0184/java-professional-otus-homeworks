package ru.otus.httpserver.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class HttpRequestParser {
    private static final int MAX_HEADER_SIZE = 64 * 1024;
    private static final int MAX_BODY_SIZE = 10 * 1024 * 1024;

    public HttpRequest parse(InputStream inputStream) throws IOException {
        String requestLine = readLine(inputStream);
        if (requestLine == null || requestLine.isEmpty()) {
            throw new BadRequestException("Empty request line");
        }

        String[] parts = requestLine.split(" ", 3);
        if (parts.length != 3) {
            throw new BadRequestException("Invalid request line: " + requestLine);
        }

        HttpMethod method;
        try {
            method = HttpMethod.from(parts[0]);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage(), ex);
        }

        String target = parts[1];
        String version = parts[2];
        if (!version.startsWith("HTTP/1.")) {
            throw new BadRequestException("Unsupported HTTP version: " + version);
        }

        String path;
        String query;
        int queryIndex = target.indexOf('?');
        if (queryIndex >= 0) {
            path = target.substring(0, queryIndex);
            query = target.substring(queryIndex + 1);
        } else {
            path = target;
            query = "";
        }
        if (path.isEmpty()) {
            throw new BadRequestException("Empty path");
        }

        Map<String, String> headers = readHeaders(inputStream);
        byte[] body = readBody(inputStream, headers);
        return new HttpRequest(method, path, query, version, headers, body);
    }

    private Map<String, String> readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        int total = 0;
        while (true) {
            String line = readLine(inputStream);
            if (line == null) {
                throw new BadRequestException("Unexpected end of headers");
            }
            if (line.isEmpty()) {
                break;
            }
            total += line.length() + 2;
            if (total > MAX_HEADER_SIZE) {
                throw new BadRequestException("Headers too large");
            }
            int colon = line.indexOf(':');
            if (colon <= 0) {
                throw new BadRequestException("Invalid header: " + line);
            }
            String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            String value = line.substring(colon + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private byte[] readBody(InputStream inputStream, Map<String, String> headers) throws IOException {
        String contentLengthHeader = headers.get("content-length");
        if (contentLengthHeader == null || contentLengthHeader.isBlank()) {
            return new byte[0];
        }
        int contentLength;
        try {
            contentLength = Integer.parseInt(contentLengthHeader.trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid Content-Length", ex);
        }
        if (contentLength < 0) {
            throw new BadRequestException("Negative Content-Length");
        }
        if (contentLength > MAX_BODY_SIZE) {
            throw new BadRequestException("Body too large");
        }
        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new BadRequestException("Unexpected end of body");
        }
        return body;
    }

    private String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int previous = -1;
        while (true) {
            int current = inputStream.read();
            if (current == -1) {
                if (buffer.size() == 0) {
                    return null;
                }
                break;
            }
            if (previous == '\r' && current == '\n') {
                break;
            }
            if (previous != -1) {
                buffer.write(previous);
                if (buffer.size() > MAX_HEADER_SIZE) {
                    throw new BadRequestException("Line too long");
                }
            }
            previous = current;
        }
        if (previous != -1 && previous != '\r') {
            buffer.write(previous);
        }
        return buffer.toString(StandardCharsets.US_ASCII);
    }
}
