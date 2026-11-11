package comp3607.telemetry.events;

public class eventTelemetry {
    private final eventTypes type;
    private final String details;
    private final long timestampMs;
    private final String timestampStr;

    public eventTelemetry(eventTypes type, String details) {
        this(type, details, System.currentTimeMillis());
    }

    public eventTelemetry(eventTypes type, String details, long timestampMs) {
        this.type = type;
        this.details = details;
        this.timestampMs = timestampMs;
        this.timestampStr = java.time.Instant.ofEpochMilli(timestampMs).toString();
    }

    public String getType() {
        return type.name();
    }

    public eventTypes getTypeEnum() {
        return type;
    }

    public String getDetails() {
        return details;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public String getTimestampStr() {
        return timestampStr;
    }
}