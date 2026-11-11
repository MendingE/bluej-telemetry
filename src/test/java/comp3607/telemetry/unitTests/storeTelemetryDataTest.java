package comp3607.telemetry.unitTests;

import comp3607.telemetry.dataCollection.storeTelemetryData;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class storeTelemetryDataTest {

    @Test
    void testTelemetryDataStorage() {
        storeTelemetryData storage = new storeTelemetryData();

        eventTelemetry event = new eventTelemetry(eventTypes.PROJECT_CREATED, "Test Project");

        storage.accept(event);
        storage.getAllEvents();

        assertEquals(1, storage.getAllEvents().size());
        assertSame(event, storage.getAllEvents().get(0));
    }

    @Test
    void testAcceptNullEvent() {
        storeTelemetryData storage = new storeTelemetryData();

        storage.accept(null);

        assertEquals(0, storage.getAllEvents().size());
    }

    @Test
    void testAcceptMultipleEvents() {
        storeTelemetryData storage = new storeTelemetryData();

        eventTelemetry event1 = new eventTelemetry(
                eventTypes.PROJECT_CREATED, "Project A");

        eventTelemetry event2 = new eventTelemetry(
                eventTypes.CLASS_CREATED, "Class A");

        eventTelemetry event3 = new eventTelemetry(
                eventTypes.EDITOR_TYPED, "User typed code");

        storage.accept(event1);
        storage.accept(event2);
        storage.accept(event3);

        assertEquals(3, storage.getAllEvents().size());
    }

    @Test
    void testGetAllEvents() {
        storeTelemetryData storage = new storeTelemetryData();

        eventTelemetry event1 = new eventTelemetry(
                eventTypes.PROJECT_CREATED, "Project A");

        eventTelemetry event2 = new eventTelemetry(
                eventTypes.CLASS_CREATED, "Class A");

        storage.accept(event1);
        storage.accept(event2);

        List<eventTelemetry> events = storage.getAllEvents();

        assertEquals(2, events.size());
        assertSame(event1, events.get(0));
        assertSame(event2, events.get(1));
    }

    @Test
    void testClear() {
        storeTelemetryData storage = new storeTelemetryData();

        eventTelemetry event = new eventTelemetry(
                eventTypes.PROJECT_CREATED, "Project A");

        storage.accept(event);

        storage.clear();

        assertEquals(0, storage.getAllEvents().size());
    }

}