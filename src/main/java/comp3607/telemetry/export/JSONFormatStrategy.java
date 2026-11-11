package comp3607.telemetry.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.reporting.ReportAggregator;

import java.io.File;
import java.util.List;

public class JSONFormatStrategy implements fileFormatStrategy {

    public static final String STUDENT_ID_PLACEHOLDER = "816000000";

    @Override
    public void generateFile(List<eventTelemetry> events, String filePath) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            ReportAggregator agg = new ReportAggregator(events);

            ObjectNode root = mapper.createObjectNode();

            ObjectNode metadata = mapper.createObjectNode();
            metadata.put("studentId", STUDENT_ID_PLACEHOLDER);
            metadata.put("totalSessionDurationSec", agg.calculateTotalSessionTimeMs() / 1000);
            metadata.put("totalActiveTimeSec", agg.calculateActiveTimeMs() / 1000);
            metadata.put("totalIdleTimeSec", agg.calculateIdleTimeMs() / 1000);
            metadata.put("totalCompileAttempts",
                    agg.getCompileSuccessCount() + agg.getCompileFailureCount());
            metadata.put("compileSuccessRate", agg.getCompileSuccessRate());
            metadata.put("totalClassesCreated", agg.getClassCreationCount());
            root.set("sessionMetadata", metadata);

            JsonNode eventsNode = mapper.valueToTree(events);
            root.set("events", eventsNode);

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), root);
            System.out.println("JSON saved successfully to " + filePath);
        } catch (Exception e) {
            System.out.println("Error saving JSON: " + e.getMessage());
        }
    }
}