package model;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the complete result of a scheduling simulation.
 */
public class SimulationResult {

    private final String algorithmName;
    private final List<Process> processes;
    private final List<TimelineSegment> timelineSegments;
    private final List<MatrixRowModel> matrixRows;
    private final double averageWaitingTime;
    private final double averageTurnaroundTime;
    private final double averageResponseTime;
    private final double cpuUtilization;
    private final int totalSimulationTime;

    public SimulationResult(String algorithmName,
                            List<Process> processes,
                            List<TimelineSegment> timelineSegments,
                            List<MatrixRowModel> matrixRows,
                            double averageWaitingTime,
                            double averageTurnaroundTime,
                            double averageResponseTime,
                            double cpuUtilization,
                            int totalSimulationTime) {
        this.algorithmName = algorithmName;
        this.processes = (processes != null) ? processes : Collections.emptyList();
        this.timelineSegments = (timelineSegments != null) ? timelineSegments : Collections.emptyList();
        this.matrixRows = (matrixRows != null) ? matrixRows : Collections.emptyList();
        this.averageWaitingTime = averageWaitingTime;
        this.averageTurnaroundTime = averageTurnaroundTime;
        this.averageResponseTime = averageResponseTime;
        this.cpuUtilization = cpuUtilization;
        this.totalSimulationTime = totalSimulationTime;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public List<TimelineSegment> getTimelineSegments() {
        return timelineSegments;
    }

    public List<MatrixRowModel> getMatrixRows() {
        return matrixRows;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    public int getTotalSimulationTime() {
        return totalSimulationTime;
    }
}
