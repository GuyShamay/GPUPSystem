package application.components.whatIf;

import application.components.app.AppController;
import application.general.Controller;
import component.target.TargetsRelationType;
import dto.TargetInfoDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.util.List;

public class WhatIfController implements Controller {
    private AppController appController;

    @Override
    public void setParentController(Controller c) {
        appController = (AppController) c;
    }

    @FXML
    private CheckBox checkBoxDependsOn;

    @FXML
    private CheckBox checkBoxRequiredFor;

    @FXML
    private ComboBox<String> comboBoxTarget;

    @FXML
    private Label labelMessage;

    @FXML
    private HBox hBoxWhatIf;

    @FXML
    void buttonSubmitClicked(ActionEvent event) {
        labelMessage.setText("");
        hBoxWhatIf.getChildren().clear();
        if (checkBoxDependsOn.isSelected() || checkBoxRequiredFor.isSelected()) {
            if (isValidChoose()) {
                TargetsRelationType relationType = checkBoxDependsOn.isSelected() ? TargetsRelationType.DependsOn : TargetsRelationType.RequiredFor;
                List<TargetInfoDTO> targetsList = appController.getTarget(comboBoxTarget.getValue(), relationType);
                if (!targetsList.isEmpty()) {
                    for (int i = 0; i < targetsList.size(); i++) {
                        Label label;
                        if (i == targetsList.size() - 1) {
                            label = new Label(targetsList.get(i).getName());
                        } else {
                            label = new Label(targetsList.get(i).getName() + ", ");
                        }
                        label.getStyleClass().add("lblItemTarget");
                        hBoxWhatIf.getChildren().add(label);
                    }
                }else {labelMessage.setText("There aren't any targets");}

            } else {
                labelMessage.setText("Please select a target");
            }
        } else {
            labelMessage.setText("Please select relation");
        }
    }

    @FXML
    void checkBoxDependsOnChosen(ActionEvent event) {
        checkBoxRequiredFor.setSelected(!checkBoxDependsOn.isSelected());
    }

    @FXML
    void checkBoxRequiredForChosen(ActionEvent event) {
        checkBoxDependsOn.setSelected(!checkBoxRequiredFor.isSelected());
    }


    private boolean isValidChoose() {
        return (!comboBoxTarget.getSelectionModel().isEmpty());
    }

    public void init() {
        appController.fillComboBoxWithTargets(comboBoxTarget);
    }
}
