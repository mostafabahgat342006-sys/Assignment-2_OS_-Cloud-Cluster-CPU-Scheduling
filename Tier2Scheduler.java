
import java.util.ArrayList;
import java.util.List;

public class Tier2Scheduler {

    private final List<Process> queue = new ArrayList<>();

    private final java.util.Map<Process, Integer> entryTimes = new java.util.HashMap<>();

    private final int contextSwitchingTime;
    public static final int DEFAULT_AGING_INTERVAL = 4;
    private final int agingInterval;


    public Tier2Scheduler(int contextSwitchingTime) {
        this(contextSwitchingTime, DEFAULT_AGING_INTERVAL);
    }


    public Tier2Scheduler(int contextSwitchingTime, int agingInterval) {
        this.contextSwitchingTime = contextSwitchingTime;
        this.agingInterval = (agingInterval > 0) ? agingInterval : DEFAULT_AGING_INTERVAL;
    }

    public int getAgingInterval() {
        return agingInterval;
    }

    public void addProcess(Process process, int currentTime) {
        if (process == null || process.getRemainingTime() <= 0) {
            return;
        }

        if (!queue.contains(process)) {
            queue.add(process);
            entryTimes.put(process, currentTime);
        }
    }

    public boolean hasReady() {
        return !queue.isEmpty();
    }

    public int calculateEffectivePriority(Process process, int currentTime) {
        Integer enteredAt = entryTimes.get(process);
        if (enteredAt == null) {
            return process.getInitialPriority();
        }

        int waitingTime = currentTime - enteredAt;

        int aging = waitingTime / agingInterval;

        return process.getInitialPriority() - aging;
    }

    public Integer getEntryTime(Process process) {
        return entryTimes.get(process);
    }

    public Process peekNextProcess(int currentTime) {
        return selectNextProcess(currentTime);
    }

    private Process selectNextProcess(int currentTime) {

        Process selected = null;
        int bestPriority = Integer.MAX_VALUE;

        for (Process process : queue) {

            int effectivePriority = calculateEffectivePriority(process, currentTime);

            if (selected == null || effectivePriority < bestPriority) {

                selected = process;
                bestPriority = effectivePriority;

            } else if (effectivePriority == bestPriority) {

                int selectedEntry = entryTimes.get(selected);
                int currentEntry = entryTimes.get(process);

                if (currentEntry < selectedEntry) {
                    selected = process;
                }
            }
        }

        return selected;
    }

    public Tier2Result runNext(int time, Process lastProcess) {

        if (queue.isEmpty()) {
            return null;
        }

        Process current = selectNextProcess(time);

        int contextSwitchStart = -1;

        if (lastProcess != null &&
                lastProcess != current &&
                contextSwitchingTime > 0) {

            contextSwitchStart = time;
            time += contextSwitchingTime;
        }

        int startTime = time;

        if (current.getFirstStartTime() == -1) {
            current.setFirstStartTime(startTime);
        }

        int executedTime = current.getRemainingTime();

        time += executedTime;

        current.setRemainingTime(0);
        current.setFinishTime(time);

        queue.remove(current);
        entryTimes.remove(current);

        return new Tier2Result(
                current,
                contextSwitchStart,
                startTime,
                time);
    }

    public List<Process> getQueue() {
        return new ArrayList<>(queue);
    }

    public static class Tier2Result {

        private final Process process;
        private final int contextSwitchStart;
        private final int startTime;
        private final int endTime;

        public Tier2Result(
                Process process,
                int contextSwitchStart,
                int startTime,
                int endTime) {

            this.process = process;
            this.contextSwitchStart = contextSwitchStart;
            this.startTime = startTime;
            this.endTime = endTime;
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
    }
}
