package scheduler;

import model.Process;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class Tier1Scheduler {

    private final int quantum;
    private final int contextSwitchingTime;
    private final List<Process> incoming;
    private final Queue<Process> queue = new ArrayDeque<>();
    private int nextArrivalIndex = 0;

    public Tier1Scheduler(List<Process> processes, int quantum, int contextSwitchingTime) {
        this.quantum = quantum;
        this.contextSwitchingTime = contextSwitchingTime;
        this.incoming = new ArrayList<>(processes);
        this.incoming.sort(Comparator.comparingInt(Process::getArrivalTime));
    }

    public void admitArrivals(int time) {
        while (nextArrivalIndex < incoming.size() && incoming.get(nextArrivalIndex).getArrivalTime() <= time) {
            queue.add(incoming.get(nextArrivalIndex));
            nextArrivalIndex++;
        }
    }

    public boolean hasReady() {
        return !queue.isEmpty();
    }

    public boolean hasPendingArrivals() {
        return nextArrivalIndex < incoming.size();
    }

    public int getNextArrivalTime() {
        if (!hasPendingArrivals()) {
            return -1;
        }
        return incoming.get(nextArrivalIndex).getArrivalTime();
    }

    public List<Process> getQueueSnapshot() {
        return new ArrayList<>(queue);
    }

    public Process peekNextProcess() {
        return queue.peek();
    }

    public Tier1Result runSlice(int time, Process lastProcess) {
        admitArrivals(time);

        Process current = queue.poll();

        if (current == null) {
            return null;
        }

        int contextSwitchStart = -1;

        if (lastProcess != null && lastProcess != current && contextSwitchingTime > 0) {
            contextSwitchStart = time;
            time += contextSwitchingTime;
            admitArrivals(time);
        }

        int startTime = time;

        if (current.getFirstStartTime() == -1) {
            current.setFirstStartTime(time);
        }

        int executed = Math.min(quantum, current.getRemainingTime());

        time += executed;
        current.setRemainingTime(current.getRemainingTime() - executed);

        admitArrivals(time);

        boolean finished = current.getRemainingTime() == 0;
        boolean demoted = !finished;

        if (finished) {
            current.setFinishTime(time);
        }

        return new Tier1Result(current, contextSwitchStart, startTime, time, finished, demoted);
    }
}
