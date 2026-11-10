package comp3607.telemetry;

import comp3607.telemetry.dataCollection.storeTelemetryData;
import comp3607.telemetry.events.eventManager;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.export.fileManager;
import comp3607.telemetry.export.JSONFormatStrategy;
import comp3607.telemetry.reporting.reportStrategyPDF;

public class telemetryPlugin {
    private eventManager manager;
    private storeTelemetryData store;

    public telemetryPlugin() {
        manager = new eventManager();
        store = new storeTelemetryData();
        
       
        manager.addObserver(store);
    }

    public void recordEvent(eventTypes type, String details) {
        var event = new eventTelemetry(type, details);
        manager.notifyObservers(event);
    }

    public void exportFiles(String JSONPath, String PDFPath) {
        var allEvents = store.getAllEvents();
        
        
        var JSONManager = new fileManager(new JSONFormatStrategy());
        JSONManager.save(allEvents, jsonPath);

        
        var PDFManager = new fileManager(new reportStrategyPDF());
        PDFManager.save(allEvents, pdfPath);
    }
}