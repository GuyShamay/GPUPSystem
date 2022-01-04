package application.components.task.config.simulation;

import application.components.task.config.TaskConfigController;
import application.general.Controller;
import component.task.config.SimulationConfig;
import component.task.simulation.ProcessingTimeType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SimulationConfigController implements Controller {
    private TaskConfigController taskController;

    private SimulationConfig simulationConfig;

    @FXML
    private TextField procTimeTextField;
    @FXML
    private CheckBox randomCheckBox;

    @FXML
    private Spinner<Integer> successSpinner;

    @FXML
    private Spinner<Integer> warningSpinner;

    @FXML
    private Button submitButton;

    @FXML
    private Label warningLabel;

    @FXML
    void buttonSubmitClicked(ActionEvent event) {
        try {
            if (validProcTime()) {
                simulationConfig.setProcessingTime(Integer.parseInt(procTimeTextField.getText()));
                simulationConfig.setProcessingTimeType(randomCheckBox.isSelected() ? ProcessingTimeType.Random : ProcessingTimeType.Permanent);
                simulationConfig.setSuccessProb(successSpinner.getValue());
                simulationConfig.setSuccessWithWarningsProb(warningSpinner.getValue());
                warningLabel.setText("Success!");
                taskController.updateConfig(simulationConfig);
                submitButton.setDisable(true);
                taskController.showFinalSubmit();
            }
        } catch (NumberFormatException ex) {
            warningLabel.setText("Processing Time must be Integer Number");
            procTimeTextField.setText("");
        }
    }

    public void setTaskConfigController(TaskConfigController taskConfigController) {
        this.taskController = taskConfigController;
    }

    private boolean validProcTime() {
        Integer.parseInt(procTimeTextField.getText());
        return true;
    }

    @FXML
    public void initialize() {
        simulationConfig = new SimulationConfig();
        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
        SpinnerValueFactory<Integer> factory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
        successSpinner.setValueFactory(factory);
        warningSpinner.setValueFactory(factory2);
        successSpinner.getValueFactory().setValue(50);
        warningSpinner.getValueFactory().setValue(50);
    }

    @Override
    public void setParentController(Controller c) {
        this.taskController = (TaskConfigController) c;
    }
}
