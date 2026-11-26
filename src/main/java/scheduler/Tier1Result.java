package scheduler;

import model.Process;

public class Tier1Result {

    private final Process process;
    private final int contextSwitchStart;
    private final int startTime;
    private final int endTime;
    private final boolean finished;
    private final boolean demoted;

    public Tier1Result(Process process, int contextSwitchStart, int startTime, int endTime, boolean finished, boolean demoted) {
        this.process = process;
        this.contextSwitchStart = contextSwitchStart;
        this.startTime = startTime;
        this.endTime = endTime;
        this.finished = finished;
        this.demoted = demoted;
    }

    public Process getProcess() {
        return process;
    }

    public int getContextSwitchStart() {
        return contextSwitchStart;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isDemoted() {
        return demoted;
    }
}
