
package comp3607.telemetry.unitTests;

import comp3607.telemetry.reporting.ActivityTimeCalculator;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityTimeCalculatorTest {

    @Test
    void testEmptyEvents() {
        List<eventTelemetry> events = new ArrayList<>();

        ActivityTimeCalculator calculator = new ActivityTimeCalculator(events);

        assertEquals(0, calculator.calculateActiveTimeMs());
    }

    @Test
    void testNoTypingEvents() {
        List<eventTelemetry> events = new ArrayList<>();

        events.add(new eventTelemetry(
                eventTypes.COMPILATION_SUCCEEDED, "Compilation successful"));

        ActivityTimeCalculator calculator = new ActivityTimeCalculator(events);

        assertEquals(0, calculator.calculateActiveTimeMs());
    }

    @Test
    void testTypingWithoutIdleEvent() throws InterruptedException {
        List<eventTelemetry> events = new ArrayList<>();

        events.add(new eventTelemetry(
                eventTypes.EDITOR_TYPED, "User typed"));

        Thread.sleep(100);

        events.add(new eventTelemetry(
                eventTypes.COMPILATION_SUCCEEDED, "Compilation successful"));

        ActivityTimeCalculator calculator = new ActivityTimeCalculator(events);

        assertEquals(0, calculator.calculateActiveTimeMs());
    }

    @Test
    void testTypingFollowedByIdle() {
        List<eventTelemetry> events = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        eventTelemetry typed = new eventTelemetry(
                eventTypes.EDITOR_TYPED, "User typed");

        eventTelemetry idle = new eventTelemetry(
                eventTypes.EDITOR_IDLE, "User inactive");

        events.add(typed);
        events.add(idle);

        ActivityTimeCalculator calculator = new ActivityTimeCalculator(events);

        assertEquals(0, calculator.calculateActiveTimeMs());
    }
}
