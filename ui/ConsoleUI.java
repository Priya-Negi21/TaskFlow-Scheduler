package ui;


import algorithms.FCFS;
import algorithms.PriorityScheduling;
import algorithms.RoundRobin;
import algorithms.SJF;
import model.Process;
import utils.FileReaderUtils;
import utils.CompartorLogic;
import utils.GanttChartPrinter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class ConsoleUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TaskFlow - Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(1200, 900));

        // NORTH - Controls
        JPanel controlPanel = new JPanel();
        JButton uploadButton = new JButton("Upload CSV File");
        JTextField quantumField = new JTextField(5);
        JButton runButton = new JButton("Run Scheduling");

        controlPanel.add(uploadButton);
        controlPanel.add(new JLabel("Time Quantum:"));
        controlPanel.add(quantumField);
        controlPanel.add(runButton);
        frame.add(controlPanel, BorderLayout.NORTH);

        // CENTER - Contains both table and Gantt chart
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Uploaded Process Data"));
        JTable processTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Limit height
        centerPanel.add(tablePanel);

        // Gantt chart panel
        JPanel ganttChartPanel = new JPanel(new BorderLayout());
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Charts (All Algorithms)"));
        JTable ganttTable = new JTable();
        JScrollPane ganttScrollPane = new JScrollPane(ganttTable);
        ganttScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setPreferredSize(new Dimension(1200, 300)); // Scroll pane visible
        ganttChartPanel.add(ganttScrollPane, BorderLayout.CENTER);
        centerPanel.add(ganttChartPanel);

        frame.add(centerPanel, BorderLayout.CENTER);

        // SOUTH - Output Log
        JTextArea outputArea = new JTextArea(10, 100);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1200, 200));
        frame.add(scrollPane, BorderLayout.SOUTH);

        final String[] filePath = new String[1];
        final List<Process> uploadedProcesses = new ArrayList<>();

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selected = fileChooser.getSelectedFile();
                filePath[0] = selected.getAbsolutePath();
                outputArea.setText("");
                outputArea.append("📂 File selected: " + filePath[0] + "\n");

                List<Process> processes = FileReaderUtils.readProcessesFromCSV(filePath[0]);
                if (processes.isEmpty()) {
                    outputArea.append("❌ No processes loaded from file!\n");
                    return;
                }

                uploadedProcesses.addAll(processes);
                updateProcessTable(processTable, processes);
            }
        });

        runButton.addActionListener(e -> {
            if (filePath[0] == null || uploadedProcesses == null) {
                JOptionPane.showMessageDialog(frame, "Please select a file first.");
                return;
            }

            int timeQuantum = 0;
            if (!quantumField.getText().trim().isEmpty()) {
                try {
                    timeQuantum = Integer.parseInt(quantumField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid time quantum.");
                    return;
                }
            }

            outputArea.append("\n🛠 Running All Scheduling Algorithms...\n\n");

            List<Process> copy = FileReaderUtils.readProcessesFromCSV(filePath[0]);
            String comparisonOutput = CompartorLogic.compareAndReturnOutput(copy, timeQuantum);
            outputArea.append(comparisonOutput + "\n");

            // Generate color map based on all scheduled processes
            List<Process> allScheduled = new ArrayList<>();
            allScheduled.addAll(FCFS.schedule(FileReaderUtils.readProcessesFromCSV(filePath[0])));
            allScheduled.addAll(SJF.schedule(FileReaderUtils.readProcessesFromCSV(filePath[0])));
            allScheduled.addAll(RoundRobin.schedule(FileReaderUtils.readProcessesFromCSV(filePath[0]), timeQuantum));
            allScheduled.addAll(PriorityScheduling.schedule(FileReaderUtils.readProcessesFromCSV(filePath[0])));
            GanttChartPrinter.generatePidColorMap(allScheduled);

            Map<String, List<String>> chart = GanttChartPrinter.getGanttChartData(uploadedProcesses, timeQuantum);
            updateGanttChart(ganttTable, chart);
        });

        frame.pack();
        frame.setVisible(true);
    }

    private static void updateProcessTable(JTable table, List<Process> processes) {
        String[] columns = {"PID", "Arrival", "Burst", "Priority"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (Process p : processes) {
            model.addRow(new Object[]{p.pid, p.arrivalTime, p.burstTime, p.priority});
        }
        table.setModel(model);

        // 🎨 Add row-wise color rendering based on PID
        Map<String, Color> colorMap = GanttChartPrinter.generatePidColorMap(processes);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String pid = table.getValueAt(row, 0).toString();
                Color color = colorMap.getOrDefault(pid, Color.WHITE);
                c.setBackground(color);
                return c;
            }
        });
    }

    private static void updateGanttChart(JTable table, Map<String, List<String>> chartData) {
        int maxCols = chartData.values().stream().mapToInt(List::size).max().orElse(0);
        String[] columns = new String[maxCols];
        for (int i = 0; i < maxCols; i++) columns[i] = String.valueOf(i);

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (Map.Entry<String, List<String>> entry : chartData.entrySet()) {
            List<String> row = entry.getValue();
            Object[] rowData = new Object[maxCols];
            for (int i = 0; i < maxCols; i++) {
                rowData[i] = i < row.size() ? row.get(i) : "";
            }
            model.addRow(rowData);
        }

        table.setModel(model);
        table.setRowHeight(30);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !value.toString().trim().isEmpty()) {
                    String pid = value.toString();
                    Color color = GanttChartPrinter.getColorForPid(pid);
                    c.setBackground(color);
                    setText(" " + pid + " ");
                } else {
                    c.setBackground(Color.WHITE);
                    setText("");
                }
                return c;
            }
        });
    }
}
