package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.MatrixRowModel;

import java.util.List;

/**
 * Controller for the official 3-column ACS Demotion Matrix TableView:
 * | Time | Process | Tier |
 * Redesigned for the Midnight Control Center visual identity.
 */
public class MatrixController {

    private final TableView<MatrixRowModel> tableMatrix;
    private final ObservableList<MatrixRowModel> matrixList = FXCollections.observableArrayList();

    public MatrixController(TableView<MatrixRowModel> tableMatrix,
            TableColumn<MatrixRowModel, Integer> colTime,
            TableColumn<MatrixRowModel, String> colProcess,
            TableColumn<MatrixRowModel, String> colTier) {
        this.tableMatrix = tableMatrix;

        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colProcess.setCellValueFactory(new PropertyValueFactory<>("process"));
        colTier.setCellValueFactory(new PropertyValueFactory<>("tier"));

        // Time column: Center-aligned monospace muted slate
        colTime.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.valueOf(item));
                    setStyle(
                            "-fx-alignment: CENTER; -fx-text-fill: #94A3B8; -fx-font-family: 'Consolas', monospace; -fx-font-weight: bold;");
                }
            }
        });

        // Process column: Badge highlight for running process vs context switch
        colProcess.setCellFactory(tc -> new TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    badge.setText(item);
                    if (item.startsWith("P")) {
                        badge.setStyle(
                                "-fx-background-color: #1B325A; -fx-text-fill: #4F8CFF; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 2px 8px; -fx-font-size: 11px;");
                    } else if (item.toLowerCase().contains("context switch")) {
                        badge.setStyle(
                                "-fx-background-color: #2D2318; -fx-text-fill: #FFB454; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 2px 8px; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle(
                                "-fx-background-color: #1B2740; -fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-padding: 2px 8px; -fx-font-size: 11px;");
                    }
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Multiline wrapping cell factory for the Tier column (Soft white on dark
        // background)
        colTier.setCellFactory(tc -> new TableCell<>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    text.setText(item);
                    text.setStyle(
                            "-fx-font-family: 'Consolas', 'Segoe UI Mono', monospace; -fx-font-size: 11px; -fx-fill: #F1F5FF;");
                    text.wrappingWidthProperty().bind(colTier.widthProperty().subtract(15.0));
                    setGraphic(text);
                    setText(null);
                }
            }
        });

        tableMatrix.setItems(matrixList);
    }

    public void setMatrixRows(List<MatrixRowModel> rows) {
        matrixList.clear();
        if (rows != null) {
            matrixList.addAll(rows);
        }
    }

    public void clear() {
        matrixList.clear();
    }
}
