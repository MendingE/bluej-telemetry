package comp3607.telemetry;

import comp3607.telemetry.dataCollection.eventStore;
import comp3607.telemetry.dataCollection.storeTelemetryData;
import comp3607.telemetry.events.eventManager;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.export.JSONFormatStrategy;
import comp3607.telemetry.export.fileFormatStrategy;
import comp3607.telemetry.export.fileManager;
import comp3607.telemetry.reporting.reportStrategyPDF;

public class telemetryPlugin {
    private final eventManager manage;
    private final eventStore store;
    private final fileFormatStrategy jsonStrategy;
    private final fileFormatStrategy pdfStrategy;

    public telemetryPlugin(fileFormatStrategy jsonStrategy,
            fileFormatStrategy pdfStrategy,
            eventStore store) {
        this.manage = new eventManager();
        this.store = store;
        this.jsonStrategy = jsonStrategy;
        this.pdfStrategy = pdfStrategy;
        manage.addObserver(store);
    }

    public telemetryPlugin() {
        this(new JSONFormatStrategy(),
                new reportStrategyPDF(),
                new storeTelemetryData());
    }

    public void startSession() {
        recordEvent(eventTypes.SESSION_STARTED, "BlueJ telemetry session started");
    }

    public void endSession() {
        recordEvent(eventTypes.SESSION_ENDED, "BlueJ telemetry session ended");
    }

    public void recordEvent(eventTypes type, String details) {
        eventTelemetry event = new eventTelemetry(type, details);
        manage.notifyObservers(event);
    }

    public void exportFiles(String jsonPath, String pdfPath) {
        var allEvents = store.getAllEvents();
        new fileManager(jsonStrategy).save(allEvents, jsonPath);
        new fileManager(pdfStrategy).save(allEvents, pdfPath);
    }

    public void clearEvents() {
        store.clear();
        recordEvent(eventTypes.SESSION_STARTED, "Data cleared by user. New session started.");
    }

    public java.util.List<eventTelemetry> getAllEvents() {
        return store.getAllEvents();
    }
}