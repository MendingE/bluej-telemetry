package comp3607.telemetry.unitTests;

import comp3607.telemetry.events.eventManager;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.events.eventTypes;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class eventManagerTest {
    @Test
    void testAddObserver() {
        eventManager manager = new eventManager();
        AtomicInteger counter = new AtomicInteger(0);
        manager.addObserver(event -> counter.incrementAndGet());
        eventTelemetry event = new eventTelemetry(eventTypes.PROJECT_CREATED, "Test Project");
        manager.notifyObservers(event);
        assertEquals(1, counter.get());
    }

    @Test
    void testNotifyMultipleObservers() {
        eventManager manager = new eventManager();
        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);
        manager.addObserver(event -> counter1.incrementAndGet());
        manager.addObserver(event -> counter2.incrementAndGet());
        eventTelemetry event = new eventTelemetry(eventTypes.PROJECT_CREATED, "Test Project");
        manager.notifyObservers(event);
        assertEquals(1, counter1.get());
        assertEquals(1, counter2.get());
    }

    @Test
    void testNotifyNoObservers() {
        eventManager manager = new eventManager();
        eventTelemetry event = new eventTelemetry(eventTypes.PROJECT_CREATED, "Test Project");
        assertDoesNotThrow(() -> manager.notifyObservers(event));
    }

    @Test
    void testNotifyObserverNewEvent() {
        eventManager manager = new eventManager();
        AtomicInteger counter = new AtomicInteger(0);
        manager.addObserver(event -> {
            if (eventTypes.PROJECT_CREATED.name().equals(event.getType())) {
                counter.incrementAndGet();
            }
        });
        eventTelemetry event1 = new eventTelemetry(eventTypes.PROJECT_CREATED, "Test Project");
        eventTelemetry event2 = new eventTelemetry(eventTypes.PROJECT_DELETED, "Test Project");
        manager.notifyObservers(event1);
        manager.notifyObservers(event2);
        assertEquals(1, counter.get());
    }
}
