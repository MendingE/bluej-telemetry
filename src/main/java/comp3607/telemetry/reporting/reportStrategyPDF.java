package comp3607.telemetry.reporting;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import comp3607.telemetry.events.eventTelemetry;
import comp3607.telemetry.export.fileFormatStrategy;

import java.util.List;
import java.util.Map;

public class reportStrategyPDF implements fileFormatStrategy {

    @Override
    public void generateFile(List<eventTelemetry> events, String filePath) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            ReportAggregator agg = new ReportAggregator(events);

            // Title
            document.add(new Paragraph("BlueJ Telemetry Report")
                    .setBold().setFontSize(20));

            // Session Summary
            document.add(new Paragraph("\nSession Summary:").setBold());
            document.add(new Paragraph(
                    "Total Session Duration: " + (agg.calculateTotalSessionTimeMs() / 1000) + " seconds"));
            document.add(new Paragraph(
                    "Total Compile Attempts: " + (agg.getCompileSuccessCount() + agg.getCompileFailureCount())));
            document.add(
                    new Paragraph(String.format("Compile Success Rate: %.2f%%", agg.getCompileSuccessRate() * 100)));
            document.add(new Paragraph("Total Classes Created: " + agg.getClassCreationCount()));

            // Compilation Error Breakdown Table
            document.add(new Paragraph("\nCompilation Error Breakdown:").setBold());
            Map<String, Integer> errors = agg.getCompileErrorFrequency();

            if (errors.isEmpty()) {
                document.add(new Paragraph("No compilation errors encountered."));
            } else {
                Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
                table.addHeaderCell("Error Message");
                table.addHeaderCell("Frequency");

                for (Map.Entry<String, Integer> entry : errors.entrySet()) {
                    table.addCell(entry.getKey());
                    table.addCell(String.valueOf(entry.getValue()));
                }
                document.add(table);
            }

            document.close();
            System.out.println("PDF Report saved to " + filePath);
        } catch (Exception e) {
            System.out.println("Error saving PDF: " + e.getMessage());
        }
    }
}