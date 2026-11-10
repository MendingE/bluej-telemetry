package comp3607.telemetry;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;
import comp3607.telemetry.dataCollection.storeTelemetryData;
import comp3607.telemetry.events.eventManager;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.export.fileManager;
import comp3607.telemetry.export.JSONFormatStrategy;
import comp3607.telemetry.reporting.reportStrategyPDF;

public class telemetryPlugin extends Extension implements CompileListener {
    private eventManager manager;
    private storeTelemetryData store;
    private final SessionSummary session;

    public void startup(BlueJ bluej) {
        plugin = new telemetryPlugin();
        bluej.addCompileListener(this);
    }

    public telemetryPlugin(eventManager manager, storeTelemetryData store) {
        this.manager = manager;
        this.store = store;
        this.session = new SessionSummary();
        manager.addObserver(store);
    }

    public void recordEvent(telemetryPlugin event) {
        session.observe(event);
        manager.notifyObservers(event);
    }

    public void exportJSON(Path path) {
        var events = store.getAllEvents();
        var payload = new SessionPayload(session.metadata(), events);
        new fileManager(new JSONFormatStrategy()).save(payload, path.toString());
    }

    public void exportPDF(Path path) {
        new fileManager(new reportStrategyPDF()).save(store.getAllEvents(), path.toString());
    }

    public void exportFiles(String JSONPath, String PDFPath) {
        exportJSON(JSONPath);
        exportPDF(PDFPath);
    }
}