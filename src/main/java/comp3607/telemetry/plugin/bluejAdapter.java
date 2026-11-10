package comp3607.telemetry.plugin;

import bluej.extensions2.event.*;
import comp3607.telemetry.telemetryPlugin;
import comp3607.telemetry.events.eventTypes;


public class bluejAdapter implements CompileListener, ClassListener, PackageListener, InvocationFinishedListener {

    private telemetryPlugin extension;

    public bluejAdapter(telemetryPlugin e) {
        this.extension = e;
    }

 
    @Override
    public void compileSucceeded(CompileEvent event) {
        extension.recordEvent(eventTypes.COMPILATION_SUCCEEDED, "Compilation has succeeded");
    }

    @Override
    public void compileFailed(CompileEvent event) {
        extension.recordEvent(eventTypes.COMPILATION_FAILED, "Compilation has failed");
    }

