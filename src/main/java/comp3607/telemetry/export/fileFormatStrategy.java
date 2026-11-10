package comp3607.telemetry.export;

import comp3607.telemetry.events.eventTelemetry;
import java.util.List;

public interface fileFormatStrategy {
    void generateFile(List<eventTelemetry> events, String filePath);
}