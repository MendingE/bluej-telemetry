package comp3607.telemetry.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class eventManager {
    private final List<Consumer<eventTelemetry>> observers = new CopyOnWriteArrayList<>();

    public void addObserver(Consumer<eventTelemetry> observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void removeObserver(Consumer<eventTelemetry> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(eventTelemetry event) {
        if (event == null)
            return;
        for (var observer : observers) {
            observer.accept(event);
        }
    }
}