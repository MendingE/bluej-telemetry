package comp3607.telemetry.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class eventManager{
    private List<Consumer<eventTelemetry>> observers = new ArrayList<>();

    public void addObserver(Consumer<eventTelemetry> observer) {
        observers.add(observer);
    }

    public void notifyObservers(eventTelemetry event) {
        for (var observer : observers) {
            observer.accept(event);
        }
    }
}