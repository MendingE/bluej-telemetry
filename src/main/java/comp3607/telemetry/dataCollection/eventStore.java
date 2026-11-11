package comp3607.telemetry.dataCollection;

import comp3607.telemetry.events.eventTelemetry;
import java.util.List;
import java.util.function.Consumer;

public interface eventStore extends Consumer<eventTelemetry> {
    List<eventTelemetry> getAllEvents();

    void clear();
}