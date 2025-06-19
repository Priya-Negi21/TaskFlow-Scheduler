package utils;

import model.Process;
import java.io.*;
import java.util.*;

public class FileReaderUtils {
    public static List<Process> readProcessesFromCSV(String filePath) {
        List<Process> processes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String id = parts[0].trim();
                    int arrival = Integer.parseInt(parts[1].trim());
                    int burst = Integer.parseInt(parts[2].trim());
                    int priority = parts.length > 3 ? Integer.parseInt(parts[3].trim()) : 0;
                    processes.add(new Process(id, arrival, burst, priority));
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + filePath);
        }

        return processes;
    }
}
