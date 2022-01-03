package application.components.welcome;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    public void loadSuccess() {
        this.labelLoadingStatus.setText("File loaded successfully!");
        this.labelLoadingStatus.setStyle("-fx-fill: green");
    }

    public void resetLabel() {
        this.labelLoadingStatus.setText("");

    }

    @FXML
    private Label labelLoadingStatus;

}
