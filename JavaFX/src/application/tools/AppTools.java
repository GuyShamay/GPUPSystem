package application.tools;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public abstract class AppTools {
    public static void warningAlert(String header, String msg, String instructions) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(header);
        alert.setHeaderText(msg);
        alert.setContentText(instructions);
        alert.showAndWait();
    }

    public static boolean confirmationAlert(String header, String msg, String instructions) {
        final boolean[] result = new boolean[1];
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, instructions, ButtonType.YES, ButtonType.NO);
        alert.setTitle(header);
        alert.setHeaderText(msg);
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                result[0] = true;
            }
        });
        return result[0];
    }

}
