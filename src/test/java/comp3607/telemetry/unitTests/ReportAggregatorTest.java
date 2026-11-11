package comp3607.telemetry.unitTests; // Make sure this matches your folder path

import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.reporting.ReportAggregator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportAggregatorTest {

    @Test
    void testCompileSuccessRateAndFrequency() {
        List<eventTelemetry> events = new ArrayList<>();
        events.add(new eventTelemetry(eventTypes.COMPILATION_SUCCEEDED, "Success"));
        events.add(new eventTelemetry(eventTypes.COMPILATION_SUCCEEDED, "Success"));
        events.add(new eventTelemetry(eventTypes.COMPILATION_FAILED, "Syntax Error"));
        events.add(new eventTelemetry(eventTypes.COMPILATION_FAILED, "Syntax Error"));
        events.add(new eventTelemetry(eventTypes.COMPILATION_FAILED, "Missing Semicolon"));

        ReportAggregator agg = new ReportAggregator(events);

        assertEquals(2, agg.getCompileSuccessCount());
        assertEquals(3, agg.getCompileFailureCount());

        assertEquals(0.4, agg.getCompileSuccessRate(), 0.001);

        Map<String, Integer> errors = agg.getCompileErrorFrequency();
        assertEquals(2, errors.get("Syntax Error"));
        assertEquals(1, errors.get("Missing Semicolon"));
    }

    @Test
    void testCompileSuccessRateWithNoCompilations() {
        List<eventTelemetry> emptyEvents = new ArrayList<>();
        ReportAggregator agg = new ReportAggregator(emptyEvents);

        assertEquals(0.0, agg.getCompileSuccessRate(), 0.001);
    }

    @Test
    void testActiveTimeCalculation() throws InterruptedException {
        List<eventTelemetry> events = new ArrayList<>();

        events.add(new eventTelemetry(eventTypes.SESSION_STARTED, "Start"));

        Thread.sleep(50);

        events.add(new eventTelemetry(eventTypes.SESSION_ENDED, "End"));

        ReportAggregator agg = new ReportAggregator(events);

        assertTrue(agg.calculateTotalSessionTimeMs() >= 50, "Session time should be at least 50ms");
    }

    @Test
    void testClassCreationCount() {
        List<eventTelemetry> events = new ArrayList<>();
        events.add(new eventTelemetry(eventTypes.CLASS_CREATED, "Class A"));
        events.add(new eventTelemetry(eventTypes.CLASS_CREATED, "Class B"));
        events.add(new eventTelemetry(eventTypes.COMPILATION_SUCCEEDED, "Success"));

        ReportAggregator agg = new ReportAggregator(events);

        assertEquals(2, agg.getClassCreationCount());
    }
}