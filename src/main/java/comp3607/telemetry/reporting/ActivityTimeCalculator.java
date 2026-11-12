
package comp3607.telemetry.reporting;

import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;

import java.util.List;

public class ActivityTimeCalculator {

    private final List<eventTelemetry> events;

    public ActivityTimeCalculator(List<eventTelemetry> events) {
        this.events = events;
    }

    public long calculateActiveTimeMs() {
        long activeTime = 0;
        long lastActivityTS = -1;

        for (eventTelemetry event : events) {
            if (event.getTypeEnum() == eventTypes.EDITOR_TYPED) {
                if (lastActivityTS >= 0) {
                    activeTime += Math.min(
                            event.getTimestampMs() - lastActivityTS,
                            60_000);
                }
                
                lastActivityTS= event.getTimestampMs();
            } else if (event.getTypeEnum() == eventTypes.EDITOR_IDLE) {

                lastActivityTS = -1;
            }
        }

        return activeTime;
    }
}
