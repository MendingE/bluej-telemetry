package comp3607.telemetry.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import comp3607.telemetry.events.eventTelemetry;
import java.io.File;
import java.util.List;

public class JSONFormatStrategy implements fileFormatStrategy {

    
    @Override
    public void generateFile(List<eventTelemetry> events, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), events);
            System.out.println("JSON saved to " + filePath);
        } catch (Exception e) {
            System.out.println("Error saving JSON: " + e.getMessage());
        }
    }
}