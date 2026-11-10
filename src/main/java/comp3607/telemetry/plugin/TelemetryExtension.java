package comp3607.telemetry.plugin;


import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;

public class TelemetryExtension extends Extension {

    @Override
    public boolean isCompatible() {
        return getExtensionsAPIVersionMajor() >= 3;
    }

    @Override
    public void startup(BlueJ bluej) {
        System.out.println("BlueJ Telemetry Extension started.");
    }

    @Override
    public String getName() {
        return "BlueJ Telemetry";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Student Coding Telemetry Analytics Extension";
    }
}
