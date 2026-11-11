package comp3607.telemetry.dataCollection;

import comp3607.telemetry.events.eventTelemetry;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class storeTelemetryData implements eventStore {
    private final List<eventTelemetry> savedEvents = new CopyOnWriteArrayList<>();

    @Override
    public void accept(eventTelemetry event) {
        if (event != null) {
            savedEvents.add(event);
        }
    }

    public List<eventTelemetry> getAllEvents() {
        return List.copyOf(savedEvents);
    }

    public void clear() {
        savedEvents.clear();
    }
}