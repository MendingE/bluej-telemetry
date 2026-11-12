package comp3607.telemetry.reporting;

import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import java.util.*;

public class ReportAggregator {
    private final List<eventTelemetry> events;
    private final ActivityTimeCalculator activityCalculator;

    public ReportAggregator(List<eventTelemetry> events) {
        this.events = events == null ? List.of() : events;
        this.activityCalculator = new ActivityTimeCalculator(this.events);
    }

    public long calculateActiveTimeMs() {
        return activityCalculator.calculateActiveTimeMs();
    }

    public long calculateTotalSessionTimeMs() {
        if (events.size() < 2)
            return 0;
        return events.get(events.size() - 1).getTimestampMs() - events.get(0).getTimestampMs();
    }

    public long getCompileSuccessCount() {
        return events.stream()
                .filter(e -> e.getTypeEnum() == eventTypes.COMPILATION_SUCCEEDED)
                .count();
    }

    public long getCompileFailureCount() {
        return events.stream()
                .filter(e -> e.getTypeEnum() == eventTypes.COMPILATION_FAILED)
                .count();
    }

    public double getCompileSuccessRate() {
        long total = getCompileSuccessCount() + getCompileFailureCount();
        if (total == 0)
            return 0.0;
        return (double) getCompileSuccessCount() / total;
    }

    public Map<String, Integer> getCompileErrorFrequency() {
        Map<String, Integer> frequency = new LinkedHashMap<>();
        for (eventTelemetry e : events) {
            if (e.getTypeEnum() == eventTypes.COMPILATION_FAILED) {
                String error = e.getDetails();
                frequency.put(error, frequency.getOrDefault(error, 0) + 1);
            }
        }
        return frequency;
    }

    public long getClassCreationCount() {
        return events.stream()
                .filter(e -> e.getTypeEnum() == eventTypes.CLASS_CREATED)
                .count();
    }

    public List<String> getClassCreationTimeline() {
        List<String> timeline = new ArrayList<>();
        for (eventTelemetry e : events) {
            if (e.getTypeEnum() == eventTypes.CLASS_CREATED) {
                timeline.add(e.getTimestampStr() + " — " + e.getDetails());
            }
        }
        return timeline;
    }

    public long calculateIdleTimeMs() {
        long total = calculateTotalSessionTimeMs();
        long active = calculateActiveTimeMs();
        return Math.max(0, total - active);
    }
}