package application.components.task.config.compile;

import application.components.task.config.TaskConfigController;
import application.general.Controller;
import component.task.config.CompileConfig;
import component.task.config.SimulationConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class CompileConfigController implements Controller {
    private TaskConfigController taskController;
    private CompileConfig compileConfig;
    private boolean validSrc;
    private boolean validDest;

    @FXML
    private Label srcLabel;

    @FXML
    private Label destLabel;
    @FXML
    private Button submitButton;
    @FXML
    void buttonDestClicked(ActionEvent event) {
        validDest = chooseDir(event, destLabel);
        if (validDest) {
            compileConfig.setDestDir(destLabel.getText());
        }
    }

    @FXML
    void buttonSrcClicked(ActionEvent event) {
        validSrc = chooseDir(event, srcLabel);
        if (validSrc) {
            compileConfig.setDestDir(srcLabel.getText());
        }
    }

    private boolean chooseDir(ActionEvent event, Label srcLabel) {
        Node node = (Node) event.getSource();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir =
                directoryChooser.showDialog(node.getScene().getWindow());

        if (selectedDir == null) {
            srcLabel.setText("No Directory selected");
            return false;
        } else {
            srcLabel.setText(selectedDir.getAbsolutePath());
            return true;
        }
    }

    @FXML
    void buttonSubmitClicked(ActionEvent event) {
        if (validDest && validSrc) {
            taskController.updateConfig(compileConfig);
            submitButton.setDisable(true);
            taskController.showFinalSubmit();
        } else {
            if (!validDest) {
                destLabel.setText("Please select folders");
            }
            if (!validSrc) {
                srcLabel.setText("Please select folders");
            }
        }
    }

    @FXML
    public void initialize() {
        compileConfig = new CompileConfig();
    }

    @Override
    public void setParentController(Controller c) {
        this.taskController = (TaskConfigController) c;
    }
}
