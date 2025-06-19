package algorithms;

import model.Process;
import java.util.*;

public class PriorityScheduling {

    public static List<Process> schedule(List<Process> processList) {
        List<Process> processes = new ArrayList<>(processList);
        List<Process> completed = new ArrayList<>();
        int currentTime = 0;

        while (!processes.isEmpty()) {
            // Get all processes that have arrived
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

            // Select process with highest priority (lowest priority number)
            Process highest = Collections.min(available, Comparator.comparingInt(p -> p.priority));

            highest.startTime = currentTime;
            highest.completionTime = highest.startTime + highest.burstTime;
            highest.turnaroundTime = highest.completionTime - highest.arrivalTime;
            highest.waitingTime = highest.turnaroundTime - highest.burstTime;

            currentTime = highest.completionTime;

            completed.add(highest);
            processes.remove(highest);
        }

        return completed;
    }
}
