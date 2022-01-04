package application.components.whatIf;

import application.components.app.AppController;
import application.general.Controller;
import component.target.TargetsRelationType;
import dto.TargetInfoDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public class WhatIfController implements Controller {
    private AppController appController;

    @Override
    public void setParentController(Controller c) {
        appController=(AppController)c;
    }

    @FXML
    private ComboBox<String> comboBoxTargets;

    @FXML
    private Label labelCircuit;

    @FXML
    private Button buttonOk;

    @FXML
    private CheckBox checkBoxDependsOn;

    @FXML
    private CheckBox checkBoxRequiredFor;

    @FXML
    void checkBoxDependsOnChoosen(ActionEvent event) {
        checkBoxRequiredFor.setSelected(false); }

    @FXML
    void checkBoxRequiredForChoosen(ActionEvent event) {
        checkBoxDependsOn.setSelected(false); }

    @FXML
    private TextFlow textFlowTargets;

    @FXML
    void buttonWhatIfClicked(ActionEvent event) {
        textFlowTargets.getChildren().clear();
        String msg = "";
        if(allDetailsIn()) {
            msg = getTargetsStringToShow();
        } else {
            msg= "Missing Details";
        }

        textFlowTargets.getChildren().add(new Text(msg));
    }

    private boolean allDetailsIn() {
        return (!comboBoxTargets.getSelectionModel().isEmpty() && (checkBoxDependsOn.isSelected()|| checkBoxRequiredFor.isSelected()));
    }

    private String getTargetsStringToShow() {
        String targets = "";
        TargetsRelationType relationType = checkBoxDependsOn.isSelected() == true ? TargetsRelationType.DependsOn : TargetsRelationType.RequiredFor;
        List<TargetInfoDTO> targetsList = appController.getTarget(comboBoxTargets.getValue(), relationType);
        ///To be replaced
        for (TargetInfoDTO targetInfoDTO : targetsList) {
            targets += targetInfoDTO.getName() + " , ";
        }
        String str = targetsList.isEmpty() == true ? "No Targets" : targets.substring(0, targets.length() - 3);
        return str;
    }

    public void Init(){
        appController.fillComboBoxWithTargets(comboBoxTargets);
    }
}
