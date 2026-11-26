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
            List<Process> baseList = (tc == 1) ? TestCases.getTestCase1() : TestCases.getTestCase2();

            for (int q : quantums) {
                for (int cs : contextSwitches) {
                    // Fresh copies for each run
                    List<Process> sjfProcs = cloneProcesses(baseList);
                    List<Process> acsProcs = cloneProcesses(baseList);

                    double[] sjfMetrics = PreemptiveSJF.execute(sjfProcs, cs, false);
                    double[] acsMetrics = ACSScheduler.execute(acsProcs, q, cs, false, false);

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

    public static void displayComparisonTables(List<ExperimentResult> results) {
        System.out.println("\n======================================================================================================================");
        System.out.println("                                EXPERIMENT RESULTS: SJF vs ACS COMPARISON TABLE");
        System.out.println("======================================================================================================================");
        System.out.printf("%-10s | %-8s | %-15s | %-15s | %-18s | %-15s | %-18s%n",
                "Test Case", "Quantum", "Context Switch", "SJF Avg Waiting", "SJF Avg Turnaround", "ACS Avg Waiting", "ACS Avg Turnaround");
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        for (ExperimentResult r : results) {
            System.out.printf("Test Case %-1d | %-8d | %-15d | %-15.2f | %-18.2f | %-15.2f | %-18.2f%n",
                    r.testCase, r.quantum, r.contextSwitch,
                    r.sjfAvgWaiting, r.sjfAvgTurnaround,
                    r.acsAvgWaiting, r.acsAvgTurnaround);
        }

        System.out.println("======================================================================================================================");
    }

    public static void displayAsciiAnalysis(List<ExperimentResult> results) {
        System.out.println("\n======================================================================================================================");
        System.out.println("                                         BASIC VISUAL GRAPHS & SENSITIVITY ANALYSIS");
        System.out.println("======================================================================================================================");

        for (int tc = 1; tc <= 2; tc++) {
            System.out.printf("%n>>> TEST CASE %d: Effect of Quantum (Q) on ACS Average Turnaround Time (at CS = 1):%n", tc);
            for (ExperimentResult r : results) {
                if (r.testCase == tc && r.contextSwitch == 1) {
                    int barLength = (int) Math.round(r.acsAvgTurnaround * 2.5);
                    String bar = "=".repeat(Math.max(1, barLength));
                    System.out.printf("  Q = %d | %-35s %.2f time units%n", r.quantum, bar, r.acsAvgTurnaround);
                }
            }

            System.out.printf("%n>>> TEST CASE %d: Effect of Context Switch (CS) on ACS Average Turnaround Time (at Q = 3):%n", tc);
            for (ExperimentResult r : results) {
                if (r.testCase == tc && r.quantum == 3) {
                    int barLength = (int) Math.round(r.acsAvgTurnaround * 2.5);
                    String bar = "#".repeat(Math.max(1, barLength));
                    System.out.printf(" CS = %d | %-35s %.2f time units%n", r.contextSwitch, bar, r.acsAvgTurnaround);
                }
            }

            System.out.printf("%n>>> TEST CASE %d: SJF vs ACS Average Waiting Time (at Q = 3):%n", tc);
            for (ExperimentResult r : results) {
                if (r.testCase == tc && r.quantum == 3) {
                    System.out.printf(" CS = %d -> SJF Avg Wait: %5.2f | ACS Avg Wait: %5.2f%n",
                            r.contextSwitch, r.sjfAvgWaiting, r.acsAvgWaiting);
                }
            }
        }
        System.out.println("\n======================================================================================================================");
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
            System.out.println(">> Successfully exported experiment results to: " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        List<ExperimentResult> results = runAllExperiments();
        displayComparisonTables(results);
        displayAsciiAnalysis(results);
        exportToCSV(results, "experiment_results.csv");
    }
}
