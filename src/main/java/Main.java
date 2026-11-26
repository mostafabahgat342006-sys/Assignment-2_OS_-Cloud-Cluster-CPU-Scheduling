import model.Process;
import scheduler.ACSScheduler;
import scheduler.PreemptiveSJF;
import scheduler.TestCases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Main console interface for the CPU Scheduling Simulator.
 * Provides options to execute Official Test Cases 1 and 2, or enter custom processes manually.
 */
public class Main {

    public static List<Process> cloneProcesses(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(new Process(p.getProcessID(), p.getArrivalTime(), p.getBurstTime(), p.getInitialPriority()));
        }
        return copy;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nCPU Scheduling Simulator");
            System.out.println("1. Run Official Test Case 1 (Q=3, CS=0)");
            System.out.println("2. Run Official Test Case 2 (Q=3, CS=1)");
            System.out.println("3. Manual Input");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter 1, 2, 3, or 4.");
                continue;
            }

            if (choice == 4) {
                System.out.println("Exiting simulator.");
                break;
            }

            if (choice == 1) {
                int q = TestCases.getQuantum();
                int cs = TestCases.getContextSwitchingTime(1);

                System.out.println("\n=======================================================");
                System.out.println("OFFICIAL TEST CASE 1 (Quantum = " + q + ", CS = " + cs + ")");
                System.out.println("=======================================================");

                System.out.println("\n[1] Preemptive Shortest-Job First (SJF):");
                PreemptiveSJF.run(cloneProcesses(TestCases.getTestCase1()), cs);

                System.out.println("\n[2] Adaptive Container Scheduler (ACS):");
                ACSScheduler.run(cloneProcesses(TestCases.getTestCase1()), q, cs);

            } else if (choice == 2) {
                int q = TestCases.getQuantum();
                int cs = TestCases.getContextSwitchingTime(2);

                System.out.println("\n=======================================================");
                System.out.println("OFFICIAL TEST CASE 2 (Quantum = " + q + ", CS = " + cs + ")");
                System.out.println("=======================================================");

                System.out.println("\n[1] Preemptive Shortest-Job First (SJF):");
                PreemptiveSJF.run(cloneProcesses(TestCases.getTestCase2()), cs);

                System.out.println("\n[2] Adaptive Container Scheduler (ACS):");
                ACSScheduler.run(cloneProcesses(TestCases.getTestCase2()), q, cs);

            } else if (choice == 3) {
                handleManualInput(scanner);
            } else {
                System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }

        scanner.close();
    }

    private static void handleManualInput(Scanner scanner) {
        System.out.println("\n--- Manual Input ---");

        int numProcesses = readInt(scanner, "Enter number of processes: ", 1,
                "Error: Number of processes must be an integer greater than zero.");

        List<Process> processes = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();

        for (int i = 1; i <= numProcesses; i++) {
            System.out.println("\nProcess " + i + ":");

            String pid = readUniqueProcessId(scanner, "Process ID: ", seenIds);

            int arrivalTime = readInt(scanner, "Arrival Time: ", 0,
                    "Error: Arrival Time must be an integer zero or greater.");

            int burstTime = readInt(scanner, "Burst Time: ", 1,
                    "Error: Burst Time must be an integer greater than zero.");

            int priority = readInt(scanner, "Initial Priority: ", 1,
                    "Error: Initial Priority must be a positive integer greater than zero.");

            processes.add(new Process(pid, arrivalTime, burstTime, priority));
        }

        System.out.println();
        int quantum = readInt(scanner, "Enter Quantum: ", 1,
                "Error: Quantum must be an integer greater than zero.");

        int cs = readInt(scanner, "Enter Context Switching Time: ", 0,
                "Error: Context Switching Time must be an integer zero or greater.");

        System.out.println("\n=======================================================");
        System.out.println("MANUAL INPUT EXECUTION (Quantum = " + quantum + ", CS = " + cs + ")");
        System.out.println("=======================================================");

        System.out.println("\n[1] Preemptive Shortest-Job First (SJF):");
        PreemptiveSJF.run(cloneProcesses(processes), cs);

        System.out.println("\n[2] Adaptive Container Scheduler (ACS):");
        ACSScheduler.run(cloneProcesses(processes), quantum, cs);
    }

    private static int readInt(Scanner scanner, String prompt, int min, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min) {
                    return val;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println(errorMsg);
        }
    }

    private static String readUniqueProcessId(Scanner scanner, String prompt, Set<String> seenIds) {
        while (true) {
            System.out.print(prompt);
            String id = scanner.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("Error: Process ID cannot be empty. Please enter a valid ID.");
                continue;
            }
            if (seenIds.contains(id)) {
                System.out.println("Error: Process ID must be unique. '" + id + "' is already used. Please enter a different ID.");
                continue;
            }
            seenIds.add(id);
            return id;
        }
    }
}
