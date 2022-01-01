package application.components.app;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class AppController {

    @FXML
    private Button buttonLoadFile;

    @FXML
    private Button buttonInfo;

    @FXML
    private Button buttonActions;

    @FXML
    private Button buttonTask;

    @FXML
    private ComboBox<String> ComboBoxActions;

    @FXML
    void buttonActionsClicked(ActionEvent event) {

    }

    @FXML
    void buttonInfoClicked(ActionEvent event) {

    }

    @FXML
    void buttonLoadFileClicked(ActionEvent event) {

    }

    @FXML
    void buttonTaskClicked(ActionEvent event) {

    }

    @FXML
    public void initialize() {
        ComboBoxActions.getItems().removeAll(ComboBoxActions.getItems());
        ComboBoxActions.getItems().addAll("Find Path","Find Circle","What-if?");
    }

    @FXML
    void ActionChoosen(ActionEvent event) {

    }


}