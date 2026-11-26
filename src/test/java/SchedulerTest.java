import gui.SimulationController;
import model.MatrixRowModel;
import model.Process;
import model.SimulationResult;
import model.TimelineSegment;
import org.junit.jupiter.api.Test;
import scheduler.ACSScheduler;
import scheduler.PreemptiveSJF;
import scheduler.TestCases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerTest {

    @Test
    public void testOfficialTestCase1_SJF() throws Exception {
        List<Process> tc1 = TestCases.getTestCase1();
        int cs = TestCases.getContextSwitchingTime(1); // 0

        SimulationResult result = SimulationController.runSimulation(false, tc1, 3, cs);

        assertEquals("Preemptive SJF", result.getAlgorithmName());
        assertEquals(2.00, result.getAverageWaitingTime(), 0.01);
        assertEquals(6.33, result.getAverageTurnaroundTime(), 0.01);

        // Verify individual processes
        Process p1 = find(result.getProcesses(), "P1");
        Process p2 = find(result.getProcesses(), "P2");
        Process p3 = find(result.getProcesses(), "P3");

        assertEquals(4, p1.getFinishTime());
        assertEquals(0, p1.getWaitingTime());
        assertEquals(4, p1.getTurnaroundTime());

        assertEquals(13, p2.getFinishTime());
        assertEquals(5, p2.getWaitingTime());
        assertEquals(12, p2.getTurnaroundTime());

        assertEquals(6, p3.getFinishTime());
        assertEquals(1, p3.getWaitingTime());
        assertEquals(3, p3.getTurnaroundTime());

        // Verify timeline segments
        assertFalse(result.getTimelineSegments().isEmpty());
    }

    @Test
    public void testOfficialTestCase1_ACS() throws Exception {
        List<Process> tc1 = TestCases.getTestCase1();
        int q = TestCases.getQuantum(); // 3
        int cs = TestCases.getContextSwitchingTime(1); // 0

        SimulationResult result = SimulationController.runSimulation(true, tc1, q, cs);

        assertEquals("Adaptive Container Scheduler", result.getAlgorithmName());
        assertEquals(4.33, result.getAverageWaitingTime(), 0.01);
        assertEquals(8.67, result.getAverageTurnaroundTime(), 0.01);

        Process p1 = find(result.getProcesses(), "P1");
        Process p2 = find(result.getProcesses(), "P2");
        Process p3 = find(result.getProcesses(), "P3");

        assertEquals(9, p1.getFinishTime());
        assertEquals(5, p1.getWaitingTime());
        assertEquals(9, p1.getTurnaroundTime());

        assertEquals(13, p2.getFinishTime());
        assertEquals(5, p2.getWaitingTime());
        assertEquals(12, p2.getTurnaroundTime());

        assertEquals(8, p3.getFinishTime());
        assertEquals(3, p3.getWaitingTime());
        assertEquals(5, p3.getTurnaroundTime());

        // Verify exact 15 matrix rows
        List<MatrixRowModel> matrix = result.getMatrixRows();
        assertEquals(15, matrix.size());
        assertEquals(0, matrix.get(0).getTime());
        assertEquals(14, matrix.get(14).getTime());

        // Verify Gantt segments
        List<TimelineSegment> timeline = result.getTimelineSegments();
        assertEquals(5, timeline.size());
        assertEquals("P1", timeline.get(0).getProcessId());
        assertEquals(0, timeline.get(0).getStartTime());
        assertEquals(3, timeline.get(0).getEndTime());

        assertEquals("P2", timeline.get(1).getProcessId());
        assertEquals(3, timeline.get(1).getStartTime());
        assertEquals(6, timeline.get(1).getEndTime());

        assertEquals("P3", timeline.get(2).getProcessId());
        assertEquals(6, timeline.get(2).getStartTime());
        assertEquals(8, timeline.get(2).getEndTime());

        assertEquals("P1", timeline.get(3).getProcessId());
        assertEquals(8, timeline.get(3).getStartTime());
        assertEquals(9, timeline.get(3).getEndTime());

        assertEquals("P2", timeline.get(4).getProcessId());
        assertEquals(9, timeline.get(4).getStartTime());
        assertEquals(13, timeline.get(4).getEndTime());
    }

    @Test
    public void testOfficialTestCase2_SJF() throws Exception {
        List<Process> tc2 = TestCases.getTestCase2();
        int cs = TestCases.getContextSwitchingTime(2); // 1

        SimulationResult result = SimulationController.runSimulation(false, tc2, 3, cs);

        assertEquals("Preemptive SJF", result.getAlgorithmName());
        assertEquals(4.00, result.getAverageWaitingTime(), 0.01);
        assertEquals(9.00, result.getAverageTurnaroundTime(), 0.01);

        Process p1 = find(result.getProcesses(), "P1");
        Process p2 = find(result.getProcesses(), "P2");
        Process p3 = find(result.getProcesses(), "P3");

        assertEquals(5, p1.getFinishTime());
        assertEquals(0, p1.getWaitingTime());
        assertEquals(5, p1.getTurnaroundTime());

        assertEquals(17, p2.getFinishTime());
        assertEquals(10, p2.getWaitingTime());
        assertEquals(16, p2.getTurnaroundTime());

        assertEquals(10, p3.getFinishTime());
        assertEquals(2, p3.getWaitingTime());
        assertEquals(6, p3.getTurnaroundTime());
    }

    @Test
    public void testOfficialTestCase2_ACS() throws Exception {
        List<Process> tc2 = TestCases.getTestCase2();
        int q = TestCases.getQuantum(); // 3
        int cs = TestCases.getContextSwitchingTime(2); // 1

        SimulationResult result = SimulationController.runSimulation(true, tc2, q, cs);

        assertEquals("Adaptive Container Scheduler", result.getAlgorithmName());
        assertEquals(11.00, result.getAverageWaitingTime(), 0.01);
        assertEquals(16.00, result.getAverageTurnaroundTime(), 0.01);

        Process p1 = find(result.getProcesses(), "P1");
        Process p2 = find(result.getProcesses(), "P2");
        Process p3 = find(result.getProcesses(), "P3");

        assertEquals(18, p1.getFinishTime());
        assertEquals(13, p1.getWaitingTime());
        assertEquals(18, p1.getTurnaroundTime());

        assertEquals(15, p2.getFinishTime());
        assertEquals(8, p2.getWaitingTime());
        assertEquals(14, p2.getTurnaroundTime());

        assertEquals(20, p3.getFinishTime());
        assertEquals(12, p3.getWaitingTime());
        assertEquals(16, p3.getTurnaroundTime());

        // Verify exact 21 matrix rows
        List<MatrixRowModel> matrix = result.getMatrixRows();
        assertEquals(21, matrix.size());
        assertEquals(0, matrix.get(0).getTime());
        assertEquals(20, matrix.get(20).getTime());

        // Verify Gantt segments including context switches
        List<TimelineSegment> timeline = result.getTimelineSegments();
        assertEquals(11, timeline.size());
        assertEquals("P1", timeline.get(0).getProcessId());
        assertTrue(timeline.get(1).isContextSwitch());
        assertEquals("P2", timeline.get(2).getProcessId());
        assertTrue(timeline.get(3).isContextSwitch());
        assertEquals("P3", timeline.get(4).getProcessId());
        assertTrue(timeline.get(5).isContextSwitch());
        assertEquals("P2", timeline.get(6).getProcessId());
        assertTrue(timeline.get(7).isContextSwitch());
        assertEquals("P1", timeline.get(8).getProcessId());
        assertTrue(timeline.get(9).isContextSwitch());
        assertEquals("P3", timeline.get(10).getProcessId());
    }

    @Test
    public void testManualInputExecution() throws Exception {
        List<Process> processes = Arrays.asList(
                new Process("P1", 0, 5, 2),
                new Process("P2", 2, 4, 1),
                new Process("P3", 4, 6, 3)
        );

        SimulationResult sjf = SimulationController.runSimulation(false, processes, 3, 1);
        assertEquals(3.67, sjf.getAverageWaitingTime(), 0.01);
        assertEquals(8.67, sjf.getAverageTurnaroundTime(), 0.01);

        SimulationResult acs = SimulationController.runSimulation(true, processes, 3, 1);
        assertEquals(9.67, acs.getAverageWaitingTime(), 0.01);
        assertEquals(14.67, acs.getAverageTurnaroundTime(), 0.01);
        assertFalse(acs.getMatrixRows().isEmpty());
    }

    @Test
    public void testValidationRules() {
        // Empty processes
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, new ArrayList<>(), 3, 0));

        // Duplicate IDs
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", 0, 5, 1),
                        new Process("P1", 1, 3, 2)
                ), 3, 0));

        // Negative arrival
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", -1, 5, 1)
                ), 3, 0));

        // Zero burst
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", 0, 0, 1)
                ), 3, 0));

        // Zero priority
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", 0, 5, 0)
                ), 3, 0));

        // Zero quantum
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", 0, 5, 1)
                ), 0, 0));

        // Negative CS
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", 0, 5, 1)
                ), 3, -1));

        // Zero or Negative Aging Step
        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", 0, 5, 1)
                ), 3, 0, 0));

        assertThrows(SimulationController.ValidationException.class, () ->
                SimulationController.runSimulation(true, Arrays.asList(
                        new Process("P1", 0, 5, 1)
                ), 3, 0, -2));
    }

    @Test
    public void testConfigurableAgingStepInTier2() {
        // Test 1: Direct effective priority calculation
        scheduler.Tier2Scheduler tier2Default = new scheduler.Tier2Scheduler(0, 4);
        scheduler.Tier2Scheduler tier2Fast = new scheduler.Tier2Scheduler(0, 2);

        Process p = new Process("P1", 0, 10, 5);
        tier2Default.addProcess(p, 0);
        tier2Fast.addProcess(p, 0);

        // At time 4:
        // Default (interval=4): 5 - (4/4) = 4
        // Fast (interval=2):    5 - (4/2) = 3
        assertEquals(4, tier2Default.calculateEffectivePriority(p, 4));
        assertEquals(3, tier2Fast.calculateEffectivePriority(p, 4));

        // At time 8:
        // Default: 5 - (8/4) = 3
        // Fast:    5 - (8/2) = 1
        assertEquals(3, tier2Default.calculateEffectivePriority(p, 8));
        assertEquals(1, tier2Fast.calculateEffectivePriority(p, 8));

        // Test 2: Observable difference in execution order
        // P_slow has initial priority 4, entered at 0.
        // P_other has initial priority 3, entered at 4.
        // With aging interval 2: at time 4, P_slow has waited 4 -> eff = 4 - (4/2) = 2.
        // P_other has eff = 3.
        // Therefore P_slow (eff 2) beats P_other (eff 3)!
        // With aging interval 10: at time 4, P_slow has eff = 4 - (4/10) = 4.
        // P_other (eff 3) beats P_slow (eff 4)!
        scheduler.Tier2Scheduler schedFast = new scheduler.Tier2Scheduler(0, 2);
        Process pA = new Process("PA", 0, 5, 4);
        Process pB = new Process("PB", 0, 5, 3);
        schedFast.addProcess(pA, 0);
        schedFast.addProcess(pB, 4);
        assertEquals("PA", schedFast.peekNextProcess(4).getProcessID());

        scheduler.Tier2Scheduler schedSlow = new scheduler.Tier2Scheduler(0, 10);
        schedSlow.addProcess(pA, 0);
        schedSlow.addProcess(pB, 4);
        assertEquals("PB", schedSlow.peekNextProcess(4).getProcessID());
    }

    @Test
    public void testAnalysisControllerSweepAndValidation() throws Exception {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {}

        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        javafx.application.Platform.runLater(() -> {
            try {
                javafx.scene.control.Spinner<Integer> qFrom = new javafx.scene.control.Spinner<>(1, 100, 2);
                javafx.scene.control.Spinner<Integer> qTo = new javafx.scene.control.Spinner<>(1, 100, 4);
                javafx.scene.control.Spinner<Integer> qStep = new javafx.scene.control.Spinner<>(1, 50, 1);
                javafx.scene.control.TextField csText = new javafx.scene.control.TextField("0, 1");

                javafx.scene.layout.HBox container = new javafx.scene.layout.HBox();
                javafx.scene.chart.LineChart<Number, Number> chartWait = new javafx.scene.chart.LineChart<>(
                        new javafx.scene.chart.NumberAxis(), new javafx.scene.chart.NumberAxis());
                javafx.scene.chart.LineChart<Number, Number> chartTurn = new javafx.scene.chart.LineChart<>(
                        new javafx.scene.chart.NumberAxis(), new javafx.scene.chart.NumberAxis());

                gui.AnalysisController controller = new gui.AnalysisController(
                        qFrom, qTo, qStep, csText, container, chartWait, chartTurn);

                // Test parse CS values
                List<Integer> parsedCs = controller.parseCsValues();
                assertEquals(Arrays.asList(0, 1), parsedCs);

                // Test invalid CS values
                csText.setText("-1, 2");
                assertThrows(SimulationController.ValidationException.class, controller::parseCsValues);

                csText.setText("abc, 2");
                assertThrows(SimulationController.ValidationException.class, controller::parseCsValues);

                csText.setText("0, 1");

                // Test Generate Analysis
                List<Process> workload = Arrays.asList(
                        new Process("P1", 0, 5, 2),
                        new Process("P2", 1, 4, 1)
                );

                controller.generateAnalysis(workload, 4, true);

                assertTrue(controller.hasGeneratedData());
                assertEquals(2, chartWait.getData().size()); // 2 series for CS=0, CS=1
                assertEquals(2, chartTurn.getData().size());

                // Points per series: Q = 2, 3, 4 -> 3 points
                assertEquals(3, chartWait.getData().get(0).getData().size());
                assertEquals(3, chartTurn.getData().get(0).getData().size());

                // Verify first data point (Q=2, CS=0)
                SimulationResult expectedQ2CS0 = SimulationController.runSimulation(true, workload, 2, 0, 4);
                assertEquals(expectedQ2CS0.getAverageWaitingTime(),
                        chartWait.getData().get(0).getData().get(0).getYValue().doubleValue(), 0.001);
                assertEquals(expectedQ2CS0.getAverageTurnaroundTime(),
                        chartTurn.getData().get(0).getData().get(0).getYValue().doubleValue(), 0.001);

            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        if (error[0] != null) {
            error[0].printStackTrace();
            fail("AnalysisController test failed: " + error[0].getMessage());
        }
    }

    @Test
    public void testProcessStateIsolation() throws Exception {
        List<Process> original = TestCases.getTestCase1();
        assertEquals(-1, original.get(0).getFinishTime());

        SimulationResult res1 = SimulationController.runSimulation(false, original, 3, 0);
        // Original process list should not be mutated
        assertEquals(-1, original.get(0).getFinishTime());

        SimulationResult res2 = SimulationController.runSimulation(true, original, 3, 0);
        assertEquals(-1, original.get(0).getFinishTime());
    }

    @Test
    public void testFxmlLoading() throws Exception {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Toolkit already initialized
        }

        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        javafx.application.Platform.runLater(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
                javafx.scene.Parent root = loader.load();
                assertNotNull(root);
                gui.MainController controller = loader.getController();
                assertNotNull(controller);
            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        if (error[0] != null) {
            error[0].printStackTrace();
            fail("FXML loading failed: " + error[0].getMessage());
        }
    }

    private Process find(List<Process> list, String pid) {
        for (Process p : list) {
            if (p.getProcessID().equals(pid)) return p;
        }
        return null;
    }
}
