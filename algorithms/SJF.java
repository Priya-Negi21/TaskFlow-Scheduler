package algorithms;
import model.Process;
import java.util.*;
public class SJF {
    public static List<Process> schedule(List<Process> processList) {
        List<Process> processes = new ArrayList<>(processList);
        List<Process> completed = new ArrayList<>();
        int currentTime = 0;
        while (!processes.isEmpty()) {
            // Get all processes that have arrived by currentTime
            List<Process> available = new ArrayList<>();
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime) {
                    available.add(p);
                }
            }
            if (available.isEmpty()) {
                currentTime++;
                continue;
            }
            // Select the one with the shortest burst time
            Process shortest = Collections.min(available, Comparator.comparingInt(p -> p.burstTime));
            shortest.startTime = currentTime;
            shortest.completionTime = shortest.startTime + shortest.burstTime;
            shortest.turnaroundTime = shortest.completionTime - shortest.arrivalTime;
            shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
            currentTime = shortest.completionTime;
            completed.add(shortest);
            processes.remove(shortest);
        }
        return completed;
    }
}
