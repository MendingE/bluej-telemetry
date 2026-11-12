package comp3607.telemetry.unitTests;

import comp3607.telemetry.dataCollection.storeTelemetryData;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.export.fileFormatStrategy;
import comp3607.telemetry.telemetryPlugin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class telemetryPluginTest {

    @Test
    void testRecordEventStoresEvent() {
        storeTelemetryData store = new storeTelemetryData();
        telemetryPlugin plugin = new telemetryPlugin(
                (events, path) -> {
                }, (events, path) -> {
                }, store);

        plugin.recordEvent(eventTypes.PROJECT_CREATED, "P1");

        List<eventTelemetry> all = store.getAllEvents();
        assertEquals(1, all.size());
        assertEquals("PROJECT_CREATED", all.get(0).getType());
    }

    @Test
    void testExportCallsBothStrategies() {
        List<String> calls = new ArrayList<>();
        fileFormatStrategy json = (events, path) -> calls.add("JSON:" + path);
        fileFormatStrategy pdf = (events, path) -> calls.add("PDF:" + path);

        telemetryPlugin plugin = new telemetryPlugin(json, pdf, new storeTelemetryData());
        plugin.recordEvent(eventTypes.SESSION_STARTED, "start");
        plugin.exportFiles("a.json", "b.pdf");

        assertEquals(2, calls.size());
        assertTrue(calls.contains("JSON:a.json"));
        assertTrue(calls.contains("PDF:b.pdf"));
    }

    @Test
    void testSessionStartAndEndEmitted() {
        storeTelemetryData store = new storeTelemetryData();
        telemetryPlugin plugin = new telemetryPlugin(
                (e, p) -> {
                }, (e, p) -> {
                }, store);

        plugin.startSession();
        plugin.endSession();

        List<eventTelemetry> all = store.getAllEvents();
        assertEquals(2, all.size());
        assertEquals("SESSION_STARTED", all.get(0).getType());
        assertEquals("SESSION_ENDED", all.get(1).getType());
    }
}