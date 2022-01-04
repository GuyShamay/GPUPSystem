package application.components.findPathes;

import application.components.app.AppController;
import application.general.Controller;
import component.target.TargetsRelationType;
import dto.PathsDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Set;

public class PathsController implements Controller {

    private AppController appController;

    @FXML
    private Label labelFrom;

    @FXML
    private Label labelTo;

    @FXML
    private CheckBox checkBoxDependsOn;

    @FXML
    private CheckBox checkBoxRequiredFor;

    @FXML
    private TextFlow textFlowPaths;

    @FXML
    private ComboBox<String> comboBoxFrom;;

    @FXML
    private ComboBox<String> comboBoxTo;

    @FXML
    void checkBoxDependsOnChoosen(ActionEvent event) {checkBoxRequiredFor.setSelected(false); }

    @FXML
    void checkBoxRequiredForChoosen(ActionEvent event) {checkBoxDependsOn.setSelected(false); }

    @FXML
    void onFindPathClicked(ActionEvent event) {
        textFlowPaths.getChildren().clear();
        String msg="";
        if(allDetailsIn()) {
            msg = getPathsToShow();
        } else{
            msg= "Missing Details";
        }
        textFlowPaths.getChildren().add(new Text(msg));
    }

    private String getPathsToShow() {
        TargetsRelationType relationType = checkBoxDependsOn.isSelected() == true ? TargetsRelationType.DependsOn : TargetsRelationType.RequiredFor;
        PathsDTO paths = appController.getFoundPaths(comboBoxFrom.getValue(), comboBoxTo.getValue(), relationType);
        return paths.toString().substring(paths.toString().indexOf(":")+1);
    }


    private boolean allDetailsIn() {
        return (!comboBoxFrom.getSelectionModel().isEmpty() &&
                !comboBoxTo.getSelectionModel().isEmpty()&&
                (checkBoxDependsOn.isSelected()|| checkBoxRequiredFor.isSelected()));
    }

    public void Init(){
        initTargetsToChoose();
        labelFrom.setStyle("-fx-font-weight: bold");
        comboBoxFrom.setOnAction((event -> {
            labelTo.setStyle("-fx-font-weight: bold");
            labelFrom.setStyle("-fx-font-weight: regular");
        }));
        comboBoxTo.setOnAction(event -> {
            labelFrom.setStyle("-fx-font-weight: bold");
            labelTo.setStyle("-fx-font-weight: regular");
        });
    }

    private void initTargetsToChoose() {
        appController.fillComboBoxWithTargets(comboBoxTo);
        appController.fillComboBoxWithTargets(comboBoxFrom);
    }

    @Override
    public void setParentController(Controller c){
        appController = (AppController)c;
    }
}
