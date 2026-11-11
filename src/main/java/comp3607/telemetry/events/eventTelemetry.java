package comp3607.telemetry.events;


import java.time.Instant;

public class eventTelemetry {
    private String type;
    private String details;
    private String timestamp;

    public eventTelemetry(eventTypes type, String details) {
        this.type = type.name();
        this.details = details;
        this.timestamp = Instant.now().toString();
    }

    public long getTimestampMs(){
        return Instant.parse(this.timestamp).toEpochMilli();
    }

    public String getType() { 
        return type;
     }

    public String getDetails() { 
        return details; 
    }

    public String getTimestamp() { 
        return timestamp;
     }
}

