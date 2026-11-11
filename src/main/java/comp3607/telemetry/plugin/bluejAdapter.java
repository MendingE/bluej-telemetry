package comp3607.telemetry.plugin;

import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;
import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.telemetryPlugin;

public class bluejAdapter implements CompileListener, ClassListener, PackageListener, InvocationFinishedListener {

    private final telemetryPlugin extension;
    private volatile boolean failureAlreadyReported = false;

    public bluejAdapter(telemetryPlugin e) {
        this.extension = e;
    }

    @Override
    public void compileStarted(CompileEvent event) {
        failureAlreadyReported = false;
        extension.recordEvent(eventTypes.COMPILATION_STARTED, "Compilation has started");
    }

    @Override
    public void compileSucceeded(CompileEvent event) {
        extension.recordEvent(eventTypes.COMPILATION_SUCCEEDED, "Compilation has succeeded");
    }

    @Override
    public void compileFailed(CompileEvent event) {
        if (!failureAlreadyReported) {
            extension.recordEvent(eventTypes.COMPILATION_FAILED,
                    "Compilation failed (no detail provided)");
        }
        failureAlreadyReported = false;
    }

    @Override
    public void compileError(CompileEvent event) {
        String errorMsg = (event.getErrorMessage() != null)
                ? event.getErrorMessage()
                : "Unknown Error";
        int errorLine = event.getErrorLineNumber();
        extension.recordEvent(eventTypes.COMPILATION_FAILED,
                "Error at Line " + errorLine + " - Error: " + errorMsg);
        failureAlreadyReported = true;
    }

    @Override
    public void compileWarning(CompileEvent event) {
        extension.recordEvent(eventTypes.COMPILATION_WARNING, "Compilation warning");
    }

    @Override
    public void classStateChanged(ClassEvent event) {
        extension.recordEvent(eventTypes.CLASS_MODIFIED,
                "Class modified: " + event.getBClass().getName());
    }

    @Override
    public void classNameChanged(ClassEvent event) {
        extension.recordEvent(eventTypes.CLASS_MODIFIED,
                "Class renamed: " + event.getBClass().getName());
    }

    @Override
    public void classRemoved(ClassEvent event) {
        extension.recordEvent(eventTypes.CLASS_DELETED,
                "Class deleted: " + event.getBClass().getName());
    }

    @Override
    public void packageOpened(PackageEvent event) {
        try {
            extension.recordEvent(eventTypes.PROJECT_OPENED,
                    "Project opened: " + event.getPackage().getName());
        } catch (Exception e) {
            extension.recordEvent(eventTypes.PROJECT_OPENED, "Project opened (Name unavailable)");
        }
    }

    @Override
    public void packageClosing(PackageEvent event) {
        try {
            extension.recordEvent(eventTypes.PROJECT_CLOSED,
                    "Project closed: " + event.getPackage().getName());
        } catch (Exception e) {
            extension.recordEvent(eventTypes.PROJECT_CLOSED, "Project closed (Name unavailable)");
        }
    }

    @Override
    public void invocationFinished(InvocationFinishedEvent event) {
        if (event.getEventType() == InvocationFinishedEvent.EventType.NORMAL_EXIT) {
            extension.recordEvent(eventTypes.PROGRAM_EXECUTED, "Method executed successfully");
        } else if (event.getEventType() == InvocationFinishedEvent.EventType.EXCEPTION_EXIT) {
            // API does not expose the exception object, so log a generic message.
            extension.recordEvent(eventTypes.RUNTIME_EXCEPTION, "Method threw an exception");
        }
    }
}