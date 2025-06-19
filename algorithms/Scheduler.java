package algorithms;

import model.Process;
import java.util.List;

public class Scheduler {
    public static void runFCFS(List<Process> processes) {
        FCFS.schedule(processes);
    }

    public static void runSJF(List<Process> processes) {
        SJF.schedule(processes);
    }

    public static void runRoundRobin(List<Process> processes, int timeQuantum) {
        RoundRobin.schedule(processes, timeQuantum);
    }

    public static void runPriority(List<Process> processes) {
        PriorityScheduling.schedule(processes);
    }
}
