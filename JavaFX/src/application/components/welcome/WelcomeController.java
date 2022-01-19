package application.components.welcome;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    public void loadSuccess() {
        labelLoadingStatus.setVisible(true);
        this.labelLoadingStatus.setText("File loaded successfully!");
    }
    public void resetLabel() {
        this.labelLoadingStatus.setText("");
    }

    @FXML
    public void initialize() {
        labelLoadingStatus.setVisible(false);
    }

    @FXML
    private Label labelLoadingStatus;
}
