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

                if (p.getArrivalTime() <= time  &&  p.getRemainingTime() > 0)
                {

                    if (selected == null || p.getRemainingTime() < selected.getRemainingTime())
                    {

                        selected = p;
                    }
                }
            }

            if (selected == null)
            {
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

            if (selected.getRemainingTime() == 0)
            {
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

        for (Process p : processes)
        {

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
}
