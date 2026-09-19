package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileProcessException("Resource not found: " + fileName);
            }
            JsonNode root = objectMapper.readTree(inputStream);
            List<Measurement> measurements = new ArrayList<>();
            for (JsonNode node : root) {
                measurements.add(new Measurement(node.get("name").asText(), node.get("value").asDouble()));
            }
            return measurements;
        } catch (FileProcessException e) {
            throw e;
        } catch (Exception e) {
            throw new FileProcessException(e);
        }
    }
}
