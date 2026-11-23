import java.util.List;

public class PreemptiveSJF
{

    public static void run(List<Process> processes, int contextSwitchingTime) {

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

        System.out.println("\nProcess Scheduling Results:");

        for (Process p : processes)
        {

            int turnaroundTime = p.getFinishTime() - p.getArrivalTime();

            int waitingTime = turnaroundTime - p.getBurstTime();

            int responseTime = p.getFirstStartTime() - p.getArrivalTime();

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            System.out.println(
                    p.getProcessID() +
                            " | Finish: " + p.getFinishTime() +
                            " | Waiting: " + waitingTime +
                            " | Turnaround: " + turnaroundTime +
                            " | Response: " + responseTime
            );
        }

        System.out.println("\nAverage Waiting Time: " + totalWaitingTime / n);

        System.out.println("Average Turnaround Time: " + totalTurnaroundTime / n);
    }
}
