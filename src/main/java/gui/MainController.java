package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.converter.IntegerStringConverter;
import model.MatrixRowModel;
import model.Process;
import model.ProcessInputRow;
import model.SimulationResult;
import scheduler.TestCases;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Top Header
    @FXML
    private ToggleButton btnSjf;
    @FXML
    private ToggleButton btnAcs;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnRun;

    // Sidebar - Parameters
    @FXML
    private Spinner<Integer> spinnerQuantum;
    @FXML
    private Spinner<Integer> spinnerContextSwitch;
    @FXML
    private Spinner<Integer> spinnerAgingStep;

    // Sidebar - Processes
    @FXML
    private ComboBox<String> comboTestCase;
    @FXML
    private TableView<ProcessInputRow> tableProcessInput;
    @FXML
    private TableColumn<ProcessInputRow, String> colInputPid;
    @FXML
    private TableColumn<ProcessInputRow, Integer> colInputArrival;
    @FXML
    private TableColumn<ProcessInputRow, Integer> colInputBurst;
    @FXML
    private TableColumn<ProcessInputRow, Integer> colInputPriority;
    @FXML
    private Button btnAddProcess;
    @FXML
    private Button btnRemoveProcess;
    @FXML
    private Button btnClearProcesses;

    // Stat Cards
    @FXML
    private Label lblAvgWaiting;
    @FXML
    private Label lblAvgTurnaround;
    @FXML
    private Label lblAvgResponse;
    @FXML
    private Label lblCpuUtilization;

    // Results TabPane & Tabs
    @FXML
    private TabPane tabPaneResults;
    @FXML
    private ScrollPane scrollTimeline;
    @FXML
    private StackPane timelineContainer;
    @FXML
    private Label lblTimelinePlaceholder;
    @FXML
    private Pane paneTimeline;
    @FXML
    private HBox legendContainer;

    @FXML
    private TableView<MatrixRowModel> tableMatrix;
    @FXML
    private TableColumn<MatrixRowModel, Integer> colMatrixTime;
    @FXML
    private TableColumn<MatrixRowModel, String> colMatrixProcess;
    @FXML
    private TableColumn<MatrixRowModel, String> colMatrixTier;

    @FXML
    private TableView<Process> tableMetrics;
    @FXML
    private TableColumn<Process, String> colMetricPid;
    @FXML
    private TableColumn<Process, Integer> colMetricArrival;
    @FXML
    private TableColumn<Process, Integer> colMetricBurst;
    @FXML
    private TableColumn<Process, Integer> colMetricFinish;
    @FXML
    private TableColumn<Process, Integer> colMetricTurnaround;
    @FXML
    private TableColumn<Process, Integer> colMetricWaiting;
    @FXML
    private TableColumn<Process, Integer> colMetricResponse;

    // Tab 4: Quantum and Context Switch Analysis
    @FXML
    private Spinner<Integer> spinnerQFrom;
    @FXML
    private Spinner<Integer> spinnerQTo;
    @FXML
    private Spinner<Integer> spinnerQStep;
    @FXML
    private TextField txtCsValues;
    @FXML
    private Button btnGenerateGraphs;
    @FXML
    private Button btnSavePng;
    @FXML
    private HBox chartsContainer;
    @FXML
    private javafx.scene.chart.LineChart<Number, Number> chartWaiting;
    @FXML
    private javafx.scene.chart.LineChart<Number, Number> chartTurnaround;

    // Child Controllers
    private TimelineController timelineController;
    private MatrixController matrixController;
    private MetricsController metricsController;
    private AnalysisController analysisController;

    private final ObservableList<ProcessInputRow> processInputList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Algorithm ToggleGroup
        ToggleGroup algoGroup = new ToggleGroup();
        btnSjf.setToggleGroup(algoGroup);
        btnAcs.setToggleGroup(algoGroup);
        btnAcs.setSelected(true);

        // Keep at least one selected
        algoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            }
        });

        // 2. Setup Spinners
        spinnerQuantum.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 3));
        spinnerContextSwitch.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spinnerAgingStep.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 4));

        setupSpinnerCommit(spinnerQuantum);
        setupSpinnerCommit(spinnerContextSwitch);
        setupSpinnerCommit(spinnerAgingStep);

        spinnerAgingStep.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                comboTestCase.getSelectionModel().select("Manual Input");
            }
        });

        // 3. Setup Process Input Table
        setupProcessInputTable();

        // 4. Setup Child Controllers
        timelineController = new TimelineController(paneTimeline, lblTimelinePlaceholder, legendContainer);
        matrixController = new MatrixController(tableMatrix, colMatrixTime, colMatrixProcess, colMatrixTier);
        metricsController = new MetricsController(tableMetrics, colMetricPid, colMetricArrival, colMetricBurst,
                colMetricFinish, colMetricTurnaround, colMetricWaiting, colMetricResponse);
        analysisController = new AnalysisController(spinnerQFrom, spinnerQTo, spinnerQStep, txtCsValues,
                chartsContainer, chartWaiting, chartTurnaround);

        // 5. Setup Preset Dropdown
        comboTestCase.getItems().addAll("Official Test Case 1", "Official Test Case 2", "Manual Input");
        comboTestCase.getSelectionModel().select(0);
        comboTestCase.setOnAction(e -> handleTestCaseSelection(comboTestCase.getSelectionModel().getSelectedItem()));

        // Load initial preset (TC1)
        loadTestCase1();
    }

    private void setupProcessInputTable() {
        tableProcessInput.setEditable(true);

        colInputPid.setCellValueFactory(cellData -> cellData.getValue().processIDProperty());
        colInputPid.setCellFactory(TextFieldTableCell.forTableColumn());
        colInputPid.setOnEditCommit(event -> {
            event.getRowValue().setProcessID(event.getNewValue());
            comboTestCase.getSelectionModel().select("Manual Input");
        });

        colInputArrival.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty().asObject());
        colInputArrival.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colInputArrival.setOnEditCommit(event -> {
            if (event.getNewValue() != null) {
                event.getRowValue().setArrivalTime(event.getNewValue());
                comboTestCase.getSelectionModel().select("Manual Input");
            }
        });

        colInputBurst.setCellValueFactory(cellData -> cellData.getValue().burstTimeProperty().asObject());
        colInputBurst.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colInputBurst.setOnEditCommit(event -> {
            if (event.getNewValue() != null) {
                event.getRowValue().setBurstTime(event.getNewValue());
                comboTestCase.getSelectionModel().select("Manual Input");
            }
        });

        colInputPriority.setCellValueFactory(cellData -> cellData.getValue().initialPriorityProperty().asObject());
        colInputPriority.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colInputPriority.setOnEditCommit(event -> {
            if (event.getNewValue() != null) {
                event.getRowValue().setInitialPriority(event.getNewValue());
                comboTestCase.getSelectionModel().select("Manual Input");
            }
        });

        tableProcessInput.setItems(processInputList);
    }

    private void handleTestCaseSelection(String selected) {
        if ("Official Test Case 1".equals(selected)) {
            loadTestCase1();
        } else if ("Official Test Case 2".equals(selected)) {
            loadTestCase2();
        }
    }

    private void loadTestCase1() {
        processInputList.clear();
        for (Process p : TestCases.getTestCase1()) {
            processInputList.add(new ProcessInputRow(p.getProcessID(), p.getArrivalTime(), p.getBurstTime(),
                    p.getInitialPriority()));
        }
        spinnerQuantum.getValueFactory().setValue(TestCases.getQuantum());
        spinnerContextSwitch.getValueFactory().setValue(TestCases.getContextSwitchingTime(1));
        spinnerAgingStep.getValueFactory().setValue(4);
    }

    private void loadTestCase2() {
        processInputList.clear();
        for (Process p : TestCases.getTestCase2()) {
            processInputList.add(new ProcessInputRow(p.getProcessID(), p.getArrivalTime(), p.getBurstTime(),
                    p.getInitialPriority()));
        }
        spinnerQuantum.getValueFactory().setValue(TestCases.getQuantum());
        spinnerContextSwitch.getValueFactory().setValue(TestCases.getContextSwitchingTime(2));
        spinnerAgingStep.getValueFactory().setValue(4);
    }

    @FXML
    private void handleAddProcess() {
        comboTestCase.getSelectionModel().select("Manual Input");
        int nextNum = processInputList.size() + 1;
        processInputList.add(new ProcessInputRow("P" + nextNum, 0, 4, 1));
        tableProcessInput.getSelectionModel().selectLast();
    }

    @FXML
    private void handleRemoveProcess() {
        ProcessInputRow selected = tableProcessInput.getSelectionModel().getSelectedItem();
        if (selected != null) {
            processInputList.remove(selected);
            comboTestCase.getSelectionModel().select("Manual Input");
        }
    }

    @FXML
    private void handleClearProcesses() {
        processInputList.clear();
        comboTestCase.getSelectionModel().select("Manual Input");
    }

    @FXML
    private void handleClearResults() {
        lblAvgWaiting.setText("-");
        lblAvgTurnaround.setText("-");
        lblAvgResponse.setText("-");
        lblCpuUtilization.setText("-");

        timelineController.clear();
        matrixController.clear();
        metricsController.clear();
        if (analysisController != null) {
            analysisController.clear();
        }
    }

    @FXML
    private void handleRunSimulation() {
        List<Process> processes = new ArrayList<>();
        for (ProcessInputRow row : processInputList) {
            processes.add(row.toProcess());
        }

        int quantum = spinnerQuantum.getValue();
        int cs = spinnerContextSwitch.getValue();
        int agingStep = spinnerAgingStep.getValue();
        boolean isAcs = btnAcs.isSelected();

        try {
            SimulationResult result = SimulationController.runSimulation(isAcs, processes, quantum, cs, agingStep);

            // Update Summary Cards
            lblAvgWaiting.setText(String.format("%.2f", result.getAverageWaitingTime()));
            lblAvgTurnaround.setText(String.format("%.2f", result.getAverageTurnaroundTime()));
            lblAvgResponse.setText(String.format("%.2f", result.getAverageResponseTime()));
            lblCpuUtilization.setText(String.format("%.1f%%", result.getCpuUtilization()));

            // Update Timeline
            timelineController.drawTimeline(result.getTimelineSegments(), result.getTotalSimulationTime());

            // Update Time-step Matrix
            if (isAcs) {
                matrixController.setMatrixRows(result.getMatrixRows());
            } else {
                matrixController.clear();
            }

            // Update Process Metrics
            metricsController.setProcesses(result.getProcesses());

        } catch (SimulationController.ValidationException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Warning");
            alert.setHeaderText("Invalid Simulation Input");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleGenerateGraphs() {
        List<Process> processes = new ArrayList<>();
        for (ProcessInputRow row : processInputList) {
            processes.add(row.toProcess());
        }

        int agingStep = spinnerAgingStep.getValue();
        boolean isAcs = btnAcs.isSelected();

        try {
            analysisController.generateAnalysis(processes, agingStep, isAcs);
        } catch (SimulationController.ValidationException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Analysis Warning");
            alert.setHeaderText("Invalid Analysis Input");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSavePng() {
        analysisController.saveAsPng(btnSavePng.getScene().getWindow());
    }

    private void setupSpinnerCommit(Spinner<Integer> spinner) {
        spinner.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && spinner.isEditable()) {
                String text = spinner.getEditor().getText();
                SpinnerValueFactory<Integer> vf = spinner.getValueFactory();
                if (vf != null && vf.getConverter() != null) {
                    try {
                        Integer val = vf.getConverter().fromString(text);
                        if (val != null) {
                            vf.setValue(val);
                        }
                    } catch (Exception e) {
                        spinner.getEditor().setText(vf.getConverter().toString(vf.getValue()));
                    }
                }
            }
        });
    }
}
