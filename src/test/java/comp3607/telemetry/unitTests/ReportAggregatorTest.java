package comp3607.telemetry.unitTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.reporting.ReportAggregator;

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
        ReportAggregator agg = new ReportAggregator(new ArrayList<>());
        assertEquals(0.0, agg.getCompileSuccessRate(), 0.001);
    }

    @Test
    void testActiveTimeCalculationDeterministic() {
        List<eventTelemetry> events = new ArrayList<>();
        events.add(new eventTelemetry(eventTypes.SESSION_STARTED, "Start", 1_000L));
        events.add(new eventTelemetry(eventTypes.EDITOR_TYPED, "typed", 2_000L));
        events.add(new eventTelemetry(eventTypes.EDITOR_TYPED, "typed", 3_000L));
        events.add(new eventTelemetry(eventTypes.SESSION_ENDED, "End", 4_000L));

        ReportAggregator agg = new ReportAggregator(events);

        assertEquals(3_000, agg.calculateTotalSessionTimeMs());
        assertEquals(1_000, agg.calculateActiveTimeMs());
        assertEquals(2_000, agg.calculateIdleTimeMs());
    }

    @Test
    void testActiveTimeWithIdleReset() {
        List<eventTelemetry> events = new ArrayList<>();
        events.add(new eventTelemetry(eventTypes.EDITOR_TYPED, "t1", 1_000L));
        events.add(new eventTelemetry(eventTypes.EDITOR_TYPED, "t2", 2_000L));
        events.add(new eventTelemetry(eventTypes.EDITOR_IDLE, "idle", 3_000L));
        events.add(new eventTelemetry(eventTypes.EDITOR_TYPED, "t3", 10_000L));
        events.add(new eventTelemetry(eventTypes.EDITOR_TYPED, "t4", 11_000L));

        ReportAggregator agg = new ReportAggregator(events);
        assertEquals(2_000, agg.calculateActiveTimeMs());
    }

    @Test

    void testClassCreationCountAndTimeline() {
        List<eventTelemetry> events = new ArrayList<>();
        events.add(new eventTelemetry(eventTypes.CLASS_CREATED, "Class A", 1_000L));
        events.add(new eventTelemetry(eventTypes.CLASS_CREATED, "Class B", 2_000L));
        events.add(new eventTelemetry(eventTypes.COMPILATION_SUCCEEDED, "Success", 3_000L));

        ReportAggregator agg = new ReportAggregator(events);
        assertEquals(2, agg.getClassCreationCount());

        List<String> timeline = agg.getClassCreationTimeline();
        assertEquals(2, timeline.size());
        assertTrue(timeline.get(0).contains("Class A"));
        assertTrue(timeline.get(1).contains("Class B"));
    }
}