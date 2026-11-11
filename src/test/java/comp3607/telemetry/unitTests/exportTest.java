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
        assertTrue(generatedFile.exists());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(generatedFile);

        assertTrue(root.has("sessionMetadata"));
        JsonNode metadata = root.get("sessionMetadata");
        assertEquals(JSONFormatStrategy.STUDENT_ID_PLACEHOLDER,
                metadata.get("studentId").asText());
        assertEquals(2, metadata.get("totalCompileAttempts").asInt());
        assertEquals(0.5, metadata.get("compileSuccessRate").asDouble(), 0.001);
        assertTrue(metadata.has("totalActiveTimeSec"));
        assertTrue(metadata.has("totalIdleTimeSec"));

        assertTrue(root.has("events"));
        JsonNode eventsArray = root.get("events");
        assertEquals(2, eventsArray.size());
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
        JsonNode root = mapper.readTree(generatedFile);
        assertEquals(0, root.get("sessionMetadata").get("totalCompileAttempts").asInt());
        assertEquals(0, root.get("events").size());
    }
}