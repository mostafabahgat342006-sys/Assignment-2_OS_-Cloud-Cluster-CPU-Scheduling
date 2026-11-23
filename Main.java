
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("CPU Scheduling Simulator");
        System.out.println("1. Official Test Case 1");
        System.out.println("2. Official Test Case 2");
        System.out.println("3. Custom CS Experiment");

        System.out.print("Choose Test Case: ");
        int choice = input.nextInt();

        List<Process> processes;
        int cs;

        if (choice == 1)
        {

            processes = TestCases.getTestCase1();
            cs = TestCases.getContextSwitchingTime(1);

        }
        else if (choice == 2)
        {

            processes = TestCases.getTestCase2();
            cs = TestCases.getContextSwitchingTime(2);

        }
        else if (choice == 3)
        {

            System.out.println("1. Use Test Case 1");
            System.out.println("2. Use Test Case 2");

            int testChoice = input.nextInt();

            if (testChoice == 1)
            {
                processes = TestCases.getTestCase1();
            }
            else if (testChoice == 2)
            {
                processes = TestCases.getTestCase2();
            }
            else
            {
                System.out.println("Invalid Test Case");
                input.close();
                return;
            }

            System.out.print("Enter Context Switching Time: ");
            cs = input.nextInt();

            if (cs < 0)
            {
                System.out.println("Invalid Context Switching Time");
                input.close();
                return;
            }

        }
        else
        {

            System.out.println("Invalid Choice");
            input.close();
            return;
        }

        System.out.println("\nRunning Preemptive SJF...");
        System.out.println("Context Switching Time: " + cs);

        PreemptiveSJF.run(processes, cs);

        input.close();
    }
}
