package comp3607.telemetry.dataCollection;

import comp3607.telemetry.events.eventTelemetry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class storeTelemetryData implements Consumer<eventTelemetry> {
    private List<eventTelemetry> savedEvents = new ArrayList<>();

    @Override
    public void accept(eventTelemetry event) {
        savedEvents.add(event); 
    }

    public List<eventTelemetry> getAllEvents() { 
        return savedEvents; 
    }
    
    public void clear() { 
        savedEvents.clear(); 
    }
}