package comp3607.telemetry;

import comp3607.telemetry.dataCollection.storeTelemetryData;
import comp3607.telemetry.events.eventManager;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.export.fileFormatStrategy;
import comp3607.telemetry.export.fileManager;

public class telemetryPlugin {
    private final eventManager manage;
    private final storeTelemetryData store;
    private final fileFormatStrategy jsonStrategy;
    private final fileFormatStrategy pdfStrategy;

    public telemetryPlugin(fileFormatStrategy jsonStrategy, fileFormatStrategy pdfStrategy) {
        this.manage = new eventManager();
        this.store = new storeTelemetryData();
        this.jsonStrategy = jsonStrategy;
        this.pdfStrategy = pdfStrategy;

        manage.addObserver(store);
    }

    public telemetryPlugin() {
        this(new comp3607.telemetry.export.JSONFormatStrategy(),
                new comp3607.telemetry.reporting.reportStrategyPDF());
    }

    public void recordEvent(eventTypes type, String details) {
        var event = new eventTelemetry(type, details);
        manage.notifyObservers(event);
    }

    public void exportFiles(String jsonPath, String pdfPath) {
        var allEvents = store.getAllEvents();

        new fileManager(jsonStrategy).save(allEvents, jsonPath);
        new fileManager(pdfStrategy).save(allEvents, pdfPath);
    }
}