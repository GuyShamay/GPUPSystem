package application.components.findPathes;

import application.general.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class PathesController implements Controller {

    @FXML
    private Label labelFrom;

    @FXML
    private Label labelFromTarget;

    @FXML
    private Label labelTo;

    @FXML
    private Label labelToTarget;

    @FXML
    private CheckBox checkBoxDependsOn;

    @FXML
    private CheckBox checkBoxRequiredFor;

    @FXML
    private TextFlow textFlowPathes;

    @FXML
    private VBox vboxTargets;

    @FXML
    void checkBoxDependsOnChoosen(ActionEvent event) {

    }

    @FXML
    void checkBoxRequiredForChoosen(ActionEvent event) {

    }

    @FXML
    void onFindPathClicked(ActionEvent event) {
    }
}
