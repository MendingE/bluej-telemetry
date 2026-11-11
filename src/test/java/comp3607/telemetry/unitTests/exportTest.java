package comp3607.telemetry.unitTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.export.JSONFormatStrategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportTest {

    @TempDir
    Path tempDir;

    @Test
    void testJSONExportCreatesValidFileWithMetadata() throws Exception {
        List<eventTelemetry> mockEvents = new ArrayList<>();
        mockEvents.add(new eventTelemetry(eventTypes.COMPILATION_SUCCEEDED, "Compile OK"));
        mockEvents.add(new eventTelemetry(eventTypes.COMPILATION_FAILED, "Syntax Error"));

        JSONFormatStrategy strategy = new JSONFormatStrategy();
        String filePath = tempDir.resolve("test_telemetry.json").toString();

        strategy.generateFile(mockEvents, filePath);

        File generatedFile = new File(filePath);
        assertTrue(generatedFile.exists(), "The JSON file should be created");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(generatedFile);

        assertTrue(rootNode.has("sessionMetadata"), "Root should contain sessionMetadata");
        JsonNode metadata = rootNode.get("sessionMetadata");

        assertEquals("816000000", metadata.get("studentId").asText());
        assertEquals(2, metadata.get("totalCompileAttempts").asInt());

        assertTrue(rootNode.has("events"), "Root should contain events array");
        JsonNode eventsArray = rootNode.get("events");

        assertEquals(2, eventsArray.size(), "There should be exactly 2 events");
        assertEquals("COMPILATION_SUCCEEDED", eventsArray.get(0).get("type").asText());
        assertEquals("Compile OK", eventsArray.get(0).get("details").asText());
    }

    @Test
    void testJSONExportHandlesEmptyEventsList() throws Exception {
        List<eventTelemetry> emptyEvents = new ArrayList<>();

        JSONFormatStrategy strategy = new JSONFormatStrategy();
        String filePath = tempDir.resolve("empty_telemetry.json").toString();

        strategy.generateFile(emptyEvents, filePath);

        File generatedFile = new File(filePath);
        assertTrue(generatedFile.exists());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(generatedFile);

        assertEquals(0, rootNode.get("sessionMetadata").get("totalCompileAttempts").asInt());
        assertEquals(0, rootNode.get("events").size());
    }
}