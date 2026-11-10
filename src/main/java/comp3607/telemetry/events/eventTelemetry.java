package comp3607.telemetry.events;

public class eventTelemetry {
    private String type;
    private String details;
    private String timestamp;

    public eventTelemetry(eventTypes type, String details) {
        this.type = type.name();
        this.details = details;
        this.timestamp = "TODO: Add timestamp logic";
    }

    public String getType() { return type; }
    public String getDetails() { return details; }
    public String getTimestamp() { return timestamp; }
}

