package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Process;

import java.util.List;

/**
 * Controller for the Process Metrics TableView.
 * Formatted with Midnight Control Center styling and clear numerical alignment.
 */
public class MetricsController {

    private final TableView<Process> tableMetrics;
    private final ObservableList<Process> processList = FXCollections.observableArrayList();

    public MetricsController(TableView<Process> tableMetrics,
                             TableColumn<Process, String> colPid,
                             TableColumn<Process, Integer> colArrival,
                             TableColumn<Process, Integer> colBurst,
                             TableColumn<Process, Integer> colFinish,
                             TableColumn<Process, Integer> colTurnaround,
                             TableColumn<Process, Integer> colWaiting,
                             TableColumn<Process, Integer> colResponse) {
        this.tableMetrics = tableMetrics;

        colPid.setCellValueFactory(new PropertyValueFactory<>("processID"));
        colArrival.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        colBurst.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        colFinish.setCellValueFactory(new PropertyValueFactory<>("finishTime"));
        colTurnaround.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        colWaiting.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        colResponse.setCellValueFactory(new PropertyValueFactory<>("responseTime"));

        // Process ID column: Electric blue bold text
        colPid.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #4F8CFF; -fx-font-weight: bold;");
                }
            }
        });

        // Numeric columns: Center-aligned monospace soft white text
        setupNumericColumn(colArrival);
        setupNumericColumn(colBurst);
        setupNumericColumn(colFinish);
        setupNumericColumn(colTurnaround);
        setupNumericColumn(colWaiting);
        setupNumericColumn(colResponse);

        tableMetrics.setItems(processList);
    }

    private <T> void setupNumericColumn(TableColumn<Process, T> col) {
        col.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #F1F5FF; -fx-font-family: 'Consolas', monospace;");
                }
            }
        });
    }

    public void setProcesses(List<Process> processes) {
        processList.clear();
        if (processes != null) {
            processList.addAll(processes);
        }
    }

    public void clear() {
        processList.clear();
    }
}
