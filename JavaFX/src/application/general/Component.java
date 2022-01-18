package application.general;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class Component {
    private Controller controller;
    private Region pane;

    public void setPane(Region pane) {
        this.pane = pane;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public Region getPane() {
        return pane;
    }

}
