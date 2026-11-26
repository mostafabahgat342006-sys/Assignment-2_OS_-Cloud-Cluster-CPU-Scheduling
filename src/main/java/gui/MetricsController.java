package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Process;

import java.util.List;

/**
 * Controller for the Process Metrics TableView.
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

        tableMetrics.setItems(processList);
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
