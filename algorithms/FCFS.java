//FCFS
package algorithms;
import model.Process;
import java.util.*;
public class FCFS {
    public static List<Process> schedule(List<Process> processes) {
        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        for (Process p : processes) {
            p.startTime = Math.max(currentTime, p.arrivalTime);
            p.completionTime = p.startTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            currentTime = p.completionTime;
        }
        return processes;
    }
}

