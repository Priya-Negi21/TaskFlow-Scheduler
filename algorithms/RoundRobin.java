package algorithms;
import model.Process;
import java.util.*;
public class RoundRobin {
    public static List<Process> schedule(List<Process> inputProcesses, int quantum) {
        List<Process> processes = new ArrayList<>();
        for (Process p : inputProcesses) {
            processes.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }
        Queue<Process> queue = new LinkedList<>();
        List<Process> completed = new ArrayList<>();
        int currentTime = 0;
        Map<String, Integer> remainingBurst = new HashMap<>();

        for (Process p : processes) {
            remainingBurst.put(p.pid, p.burstTime);
        }

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int index = 0;
        Set<String> inQueue = new HashSet<>();

        while (index < processes.size() || !queue.isEmpty()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                Process p = processes.get(index);
                if (!inQueue.contains(p.pid)) {
                    queue.add(p);
                    inQueue.add(p.pid);
                }
                index++;
            }

            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = queue.poll();
            inQueue.remove(current.pid);

            int remaining = remainingBurst.get(current.pid);
            int timeExecuted = Math.min(quantum, remaining);

            if (remaining == current.burstTime) {
                current.startTime = currentTime;
            }

            currentTime += timeExecuted;
            remainingBurst.put(current.pid, remaining - timeExecuted);

            // Check for new arrivals during this quantum
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                Process p = processes.get(index);
                if (!inQueue.contains(p.pid)) {
                    queue.add(p);
                    inQueue.add(p.pid);
                }
                index++;
            }

            if (remainingBurst.get(current.pid) > 0) {
                queue.add(current);
                inQueue.add(current.pid);
            } else {
                current.completionTime = currentTime;
                current.turnaroundTime = current.completionTime - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                completed.add(current);
            }
        }

        completed.sort(Comparator.comparing(p -> p.pid));
        return completed;
    }
}
