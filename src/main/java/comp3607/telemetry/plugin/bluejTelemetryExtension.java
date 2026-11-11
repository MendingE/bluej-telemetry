package comp3607.telemetry.plugin;

import java.util.Timer;
import java.util.TimerTask;

import bluej.extensions2.BClass;
import bluej.extensions2.BProject;
import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import bluej.extensions2.editor.JavaEditor;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.telemetryPlugin;

public class bluejTelemetryExtension extends Extension {

    private bluejAdapter adapter;
    private telemetryPlugin plugin;
    private Timer editorTimer;

    @Override
    public void startup(BlueJ bluej) {
        System.out.println("BlueJ Telemetry Extension started.");

        plugin = new telemetryPlugin();
        adapter = new bluejAdapter(plugin);

        bluej.addCompileListener(adapter);
        bluej.addClassListener(adapter);
        bluej.addPackageListener(adapter);
        bluej.addInvocationFinishedListener(adapter);

        bluej.setMenuGenerator(new bluej.extensions2.MenuGenerator() {
            @Override
            public javafx.scene.control.MenuItem getToolsMenuItem(bluej.extensions2.BPackage bPackage) {
                javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem("Export Telemetry Data");
                item.setOnAction(e -> bluejTelemetryExtension.this.exportData());
                return item;
            }
        });

        beginEditorPolling(bluej);
    }

    private void beginEditorPolling(BlueJ bluej) {
        editorTimer = new Timer(true);

        editorTimer.schedule(new TimerTask() {
            private int lastLength = -1;
            private long lastChangeTime = System.currentTimeMillis();
            private final long IDLE_THRESHOLD = 60000;

            @Override
            public void run() {
                try {
                    BProject[] projects = bluej.getOpenProjects();
                    if (projects.length > 0) {
                        BClass[] classes = projects[0].getPackage("").getClasses();
                        if (classes.length > 0) {
                            JavaEditor editor = classes[0].getJavaEditor();

                            if (editor != null) {
                                int currentLength = editor.getTextLength();

                                if (currentLength != lastLength) {
                                    lastLength = currentLength;
                                    lastChangeTime = System.currentTimeMillis();
                                    plugin.recordEvent(eventTypes.EDITOR_TYPED, "The user typed or edited their code");
                                } else if (System.currentTimeMillis() - lastChangeTime > IDLE_THRESHOLD) {
                                    plugin.recordEvent(eventTypes.EDITOR_IDLE, "User is inactive");
                                    lastChangeTime = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }, 0, 1000);
    }

    public void exportData() {
        try {
            String desktopPath = System.getProperty("user.home") + java.io.File.separator + "Desktop";
            String jsonPath = desktopPath + java.io.File.separator + "telemetry_data.json";
            String pdfPath = desktopPath + java.io.File.separator + "telemetry_data.pdf";

            plugin.exportFiles(jsonPath, pdfPath);
            System.out.println("Manual Export Completed! Check your Desktop.");
        } catch (Exception e) {
            System.out.println("Error exporting files: " + e.getMessage());
        }
    }

    @Override
    public void terminate() {

        if (editorTimer != null) {
            editorTimer.cancel();
        }

        if (plugin != null) {
            plugin.exportFiles("telemetry_data.json", "telemetry_data.pdf");
        }
    }

    @Override
    public boolean isCompatible() {
        return getExtensionsAPIVersionMajor() >= 3;
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
