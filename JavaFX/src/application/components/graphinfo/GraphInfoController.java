package application.components.graphinfo;

import application.components.app.AppController;
import application.general.Controller;
import gpup.dto.TargetInfoDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphInfoController implements Controller {

    private AppController appController;

    private ObservableList<TargetInfoDTO> data;

    @FXML
    private TableView<TargetInfoDTO> graphTable;
    @FXML
    private TableColumn<TargetInfoDTO, String> targetCol;
    @FXML
    private TableColumn<TargetInfoDTO, String> typeCol;
    @FXML
    private TableColumn<TargetInfoDTO, Integer> requiredForCol;
    @FXML
    private TableColumn<TargetInfoDTO, Integer> dependsOnCol;
    @FXML
    private TableColumn<TargetInfoDTO, Integer> serialCountCol;
    @FXML
    private TableColumn<TargetInfoDTO, String> dataCol;
    @FXML
    private Label totalTargetsLabel;
    @FXML
    private Label independentLabel;
    @FXML
    private Label rootLabel;
    @FXML
    private Label midLabel;
    @FXML
    private Label leafLabel;
    @FXML
    private TableView<?> serialSetTable;
    @FXML
    private TableColumn<?, ?> serialNameCol;
    @FXML
    private TableColumn<?, ?> serialTargets;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void initialize() {
        graphTable = new TableView<>();
        initColumns();
        data = appController.getTargetsInfo();
        graphTable.setItems(data);
        graphTable.getColumns().addAll(targetCol, typeCol, requiredForCol, dependsOnCol, serialCountCol, dataCol);
    }

    private void initColumns() {
        targetCol.setCellValueFactory(
                new PropertyValueFactory<TargetInfoDTO, String>("name")
        );
        dataCol.setCellValueFactory(
                new PropertyValueFactory<TargetInfoDTO, String>("data")
        );
        typeCol.setCellValueFactory(
                new PropertyValueFactory<TargetInfoDTO, String>("type")
        );
        dependsOnCol.setCellValueFactory(
                new PropertyValueFactory<TargetInfoDTO, Integer>("dependsOn")
        );
        requiredForCol.setCellValueFactory(
                new PropertyValueFactory<TargetInfoDTO, Integer>("requiredFor")
        );
        serialCountCol.setCellValueFactory(
                new PropertyValueFactory<TargetInfoDTO, Integer>("serialSets")
        );
    }
}
