package application.components.task.config;

import application.components.task.TaskController;
import application.components.task.config.compile.CompileConfigController;
import application.components.task.config.simulation.SimulationConfigController;
import application.general.Component;
import application.general.ComponentCreator;
import application.general.Controller;
import component.target.TargetsRelationType;
import component.task.ProcessingType;
import component.task.config.Config;
import component.task.config.Selections;
import component.task.config.TaskConfig;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class TaskConfigController implements Controller {
    private final String SIMULATION_CONFIG_FXML_NAME = "simulation/simulation-config.fxml";
    private final String COMPILE_CONFIG_FXML_NAME = "compile/compile-config.fxml";

    private TaskController taskController;
    private SimulationConfigController simulationController;
    private CompileConfigController compileController;

    private TaskConfig taskConfig;
    private Config typeConfig;

    private BooleanProperty isSubmitted;
    private boolean targetSubmit;
    private boolean settingsSubmit;

    private Selections.TargetSelect targetSelect = Selections.TargetSelect.non;
    private Selections.SettingsSelect settingsSelect = Selections.SettingsSelect.non;
    private Selections.TypeSelect typeSelect = Selections.TypeSelect.non;
    ObservableList<String> targets;

    public void setParentController(Controller taskController) {
        this.taskController = (TaskController) taskController;
    }

    public BooleanProperty submittedProperty() {
        return isSubmitted;
    }

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
    private Label warningTaskTypeLabel;
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
    private BorderPane taskParamBorderPane;
    @FXML
    private Button finalSubmitButton;
    @FXML
    private TitledPane titledPaneStep1;
    @FXML
    private TitledPane titledPaneStep2;
    @FXML
    private TitledPane titledPaneStep3;

    @FXML
    public void initialize() {
        taskConfig = new TaskConfig();
        wayChoice.getItems().addAll("Depends On", "Required For");
        targetSubmit = false;
        settingsSubmit = false;
        finalSubmitButton.setVisible(false);
        isSubmitted = new SimpleBooleanProperty(false);
        titledPaneStep1.setExpanded(true);
    }

    public void fetchData() {
        fillTargetsInView();
        fillThreadsSpinner();
    }

    private void fillThreadsSpinner() {
        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, taskController.getMaxParallelism());
        threadSpinner.setValueFactory(factory);
    }

    private void fillTargetsInView() {
        Set<String> targetsNames = taskController.getTargetsListByName();
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
            targetSubmit = true;
            titledPaneStep1.setExpanded(false);
            titledPaneStep2.setExpanded(true);
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
                taskConfig.setProcessingType(ProcessingType.FromScratch);
                break;
            case inc:
                taskConfig.setProcessingType(ProcessingType.Incremental);
                break;
        }
        if (settingsSelect != Selections.SettingsSelect.non) {
            // fromScratchCheckBox.setDisable(true);
            //incrementalCheckBox.setDisable(true);
            taskConfig.setThreadsParallelism(threadSpinner.getValue());
            warningSettingsLabel.setText("Settings submitted!");
            settingsSubmit = true;
            titledPaneStep2.setExpanded(false);
            titledPaneStep3.setExpanded(true);
        }
    }
    // -------------------------------------------------------------------
    // Type

    @FXML
    void simulationChecked(ActionEvent event) {
        if (simulationCheckBox.isSelected()) {
            compileCheckBox.setSelected(!simulationCheckBox.isSelected());
            typeSelect = Selections.TypeSelect.simulation;
            URL url = getClass().getResource(SIMULATION_CONFIG_FXML_NAME);
            Component simulationComp = ComponentCreator.createComponent(url);
            simulationController = (SimulationConfigController) simulationComp.getController();
            simulationController.setParentController(this);
            taskParamBorderPane.setCenter(simulationComp.getPane());
        } else {
            typeSelect = Selections.TypeSelect.non;
        }

    }

    @FXML
    void compileChecked(ActionEvent event) {
        if (compileCheckBox.isSelected()) {
            simulationCheckBox.setSelected(!compileCheckBox.isSelected());
            typeSelect = Selections.TypeSelect.compile;

            URL url = getClass().getResource(COMPILE_CONFIG_FXML_NAME);
            Component compileComp = ComponentCreator.createComponent(url);
            compileController = (CompileConfigController) compileComp.getController();
            compileController.setParentController(this);
            taskParamBorderPane.setCenter(compileComp.getPane());
        } else {
            typeSelect = Selections.TypeSelect.non;
        }
    }

    @FXML
    void buttonFinalSubmitClicked(ActionEvent event) {
        switch (typeSelect) {
            case non:
                warningTaskTypeLabel.setText("You have to select an option");
                break;
            case simulation:
                break;
            case compile:
                break;
        }
        if (typeSelect != Selections.TypeSelect.non) {
            if (targetSubmit) {
                if (settingsSubmit) {
                    isSubmitted.set(true);
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                    taskController.setTasConfig(taskConfig);
                } else {
                    warningTaskTypeLabel.setText("Please complete step 2");
                }
            } else {
                warningTaskTypeLabel.setText("Please complete step 1");
            }
        }
    }

    public void updateConfig(Config config) {
        this.taskConfig.setConfig(config);
    }

    public void showFinalSubmit() {
        finalSubmitButton.setVisible(true);
    }
}
