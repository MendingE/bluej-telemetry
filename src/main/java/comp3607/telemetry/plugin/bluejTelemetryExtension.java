package comp3607.telemetry.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.BProject;
import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import bluej.extensions2.MenuGenerator;
import bluej.extensions2.editor.JavaEditor;
import comp3607.telemetry.events.eventTypes;
import comp3607.telemetry.telemetryPlugin;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class bluejTelemetryExtension extends Extension {

    private bluejAdapter adapter;
    private telemetryPlugin plugin;
    private Timer editorTimer;
    private Timer exportTimer;

    private final Set<String> knownProjects = new HashSet<>();
    private final Map<String, Set<String>> knownClassesByProject = new HashMap<>();

    @Override
    public void startup(BlueJ bluej) {
        System.out.println("BlueJ Telemetry Extension started.");

        plugin = new telemetryPlugin();
        adapter = new bluejAdapter(plugin);

        bluej.addCompileListener(adapter);
        bluej.addClassListener(adapter);
        bluej.addPackageListener(adapter);
        bluej.addInvocationFinishedListener(adapter);

        bluej.setMenuGenerator(new MenuBuilder());

        plugin.startSession();
        beginEditorPolling(bluej);
        beginAutoExport();

        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        } catch (ClassNotFoundException e) {
            System.out.println("CRITICAL ERROR: Jackson is missing from the JAR!");
        }
    }

    private class MenuBuilder extends MenuGenerator {
        @Override
        public javafx.scene.control.MenuItem getToolsMenuItem(BPackage bp) {
            Menu telemetryMenu = new Menu("Telemetry");

            MenuItem exportItem = new MenuItem("Export Data Now");
            exportItem.setOnAction(e -> {
                exportData();
            });

            MenuItem clearItem = new MenuItem("Clear Collected Data");
            clearItem.setOnAction(e -> {
                plugin.clearEvents();
            });

            telemetryMenu.getItems().addAll(exportItem, clearItem);

            return telemetryMenu;
        }
    }

    private void beginEditorPolling(BlueJ bluej) {
        editorTimer = new Timer(true);

        editorTimer.schedule(new TimerTask() {
            private final Map<String, Integer> lastLengthByClass = new HashMap<>();
            private long lastChangeTime = System.currentTimeMillis();
            private final long IDLE_THRESHOLD = 60_000;

            @Override
            public void run() {
                try {
                    BProject[] projects = bluej.getOpenProjects();
                    Set<String> currentProjects = new HashSet<>();
                    for (BProject p : projects) {
                        currentProjects.add(safeProjectName(p));
                    }
                    for (String name : currentProjects) {
                        if (!knownProjects.contains(name)) {
                            plugin.recordEvent(eventTypes.PROJECT_CREATED, "Project created/opened: " + name);
                        }
                    }
                    for (String name : new HashSet<>(knownProjects)) {
                        if (!currentProjects.contains(name)) {
                            plugin.recordEvent(eventTypes.PROJECT_DELETED, "Project closed/deleted: " + name);
                        }
                    }
                    knownProjects.clear();
                    knownProjects.addAll(currentProjects);

                    for (BProject project : projects) {
                        String projectName = safeProjectName(project);
                        BClass[] classes;
                        try {
                            classes = project.getPackage("").getClasses();
                        } catch (Exception ex) {
                            continue;
                        }

                        Set<String> currentClasses = new HashSet<>();
                        for (BClass c : classes)
                            currentClasses.add(c.getName());

                        Set<String> known = knownClassesByProject.computeIfAbsent(projectName, k -> new HashSet<>());
                        for (String cls : currentClasses) {
                            if (!known.contains(cls)) {
                                plugin.recordEvent(eventTypes.CLASS_CREATED,
                                        "Class created: " + cls + " (project: " + projectName + ")");
                            }
                        }
                        known.clear();
                        known.addAll(currentClasses);

                        for (BClass c : classes) {
                            JavaEditor editor;
                            try {
                                editor = c.getJavaEditor();
                            } catch (Exception ex) {
                                continue;
                            }
                            if (editor == null)
                                continue;

                            String key = projectName + "::" + c.getName();
                            int currentLength = editor.getTextLength();
                            Integer last = lastLengthByClass.get(key);

                            if (last == null) {
                                lastLengthByClass.put(key, currentLength);
                                continue;
                            }
                            if (currentLength != last) {
                                int delta = currentLength - last;
                                lastLengthByClass.put(key, currentLength);
                                lastChangeTime = System.currentTimeMillis();
                                plugin.recordEvent(eventTypes.EDITOR_TYPED,
                                        "Edit in " + c.getName() + " (delta=" + delta + " chars)");
                            }
                        }
                    }

                    if (System.currentTimeMillis() - lastChangeTime > IDLE_THRESHOLD) {
                        plugin.recordEvent(eventTypes.EDITOR_IDLE, "User is inactive");
                        lastChangeTime = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                }
            }
        }, 0, 1000);
    }

    private static String safeProjectName(BProject p) {
        try {
            return p.getName();
        } catch (Exception e) {
            return "UnknownProject@" + System.identityHashCode(p);
        }
    }

    private void beginAutoExport() {
        exportTimer = new Timer(true);
        exportTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (plugin != null) {
                    String desktopPath = System.getProperty("user.home") + java.io.File.separator + "Desktop";
                    String jsonPath = desktopPath + java.io.File.separator + "telemetry_data.json";
                    String pdfPath = desktopPath + java.io.File.separator + "telemetry_data.pdf";
                    try {
                        plugin.exportFiles(jsonPath, pdfPath);
                        System.out.println("Auto-exported telemetry to Desktop.");
                    } catch (Exception ex) {
                        System.out.println("Auto-export failed: " + ex.getMessage());
                    }
                }
            }
        }, 10_000, 10_000);
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
        if (editorTimer != null)
            editorTimer.cancel();
        if (exportTimer != null)
            exportTimer.cancel();
        if (plugin != null) {
            plugin.endSession();
            exportData();
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
        return "1.1";
    }

    @Override
    public String getDescription() {
        return "Student Coding Telemetry Analytics Extension";
    }
}