package comp3607.telemetry.reporting;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.export.fileFormatStrategy;

public class reportStrategyPDF implements fileFormatStrategy {

    @Override
    public void generateFile(List<eventTelemetry> events, String filePath) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            ActivityTimeCalculator timeCalc = new ActivityTimeCalculator(events);
            ReportAggregator agg = new ReportAggregator(events);

            Paragraph title = new Paragraph("BlueJ Telemetry Report");
            title.getFont().setSize(20);
            title.getFont().setStyle(com.lowagie.text.Font.BOLD);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Session Summary:"));
            document.add(new Paragraph("Total Session Duration: " + (agg.calculateTotalSessionTimeMs() / 1000) + " seconds"));

            long activeTimeMs = timeCalc.calculateActiveTimeMs();
            long totalTimeMs = agg.calculateTotalSessionTimeMs();
            long idleTimeMs = Math.max(0, totalTimeMs - activeTimeMs);
            
            document.add(new Paragraph("Active Coding Time: " + (activeTimeMs / 1000) + " seconds"));
            document.add(new Paragraph("Idle Time: " + (idleTimeMs / 1000) + " seconds"));
            document.add(new Paragraph("Total Compile Attempts: " + (agg.getCompileSuccessCount() + agg.getCompileFailureCount())));

            document.add(new Paragraph(String.format("Compile Success Rate: %.2f%%", agg.getCompileSuccessRate() * 100)));

            document.add(new Paragraph("Total Classes Created: " + agg.getClassCreationCount()));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Compilation Error Breakdown:"));
            Map<String, Integer> errors = agg.getCompileErrorFrequency();

            if (errors.isEmpty()) {
                document.add(new Paragraph("No compilation errors encountered."));
            } else {
                PdfPTable table = new PdfPTable(2); // 2 columns
                table.setWidthPercentage(100);

                table.addCell("Error Message");
                table.addCell("Frequency");

                for (Map.Entry<String, Integer> entry : errors.entrySet()) {
                    table.addCell(entry.getKey());
                    table.addCell(String.valueOf(entry.getValue()));
                }
                document.add(table);
            }

            System.out.println("PDF Report saved to " + filePath);
        } catch (Exception e) {
            System.out.println("Error saving PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}