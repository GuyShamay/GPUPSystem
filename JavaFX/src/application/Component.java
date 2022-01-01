package application;

import javafx.scene.layout.Pane;

public class Component {
    private Controller controller;
    private Pane pane;

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public Pane getPane() {
        return pane;
    }
}
