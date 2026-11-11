package comp3607.telemetry.reporting;

import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportAggregator {
    private final List<eventTelemetry> events;
    private final ActivityTimeCalculator activityCalculator; // js

    public ReportAggregator(List<eventTelemetry> events) {
        this.events = events;
        this.activityCalculator = new ActivityTimeCalculator(events); // js
    }

    public long calculateActiveTimeMs() { // js
        return activityCalculator.calculateActiveTimeMs();
    }

    public long calculateTotalSessionTimeMs() {
        if (events.size() < 2)
            return 0;
        return events.get(events.size() - 1).getTimestampMs() - events.get(0).getTimestampMs();
    }

    public long getCompileSuccessCount() {
        return events.stream().filter(e -> e.getType().equals(eventTypes.COMPILATION_SUCCEEDED.name())).count();
    }

    public long getCompileFailureCount() {
        return events.stream().filter(e -> e.getType().equals(eventTypes.COMPILATION_FAILED.name())).count();
    }

    public double getCompileSuccessRate() {
        long total = getCompileSuccessCount() + getCompileFailureCount();
        if (total == 0)
            return 0.0;
        return (double) getCompileSuccessCount() / total;
    }

    public Map<String, Integer> getCompileErrorFrequency() {
        Map<String, Integer> frequency = new HashMap<>();
        for (eventTelemetry e : events) {
            if (e.getType().equals(eventTypes.COMPILATION_FAILED.name())) {
                String error = e.getDetails();
                frequency.put(error, frequency.getOrDefault(error, 0) + 1);
            }
        }
        return frequency;
    }

    public long getClassCreationCount() {
        return events.stream().filter(e -> e.getType().equals(eventTypes.CLASS_CREATED.name())).count();
    }

}