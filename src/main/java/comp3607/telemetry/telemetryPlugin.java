package comp3607.telemetry;

import comp3607.telemetry.dataCollection.storeTelemetryData;
import comp3607.telemetry.events.eventManager;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.export.fileManager;
import comp3607.telemetry.export.JSONFormatStrategy;
import comp3607.telemetry.reporting.reportStrategyPDF;

public class telemetryPlugin {
    private final eventManager manage;
    private final storeTelemetryData store;

    public telemetryPlugin() {
        this.manage = new eventManager();
        this.store = new storeTelemetryData();
        
        manage.addObserver(store);
    }

    
    public void recordEvent(eventTypes type, String details) {
        var event = new eventTelemetry(type, details);
        manage.notifyObservers(event);
    }

  
    public void exportFiles(String jsonPath, String pdfPath) {
        var allEvents = store.getAllEvents();
        
        new fileManager(new JSONFormatStrategy()).save(allEvents, jsonPath);
        new fileManager(new reportStrategyPDF()).save(allEvents, pdfPath);
    }
}