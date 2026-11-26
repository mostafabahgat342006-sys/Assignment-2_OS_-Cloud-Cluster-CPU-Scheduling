import model.Process;
import scheduler.ACSScheduler;
import scheduler.PreemptiveSJF;
import scheduler.TestCases;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Automates testing across multiple configurations:
 * - Quantum: 2, 3, 4, 5
 * - Context Switching: 0, 1, 2, 3
 * Generates comparison tables, ASCII graphs, and exports data to CSV.
 */
public class ExperimentRunner {

    public static class ExperimentResult {
        public final int testCase;
        public final int quantum;
        public final int contextSwitch;
        public final double sjfAvgWaiting;
        public final double sjfAvgTurnaround;
        public final double acsAvgWaiting;
        public final double acsAvgTurnaround;

        public ExperimentResult(int testCase, int quantum, int contextSwitch,
                                double sjfAvgWaiting, double sjfAvgTurnaround,
                                double acsAvgWaiting, double acsAvgTurnaround) {
            this.testCase = testCase;
            this.quantum = quantum;
            this.contextSwitch = contextSwitch;
            this.sjfAvgWaiting = sjfAvgWaiting;
            this.sjfAvgTurnaround = sjfAvgTurnaround;
            this.acsAvgWaiting = acsAvgWaiting;
            this.acsAvgTurnaround = acsAvgTurnaround;
        }
    }

    public static List<Process> cloneProcesses(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(new Process(p.getProcessID(), p.getArrivalTime(), p.getBurstTime(), p.getInitialPriority()));
        }
        return copy;
    }

    public static List<ExperimentResult> runAllExperiments() {
        List<ExperimentResult> results = new ArrayList<>();

        int[] quantums = {2, 3, 4, 5};
        int[] contextSwitches = {0, 1, 2, 3};

        for (int tc = 1; tc <= 2; tc++) {
            for (int cs : contextSwitches) {
                List<Process> baseForSjf = (tc == 1) ? TestCases.getTestCase1() : TestCases.getTestCase2();
                double[] sjfMetrics = PreemptiveSJF.execute(cloneProcesses(baseForSjf), cs, false);

                for (int q : quantums) {
                    List<Process> baseForAcs = (tc == 1) ? TestCases.getTestCase1() : TestCases.getTestCase2();
                    double[] acsMetrics = ACSScheduler.execute(cloneProcesses(baseForAcs), q, cs, false, false);

                    results.add(new ExperimentResult(
                            tc, q, cs,
                            sjfMetrics[0], sjfMetrics[1],
                            acsMetrics[0], acsMetrics[1]
                    ));
                }
            }
        }

        return results;
    }

    public static void printComparisonTable(List<ExperimentResult> results) {
        System.out.println("\n" + "=".repeat(95));
        System.out.println("COMPREHENSIVE EXPERIMENT RESULTS (Quantum vs Context Switching)");
        System.out.println("=".repeat(95));
        System.out.printf("%-5s | %-3s | %-3s | %-12s | %-12s | %-12s | %-12s%n",
                "TC", "Q", "CS", "SJF Wait", "SJF Turn", "ACS Wait", "ACS Turn");
        System.out.println("-".repeat(95));

        for (ExperimentResult r : results) {
            System.out.printf("TC%-3d | %-3d | %-3d | %-12.2f | %-12.2f | %-12.2f | %-12.2f%n",
                    r.testCase, r.quantum, r.contextSwitch,
                    r.sjfAvgWaiting, r.sjfAvgTurnaround,
                    r.acsAvgWaiting, r.acsAvgTurnaround);
        }
        System.out.println("=".repeat(95));
    }

    public static void exportToCSV(List<ExperimentResult> results, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TestCase,Quantum,ContextSwitch,SJF_AvgWaiting,SJF_AvgTurnaround,ACS_AvgWaiting,ACS_AvgTurnaround");
            for (ExperimentResult r : results) {
                writer.printf("%d,%d,%d,%.2f,%.2f,%.2f,%.2f%n",
                        r.testCase, r.quantum, r.contextSwitch,
                        r.sjfAvgWaiting, r.sjfAvgTurnaround,
                        r.acsAvgWaiting, r.acsAvgTurnaround);
            }
            System.out.println("\nResults successfully exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Running automated scheduler experiments...");
        List<ExperimentResult> results = runAllExperiments();
        printComparisonTable(results);
        exportToCSV(results, "experiment_results.csv");
    }
}
