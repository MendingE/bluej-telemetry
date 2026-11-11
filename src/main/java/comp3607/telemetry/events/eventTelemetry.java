package comp3607.telemetry.events;

public class eventTelemetry {
    private String type;
    private String details;
    private long timestampMs;
    private String timestampStr;

    public eventTelemetry(eventTypes type, String details) {
        this.type = type.name();
        this.details = details;
        this.timestampMs = System.currentTimeMillis();
        this.timestampStr = java.time.Instant.ofEpochMilli(timestampMs).toString();
    }

    public String getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }

    // These two methods fix the error!
    public long getTimestampMs() {
        return timestampMs;
    }

    public String getTimestampStr() {
        return timestampStr;
    }
}