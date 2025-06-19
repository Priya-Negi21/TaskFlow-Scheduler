
package utils;

import model.Process;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GanttChartPrinter {

    // 🎨 Soft pastel colors
    private static final Color[] COLORS = {
            new Color(255, 179, 186),
            new Color(255, 223, 186),
            new Color(255, 255, 186),
            new Color(186, 255, 201),
            new Color(186, 225, 255),
            new Color(220, 220, 255),
            new Color(240, 220, 255),
            new Color(255, 204, 229),
            new Color(204, 255, 229),
            new Color(229, 204, 255)
    };

    // 🗺 Static map for consistent PID → Color
    private static final Map<String, Color> pidColorMap = new HashMap<>();

    // ✅ Call this ONCE to generate and store colors per process
    public static Map<String, Color> generatePidColorMap(List<Process> processes) {
        pidColorMap.clear();
        int i = 0;
        for (Process p : processes) {
            if (!pidColorMap.containsKey(p.pid)) {
                pidColorMap.put(p.pid, COLORS[i % COLORS.length]);
                i++;
            }
        }
        return pidColorMap;
    }

    // ✅ Retrieve color for a specific PID
    public static Color getColorForPid(String pid) {
        return pidColorMap.getOrDefault(pid, Color.LIGHT_GRAY);
    }

    // 🔁 Clone the process list (to avoid side effects)
    private static List<Process> cloneList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }
        return copy;
    }

    // 📋 Convert scheduled process list into a Gantt-friendly string list
    private static List<String> convertToPidList(List<Process> processes) {
        List<String> result = new ArrayList<>();
        processes.sort(Comparator.comparingInt(p -> p.startTime));
        for (Process p : processes) {
            int duration = p.completionTime - p.startTime;
            for (int i = 0; i < duration; i++) {
                result.add(p.pid);
            }
        }
        return result;
    }

    // 📊 Generate Gantt data for all algorithms
    public static Map<String, List<String>> getGanttChartData(List<Process> processes, int timeQuantum) {
        Map<String, List<String>> chart = new LinkedHashMap<>();

        List<Process> fcfs = algorithms.FCFS.schedule(cloneList(processes));
        List<Process> sjf = algorithms.SJF.schedule(cloneList(processes));
        List<Process> rr = algorithms.RoundRobin.schedule(cloneList(processes), timeQuantum);
        List<Process> priority = algorithms.PriorityScheduling.schedule(cloneList(processes));

        chart.put("FCFS", convertToPidList(fcfs));
        chart.put("SJF", convertToPidList(sjf));
        chart.put("RR", convertToPidList(rr));
        chart.put("Priority", convertToPidList(priority));

        return chart;
    }

    // 🖨 Optional: For console output (colored timeline)
    public static void printGanttChart(List<Process> processes, String algorithmName) {
        System.out.println("\n📊 Gantt Chart for " + algorithmName + ":");

        processes.sort(Comparator.comparingInt(p -> p.startTime));

        StringBuilder timeline = new StringBuilder();
        StringBuilder labelLine = new StringBuilder();
        StringBuilder timeLine = new StringBuilder();

        int currentTime = 0;

        for (Process p : processes) {
            int duration = p.completionTime - p.startTime;
            String block = "|".repeat(duration * 2);
            String label = String.format("%-" + (duration * 2) + "s", p.pid);
            String time = String.format("%-" + (duration * 2) + "s", p.startTime);

            timeline.append(block);
            labelLine.append(label);
            timeLine.append(time);

            currentTime = p.completionTime;
        }

        timeLine.append(currentTime).append("\n");

        System.out.println(timeline);
        System.out.println(labelLine);
        System.out.println(timeLine);
    }
}



