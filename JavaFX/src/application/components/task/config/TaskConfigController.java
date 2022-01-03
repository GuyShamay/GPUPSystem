package application.components.task.config;

import application.components.app.AppController;
import application.general.Controller;
import component.target.TargetsRelationType;
import component.task.config.Selections;
import component.task.config.TaskConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class TaskConfigController implements Controller {

    public void setAppController(Controller appController) {
        this.appController = (AppController) appController;
    }

    private AppController appController;

    private TaskConfig taskConfig;
    private Selections.TargetSelect targetSelect = Selections.TargetSelect.non;
    private Selections.SettingsSelect settingsSelect = Selections.SettingsSelect.non;
    private Selections.TypeSelect typeSelect = Selections.TypeSelect.non;

    ObservableList<String> targets;
    @FXML
    private CheckBox allTargetCheckBox;

    @FXML
    private ListView<String> targetListView;

    @FXML
    private CheckBox customTargetCheckBox;

    @FXML
    private CheckBox whatIfCheckBox;

    @FXML
    private ChoiceBox<String> wayChoice;

    @FXML
    private Button submitTargetButton;

    @FXML
    private ComboBox<String> targetChoice;

    @FXML
    private Label warningTargetsLabel;

    @FXML
    private CheckBox fromScratchCheckBox;

    @FXML
    private CheckBox incrementalCheckBox;

    @FXML
    private Spinner<Integer> threadSpinner;

    @FXML
    private Button submitSettingsButton;

    @FXML
    private Label warningSettingsLabel;

    @FXML
    private Label incLabel;

    @FXML
    private CheckBox simulationCheckBox;

    @FXML
    private CheckBox compileCheckBox;

    @FXML
    private Pane taskParamPane;

    @FXML
    private Button finalSubmitButton;

    @FXML
    public void initialize() {
        taskConfig = new TaskConfig();
        wayChoice.getItems().addAll("Depends On", "Required For");
    }

    public void fetchData() {
        fillTargetsInView();
        fillThreadsSpinner();
    }

    private void fillThreadsSpinner() {

    }

    private void fillTargetsInView() {
        Set<String> targetsNames = appController.getTargetsListByName();
        targets = FXCollections.observableArrayList(targetsNames);
        targetListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        targetListView.setItems(targets);
        targetChoice.setItems(targets);
    }

    // -------------------------------------------------------------------
    // Targets
    @FXML
    void allTargetsChecked(ActionEvent event) {
        if (allTargetCheckBox.isSelected()) {
            customTargetCheckBox.setSelected(!allTargetCheckBox.isSelected());
            disableCustomTargets(true);
            disableWhatIfTarget(true);
            whatIfCheckBox.setSelected(!allTargetCheckBox.isSelected());
            targetSelect = Selections.TargetSelect.all;
        } else {
            targetSelect = Selections.TargetSelect.non;
        }
    }

    @FXML
    void customTargetsChecked(ActionEvent event) {
        if (customTargetCheckBox.isSelected()) {
            allTargetCheckBox.setSelected(!customTargetCheckBox.isSelected());
            whatIfCheckBox.setSelected(!customTargetCheckBox.isSelected());
            disableCustomTargets(false);
            disableWhatIfTarget(true);
            targetSelect = Selections.TargetSelect.custom;
        } else {
            disableCustomTargets(true);
            targetSelect = Selections.TargetSelect.non;
        }
    }

    @FXML
    void whatIfTargetChecked(ActionEvent event) {
        if (whatIfCheckBox.isSelected()) {
            disableWhatIfTarget(false);
            allTargetCheckBox.setSelected(!whatIfCheckBox.isSelected());
            customTargetCheckBox.setSelected(!whatIfCheckBox.isSelected());
            disableCustomTargets(true);
            targetSelect = Selections.TargetSelect.whatif;
        } else {
            disableWhatIfTarget(true);
            targetSelect = Selections.TargetSelect.non;
        }
    }

    @FXML
    void buttonTargetClicked(ActionEvent event) {
        switch (targetSelect) {
            case non:
                warningTargetsLabel.setText("You have to select an option");
                break;
            case all:
                taskConfig.setAllTargets(true);
                break;
            case custom:
                if (targetListView.getSelectionModel().getSelectedItems().size() == 0) {
                    targetSelect = Selections.TargetSelect.non;
                    customTargetCheckBox.setSelected(false);
                    warningTargetsLabel.setText("You didn't chose any targets.");
                } else {
                    updateCustomTargetInTaskConfig();
                }
                break;
            case whatif:
                if (targetChoice.getSelectionModel().isEmpty() || wayChoice.getSelectionModel().isEmpty()) {
                    targetSelect = Selections.TargetSelect.non;
                    whatIfCheckBox.setSelected(false);
                    warningTargetsLabel.setText("You didn't chose a target and/or a relation (way).");
                } else {
                    updateWhatIfInTaskConfig();
                }
                break;
        }

        if (targetSelect != Selections.TargetSelect.non) {
            warningTargetsLabel.setText("Settings submitted!");
            submitTargetButton.setDisable(true);
            disableAllTargets();
        }
    }

    private void updateWhatIfInTaskConfig() {
        switch (wayChoice.getSelectionModel().getSelectedItem()) {
            case "Depends On":
                taskConfig.setWhatIfRelation(TargetsRelationType.DependsOn);
                break;
            case "Required For":
                taskConfig.setWhatIfRelation(TargetsRelationType.RequiredFor);
                break;
        }
        taskConfig.setWhatIfTarget(targetChoice.getSelectionModel().getSelectedItem());
    }

    private void updateCustomTargetInTaskConfig() {
        ObservableList<String> selectedTargets = targetListView.getSelectionModel().getSelectedItems();
        List<String> list = new ArrayList<>(selectedTargets);
        taskConfig.setCustomTargets(list);
    }

    private void disableAllTargets() {
        allTargetCheckBox.setDisable(true);
        customTargetCheckBox.setDisable(true);
        targetListView.setDisable(true);
        whatIfCheckBox.setDisable(true);
        targetChoice.setDisable(true);
        wayChoice.setDisable(true);

    }

    private void disableCustomTargets(boolean bool) {
        targetListView.setDisable(bool);
    }

    private void disableWhatIfTarget(boolean bool) {
        targetChoice.setDisable(bool);
        wayChoice.setDisable(bool);
    }


    // -------------------------------------------------------------------
    // Settings

    @FXML
    void fromScratchChecked(ActionEvent event) {
        if (fromScratchCheckBox.isSelected()) {
            incrementalCheckBox.setSelected(!fromScratchCheckBox.isSelected());
            settingsSelect = Selections.SettingsSelect.scratch;
        } else {
            settingsSelect = Selections.SettingsSelect.non;
        }
    }

    @FXML
    void incrementalChecked(ActionEvent event) {
        if (incrementalCheckBox.isSelected()) {
            fromScratchCheckBox.setSelected(!incrementalCheckBox.isSelected());
            settingsSelect = Selections.SettingsSelect.inc;
        } else {
            settingsSelect = Selections.SettingsSelect.non;
        }
    }

    @FXML
    void buttonSettingsClicked(ActionEvent event) {

        switch (settingsSelect) {
            case non:
                warningSettingsLabel.setText("You have to select an option");
                break;
            case scratch:
                taskConfig.setFromScratch(true);
                break;
            case inc:
                taskConfig.setIncremental(true);
                break;
        }
        if (settingsSelect != Selections.SettingsSelect.non) {
            fromScratchCheckBox.setDisable(true);
            incrementalCheckBox.setDisable(true);
            taskConfig.setThreadsParallelism(threadSpinner.getValue());
        }
    }
    // -------------------------------------------------------------------
    // Type

    @FXML
    void simulationChecked(ActionEvent event) {
        compileCheckBox.setSelected(!simulationCheckBox.isSelected());

    }

    @FXML
    void compileChecked(ActionEvent event) {
        simulationCheckBox.setSelected(!compileCheckBox.isSelected());

    }


    @FXML
    void buttonFinalSubmitClicked(ActionEvent event) {

    }
}
