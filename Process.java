
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

    public String getProcessID() {
        return processID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getInitialPriority() {
        return initialPriority;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getFirstStartTime() {
        return firstStartTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setFirstStartTime(int firstStartTime) {
        this.firstStartTime = firstStartTime;
    }
}
