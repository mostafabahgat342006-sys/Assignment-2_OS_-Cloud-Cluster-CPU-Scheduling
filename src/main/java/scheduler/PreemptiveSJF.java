package scheduler;

import model.Process;
import model.SimulationResult;
import model.TimelineSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreemptiveSJF {

    public static double[] run(List<Process> processes, int contextSwitchingTime) {
        return execute(processes, contextSwitchingTime, true);
    }

    public static double[] execute(List<Process> processes, int contextSwitchingTime, boolean printOutput) {
        int time = 0;
        int completed = 0;
        int n = processes.size();

        Process lastProcess = null;

        while (completed < n) {
            Process selected = null;

            for (Process p : processes) {
                if (p.getArrivalTime() <= time && p.getRemainingTime() > 0) {
                    if (selected == null || p.getRemainingTime() < selected.getRemainingTime()) {
                        selected = p;
                    }
                }
            }

            if (selected == null) {
                lastProcess = null;
                time++;
                continue;
            }

            if (lastProcess != null && lastProcess != selected) {
                time += contextSwitchingTime;
                lastProcess = null;
                continue;
            }

            if (selected.getFirstStartTime() == -1) {
                selected.setFirstStartTime(time);
            }

            selected.setRemainingTime(selected.getRemainingTime() - 1);
            time++;

            if (selected.getRemainingTime() == 0) {
                selected.setFinishTime(time);
                completed++;
            }

            lastProcess = selected;
        }

        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        if (printOutput) {
            System.out.println("\nProcess Scheduling Results (Preemptive SJF):");
            System.out.println("--------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s | %-12s | %-10s | %-11s | %-12s | %-15s | %-13s%n",
                    "Process", "Arrival Time", "Burst Time", "Finish Time", "Waiting Time", "Turnaround Time", "Response Time");
            System.out.println("--------------------------------------------------------------------------------------------------");
        }

        for (Process p : processes) {
            int turnaroundTime = p.getFinishTime() - p.getArrivalTime();
            int waitingTime = turnaroundTime - p.getBurstTime();
            int responseTime = p.getFirstStartTime() - p.getArrivalTime();

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            if (printOutput) {
                System.out.printf("%-10s | %-12d | %-10d | %-11d | %-12d | %-15d | %-13d%n",
                        p.getProcessID(),
                        p.getArrivalTime(),
                        p.getBurstTime(),
                        p.getFinishTime(),
                        waitingTime,
                        turnaroundTime,
                        responseTime
                );
            }
        }

        double avgWait = n > 0 ? (totalWaitingTime / n) : 0;
        double avgTurnaround = n > 0 ? (totalTurnaroundTime / n) : 0;

        if (printOutput) {
            System.out.println("--------------------------------------------------------------------------------------------------");
            System.out.printf("Average Waiting Time    : %.2f time units%n", avgWait);
            System.out.printf("Average Turnaround Time : %.2f time units%n", avgTurnaround);
        }

        return new double[] { avgWait, avgTurnaround };
    }

    public static SimulationResult simulate(List<Process> inputProcesses, int contextSwitchingTime) {
        List<Process> processes = new ArrayList<>();
        for (Process p : inputProcesses) {
            processes.add(new Process(p));
        }

        int time = 0;
        int completed = 0;
        int n = processes.size();

        Process lastProcess = null;
        List<TimelineSegment> timeline = new ArrayList<>();

        while (completed < n) {
            Process selected = null;

            for (Process p : processes) {
                if (p.getArrivalTime() <= time && p.getRemainingTime() > 0) {
                    if (selected == null || p.getRemainingTime() < selected.getRemainingTime()) {
                        selected = p;
                    }
                }
            }

            if (selected == null) {
                lastProcess = null;
                time++;
                continue;
            }

            if (lastProcess != null && lastProcess != selected && contextSwitchingTime > 0) {
                timeline.add(new TimelineSegment(time, time + contextSwitchingTime, "CS", true));
                time += contextSwitchingTime;
                lastProcess = null;
                continue;
            }

            if (selected.getFirstStartTime() == -1) {
                selected.setFirstStartTime(time);
            }

            int start = time;
            int end = time + 1;
            selected.setRemainingTime(selected.getRemainingTime() - 1);
            time++;

            // Merge with previous segment if same process
            if (!timeline.isEmpty()) {
                TimelineSegment lastSeg = timeline.get(timeline.size() - 1);
                if (!lastSeg.isContextSwitch() && lastSeg.getProcessId().equals(selected.getProcessID()) && lastSeg.getEndTime() == start) {
                    timeline.set(timeline.size() - 1, new TimelineSegment(lastSeg.getStartTime(), end, selected.getProcessID(), false));
                } else {
                    timeline.add(new TimelineSegment(start, end, selected.getProcessID(), false));
                }
            } else {
                timeline.add(new TimelineSegment(start, end, selected.getProcessID(), false));
            }

            if (selected.getRemainingTime() == 0) {
                selected.setFinishTime(time);
                completed++;
            }

            lastProcess = selected;
        }

        double totalWait = 0;
        double totalTAT = 0;
        double totalResp = 0;
        int totalBurst = 0;

        for (Process p : processes) {
            totalWait += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
            totalResp += p.getResponseTime();
            totalBurst += p.getBurstTime();
        }

        double avgWait = n > 0 ? (totalWait / n) : 0;
        double avgTAT = n > 0 ? (totalTAT / n) : 0;
        double avgResp = n > 0 ? (totalResp / n) : 0;
        double cpuUtil = time > 0 ? ((double) totalBurst / time) * 100.0 : 0.0;

        return new SimulationResult(
                "Preemptive SJF",
                processes,
                timeline,
                Collections.emptyList(),
                avgWait,
                avgTAT,
                avgResp,
                cpuUtil,
                time
        );
    }
}
