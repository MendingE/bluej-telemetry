package comp3607.telemetry.reporting;

import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;

import java.util.List;

public class ActivityTimeCalculator {

    private final List<eventTelemetry> events;
    private static final long IDLE_THRESHOLD_MS = 60000;

    public ActivityTimeCalculator(List<eventTelemetry> events) {
        this.events = events;
    }

    // https://www.w3schools.com/java/java_data_types.asp
    public long calculateActiveTimeMs() {
        long activeTime = 0;
        long activeStartTime = -1;
        boolean isActive = false;

        for (eventTelemetry event : events) {

            if (event.getType().equals(eventTypes.EDITOR_TYPED.name()) && isActive == false) {
                activeStartTime = event.getTimestampMs();
                isActive = true;
            }

            if (event.getType().equals(eventTypes.EDITOR_IDLE.name()) && isActive == true) {
                activeTime += Math.max(0, event.getTimestampMs() - activeStartTime - IDLE_THRESHOLD_MS);
                isActive = false;
                activeStartTime = -1;
            }
        }

        if (isActive && !events.isEmpty()) {
            activeTime += Math.max(0,
                    events.get(events.size() - 1).getTimestampMs() - activeStartTime - IDLE_THRESHOLD_MS);
        }

        return activeTime;
    }

}
