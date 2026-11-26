package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

        // Multiline wrapping cell factory for the Tier column
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
                    text.setStyle("-fx-font-family: 'Segoe UI', monospace; -fx-font-size: 11px; -fx-fill: #1e293b;");
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
