package application.tools;

import javafx.scene.control.Alert;

public abstract class AppTools {
    public static void warningAlert(String header, String msg, String instructions){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(header);
        alert.setHeaderText(msg);
        alert.setContentText(instructions);
        alert.showAndWait();
    }

}
