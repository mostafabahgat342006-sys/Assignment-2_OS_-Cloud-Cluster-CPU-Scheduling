package scheduler;

import model.Process;
import java.util.ArrayList;
import java.util.List;

public class TestCases {

    public static List<Process> getTestCase1() {
        List<Process> processes = new ArrayList<>();

        processes.add(new Process("P1", 0, 4, 2));
        processes.add(new Process("P2", 1, 7, 3));
        processes.add(new Process("P3", 3, 2, 1));

        return processes;
    }

    public static List<Process> getTestCase2() {
        List<Process> processes = new ArrayList<>();

        processes.add(new Process("P1", 0, 5, 4));
        processes.add(new Process("P2", 1, 6, 2));
        processes.add(new Process("P3", 4, 4, 3));

        return processes;
    }

    public static int getQuantum() {
        return 3;
    }

    public static int getContextSwitchingTime(int testCaseNumber) {
        if (testCaseNumber == 1) {
            return 0;
        }

        if (testCaseNumber == 2) {
            return 1;
        }

        throw new IllegalArgumentException("Invalid test case number");
    }
}
