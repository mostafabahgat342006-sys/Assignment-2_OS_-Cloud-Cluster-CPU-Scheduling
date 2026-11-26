package gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.Process;
import model.SimulationResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Quantum and Context Switch Analysis Tab.
 * Sweeps over Quantum values (Q from to Q to by Q step) and Context Switch values,
 * generating Average Waiting Time and Average Turnaround Time curves.
 */
public class AnalysisController {

    private final Spinner<Integer> spinnerQFrom;
    private final Spinner<Integer> spinnerQTo;
    private final Spinner<Integer> spinnerQStep;
    private final TextField txtCsValues;

    private final HBox chartsContainer;
    private final LineChart<Number, Number> chartWaiting;
    private final LineChart<Number, Number> chartTurnaround;

    private boolean hasGeneratedData = false;

    // Distinct palette for different CS values matching reference style
    private static final String[] CS_COLORS = {
            "#ea580c", // CS 0: Orange / Red
            "#eab308", // CS 1: Amber / Yellow
            "#16a34a", // CS 2: Green
            "#0ea5e9", // CS 3: Blue / Cyan
            "#8b5cf6", // CS 4: Violet
            "#ec4899", // CS 5: Pink
            "#64748b"  // CS 6+: Slate
    };

    public AnalysisController(Spinner<Integer> spinnerQFrom,
                              Spinner<Integer> spinnerQTo,
                              Spinner<Integer> spinnerQStep,
                              TextField txtCsValues,
                              HBox chartsContainer,
                              LineChart<Number, Number> chartWaiting,
                              LineChart<Number, Number> chartTurnaround) {
        this.spinnerQFrom = spinnerQFrom;
        this.spinnerQTo = spinnerQTo;
        this.spinnerQStep = spinnerQStep;
        this.txtCsValues = txtCsValues;
        this.chartsContainer = chartsContainer;
        this.chartWaiting = chartWaiting;
        this.chartTurnaround = chartTurnaround;

        initialize();
    }

    private void initialize() {
        if (spinnerQFrom.getValueFactory() == null) {
            spinnerQFrom.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        }
        if (spinnerQTo.getValueFactory() == null) {
            spinnerQTo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));
        }
        if (spinnerQStep.getValueFactory() == null) {
            spinnerQStep.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        }

        chartWaiting.setAnimated(false);
        chartTurnaround.setAnimated(false);
        chartWaiting.setLegendSide(Side.BOTTOM);
        chartTurnaround.setLegendSide(Side.BOTTOM);
    }

    public List<Integer> parseCsValues() throws SimulationController.ValidationException {
        String raw = txtCsValues.getText();
        if (raw == null || raw.trim().isEmpty()) {
            throw new SimulationController.ValidationException("Context Switch values cannot be empty. Please enter comma-separated non-negative integers (e.g. 0, 1, 2, 3).");
        }

        String[] tokens = raw.split(",");
        List<Integer> list = new ArrayList<>();
        for (String t : tokens) {
            String s = t.trim();
            if (s.isEmpty()) continue;
            try {
                int val = Integer.parseInt(s);
                if (val < 0) {
                    throw new SimulationController.ValidationException("Context Switch value '" + s + "' is negative. All CS values must be 0 or greater.");
                }
                if (!list.contains(val)) {
                    list.add(val);
                }
            } catch (NumberFormatException e) {
                throw new SimulationController.ValidationException("Invalid Context Switch value '" + s + "'. Please enter valid integers separated by commas.");
            }
        }

        if (list.isEmpty()) {
            throw new SimulationController.ValidationException("Please enter at least one valid Context Switch value.");
        }

        return list;
    }

    public void generateAnalysis(List<Process> processes, int agingStep, boolean isAcs) throws SimulationController.ValidationException {
        // Validate Quantum parameters
        int qFrom = spinnerQFrom.getValue();
        int qTo = spinnerQTo.getValue();
        int qStep = spinnerQStep.getValue();

        if (qFrom <= 0) {
            throw new SimulationController.ValidationException("Q from must be a positive integer greater than zero.");
        }
        if (qTo < qFrom) {
            throw new SimulationController.ValidationException("Q to (" + qTo + ") must be greater than or equal to Q from (" + qFrom + ").");
        }
        if (qStep <= 0) {
            throw new SimulationController.ValidationException("Q step must be a positive integer greater than zero.");
        }

        List<Integer> csValues = parseCsValues();

        if (processes == null || processes.isEmpty()) {
            throw new SimulationController.ValidationException("Please enter at least one process in the sidebar to generate analysis.");
        }

        // Clear existing chart data
        chartWaiting.getData().clear();
        chartTurnaround.getData().clear();

        String algoTitleSuffix = isAcs ? " (ACS)" : " (Preemptive SJF)";
        chartWaiting.setTitle("Average waiting time" + algoTitleSuffix);
        chartTurnaround.setTitle("Average turnaround time" + algoTitleSuffix);

        // Run simulation sweep
        int colorIndex = 0;
        for (int cs : csValues) {
            XYChart.Series<Number, Number> waitSeries = new XYChart.Series<>();
            waitSeries.setName("CS = " + cs);

            XYChart.Series<Number, Number> turnSeries = new XYChart.Series<>();
            turnSeries.setName("CS = " + cs);

            for (int q = qFrom; q <= qTo; q += qStep) {
                SimulationResult result = SimulationController.runSimulation(isAcs, processes, q, cs, agingStep);
                waitSeries.getData().add(new XYChart.Data<>(q, result.getAverageWaitingTime()));
                turnSeries.getData().add(new XYChart.Data<>(q, result.getAverageTurnaroundTime()));
            }

            chartWaiting.getData().add(waitSeries);
            chartTurnaround.getData().add(turnSeries);

            applySeriesColor(waitSeries, colorIndex);
            applySeriesColor(turnSeries, colorIndex);
            colorIndex++;
        }

        hasGeneratedData = true;
    }

    private void applySeriesColor(XYChart.Series<Number, Number> series, int index) {
        String color = CS_COLORS[index % CS_COLORS.length];
        if (series.getNode() != null) {
            series.getNode().setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 2px;");
        }
    }

    public void saveAsPng(Window ownerWindow) {
        if (!hasGeneratedData || chartWaiting.getData().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Save as PNG");
            alert.setHeaderText("No Analysis Generated");
            alert.setContentText("No analysis has been generated yet. Please click 'Generate graphs' first.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Analysis Charts as PNG");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image (*.png)", "*.png"));
        fileChooser.setInitialFileName("quantum_context_switch_analysis.png");

        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file == null) {
            return; // User cancelled
        }

        try {
            SnapshotParameters params = new SnapshotParameters();
            WritableImage fxImage = chartsContainer.snapshot(params, null);
            BufferedImage bImage = SwingFXUtils.fromFXImage(fxImage, null);

            ImageIO.write(bImage, "png", file);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Save Successful");
            info.setHeaderText("Chart Exported Successfully");
            info.setContentText("The analysis charts have been saved to:\n" + file.getAbsolutePath());
            info.showAndWait();

        } catch (IOException | RuntimeException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Export Error");
            error.setHeaderText("Failed to Save PNG");
            error.setContentText("An error occurred while saving the PNG image:\n" + e.getMessage());
            error.showAndWait();
        }
    }

    public void clear() {
        chartWaiting.getData().clear();
        chartTurnaround.getData().clear();
        hasGeneratedData = false;
    }

    public boolean hasGeneratedData() {
        return hasGeneratedData;
    }
}
