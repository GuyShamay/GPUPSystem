package application.components.graphinfo;

import application.components.app.AppController;
import application.general.Controller;
import dto.SerialSetDTO;
import dto.TargetGraphDTO;
import dto.TargetInfoDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class InfoController implements Controller {
    private AppController appController;
    private ObservableList<TargetInfoDTO> targetsData;
    private ObservableList<SerialSetDTO> serialSetData;

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
    private TableView<SerialSetDTO> serialSetTable;

    @FXML
    private TableColumn<SerialSetDTO, String> serialNameCol;

    @FXML
    private TableColumn<SerialSetDTO, String> serialTargets;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void fetchData() {
        initTargetTableColumns();
        initSerialSetTableColumns();
        targetsData = appController.getTargetsInfo();
        serialSetData = appController.getSerialSetInfo();
        updateSumSection(appController.getGraphInfo());
        graphTable.setItems(targetsData);
        serialSetTable.setItems(serialSetData);
    }

    private void updateSumSection(TargetGraphDTO graphInfo) {
        totalTargetsLabel.setText(Integer.toString(graphInfo.getCount()));
        leafLabel.setText(Integer.toString(graphInfo.getLeaves()));
        independentLabel.setText(Integer.toString(graphInfo.getIndependent()));
        midLabel.setText(Integer.toString(graphInfo.getMiddle()));
        rootLabel.setText(Integer.toString(graphInfo.getRoot()));
    }

    private void initTargetTableColumns() {
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

    private void initSerialSetTableColumns() {
        serialNameCol.setCellValueFactory(
                new PropertyValueFactory<SerialSetDTO, String>("name")
        );
        serialTargets.setCellValueFactory(
                new PropertyValueFactory<SerialSetDTO, String>("targetsAsString")
        );
    }
}
