package comp3607.telemetry.export;

import comp3607.telemetry.events.eventTelemetry;
import java.util.List;

public class fileManager {
    private final fileFormatStrategy strategy;

    public fileManager(fileFormatStrategy strategy) {
        if (strategy == null)
            throw new IllegalArgumentException("strategy cannot be null");
        this.strategy = strategy;
    }

    public void save(List<eventTelemetry> events, String filePath) {
        strategy.generateFile(events, filePath);
    }
}