package model;

/**
 * Represents an execution slice or context switch on the Gantt timeline.
 */
public class TimelineSegment {

    private final int startTime;
    private final int endTime;
    private final String processId;
    private final boolean contextSwitch;

    public TimelineSegment(int startTime, int endTime, String processId, boolean contextSwitch) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.processId = processId;
        this.contextSwitch = contextSwitch;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return endTime - startTime;
    }

    public String getProcessId() {
        return processId;
    }

    public boolean isContextSwitch() {
        return contextSwitch;
    }

    @Override
    public String toString() {
        return String.format("[%d-%d: %s%s]", startTime, endTime, processId, contextSwitch ? " (CS)" : "");
    }
}
