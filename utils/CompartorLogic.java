package utils;

import model.Process;
import algorithms.FCFS;
import algorithms.SJF;
import algorithms.RoundRobin;
import algorithms.PriorityScheduling;

import java.util.*;

 public class CompartorLogic {

    public static void compareAll(List<Process> originalProcesses, int timeQuantum) {
        System.out.println("📊 Comparing algorithms...");

        List<Process> fcfsInput = deepCopyProcesses(originalProcesses);
        List<Process> sjfInput = deepCopyProcesses(originalProcesses);
        List<Process> rrInput = deepCopyProcesses(originalProcesses);
        List<Process> priorityInput = deepCopyProcesses(originalProcesses);

        List<Process> fcfsResult = FCFS.schedule(fcfsInput);
        List<Process> sjfResult = SJF.schedule(sjfInput);
        List<Process> rrResult = RoundRobin.schedule(rrInput, timeQuantum);
        List<Process> priorityResult = PriorityScheduling.schedule(priorityInput);

        double fcfsAvgWT = calculateAverageWaitingTime(fcfsResult);
        double sjfAvgWT = calculateAverageWaitingTime(sjfResult);
        double rrAvgWT = calculateAverageWaitingTime(rrResult);
        double priorityAvgWT = calculateAverageWaitingTime(priorityResult);

        System.out.printf("✅ FCFS: Avg WT = %.2f\n", fcfsAvgWT);
        System.out.printf("✅ SJF: Avg WT = %.2f\n", sjfAvgWT);
        System.out.printf("✅ RR: Avg WT = %.2f\n", rrAvgWT);
        System.out.printf("✅ Priority: Avg WT = %.2f\n", priorityAvgWT);

        double min = Math.min(Math.min(fcfsAvgWT, sjfAvgWT), Math.min(rrAvgWT, priorityAvgWT));
        String bestAlgo = "";

        if (min == fcfsAvgWT) bestAlgo = "FCFS";
        else if (min == sjfAvgWT) bestAlgo = "SJF";
        else if (min == rrAvgWT) bestAlgo = "Round Robin";
        else bestAlgo = "Priority Scheduling";

        System.out.println("🏆 Best algorithm based on average waiting time: " + bestAlgo);
    }

    private static double calculateAverageWaitingTime(List<Process> processes) {
        int total = 0;
        for (Process p : processes) {
            total += p.waitingTime;
        }
        return (double) total / processes.size();
    }

    private static List<Process> deepCopyProcesses(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }
        return copy;
    }

    public static String compareAndReturnOutput(List<Process> originalProcesses, int timeQuantum) {
        StringBuilder sb = new StringBuilder();

        sb.append("📊 Comparing algorithms...\n\n");

        List<Process> fcfsInput = deepCopyProcesses(originalProcesses);
        List<Process> sjfInput = deepCopyProcesses(originalProcesses);
        List<Process> rrInput = deepCopyProcesses(originalProcesses);
        List<Process> priorityInput = deepCopyProcesses(originalProcesses);

        List<Process> fcfsResult = FCFS.schedule(fcfsInput);
        List<Process> sjfResult = SJF.schedule(sjfInput);
        List<Process> rrResult = RoundRobin.schedule(rrInput, timeQuantum);
        List<Process> priorityResult = PriorityScheduling.schedule(priorityInput);

        double fcfsAvgWT = calculateAverageWaitingTime(fcfsResult);
        double sjfAvgWT = calculateAverageWaitingTime(sjfResult);
        double rrAvgWT = calculateAverageWaitingTime(rrResult);
        double priorityAvgWT = calculateAverageWaitingTime(priorityResult);

        sb.append(String.format("✅ FCFS:     Avg WT = %.2f\n", fcfsAvgWT));
        sb.append(String.format("✅ SJF:      Avg WT = %.2f\n", sjfAvgWT));
        sb.append(String.format("✅ RR:       Avg WT = %.2f\n", rrAvgWT));
        sb.append(String.format("✅ Priority: Avg WT = %.2f\n", priorityAvgWT));

        double min = Math.min(Math.min(fcfsAvgWT, sjfAvgWT), Math.min(rrAvgWT, priorityAvgWT));
        String bestAlgo;

        if (min == fcfsAvgWT) bestAlgo = "FCFS";
        else if (min == sjfAvgWT) bestAlgo = "SJF";
        else if (min == rrAvgWT) bestAlgo = "RR";
        else bestAlgo = "Priority";

        sb.append("\n🏆 Best algorithm based on average waiting time: ").append(bestAlgo).append("\n");

        return sb.toString();
    }

}

