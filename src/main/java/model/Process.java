package model;

/**
 * Core Process entity representing a process in the CPU scheduling simulator.
 */
public class Process {

    private String processID;
    private int arrivalTime;
    private int burstTime;
    private int remainingTime;
    private int initialPriority;
    private int finishTime;
    private int firstStartTime;

    public Process(String processID, int arrivalTime, int burstTime, int initialPriority) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.initialPriority = initialPriority;
        this.finishTime = -1;
        this.firstStartTime = -1;
    }

    public Process(Process other) {
        this.processID = other.processID;
        this.arrivalTime = other.arrivalTime;
        this.burstTime = other.burstTime;
        this.remainingTime = other.remainingTime;
        this.initialPriority = other.initialPriority;
        this.finishTime = other.finishTime;
        this.firstStartTime = other.firstStartTime;
    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getInitialPriority() {
        return initialPriority;
    }

    public void setInitialPriority(int initialPriority) {
        this.initialPriority = initialPriority;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getFirstStartTime() {
        return firstStartTime;
    }

    public void setFirstStartTime(int firstStartTime) {
        this.firstStartTime = firstStartTime;
    }

    public int getTurnaroundTime() {
        return (finishTime != -1) ? (finishTime - arrivalTime) : 0;
    }

    public int getWaitingTime() {
        return (finishTime != -1) ? (getTurnaroundTime() - burstTime) : 0;
    }

    public int getResponseTime() {
        return (firstStartTime != -1) ? (firstStartTime - arrivalTime) : 0;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id='" + processID + '\'' +
                ", arrival=" + arrivalTime +
                ", burst=" + burstTime +
                ", priority=" + initialPriority +
                '}';
    }
}
