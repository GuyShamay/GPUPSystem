package application.components.graphinfo;

import application.components.app.AppController;
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

public class GraphInfoController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();

        data = appController.getEngineInfo();
        graphTable.setItems(data);
        graphTable.getColumns().addAll(targetCol,typeCol,requiredForCol,dependsOnCol,serialCountCol,dataCol);
    }

    private void initColumns() {
        targetCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        dataCol.setCellValueFactory(
                new PropertyValueFactory<>("data")
        );
        typeCol.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );
        targetCol.setCellValueFactory(
                new PropertyValueFactory<>("dependsOn")
        );
        targetCol.setCellValueFactory(
                new PropertyValueFactory<>("requiredFor")
        );
        targetCol.setCellValueFactory(
                new PropertyValueFactory<>("serialSets")
        );
    }
}
